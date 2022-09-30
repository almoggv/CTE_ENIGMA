package main.java.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.java.component.MachineHandler;
import main.java.dto.InventoryInfo;
import main.java.service.PropertiesService;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/get-machine-details")
public class MachineInventoryDetails extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            MachineHandler machineHandler = (MachineHandler) this.getServletContext().getAttribute(PropertiesService.getMachineHandlerAttributeName());
            Optional<InventoryInfo> inventoryInfo = machineHandler.getInventoryInfo();
            if(inventoryInfo.isPresent()){
                Gson gson = new Gson();
                resp.getWriter().print(gson.toJson(inventoryInfo.get()));
                resp.setStatus(200);
            }
        }
        catch (Exception ignore){
            resp.setStatus(500);
            resp.getWriter().print("problem");
        }
    }
}
