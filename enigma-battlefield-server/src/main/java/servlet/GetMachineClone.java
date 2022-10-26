package servlet;

import dto.MachineClonePayload;
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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "GetMachineCloneServlet" ,urlPatterns = {"/clone-machine"})
public class GetMachineClone extends HttpServlet {
    private static final Logger log = Logger.getLogger(AllRoomsInfo.class);
    static {
        try {
            Properties p = new Properties();
            p.load(GetMachineClone.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + GetMachineClone.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + GetMachineClone.class.getSimpleName() ) ;
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
        MachineClonePayload payload = new MachineClonePayload();
        RoomManager roomManager = ServletUtils.getRoomManager(this.getServletContext());
        UserManager userManager = ServletUtils.getUserManager(this.getServletContext());
        User user = userManager.getUserByName((String) req.getSession(false).getAttribute(PropertiesService.getUsernameAttributeName()));   //Username by session
        if(user == null){
            resp.setStatus(SC_UNAUTHORIZED);
            payload.setMessage("Please login first");
            respWriter.print(payload);
            return;
        }
        try{
//            MachineHandler handler = user.getContestRoom().getMachineHandler();
//            payload.setEncryptionMachine(handler.getEncryptionMachineClone());
//            payload.setMachineStaste(handler.getEncryptionMachineClone().getMachineState().get());
        }
        catch (Exception e){
            log.error("GetEncryptionMachine failed to get machine handler to user=" + user + "exception=" + e.getMessage());
            payload.setMessage("Failed to get machine, machine might not be configured yet");
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            respWriter.print(payload);
            return;
        }
        payload.setMessage("Machine Cloned Successfully");
        resp.setStatus(SC_OK);
        respWriter.print(payload);
        return;
    }
}
