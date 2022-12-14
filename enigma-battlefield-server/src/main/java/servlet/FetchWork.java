package servlet;

import com.google.gson.Gson;
import dto.AgentData;
import dto.DecryptionWorkPayload;
import dto.MachineState;
import enums.GameStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.AllyClientDM;
import manager.UserManager;
import manager.impl.AllyClientDMImpl;
import model.Ally;
import model.Uboat;
import model.User;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "FetchWorkServlet" ,urlPatterns = {"/fetch-work"})
public class FetchWork extends HttpServlet {
    private static final Logger log = Logger.getLogger(FetchWork.class);
    static {
        try {
            Properties p = new Properties();
            p.load(FetchWork.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + FetchWork.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + FetchWork.class.getSimpleName() ) ;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        DecryptionWorkPayload payload = new DecryptionWorkPayload();
        PrintWriter respWriter;
        try {
            respWriter = resp.getWriter();
        }
        catch (Exception e){
            log.error("Failed to get response Writer, Exception Message=" + e.getMessage());
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            return;
        }
        String sizeParam = req.getParameter(PropertiesService.getBatchSizeAttributeName());
        if(sizeParam == null){
            resp.setStatus(SC_BAD_REQUEST);
            payload.setMessage("please specify batch size - paramName=" + PropertiesService.getBatchSizeAttributeName());
            respWriter.print(gson.toJson(payload));
            return;
        }
        int batchSize = Integer.valueOf(sizeParam);
        if(batchSize<0){
            resp.setStatus(SC_BAD_REQUEST);
            payload.setMessage("batch size cannot be negative");
            respWriter.print(gson.toJson(payload));
            return;
        }
        UserManager userManager = ServletUtils.getUserManager(this.getServletContext());
        User user = userManager.getUserByName((String) req.getSession(false).getAttribute(PropertiesService.getUsernameAttributeName()));   //Username by session
        if(user == null){
            resp.setStatus(SC_UNAUTHORIZED);
            payload.setMessage("Please login first");
            respWriter.print(gson.toJson(payload));
            return;
        }
        if(user.getContestRoom().getGameStatus() != GameStatus.READY && user.getContestRoom().getGameStatus() != GameStatus.IN_PROGRESS){
            resp.setStatus(SC_UNAUTHORIZED);
            payload.setMessage("Contest Have not started yet");
            respWriter.print(gson.toJson(payload));
            return;
        }
        Ally currentLoggedInAlly = findAlly(userManager,user);
        if(currentLoggedInAlly == null){
            log.error("Failed to Fetch work - could not find ally with User=" + user);
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            payload.setMessage("Could not find ally");
            respWriter.print(gson.toJson(payload));
            return;
        }
        AllyClientDM allyClientDM = currentLoggedInAlly.getDecryptionManager();
        if(allyClientDM == null){
            log.warn("Fetch work: ally client DM is null, creating a new ally DM");
            currentLoggedInAlly.setDecryptionManager(new AllyClientDMImpl());
            allyClientDM = currentLoggedInAlly.getDecryptionManager();
            allyClientDM.setDifficultyLevel(user.getContestRoom().getDifficultyLevel());
            allyClientDM.setTaskSize(currentLoggedInAlly.getTaskSize());
            allyClientDM.setInventoryInfo(user.getContestRoom().getMachineHandler().getInventoryInfo().get());
            currentLoggedInAlly.setDecryptionManagerThread(new Thread(allyClientDM));
            currentLoggedInAlly.getDecryptionManagerThread().start();
        }
        List<List<MachineState>> workBatchesRecieved = new ArrayList<>();
        int  amountOfStates = 0;
        for (int i = 0; i < batchSize ; i++) {
            List<MachineState> newWorkBatch = allyClientDM.getNextBatch();
            if(newWorkBatch == null){
                break;
            }
            amountOfStates += newWorkBatch.size();
            workBatchesRecieved.add(newWorkBatch);
        }
        if(workBatchesRecieved == null || workBatchesRecieved.isEmpty() || workBatchesRecieved.get(0).isEmpty() ){
            log.error("Failed to Fetch work - work Batches Recieved is null or empty, value= " + workBatchesRecieved + "AllyDM=" + allyClientDM );
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            payload.setMessage("Didnt receive a work batch");
            payload.setAmountOfStates(0);
            respWriter.print(gson.toJson(payload));
            return;
        }
        payload.setFirstState(workBatchesRecieved.get(0).get(0));
        List<MachineState> lastBatch = workBatchesRecieved.get(workBatchesRecieved.size()-1);
        payload.setLastState(lastBatch.get(lastBatch.size()-1));
        payload.setAmountOfStates(amountOfStates);
        payload.setDifficultyLevel(user.getContestRoom().getDifficultyLevel());
        payload.setInputToDecrypt(user.getContestRoom().getWordToDecrypt());
        payload.setMessage("workBatch created successfully");
        resp.setStatus(SC_OK);
        respWriter.print(gson.toJson(payload));
        return;
    }

    private Ally findAlly(UserManager userManager, User user){
        Ally foundAlly = userManager.getAllyByName(user.getUsername());
        if(foundAlly!=null){
            return foundAlly;
        }
        AgentData agent = userManager.getAgentByName(user.getUsername());
        if(agent != null){
            return userManager.getAllyByName(agent.getAllyName());
        }
        Uboat uboat = userManager.getUboatByName(user.getUsername());
        if(uboat != null){
            return null;
        }
        return null;
    }
}
