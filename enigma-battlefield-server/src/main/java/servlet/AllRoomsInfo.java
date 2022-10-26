package servlet;

import com.google.gson.Gson;
import dto.AllContestRoomsPayload;
import dto.ContestRoomData;
import dto.ContestRoomPayload;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.RoomManager;
import model.ContestRoom;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Set;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@WebServlet(name = "AllRoomsInfoServlet" ,urlPatterns = {"/get-rooms-info"})
public class AllRoomsInfo extends HttpServlet {
    private static final Logger log = Logger.getLogger(AllRoomsInfo.class);
    static {
        try {
            Properties p = new Properties();
            p.load(AllRoomsInfo.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + AllRoomsInfo.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + AllRoomsInfo.class.getSimpleName() ) ;
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

        Set<ContestRoomData> roomsInfo = roomManager.getRoomsData();

        AllContestRoomsPayload payload = new AllContestRoomsPayload();
        if(roomsInfo == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            //todo: is the best response
            payload.setMessage("No rooms.");
        }
        else{
            resp.setStatus(SC_OK);
            payload.setContestRooms(roomsInfo);
        }
        Gson gson = new Gson();
        resp.setHeader(PropertiesService.getHttpHeaderContentType(),PropertiesService.getJsonHttpContentType());
        respWriter.print(gson.toJson(payload));
    }
}
