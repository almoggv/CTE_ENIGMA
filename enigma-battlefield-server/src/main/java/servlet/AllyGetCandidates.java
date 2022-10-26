package servlet;

import com.google.gson.Gson;
import dto.*;
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
import utils.ServletUtils;
import utils.SessionUtills;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "AllyGetCandidatesServlet" ,urlPatterns = {"/ally-get-candidates"})
public class AllyGetCandidates extends HttpServlet {
    private static final Logger log = Logger.getLogger(AllyGetCandidates.class);
    static {
        try {
            Properties p = new Properties();
            p.load(AllyGetCandidates.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + AllyGetCandidates.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + AllyGetCandidates.class.getSimpleName() ) ;
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
        String usernameFromSession = SessionUtills.getUsername(req);
        if(usernameFromSession == null){
            resp.setStatus(SC_UNAUTHORIZED);
            respWriter.print("Please login first.");
            return;
        }

        DecryptionResultPayload payload = new DecryptionResultPayload();
        resp.setStatus(SC_OK);
        AllyTeamData ally = userManager.getAllyByName(usernameFromSession);
        if(ally == null){
            resp.setStatus(SC_UNAUTHORIZED);
            respWriter.print("Not an ally.");
            return;
        }
        payload.setEncryptionCandidateList(ally.getEncryptionCandidateList());

        Gson gson = new Gson();
        resp.setHeader(PropertiesService.getHttpHeaderContentType(),PropertiesService.getJsonHttpContentType());
        respWriter.print(gson.toJson(payload));
    }
}
