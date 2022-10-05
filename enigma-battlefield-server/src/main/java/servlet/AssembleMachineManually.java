package servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import component.MachineHandler;
import dto.MachineState;
import enums.ReflectorsId;
import generictype.MappingPair;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.PropertiesService;
import jakarta.servlet.annotation.WebServlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


@WebServlet("/assemble-machine-manually")
public class AssembleMachineManually extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(req.getContentType() == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("Must enter a legal state in json format.");
            return;
        }
        if(!req.getHeader(PropertiesService.getHttpHeaderContentType()).equals(PropertiesService.getJsonHttpContentType())){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("Expecting json content type");
            return;
        }

        MachineHandler machineHandler = (MachineHandler) this.getServletContext().getAttribute(PropertiesService.getMachineHandlerAttributeName());
        if(machineHandler == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("Please upload a schema file first. (/upload-machine-file)");
            return;
        }

        BufferedReader bufferedReader = req.getReader();
        String rawMachineState = bufferedReader.lines().collect(Collectors.joining());
        Gson gson = new Gson();
        try{
            MachineState machineState = gson.fromJson(rawMachineState, MachineState.class);
            if(!isMachineStatePresent(machineState)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().print("Not a valid machine state.");
                return;
            }
            if(machineState.getPlugMapping() == null){
                machineState.setPlugMapping(new ArrayList<>());
            }
            try {
                machineHandler.assembleMachine(machineState);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().print("Machine assembled successfully");
            }
            catch (Exception exception){
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().print("Problem assembling machine.");
            }
        }
        catch (JsonSyntaxException exception){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("Problem parsing json file. please try again.");
        }
    }

    private boolean isMachineStatePresent(MachineState machineState) {
        boolean result = machineState.getRotorIds() != null
                && machineState.getRotorsHeadsInitialValues() != null
                && machineState.getReflectorId() != null;
        return result;
    }
}
