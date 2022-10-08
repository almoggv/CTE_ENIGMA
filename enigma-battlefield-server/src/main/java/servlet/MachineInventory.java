package servlet;

import com.google.gson.Gson;
import component.MachineHandler;
import dto.InventoryInfo;
import dto.MachineInventoryPayload;
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
import java.util.Optional;
import java.util.Properties;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@WebServlet(name = "MachineInventoryServlet" ,urlPatterns = {"/get-machine-inventory"})
public class MachineInventory extends HttpServlet {
    private static final Logger log = Logger.getLogger(MachineInventory.class);
    static {
        try {
            Properties p = new Properties();
            p.load(MachineInventory.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + MachineInventory.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + MachineInventory.class.getSimpleName() ) ;
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
        MachineInventoryPayload payload = new MachineInventoryPayload();
        if(machineHandler == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            payload.setMessage("Please upload a schema file first. (/upload-machine-file)");
        }
        else{
            Optional<InventoryInfo> inventoryInfo = machineHandler.getInventoryInfo();
            if(!inventoryInfo.isPresent()){
                log.error("Inventory info is not present");
                resp.setStatus(SC_INTERNAL_SERVER_ERROR);
                payload.setMessage("Internal error - Please re-upload a schema. (/upload-machine-file)");
            }
            else{
                resp.setStatus(SC_OK);
                payload.setInventory(inventoryInfo.get());
            }
        }
        Gson gson = new Gson();
        resp.setHeader(PropertiesService.getHttpHeaderContentType(),PropertiesService.getJsonHttpContentType());
        respWriter.print(gson.toJson(payload));
    }
}
