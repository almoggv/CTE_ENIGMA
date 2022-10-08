package servlet;

import com.google.gson.Gson;
import component.MachineHandler;
import dto.MachineStatePayload;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import component.MachineHandler;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

@WebServlet(urlPatterns = {"/assemble-machine-randomly"})
public class AssembleMachineRandomly extends HttpServlet {
    private static final Logger log = Logger.getLogger(AssembleMachineRandomly.class);
    static {
        try {
            Properties p = new Properties();
            p.load(AssembleMachineRandomly.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + AssembleMachineRandomly.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + AssembleMachineRandomly.class.getSimpleName() ) ;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        MachineStatePayload payload = new MachineStatePayload();
        if(machineHandler == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            payload.setMessage("Please upload a schema file first. (/upload-machine-file)");
        }
        else {
            try {
                machineHandler.assembleMachine();
            } catch (Exception ignore) {            }
            resp.setStatus(200);
            payload.setMessage("Machine assembled successfully");
        }
        Gson gson = new Gson();
        resp.setHeader(PropertiesService.getHttpHeaderContentType(),PropertiesService.getJsonHttpContentType());
        respWriter.print(gson.toJson(payload));
    }
}
