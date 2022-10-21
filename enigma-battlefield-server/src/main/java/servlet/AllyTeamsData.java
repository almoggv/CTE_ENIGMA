package servlet;

import com.google.gson.Gson;
import dto.ContestAllyTeamsPayload;
import dto.ContestRoom;
import dto.ContestRoomPayload;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.RoomManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@WebServlet(name = "AllyTeamsDataServlet" ,urlPatterns = {"/ally-teams"})
public class AllyTeamsData extends HttpServlet {
    private static final Logger log = Logger.getLogger(AllyTeamsData.class);
    static {
        try {
            Properties p = new Properties();
            p.load(AllyTeamsData.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + AllyTeamsData.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + AllyTeamsData.class.getSimpleName() ) ;
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

        Object roomName =  req.getSession(false).getAttribute(PropertiesService.getRoomNameAttributeName());

        if(roomName == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("You are not assigned to a room.");
            return;
        }
        ContestRoom roomInfo = roomManager.getRoomByName((String) roomName);

        ContestAllyTeamsPayload payload = new ContestAllyTeamsPayload();
        if(roomInfo == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            //todo: is the best response
            payload.setMessage("Please upload a schema file first. (/upload-machine-file)");
        }
        else{
            resp.setStatus(SC_OK);
            payload.setAllyTeamsData(roomInfo.getAlliesList());
        }
        Gson gson = new Gson();
        resp.setHeader(PropertiesService.getHttpHeaderContentType(),PropertiesService.getJsonHttpContentType());
        respWriter.print(gson.toJson(payload));
    }
}
