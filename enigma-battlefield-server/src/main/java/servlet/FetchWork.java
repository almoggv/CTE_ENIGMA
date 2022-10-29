package servlet;

import com.google.gson.Gson;
import dto.DecryptionWorkPayload;
import dto.MachineState;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.AllyClientDM;
import manager.UserManager;
import manager.impl.AllyClientDMImpl;
import model.Ally;
import model.User;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
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
        Ally currentLoggedInAlly = userManager.getAllyByName(user.getUsername());
        if(currentLoggedInAlly == null){
            log.error("Failed to Fetch work - could not find ally with User=" + user);
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            payload.setMessage("Could not find ally");
            respWriter.print(gson.toJson(payload));
            return;
        }
        AllyClientDM allyClientDM = currentLoggedInAlly.getDecryptionManager();
        if(allyClientDM == null){
            currentLoggedInAlly.setDecryptionManager(new AllyClientDMImpl());
            allyClientDM = currentLoggedInAlly.getDecryptionManager();
            allyClientDM.setDifficultyLevel(user.getContestRoom().getDifficultyLevel());
            allyClientDM.setTaskSize(currentLoggedInAlly.getTaskSize());
            allyClientDM.setInventoryInfo(user.getContestRoom().getMachineHandler().getInventoryInfo().get());
            currentLoggedInAlly.setDecryptionManagerThread(new Thread(allyClientDM));
            currentLoggedInAlly.getDecryptionManagerThread().start();
        }
        List<MachineState> newWorkBatch = allyClientDM.getNextBatch();
        payload.setFirstState(newWorkBatch.get(0));
        payload.setLastState(newWorkBatch.get(newWorkBatch.size()-1));
        payload.setAmountOfStates(newWorkBatch.size());
        payload.setDifficultyLevel(user.getContestRoom().getDifficultyLevel());
        payload.setInputToDecrypt(user.getContestRoom().getWordToDecrypt());
        payload.setMessage("workBatch created successfully");
        resp.setStatus(SC_OK);
        respWriter.print(gson.toJson(payload));
        return;
    }
}
