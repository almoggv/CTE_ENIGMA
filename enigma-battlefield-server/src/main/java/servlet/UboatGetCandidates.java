package servlet;

import com.google.gson.Gson;
import dto.DecryptionResultPayload;
import dto.EncryptionCandidate;
import enums.UserType;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.RoomManager;
import manager.UserManager;
import model.Ally;
import model.Uboat;
import model.User;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;
import utils.ServletUtils;
import utils.SessionUtills;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "UboatGetCandidatesServlet" ,urlPatterns = {"/uboat-get-candidates"})
public class UboatGetCandidates extends HttpServlet {
    private static final Logger log = Logger.getLogger(UboatGetCandidates.class);
    static {
        try {
            Properties p = new Properties();
            p.load(UboatGetCandidates.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + UboatGetCandidates.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + UboatGetCandidates.class.getSimpleName() ) ;
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
        RoomManager roomManager = ServletUtils.getRoomManager(this.getServletContext());
        String usernameFromSession = SessionUtills.getUsername(req);
        if(usernameFromSession == null){
            resp.setStatus(SC_UNAUTHORIZED);
            respWriter.print("Please login first.");
            return;
        }

        DecryptionResultPayload payload = new DecryptionResultPayload();
        resp.setStatus(SC_OK);
        User uboat = userManager.getUserByName(usernameFromSession);
        if(uboat == null){
            resp.setStatus(SC_UNAUTHORIZED);
            respWriter.print("Not a user.");
            return;
        }
        if(!uboat.getType().equals(UserType.UBOAT)){
            resp.setStatus(SC_UNAUTHORIZED);
            respWriter.print("Not a uboat.");
            return;
        }
        List<EncryptionCandidate> candidateList;
        if(uboat.getContestRoom() == null || uboat.getContestRoom().getEncryptionCandidateList() == null){
            candidateList = new ArrayList<>();
        }
        else{
            candidateList = uboat.getContestRoom().getEncryptionCandidateList();
        }
        payload.setEncryptionCandidateList(candidateList);
        Gson gson = new Gson();
        resp.setHeader(PropertiesService.getHttpHeaderContentType(),PropertiesService.getJsonHttpContentType());
        respWriter.print(gson.toJson(payload));
    }
}
