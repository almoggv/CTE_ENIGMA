package main.java.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.java.component.MachineHandler;
import main.java.service.PropertiesService;

import java.io.IOException;

@WebServlet(urlPatterns = {"/assemble-machine-randomly"})
public class AssembleMachineRandomly extends CurrMachineConfiguration {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            MachineHandler machineHandler = (MachineHandler) this.getServletContext().getAttribute(PropertiesService.getMachineHandlerAttributeName());
            machineHandler.assembleMachine();
            resp.setStatus(200);
            resp.getWriter().print("Machine assembled");
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().print(e.getMessage());
        }
    }

}
