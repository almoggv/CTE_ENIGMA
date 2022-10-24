package servlet;

import com.google.gson.Gson;
import dto.BattlefieldInfo;
import dto.ContestRoom;
import dto.MachineInventoryPayload;
import dto.User;
import enums.DecryptionDifficultyLevel;
import enums.GameStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import component.MachineHandler;
import component.impl.MachineHandlerImpl;
import manager.RoomManager;
import manager.UserManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Scanner;

import static jakarta.servlet.http.HttpServletResponse.*;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@WebServlet("/upload-machine-file")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)

public class UploadMachineFile extends HttpServlet {
    private static final Logger log = Logger.getLogger(UploadMachineFile.class);
    static {
        try {
            Properties p = new Properties();
            p.load(UploadMachineFile.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + UploadMachineFile.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + UploadMachineFile.class.getSimpleName() ) ;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //TODO: use this template to authenticate users
        UserManager userManager = ServletUtils.getUserManager(this.getServletContext());
        if(!userManager.isUserRegistered(req.getHeader(PropertiesService.getTokenAttributeName()))){
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        PrintWriter outWriter;
        try {
            outWriter = resp.getWriter();
        }
        catch (Exception e){
            log.error("Failed to get response Writer, Exception Message=" + e.getMessage());
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            return;
        }
        User user = userManager.getUserByName(req.getHeader(PropertiesService.getTokenAttributeName()));
        if(user == null){
            resp.setStatus(SC_BAD_REQUEST);
            outWriter.print("user does not exist");
            return;
        }
        RoomManager roomManager = ServletUtils.getRoomManager(this.getServletContext());
        Gson gson = new Gson();
        MachineInventoryPayload inventoryPayload = new MachineInventoryPayload();
        resp.setContentType("text/plain");

        Collection<Part> parts = req.getParts();
        Part uploadedFile = req.getPart("file");
        new Scanner(uploadedFile.getInputStream()).useDelimiter("\\Z").next();
        MachineHandler machineHandler = new MachineHandlerImpl();
        try{
            BattlefieldInfo battlefieldInfo = machineHandler.buildBattlefieldInfoInventory(uploadedFile.getInputStream());
            synchronized (this) {
                if (roomManager.isRoomExists(battlefieldInfo.getBattlefieldName())){
                    resp.setStatus(SC_BAD_REQUEST);
                    inventoryPayload.setMessage("Can't upload file. There is already a battlefield with the defined name.");
                    return;
                }
                else{
                    //create inventory from file
                    machineHandler.buildMachinePartsInventory(uploadedFile.getInputStream());
                    //create and save to room
                    String creatorName = (String) req.getSession(false).getAttribute(PropertiesService.getUsernameAttributeName());
                    ContestRoom contestRoom = createContestRoomInfo(creatorName, battlefieldInfo);
                    roomManager.addRoom(battlefieldInfo.getBattlefieldName(), contestRoom);
                    req.getSession(true).setAttribute(PropertiesService.getRoomNameAttributeName(), battlefieldInfo.getBattlefieldName());
                    user.setInARoom(true);
                    user.setContestRoom(contestRoom);
                    req.getSession(true).setAttribute(PropertiesService.getMachineHandlerAttributeName(), machineHandler);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    inventoryPayload.setInventory(machineHandler.getInventoryInfo().get());
                    inventoryPayload.setMessage("Machine built successfully");
                }
            }
        }
        catch (Exception e){
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            inventoryPayload.setMessage("Failed to build machine, " + e.getMessage());
        }
        finally {
            outWriter.print(gson.toJson(inventoryPayload));
        }
    }

    private ContestRoom createContestRoomInfo(String creatorName, BattlefieldInfo battlefieldInfo) {
        ContestRoom contestRoom = new ContestRoom();
        contestRoom.setCreatorName(creatorName);
        contestRoom.setCurrNumOfTeams(0);
        contestRoom.setGameStatus(GameStatus.WAITING);
        contestRoom.setName(battlefieldInfo.getBattlefieldName());
        String levelName = battlefieldInfo.getDifficultyLevel();

        contestRoom.setDifficultyLevel(DecryptionDifficultyLevel.getByName(battlefieldInfo.getDifficultyLevel()));
//        contestRoom.setDifficultyLevel(battlefieldInfo.getDifficultyLevel());
        contestRoom.setRequiredNumOfTeams(battlefieldInfo.getRequiredNumOfTeams());
        contestRoom.setAlliesList(new ArrayList<>());
        contestRoom.setWordToDecrypt(null);
        return contestRoom;
    }
}
