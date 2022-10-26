package servlet;

import com.google.gson.Gson;
import dto.ContestAllyTeamsPayload;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.RoomManager;
import manager.UserManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;
import sun.net.www.http.HttpClient;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
@WebServlet(name = "AllAliesServlet" ,urlPatterns = {"/all-ally-teams"})
public class AllAlies extends HttpServlet {
    private static final Logger log = Logger.getLogger(AllAlies.class);
    static {
        try {
            Properties p = new Properties();
            p.load(AllAlies.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + AllAlies.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + AllAlies.class.getSimpleName() ) ;
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
        UserManager userManager = ServletUtils.getUserManager(this.getServletContext());
        if(userManager == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            //todo: is the best response
            respWriter.print("no user manager.");
        }

        ContestAllyTeamsPayload payload = new ContestAllyTeamsPayload();
        resp.setStatus(SC_OK);
        payload.setAllyTeamsData(userManager.getAllALies());

        Gson gson = new Gson();
        resp.setHeader(PropertiesService.getHttpHeaderContentType(),PropertiesService.getJsonHttpContentType());
        respWriter.print(gson.toJson(payload));
    }
}