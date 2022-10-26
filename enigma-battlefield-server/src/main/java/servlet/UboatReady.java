package servlet;

import com.google.gson.Gson;
import component.MachineHandler;
import dto.ContestRoom;
import dto.ContestRoomPayload;
import dto.MachineInventoryPayload;
import dto.User;
import enums.UserType;
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
        UserManager userManager = ServletUtils.getUserManager(this.getServletContext());

        User user = userManager.getUserByName((String) req.getSession(false).getAttribute(PropertiesService.getUsernameAttributeName()));
        if(user == null){
            resp.setStatus(SC_BAD_REQUEST);
            respWriter.print("user does not exist");
            return;
        }
        ContestRoom contestRoom = user.getContestRoom();

        String wordToDecrypt = req.getParameter(PropertiesService.getEncryptedWordAttributeName());
        String originalWord = req.getParameter(PropertiesService.getOriginalWordAttributeName());

        if(contestRoom == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            if (user.getType().equals(UserType.UBOAT)) {
                respWriter.print("Please upload a schema file first. (/upload-machine-file)");
            }
            else {
                respWriter.print("You are not assigned to a room.");
            }
        }
        //todo: maybe change to save every encryption?
        if(wordToDecrypt == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("You need to give a word for the contest.");
            return;
        }
        if(originalWord == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("Missing original word.");
            return;
        }

        MachineHandler machineHandler = (MachineHandler) req.getSession(false).getAttribute(PropertiesService.getMachineHandlerAttributeName());
        if(machineHandler == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            respWriter.print("Please upload a schema file first. (/upload-machine-file)");
        }
        else{
            resp.setStatus(SC_OK);
            roomManager.setUserReady(user,contestRoom);
            contestRoom.setWordToDecrypt((String) wordToDecrypt);
            userManager.getUboatByName(user.getUsername()).setOriginalWord(originalWord);
            //todo - see how to send
            //causes problem to ally - gson from payload - because there isnt an empty machine handler constructor ?
            //Interface can't be instantiated! Interface name: component.MachineHandler
            //Unable to invoke no-args constructor for interface component.MachineHandler
//            contestRoom.setMachineHandler(machineHandler);
            respWriter.print("Ready with contest word: " + wordToDecrypt);
        }
    }
}
