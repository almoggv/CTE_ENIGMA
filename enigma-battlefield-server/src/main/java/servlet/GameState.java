package servlet;

import com.google.gson.Gson;
import dto.GameStatePayload;
import model.ContestRoom;
import model.User;
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

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "GameStatusServlet" ,urlPatterns = {"/game-status"})
public class GameState extends HttpServlet {
    private static final Logger log = Logger.getLogger(GameState.class);
    static {
        try {
            Properties p = new Properties();
            p.load(GameState.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Don't forget here
            log.debug("Logger Instantiated for : " + GameState.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + GameState.class.getSimpleName() ) ;
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
        Object roomName =  req.getSession(false).getAttribute(PropertiesService.getRoomNameAttributeName());

        if(roomName == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("You are not assigned to a room.");
            return;
        }

        else{
            User loggedUser = userManager.getUserByName(usernameFromSession);
            ContestRoom contestRoom = loggedUser.getContestRoom();
            GameStatePayload payload = new GameStatePayload();
            payload.setGameState(contestRoom.getGameStatus());
            resp.setStatus(SC_OK);

            Gson gson = new Gson();
            resp.setHeader(PropertiesService.getHttpHeaderContentType(),PropertiesService.getJsonHttpContentType());
            respWriter.print(gson.toJson(payload));
            return;
        }
    }
}
