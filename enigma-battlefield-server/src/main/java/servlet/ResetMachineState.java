package servlet;

import component.MachineHandler;
import dto.MachineStatePayload;
import dto.VersionDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
@WebServlet("/reset-machine-state")
public class ResetMachineState extends HttpServlet {
    private static final Logger log = Logger.getLogger(ResetMachineState.class);
    static {
        try {
            Properties p = new Properties();
            p.load(ResetMachineState.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Don't forget here
            log.debug("Logger Instantiated for : " + ResetMachineState.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + ResetMachineState.class.getSimpleName() ) ;
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

        MachineHandler machineHandler = (MachineHandler) req.getSession(false).getAttribute(PropertiesService.getMachineHandlerAttributeName());
        if(machineHandler == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            respWriter.print("Please upload a schema file first. (/upload-machine-file)");
            return;
        }

        machineHandler.resetToLastSetState();
        resp.setStatus(SC_OK);
        respWriter.print("Reset to last set machine state.");
    }
}
