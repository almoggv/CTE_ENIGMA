package servlet;

import com.google.gson.Gson;
import dto.EncryptionResponsePayload;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import component.MachineHandler;
import dto.MachineState;
import manager.DictionaryManager;
import manager.DictionaryManagerStatic;
import manager.UserManager;
import model.User;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;
import utils.ServletUtils;
import utils.StringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "EncryptServlet" ,urlPatterns = {"/encrypt"})
public class Encrypt extends HttpServlet {
    private static final Logger log = Logger.getLogger(Encrypt.class);
    static {
        try {
            Properties p = new Properties();
            p.load(Encrypt.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + Encrypt.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + Encrypt.class.getSimpleName() ) ;
        }
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        PrintWriter respWriter = null;
        MachineHandler machineHandler;
        String encryptionInputRaw = null;
        List<String> encryptionInputs = new ArrayList<>();
        List<String> encryptionOutputs = new ArrayList<>();
        EncryptionResponsePayload responsePayload = new EncryptionResponsePayload();
        try {
            respWriter = resp.getWriter();
        }
        catch (Exception e){
            log.error("Failed to get response Writer, Exception Message=" + e.getMessage());
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            return;
        }
        try{
            encryptionInputRaw = req.getParameter(PropertiesService.getEncryptionInputAttributeName());
            responsePayload.setInput(encryptionInputRaw);
        }
        catch(Exception e){
            log.info("Encrypt request failed - no input param in request, ExceptionMessage=" + e.getMessage());
            resp.setStatus(SC_BAD_REQUEST);
            responsePayload.setMessage("Missing encryption input");
            respWriter.print(gson.toJson(responsePayload));
            return;
        }
        UserManager userManager = ServletUtils.getUserManager(this.getServletContext());
        User user = userManager.getUserByName((String) req.getSession(false).getAttribute(PropertiesService.getUsernameAttributeName()));   //Username by session
        if(user == null){
            resp.setStatus(SC_UNAUTHORIZED);
            respWriter.print("Please login first");
            return;
        }
        machineHandler = (MachineHandler) req.getSession(false).getAttribute(PropertiesService.getMachineHandlerAttributeName());
        if(machineHandler == null){
            log.info("Encrypt request failed - missing machine schema for session");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            responsePayload.setMessage("Missing Machine schema. please upload a schema first. (/upload-machine-file)");
            respWriter.print(gson.toJson(responsePayload));
            return;
        }
        Optional<MachineState> machineState = machineHandler.getMachineState();
        if(!machineState.isPresent()){
            log.info("Encrypt request failed - missing machine configuration for session");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            responsePayload.setMessage("Please configure a machine state first. (/assemble-machine-randomly, or //assemble-machine-manually)");
            respWriter.print(gson.toJson(responsePayload));
            return;
        }
        //Input validation check
        final String abc = machineHandler.getInventoryInfo().get().getABC();
        if(abc.contains(System.lineSeparator())){
            encryptionInputs.add(encryptionInputRaw);
        }
        else{
            encryptionInputs.addAll(Arrays.asList(encryptionInputRaw.split(System.lineSeparator())));
        }
        for (String input : encryptionInputs) {
            for (int i = 0; i < input.length(); i++) {
                String currLetter = input.substring(i,i+1);
                if(!machineHandler.checkInputIsInAbc(currLetter).isPresent()){
                    log.info("Encrypt request failed - \"" + input + "\" is not in the abc=" + abc);
                    resp.setStatus(SC_BAD_REQUEST);
                    responsePayload.setMessage("\"" + input + "\" is not in the abc=" + abc);
                    respWriter.print(gson.toJson(responsePayload));
                    return;
                }
            }
        }
        DictionaryManager dictionaryManager = user.getContestRoom().getDictionaryManager();
        if(dictionaryManager.getDictionary() ==  null || dictionaryManager.getDictionary().isEmpty()){
            log.error("DictonaryManager from user=" + user.getUsername() + " is null or empty");
        }
        //check words in dictionary:
        if(!dictionaryManager.getDictionary().isEmpty()){
            for (String input : encryptionInputs ){
                if (!DictionaryManagerStatic.isInDictionary(input)) {
                    log.info("Encrypt request failed - \"" + input + "\" is not in the dictionary");
                    resp.setStatus(SC_BAD_REQUEST);
                    responsePayload.setMessage("\"" + input + "\" is not in the dictionary");
                    respWriter.print(gson.toJson(responsePayload));
                    return;
                }
            }
        }
        //Actual Encryption:
        synchronized (this){
            for (String input : encryptionInputs ){
                encryptionOutputs.add(machineHandler.encrypt(input));
            }
        }
        //Preparing response:
        responsePayload.setOutput(StringUtils.join(encryptionOutputs,System.lineSeparator()));
//        responsePayload.setOutput(String.valueOf(encryptionOutputs));
        resp.setStatus(SC_OK);
        responsePayload.setMessage("Encrypted Successfully");
        respWriter.print(gson.toJson(responsePayload));
        return;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req,resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req,resp);
    }
}
