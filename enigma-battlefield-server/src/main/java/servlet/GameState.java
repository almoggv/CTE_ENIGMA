package servlet;

import dto.GameStatePayload;
import dto.User;
import enums.GameStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.RoomManager;
import manager.UserManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;
import utils.ServletUtils;
import utils.SessionUtills;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@WebServlet(name = "GameStatusServel" ,urlPatterns = {"/join"})
public class GameState extends HttpServlet {
    private static final Logger log = Logger.getLogger(HttpServlet.class);
    static {
        try {
            Properties p = new Properties();
            p.load(HttpServlet.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Don't forget here
            log.debug("Logger Instantiated for : " + HttpServlet.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + HttpServlet.class.getSimpleName() ) ;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter respWriter;
        GameStatePayload gameStatePayload = new GameStatePayload();
        try {
            respWriter = resp.getWriter();
        }
        catch (Exception e){
            log.error("Failed to get response Writer, Exception Message=" + e.getMessage());
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            return;
        }

        RoomManager roomManager = ServletUtils.getRoomManager(this.getServletContext());
        UserManager userManager = ServletUtils.getUserManager(this.getServletContext());
        String usernameFromSession = SessionUtills.getUsername(req);
        if(usernameFromSession == null){
            resp.setStatus(SC_UNAUTHORIZED);
            gameStatePayload.setMessage("Please login first to");
        }
        else{
            User loggedUser = userManager.getUserByName(usernameFromSession);
            // how do i get from the user to the room he is in?
            roomManager.get

        }
    }
}
