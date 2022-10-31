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

@WebServlet(name = "LoginAgentServlet" ,urlPatterns = {"/login-agent"})
public class LoginAgent extends HttpServlet {
    private static final Logger log = Logger.getLogger(LoginAgent.class);
    static {
        try {
            Properties p = new Properties();
            p.load(LoginAgent.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Don't forget here
            log.debug("Logger Instantiated for : " + LoginAgent.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + LoginAgent.class.getSimpleName() ) ;
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
        String allynameFromParameter = req.getParameter(PropertiesService.getAllyNameAttributeName());
        String taskSizeFromParameter = req.getParameter(PropertiesService.getTaskSizeAttributeName());
        String threadNumFromParameter = req.getParameter(PropertiesService.getNumOfThreadsAttributeName());

        if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
            resp.setStatus(SC_BAD_REQUEST);
            repsPayload.setMessage("Missing username in params");
            repsPayload.setAccessToken("");
        }
        else if (allynameFromParameter == null || allynameFromParameter.isEmpty()) {
            resp.setStatus(SC_BAD_REQUEST);
            repsPayload.setMessage("Missing ally name in params");
            repsPayload.setAccessToken("");
        }
        else if( userManager.getAllyByName(allynameFromParameter) == null){
            resp.setStatus(SC_UNAUTHORIZED);
            repsPayload.setMessage("No Ally=" + allynameFromParameter + ", does not exist");
            repsPayload.setAccessToken("");
        }
        else if (taskSizeFromParameter == null || taskSizeFromParameter.isEmpty()) {
            resp.setStatus(SC_BAD_REQUEST);
            repsPayload.setMessage("Missing task size in params");
            repsPayload.setAccessToken("");
        }
        else if (threadNumFromParameter == null || threadNumFromParameter.isEmpty()) {
            resp.setStatus(SC_BAD_REQUEST);
            repsPayload.setMessage("Missing thread number in params");
            repsPayload.setAccessToken("");
        }
        else if (userManager.getUserByName(allynameFromParameter) == null) {
            resp.setStatus(SC_BAD_REQUEST);
            repsPayload.setMessage("No such ally");
            repsPayload.setAccessToken("");
        }
        else {
            try{
                int taskSize = Integer.parseInt(taskSizeFromParameter);
                if(taskSize < 1){
                    resp.setStatus(SC_BAD_REQUEST);
                    repsPayload.setMessage("Task size needs to be a positive number.");
                    repsPayload.setAccessToken("");
                    resp.setHeader(PropertiesService.getHttpHeaderContentType(),PropertiesService.getJsonHttpContentType());
                    Gson gson = LoginPayloadJsonAdapter.buildGsonLoginPayloadAdapter();
                    String serializedPayload = gson.toJson(repsPayload);
                    respWriter.print(serializedPayload);
                    return;
                }
            }
            catch( Exception e) {
                resp.setStatus(SC_BAD_REQUEST);
                repsPayload.setMessage("Task size needs to be a positive number.");
                repsPayload.setAccessToken("");
                resp.setHeader(PropertiesService.getHttpHeaderContentType(),PropertiesService.getJsonHttpContentType());
                Gson gson = LoginPayloadJsonAdapter.buildGsonLoginPayloadAdapter();
                String serializedPayload = gson.toJson(repsPayload);
                respWriter.print(serializedPayload);
                return;
            }
            try{
                int threadNum = Integer.parseInt(threadNumFromParameter);
                if(threadNum < 1 || threadNum >4){
                    resp.setStatus(SC_BAD_REQUEST);
                    repsPayload.setMessage("Thread number needs to be between 1 and 4.");
                    repsPayload.setAccessToken("");
                    resp.setHeader(PropertiesService.getHttpHeaderContentType(),PropertiesService.getJsonHttpContentType());
                    Gson gson = LoginPayloadJsonAdapter.buildGsonLoginPayloadAdapter();
                    String serializedPayload = gson.toJson(repsPayload);
                    respWriter.print(serializedPayload);
                    return;
                }
            }
            catch(Exception e) {
                resp.setStatus(SC_BAD_REQUEST);
                repsPayload.setMessage("Thread number needs to be between 1 and 4.");
                repsPayload.setAccessToken("");
                resp.setHeader(PropertiesService.getHttpHeaderContentType(),PropertiesService.getJsonHttpContentType());
                Gson gson = LoginPayloadJsonAdapter.buildGsonLoginPayloadAdapter();
                String serializedPayload = gson.toJson(repsPayload);
                respWriter.print(serializedPayload);
                return;
            }

            usernameFromParameter = usernameFromParameter.trim();
            synchronized (this) {
                if (userManager.isUserExists(usernameFromParameter)){
                    resp.setStatus(SC_UNAUTHORIZED);
                    repsPayload.setMessage("Username already in use");
                    repsPayload.setAccessToken("");
                }
                else{
                    userManager.addUser(usernameFromParameter, usertypeFromParameter, threadNumFromParameter, taskSizeFromParameter, allynameFromParameter);
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //TODO: do we want to support Post req to login?

        // get body
        //        req.getReader()
    }
}
