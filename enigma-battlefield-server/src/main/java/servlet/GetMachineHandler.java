package servlet;


import component.EncryptionMachine;
import component.MachineHandler;
import dto.MachineClonePayload;
import dto.MachineHandlerPayload;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.RoomManager;
import manager.UserManager;
import model.User;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "GetMachineHandlerServlet" ,urlPatterns = {"/machine-handler"})
public class GetMachineHandler extends HttpServlet {
    private static final Logger log = Logger.getLogger(GetMachineHandler.class);
    static {
        try {
            Properties p = new Properties();
            p.load(GetMachineHandler.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + GetMachineHandler.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + GetMachineHandler.class.getSimpleName() ) ;
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
        MachineHandlerPayload payload = new MachineHandlerPayload();
        UserManager userManager = ServletUtils.getUserManager(this.getServletContext());
        User user = userManager.getUserByName((String) req.getSession(false).getAttribute(PropertiesService.getUsernameAttributeName()));   //Username by session
        if(user == null){
            resp.setStatus(SC_UNAUTHORIZED);
            payload.setMessage("Please login first");
            respWriter.print(payload);
            return;
        }
        try{
            MachineHandler handler = user.getContestRoom().getMachineHandler();
            payload.setMachineHandler(handler);
        }
        catch (Exception e){
            log.error("GetEncryptionMachine failed to get machine handler to user=" + user + "exception=" + e.getMessage());
            payload.setMessage("Failed to get machine, machine might not be configured yet");
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            respWriter.print(payload);
            return;
        }
        payload.setMessage("Handler retrieved successfully");
        resp.setStatus(SC_OK);
        respWriter.print(payload);
        return;
    }
}
