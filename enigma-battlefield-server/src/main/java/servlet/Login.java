package servlet;

import com.google.gson.Gson;
import dto.LoginPayload;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jsonadapter.LoginPayloadJsonAdapter;
import manager.UserManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;
import utils.ServletUtils;
import utils.SessionUtills;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "LoginServlet" ,urlPatterns = {"/login"})
public class Login extends HttpServlet {
    private static final Logger log = Logger.getLogger(Login.class);
    static {
        try {
            Properties p = new Properties();
            p.load(Login.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Don't forget here
            log.debug("Logger Instantiated for : " + Login.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + Login.class.getSimpleName() ) ;
        }
    }
    /**
     * returns a users auth token
     * @param req
     * @return - a user token if login was successful, or null if failed
     */
    protected void processLoginRequest(HttpServletRequest req, HttpServletResponse resp){
        UserManager userManager = ServletUtils.getUserManager(this.getServletContext());
        String usernameFromSession = SessionUtills.getUsername(req);
        LoginPayload repsPayload = new LoginPayload();
        PrintWriter respWriter;
        try {
            respWriter = resp.getWriter();
        }
        catch (Exception e){
            log.error("Failed to get response Writer, Exception Message=" + e.getMessage());
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            return;
        }
        //user is already logged in
        if(usernameFromSession != null){
            String userAccessToken = userManager.getUserToken(usernameFromSession);
            resp.setStatus(SC_OK);
            repsPayload.setMessage("Successfully logged in");
            repsPayload.setAccessToken(userAccessToken);
        }
        //user is not logged in yet, has no session yet
        String usernameFromParameter = req.getParameter(PropertiesService.getUsernameAttributeName());
        String usertypeFromParameter = req.getParameter(PropertiesService.getUserTypeAttributeName());
        if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
            resp.setStatus(SC_BAD_REQUEST);
            repsPayload.setMessage("Missing username in params");
            repsPayload.setAccessToken("");
        }
        else if (usertypeFromParameter == null){
            resp.setStatus(SC_BAD_REQUEST);
            repsPayload.setMessage("Missing userType in params");
            repsPayload.setAccessToken("");
        }
        else{
            usernameFromParameter = usernameFromParameter.trim();
            synchronized (this) {
                if (userManager.isUserExists(usernameFromParameter)){
                    resp.setStatus(SC_UNAUTHORIZED);
                    repsPayload.setMessage("Username already in use");
                    repsPayload.setAccessToken("");
                }
                else{
                    userManager.addUser(usernameFromParameter, usertypeFromParameter);
                    req.getSession(true).setAttribute(PropertiesService.getUsernameAttributeName(), usernameFromParameter);
                    String userAccessToken = userManager.getUserToken(usernameFromParameter);
                    resp.setStatus(SC_OK);
                    repsPayload.setMessage("Successfully logged in");
                    repsPayload.setAccessToken(userAccessToken);
                }
            }
        }
        resp.setHeader(PropertiesService.getHttpHeaderContentType(),PropertiesService.getJsonHttpContentType());
        Gson gson = LoginPayloadJsonAdapter.buildGsonLoginPayloadAdapter();
        String serializedPayload = gson.toJson(repsPayload);
        respWriter.print(serializedPayload);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processLoginRequest(req,resp);
    }
}
