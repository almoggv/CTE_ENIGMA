package servlet;

import com.google.gson.Gson;
import dto.AllyWorkProgressPayload;
import generictype.MappingPair;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.AllyClientDM;
import manager.RoomManager;
import manager.UserManager;
import model.Ally;
import model.User;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

@WebServlet(name = "AllyCreatedWorkProgress" ,urlPatterns = {"/ally-progress"})
public class AllyCreatedWorkProgress extends HttpServlet {
    private static final Logger log = Logger.getLogger(AllyCreatedWorkProgress.class);
    static {
        try {
            Properties p = new Properties();
            p.load(AllyCreatedWorkProgress.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + AllyCreatedWorkProgress.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + AllyCreatedWorkProgress.class.getSimpleName() ) ;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AllyWorkProgressPayload payload = new AllyWorkProgressPayload();
        Gson gson = new Gson();
        PrintWriter respWriter;
        try {
            respWriter = resp.getWriter();
        }
        catch (Exception e){
            log.error("Failed to get response Writer, Exception Message=" + e.getMessage());
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            return;
        }
        RoomManager roomManager = ServletUtils.getRoomManager(this.getServletContext());
        UserManager userManager = ServletUtils.getUserManager(this.getServletContext());
        String username = (String) req.getSession(false).getAttribute(PropertiesService.getUsernameAttributeName());
        if(username == null){
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            payload.setMessage("Please login first");
            resp.getWriter().print(gson.toJson(payload));
            return;
        }
        Ally ally = userManager.getAllyByName(username);
        if(ally == null){
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            payload.setMessage("only allies are allowed access");
            resp.getWriter().print(gson.toJson(payload));
            return;
        }
        AllyClientDM allyDm = ally.getDecryptionManager();
        if(allyDm == null){
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            payload.setMessage("Decryption Manager was not configured yet");
            resp.getWriter().print(gson.toJson(payload));
            return;
        }
        MappingPair<Long,Long> progress = allyDm.getProgressProperty().get();
        payload.setProgress(progress);
        payload.setMessage("Progress fetched successfully");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().print(gson.toJson(payload));
        return;
    }
}
