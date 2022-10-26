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
import model.ContestRoom;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;
import utils.ServletUtils;
import utils.SessionUtills;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "PostCandidatesServlet" ,urlPatterns = {"/post-candidates"})
public class PostCandidates extends HttpServlet {
    private static final Logger log = Logger.getLogger(PostCandidates.class);
    static {
        try {
            Properties p = new Properties();
            p.load(PostCandidates.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Don't forget here
            log.debug("Logger Instantiated for : " + PostCandidates.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + PostCandidates.class.getSimpleName() ) ;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.getWriter().print("in post candidates servlet");
        if(!req.getHeader(PropertiesService.getHttpHeaderContentType()).contains(PropertiesService.getJsonHttpContentType())){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("Expecting json content type");
            return;
        }
        if(req.getContentType() == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("Must enter a legal state in json format.");
            return;
        }
        PrintWriter respWriter;
        try{
            respWriter = resp.getWriter();

        } catch (Exception e) {
            log.error("Failed to get response Writer, Exception Message=" + e.getMessage());
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            return;
        }
//
        //todo: return -this is in comment just to see if works:
        RoomManager roomManager = ServletUtils.getRoomManager(this.getServletContext());
        UserManager userManager = ServletUtils.getUserManager(this.getServletContext());
        String usernameFromSession = SessionUtills.getUsername(req);
        if (usernameFromSession == null) {
            resp.setStatus(SC_UNAUTHORIZED);
            respWriter.print("Please login first to");
            return;
        }

        AgentData agent = userManager.getAgentByName(usernameFromSession);
        Ally ally = userManager.getAllyByName(agent.getAllyName());

        ContestRoom contestRoom = userManager.getUserByName(usernameFromSession).getContestRoom();
        if (contestRoom == null) {
            resp.setStatus(SC_UNAUTHORIZED);
            respWriter.print("you are not connected to a room.");
            return;
        }

        BufferedReader bufferedReader = req.getReader();
        String rawMachineState = bufferedReader.lines().collect(Collectors.joining());
        Gson gson = new Gson();

        DecryptionResultPayload recvPayload = gson.fromJson(rawMachineState, DecryptionResultPayload.class);
        List<EncryptionCandidate> candidateList = recvPayload.getEncryptionCandidateList();
        if(candidateList == null || candidateList.isEmpty()) {
//                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            respWriter.print("No candidate given.");
            return;
        }
        else{
            synchronized (this.getServletContext()) {
                //adds candidates to agents ally team
                ally.getEncryptionCandidateList().addAll(candidateList);
                //adds candidates to contest room
                contestRoom.getEncryptionCandidateList().addAll(candidateList);
                roomManager.checkWin(contestRoom, userManager.getUboatByName(contestRoom.getCreatorName()).getOriginalWord());
                resp.getWriter().print("got candidates:" + candidateList);
                resp.setStatus(SC_OK);
            }
        }
    }
}
