package servlet;

import com.google.gson.Gson;
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

@WebServlet(name = "UboatReadyServlet" ,urlPatterns = {"/uboat-ready"})
public class UboatReady extends HttpServlet {
    private static final Logger log = Logger.getLogger(UboatReady.class);
    static {
        try {
            Properties p = new Properties();
            p.load(UboatReady.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + UboatReady.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + UboatReady.class.getSimpleName() ) ;
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
        String wordToDecrypt = req.getParameter(PropertiesService.getEncryptionInputAttributeName());

        if(roomName == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("You are not assigned to a room.");
            return;
        }
        if(wordToDecrypt == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("You need to give a word for the contest.");
            return;
        }
        ContestRoom roomInfo = roomManager.getRoomByName((String) roomName);

        if(roomInfo == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            respWriter.print("Please upload a schema file first. (/upload-machine-file)");
        }
        else{
            resp.setStatus(SC_OK);
            roomInfo.setWordToDecrypt((String) wordToDecrypt);
            respWriter.print("Ready with contest word: " + wordToDecrypt);
        }
    }
}
