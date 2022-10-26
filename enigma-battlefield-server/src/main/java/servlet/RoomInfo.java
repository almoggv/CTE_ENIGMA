package servlet;

import com.google.gson.Gson;
import dto.*;
import enums.UserType;
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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "RoomInfoServlet" ,urlPatterns = {"/get-room-info"})
public class RoomInfo extends HttpServlet {
    private static final Logger log = Logger.getLogger(RoomInfo.class);
    static {
        try {
            Properties p = new Properties();
            p.load(RoomInfo.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + RoomInfo.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + RoomInfo.class.getSimpleName() ) ;
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
        UserManager userManager = ServletUtils.getUserManager(this.getServletContext());

        User user = userManager.getUserByName((String) req.getSession(false).getAttribute(PropertiesService.getUsernameAttributeName()));
        if(user == null){
            resp.setStatus(SC_BAD_REQUEST);
            respWriter.print("user does not exist");
            return;
        }
        ContestRoom contestRoom = user.getContestRoom();

        ContestRoomPayload payload = new ContestRoomPayload();

        if(contestRoom == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            if (user.getType().equals(UserType.UBOAT)) {
                payload.setMessage("Please upload a schema file first. (/upload-machine-file)");
            }
            else {
                payload.setMessage("You are not assigned to a room.");
            }
        }
        else{
            resp.setStatus(SC_OK);
            payload.setContestRoom(roomManager.makeRoomDataFromRoom(contestRoom));
        }
        Gson gson = new Gson();
        resp.setHeader(PropertiesService.getHttpHeaderContentType(),PropertiesService.getJsonHttpContentType());
        respWriter.print(gson.toJson(payload));
    }
}
