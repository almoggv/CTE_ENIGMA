package servlet;

import component.MachineHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import component.MachineHandler;
import service.PropertiesService;

import java.io.IOException;

@WebServlet(urlPatterns = {"/assemble-machine-randomly"})
public class AssembleMachineRandomly extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            MachineHandler machineHandler = (MachineHandler) this.getServletContext().getAttribute(PropertiesService.getMachineHandlerAttributeName());
            if(machineHandler == null){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().print("Please upload a schema file first. (/upload-machine-file)");
            }
            else {
                machineHandler.assembleMachine();
                resp.setStatus(200);
                resp.getWriter().print("Machine assembled successfully");
            }
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().print(e.getMessage());
        }
    }

}
