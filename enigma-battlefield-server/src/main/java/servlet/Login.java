package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.LoginPayload;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jsonadapter.LoginPayloadSerializer;
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
            PropertyConfigurator.configure(p);      //Dont forget here
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
            return;
        }
        if(usernameFromSession != null){ //user is already logged in
            String userAccessToken = userManager.getUserToken(usernameFromSession);
            resp.setStatus(SC_OK);
            resp.setHeader(PropertiesService.getTokenAttributeName(),userAccessToken);
            return;
        }
        //user is not logged in yet, has no seesion yet
        String usernameFromParameter = req.getParameter(PropertiesService.getUsernameAttributeName());
        if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
            resp.setStatus(SC_BAD_REQUEST);
            repsPayload.setMessage("Missing username in params");
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
                    userManager.addUser(usernameFromParameter);
                    req.getSession(true).setAttribute(PropertiesService.getUsernameAttributeName(), usernameFromParameter);
                    String userAccessToken = userManager.getUserToken(usernameFromParameter);
                    resp.setStatus(SC_OK);
                    repsPayload.setMessage("Successfully logged in");
                    repsPayload.setAccessToken(userAccessToken);
                }
            }
        }
        Gson gson = buildGsonLoginPayloadSerializer();
        String serializedPayload = gson.toJson(repsPayload);
        respWriter.print(serializedPayload);
        resp.setHeader(PropertiesService.getHttpHeaderContentType(),PropertiesService.getJsonHttpContentType());
        //Before:
        //resp.setHeader(PropertiesService.getTokenAttributeName(),userAccessToken);
    }

    private Gson buildGsonLoginPayloadSerializer(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LoginPayload.class, new LoginPayloadSerializer())
                .setPrettyPrinting()
                .create();
        return gson;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processLoginRequest(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //TOOD: do we want to support Post req to login?

        // get body
        //        req.getReader()
    }
}
