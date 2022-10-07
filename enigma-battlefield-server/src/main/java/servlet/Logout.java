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

@WebServlet(name = "LogoutServlet" ,urlPatterns = {"/logout"})
public class Logout extends HttpServlet {
    private static final Logger log = Logger.getLogger(Logout.class);
    static {
        try {
            Properties p = new Properties();
            p.load(Logout.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + Logout.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + Logout.class.getSimpleName() ) ;
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserManager userManager = ServletUtils.getUserManager(this.getServletContext());
        String usernameFromSession = SessionUtills.getUsername(req);
        String usernameFromParameter = req.getParameter(PropertiesService.getUsernameAttributeName());
        LoginPayload repsPayload = new LoginPayload();
        repsPayload.setAccessToken("");
        PrintWriter respWriter;
        try {
            respWriter = resp.getWriter();
        }
        catch (Exception e){
            log.error("Failed to get response Writer, Exception Message=" + e.getMessage());
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            return;
        }
        if(usernameFromSession != null){ //user is already logged in
            synchronized (this) {
                if (userManager.isUserExists(usernameFromSession)) {
                    userManager.removeUser(usernameFromSession);
                    resp.setStatus(SC_OK);
                    repsPayload.setMessage("User logged out successfully");
                    repsPayload.setAccessToken("");
                }
            }
        }
        else if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
            resp.setStatus(SC_BAD_REQUEST);
            repsPayload.setMessage("Missing username in params");
            repsPayload.setAccessToken("");
        }
        else{
            usernameFromParameter = usernameFromParameter.trim();
            synchronized (this) {
                if (userManager.isUserExists(usernameFromParameter)) {
                    userManager.removeUser(usernameFromParameter);
                    resp.setStatus(SC_OK);
                    repsPayload.setMessage("User logged out successfully");
                    repsPayload.setAccessToken("");
                }
            }
        }
        resp.setHeader(PropertiesService.getHttpHeaderContentType(),PropertiesService.getJsonHttpContentType());
        Gson gson = buildGsonLoginPayloadSerializer();
        String serializedPayload = gson.toJson(repsPayload);
        respWriter.print(serializedPayload);
    }

    private Gson buildGsonLoginPayloadSerializer(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LoginPayload.class, new LoginPayloadSerializer())
                .setPrettyPrinting()
                .create();
        return gson;
    }
}
