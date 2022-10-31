package servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import component.MachineHandler;
import dto.MachineState;
import dto.MachineStatePayload;
import enums.ReflectorsId;
import generictype.MappingPair;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;
import jakarta.servlet.annotation.WebServlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;


@WebServlet("/assemble-machine-manually")
public class AssembleMachineManually extends HttpServlet {
    private static final Logger log = Logger.getLogger(AssembleMachineManually.class);
    static {
        try {
            Properties p = new Properties();
            p.load(AssembleMachineManually.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + AssembleMachineManually.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + AssembleMachineManually.class.getSimpleName() ) ;
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(req.getContentType() == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("Must enter a legal state in json format.");
            return;
        }
        if(!req.getHeader(PropertiesService.getHttpHeaderContentType()).contains(PropertiesService.getJsonHttpContentType())){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("Expecting json content type");
            return;
        }
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
        MachineStatePayload sendPayload = new MachineStatePayload();
        if(machineHandler == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendPayload.setMessage("Please upload a schema file first. (/upload-machine-file)");
            return;
        }

        BufferedReader bufferedReader = req.getReader();
        String rawMachineState = bufferedReader.lines().collect(Collectors.joining());
        Gson gson = new Gson();
        try{
            MachineStatePayload receivePayload = gson.fromJson(rawMachineState, MachineStatePayload.class);
            MachineState machineState = receivePayload.getMachineState();
            if(!isMachineStatePresent(machineState)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendPayload.setMessage("Not a valid machine state.");
                return;
            }
            if(machineState.getPlugMapping() == null){
                machineState.setPlugMapping(new ArrayList<>());
            }
            try {
                machineHandler.assembleMachine(machineState);
                resp.setStatus(HttpServletResponse.SC_OK);
                sendPayload.setMessage("Machine assembled successfully");
                sendPayload.setMachineState(machineHandler.getInitialMachineState().get());
            }
            catch (Exception exception){
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                sendPayload.setMessage("Problem assembling machine.");
            }
        }
        catch (JsonSyntaxException exception){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("Problem parsing json file. please try again.");
        }
        finally {
            respWriter.print(gson.toJson(sendPayload));
        }
    }

    private boolean isMachineStatePresent(MachineState machineState) {
        boolean result = machineState.getRotorIds() != null
                && machineState.getRotorsHeadsInitialValues() != null
                && machineState.getReflectorId() != null;
        return result;
    }
}
