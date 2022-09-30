package main.java.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.java.component.MachineHandler;
import main.java.service.PropertiesService;

import java.io.IOException;

@WebServlet("/assemble-machine-manually")
public class AssembleMachineManually extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!req.getContentType().equals(PropertiesService.getJsonHttpContentType())){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("Expecting json content type");
        }
        else{
//            try{
//                MachineHandler machineHandler = (MachineHandler) this.getServletContext().getAttribute(PropertiesService.getMachineHandlerAttributeName());
//
//                Gson gson = new Gson();
//
//            }
        }
    }
}
