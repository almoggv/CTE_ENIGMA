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
import model.Ally;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;
import utils.ServletUtils;
import utils.SessionUtills;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        Ally ally = userManager.getAllyByName(usernameFromSession);
        if(ally == null){
            resp.setStatus(SC_UNAUTHORIZED);
            respWriter.print("Not an ally.");
            return;
        }
        List<EncryptionCandidate> currCandidateList =  ally.getEncryptionCandidateList();
        if(currCandidateList == null || currCandidateList.isEmpty()){
            payload.setEncryptionCandidateList(new ArrayList<>());
        }
        else{
            payload.setEncryptionCandidateList(Arrays.asList(ally.getEncryptionCandidateList().get(ally.getEncryptionCandidateList().size()-1)));
        }

        resp.setStatus(SC_OK);
        Gson gson = new Gson();
        resp.setHeader(PropertiesService.getHttpHeaderContentType(),PropertiesService.getJsonHttpContentType());
        respWriter.print(gson.toJson(payload));
    }
}
