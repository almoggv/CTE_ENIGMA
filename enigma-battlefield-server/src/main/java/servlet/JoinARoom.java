package servlet;

import com.google.gson.Gson;
import dto.AllyTeamData;
import dto.ContestRoom;
import dto.ContestRoomPayload;
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
import dto.User;
import utils.SessionUtills;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "JoinRoomServlet" ,urlPatterns = {"/join"})
public class JoinARoom extends HttpServlet {
    private static final Logger log = Logger.getLogger(JoinARoom.class);
    static {
        try {
            Properties p = new Properties();
            p.load(JoinARoom.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Don't forget here
            log.debug("Logger Instantiated for : " + JoinARoom.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + JoinARoom.class.getSimpleName() ) ;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter respWriter;
        try {
            respWriter = resp.getWriter();
        }
        catch (Exception e){
            log.error("Failed to get response Writer, Exception Message=" + e.getMessage());
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            return;
        }

        RoomManager roomManager = ServletUtils.getRoomManager(this.getServletContext());
        String roomName = req.getParameter(PropertiesService.getRoomNameAttributeName());

        UserManager userManager = ServletUtils.getUserManager(this.getServletContext());
        String usernameFromSession = SessionUtills.getUsername(req);
        if(usernameFromSession == null){
            resp.setStatus(SC_BAD_REQUEST);
            respWriter.print("Missing user name in params.");
            respWriter.print("");
            return;
        }
        if (roomName == null || roomName.isEmpty()) {
            resp.setStatus(SC_BAD_REQUEST);
            respWriter.print("Missing room name in params.");
            respWriter.print("");
            return;
        }

        ContestRoom contestRoom = roomManager.getRoomByName((String) roomName);
        User user = userManager.getUserByName(usernameFromSession);

        ContestRoomPayload payload = new ContestRoomPayload();
        if(contestRoom == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            //todo: is the best response
            payload.setMessage("Room doesnt exist.");
        } else if (contestRoom.getCurrNumOfTeams() >= contestRoom.getRequiredNumOfTeams()){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            //todo: is the best response
            payload.setMessage("The room is already full, you can't join.");
        }
        if(user.isInARoom()){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            //todo: is the best response
            payload.setMessage("You are already in a contest.");
        }
        else{
            AllyTeamData ally  = userManager.getAllyByName(usernameFromSession);
            User allyUser = userManager.getUserByName(usernameFromSession);
            roomManager.addUserToRoom(ally,userManager, contestRoom);
            user.setInARoom(true);
            resp.setStatus(SC_OK);

            //todo - start the DM by the rooms machine handler or inventory;

            payload.setContestRoom(contestRoom);
            payload.setMessage("Joined room "+ contestRoom.getName() + " Successfully.");

            req.getSession(false).setAttribute(PropertiesService.getRoomNameAttributeName(), roomName);
        }
        Gson gson = new Gson();
        resp.setHeader(PropertiesService.getHttpHeaderContentType(),PropertiesService.getJsonHttpContentType());
        respWriter.print(gson.toJson(payload));
    }

}
