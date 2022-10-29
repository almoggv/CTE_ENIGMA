package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.RoomManager;
import manager.UserManager;
import model.ContestRoom;
import model.User;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;
import utils.ServletUtils;
import utils.SessionUtills;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "PostGotWinHandshakeServlet" ,urlPatterns = {"/got-win"})
public class GotWinHandshake extends HttpServlet {
    private static final Logger log = Logger.getLogger(GotWinHandshake.class);

    static {
        try {
            Properties p = new Properties();
            p.load(GotWinHandshake.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Don't forget here
            log.debug("Logger Instantiated for : " + GotWinHandshake.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + GotWinHandshake.class.getSimpleName());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter respWriter;
        try{
            respWriter = resp.getWriter();

        } catch (Exception e) {
            log.error("Failed to get response Writer, Exception Message=" + e.getMessage());
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            return;
        }

        RoomManager roomManager = ServletUtils.getRoomManager(this.getServletContext());
        UserManager userManager = ServletUtils.getUserManager(this.getServletContext());
        String usernameFromSession = SessionUtills.getUsername(req);
        if (usernameFromSession == null) {
            resp.setStatus(SC_UNAUTHORIZED);
            respWriter.print("Please login first to");
            return;
        }

        ContestRoom contestRoom = userManager.getUserByName(usernameFromSession).getContestRoom();
        if (contestRoom == null) {
            resp.setStatus(SC_UNAUTHORIZED);
            respWriter.print("you are not connected to a room.");
            return;
        }

        User user = userManager.getUserByName(usernameFromSession);
        if(!user.isSentGotWin()) {
            roomManager.updateGotWon(contestRoom, userManager);
            user.setSentGotWin(true);
            respWriter.print("thanks for telling the server you  got a win.");
            resp.setStatus(SC_OK);
        }
        else{
            respWriter.print("you have already sent about receiving a win.");
            resp.setStatus(SC_BAD_REQUEST);
        }
    }
}


