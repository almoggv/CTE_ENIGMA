package servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dto.User;
import manager.UserManager;
import service.PropertiesService;
import utils.ServletUtils;
import utils.SessionUtills;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "LoginServlet" ,urlPatterns = {"/login"})
public class Login extends HttpServlet {

    /**
     * returns a users auth token
     * @param req
     * @return - a user token if login was successful, or null if failed
     */
    protected void processLoginRequest(HttpServletRequest req, HttpServletResponse resp){
        UserManager userManager = ServletUtils.getUserManager(this.getServletContext());
        String usernameFromSession = SessionUtills.getUsername(req);
        if (usernameFromSession == null) { //user is not logged in yet, has no seesion yet
            String usernameFromParameter = req.getParameter(PropertiesService.getUsernameAttributeName());
            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                resp.setStatus(SC_BAD_REQUEST);
                return;
            }
            usernameFromParameter = usernameFromParameter.trim();
            synchronized (this) {
                if (userManager.isUserExists(usernameFromParameter)){
                    resp.setStatus(SC_UNAUTHORIZED);
                    return;
                }
                userManager.addUser(usernameFromParameter);
                req.getSession(true).setAttribute(PropertiesService.getUsernameAttributeName(), usernameFromParameter);
                String userAccessToken = userManager.getUserToken(usernameFromParameter);
                resp.setStatus(SC_OK);
                resp.setHeader(PropertiesService.getTokenAttributeName(),userAccessToken);
            }
        }
        else {
            String userAccessToken = userManager.getUserToken(usernameFromSession);
            resp.setStatus(SC_OK);
            resp.setHeader(PropertiesService.getTokenAttributeName(),userAccessToken);
            return;
        }
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
