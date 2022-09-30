package main.java.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.java.component.MachineHandler;
import main.java.dto.MachineState;
import main.java.service.PropertiesService;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/curr-machine-config")
public class CurrMachineConfiguration extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            MachineHandler machineHandler = (MachineHandler) this.getServletContext().getAttribute(PropertiesService.getMachineHandlerAttributeName());
            if(machineHandler == null){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().print("Please upload a schema file first. (/upload-machine-file)");
            }
            else {
                Optional<MachineState> machineState = machineHandler.getMachineState();
                if (machineState.isPresent()) {
                    Gson gson = new Gson();
                    resp.getWriter().print(gson.toJson(machineState.get()));
                    resp.setStatus(200);
                }
            }
        }
        catch (Exception ignore){
            resp.setStatus(500);
            resp.getWriter().print("problem");
        }
    }
}
