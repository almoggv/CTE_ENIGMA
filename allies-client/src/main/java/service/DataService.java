package service;


import com.google.gson.Gson;
import dto.*;
import enums.GameStatus;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class DataService {
    private static final Logger log = Logger.getLogger(DataService.class);
    static {
        try {
            Properties p = new Properties();
            p.load(DataService.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + DataService.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + DataService.class.getSimpleName());
        }
    }

    @Getter private static final ObjectProperty<InventoryInfo> inventoryInfoProperty = new SimpleObjectProperty<>();
    @Getter private static final ObjectProperty<MachineState> originalMachineStateProperty = new SimpleObjectProperty<>();
    @Getter private static final ObjectProperty<MachineState> currentMachineStateProperty = new SimpleObjectProperty<>();
    @Getter private static final ObjectProperty<Set<ContestRoom>> contestRoomsStateProperty = new SimpleObjectProperty<>();
    @Getter private static final ObjectProperty<ContestRoom> currentContestRoomsStateProperty = new SimpleObjectProperty<>();
    @Getter private static final ObjectProperty<List<AgentData>> agentsListStateProperty = new SimpleObjectProperty<>();
    @Getter private static final ObjectProperty<List<AllyTeamData>> currentTeamsProperty = new SimpleObjectProperty<>();

    @Getter private static final BooleanProperty isContestStartedProperty = new SimpleBooleanProperty(false);
    @Getter private static final ObjectProperty<List<EncryptionCandidate>> lastCandidatesProperty = new SimpleObjectProperty<>();
    @Getter private static final ObjectProperty<GameStatus> gameStatusProperty = new SimpleObjectProperty<>();

    private static final ScheduledExecutorService executor;
    private static final String agentsDataUrl;
    private static final String contestsDataUrl;
    private static final String allyTeamsUrl;
    private static final String contestDataUrl;
    private static final String candidatesUrl;
    private static final String contestStatusUrl;

    private static final Runnable contestsDataFetcher = new Runnable() {
        @Override
        public void run() {
            HttpClientService.runAsync(contestsDataUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    log.error("contestsDataStateFetcher failed, ExceptionMessage="+e.getMessage());                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (response.code() >= 500) {
                        log.error("contestsDataState Fetching failed - statusCode=" + response.code());
                        return;
                    }
                    AllContestRoomsPayload payload;
                    try{
                        payload = new Gson().fromJson(responseBody,AllContestRoomsPayload.class);
                    }
                    catch (Exception e){
                        log.error("Failed to parse response on contestsDataFetcher, Message=" + e.getMessage());
                        return;
                    }
                    if (response.code() != 200) {
                        log.error("Failed to fetch contests data - statusCode=" + response.code() + ", ServerMessage=" + payload.getMessage());
                    }
                    else {
                        log.info("Contest data Successfully Fetched - responseCode = 200, ServerMessage=" + payload.getContestRooms());
                        if(payload.getContestRooms() != null && !payload.getContestRooms().isEmpty() && !payload.getContestRooms().equals(contestRoomsStateProperty.get())){
                            contestRoomsStateProperty.setValue(null);
                            contestRoomsStateProperty.setValue(payload.getContestRooms());
                        }
                    }
                }
            });
        }
    };
    private static final Runnable agentsDataFetcher = new Runnable() {
        @Override
        public void run() {
            HttpClientService.runAsync(agentsDataUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    log.error("agentsDataFetcher failed, ExceptionMessage="+e.getMessage());                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (response.code() >= 500) {
                        log.error("agents Data Fetching failed - statusCode=" + response.code());
                        return;
                    }
                    AgentsListPayload payload;
                    try{
                        payload = new Gson().fromJson(responseBody,AgentsListPayload.class);
                    }
                    catch (Exception e){
                        log.error("Failed to parse response on agentsDataFetcher, Message=" + e.getMessage());
                        return;
                    }
                    if (response.code() != 200) {
                        log.error("Failed to fetch agents data - statusCode=" + response.code() + ", ServerMessage=" + payload.getMessage());
                    }
                    else {
                        log.info("Agents data Successfully Fetched - responseCode = 200, ServerMessage=" + payload.getAgentsList());
                        if(payload.getAgentsList() != null && !payload.getAgentsList().isEmpty() && !payload.getAgentsList().equals(agentsListStateProperty.get())){
                            agentsListStateProperty.setValue(null);
                            agentsListStateProperty.setValue(payload.getAgentsList());
                        }
                    }
                }
            });
        }
    };
    private static final Runnable currTeamsFetcher = new Runnable() {
        @Override
        public void run() {
            HttpClientService.runAsync(allyTeamsUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    log.error("currTeamsFetcher failed, ExceptionMessage="+e.getMessage());                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (response.code() >= 500) {
                        log.error("currTeamsFetcher Fetching failed - statusCode=" + response.code());
                        return;
                    }
                    ContestAllyTeamsPayload allyTeamsPayload;
                    try{
                        allyTeamsPayload = new Gson().fromJson(responseBody,ContestAllyTeamsPayload.class);
                    }
                    catch (Exception e){
                        log.error("Failed to parse response on currTeamsFetcher, Message=" + e.getMessage());
                        return;
                    }
                    if (response.code() != 200) {
                        log.error("Failed to fetch curr Teams - statusCode=" + response.code() + ", ServerMessage=" + allyTeamsPayload.getMessage());
                    }
                    else {
                        log.info("Current teams Fetched - responseCode = 200, ServerMessage=" + allyTeamsPayload.getMessage());
                        if(allyTeamsPayload.getAllyTeamsData() != null &&
                                allyTeamsPayload.getAllyTeamsData() != currentTeamsProperty.get()){
                            currentTeamsProperty.setValue(null);
                            currentTeamsProperty.setValue(allyTeamsPayload.getAllyTeamsData());
                        }
                    }
                }
            });
        }
    };
    private static final Runnable contestDataFetcher = new Runnable() {
        @Override
        public void run() {
            HttpClientService.runAsync(contestDataUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    log.error("contestsDataStateFetcher failed, ExceptionMessage="+e.getMessage());                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (response.code() >= 500) {
                        log.error("contestsDataState Fetching failed - statusCode=" + response.code());
                        return;
                    }
                    ContestRoomPayload payload;
                    try{
                        payload = new Gson().fromJson(responseBody,ContestRoomPayload.class);
                    }
                    catch (Exception e){
                        log.error("Failed to parse response on contestsDataFetcher, Message=" + e.getMessage());
                        return;
                    }
                    if (response.code() != 200) {
                        log.error("Failed to fetch contests data - statusCode=" + response.code() + ", ServerMessage=" + payload.getMessage());
                    }
                    else {
                        log.info("Contest data Successfully Fetched - responseCode = 200, ServerMessage=" + payload.getContestRoom());
                        if(payload.getContestRoom() != null  ){
                            currentContestRoomsStateProperty.setValue(null);
                            currentContestRoomsStateProperty.setValue(payload.getContestRoom());
                            if(payload.getContestRoom().getGameStatus()!= GameStatus.WAITING){
                                if(getIsContestStartedProperty().get() == false) {
                                    getIsContestStartedProperty().setValue(true);
                                }
                            }
                        }
                    }
                }
            });
        }

    };
    private static final Runnable candidatesFetcher = new Runnable() {
        @Override
        public void run() {
            HttpClientService.runAsync(candidatesUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    log.info("candidatesFetcher failed, ExceptionMessage="+e.getMessage());                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (response.code() >= 500) {
                        log.info("candidatesFetcher Fetching failed - statusCode=" + response.code());
                        return;
                    }
                    DecryptionResultPayload payload;
                    try{
                        payload = new Gson().fromJson(responseBody,DecryptionResultPayload.class);
                    }
                    catch (Exception e){
                        log.info("Failed to parse response on candidatesFetcher, Message=" + e.getMessage());
                        return;
                    }
                    if (response.code() != 200) {
                        log.info("Failed to fetch candidates - statusCode=" + response.code() + ", ServerMessage=" + payload.getMessage());
                    }
                    else {
                        log.info("Candidates Successfully Fetched - responseCode = 200, ServerMessage=" + payload.getEncryptionCandidateList());
                        if(payload.getEncryptionCandidateList() != null
                        && !payload.getEncryptionCandidateList().isEmpty()){
                            getLastCandidatesProperty().setValue(payload.getEncryptionCandidateList());
                        }
                    }
                }
            });
        }
    };

    private static final Runnable contestStartedFetcher = new Runnable() {
        @Override
        public void run() {
            HttpClientService.runAsync(contestStatusUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    log.error("contestStarted failed, ExceptionMessage="+e.getMessage());                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (response.code() >= 500) {
                        log.error("contestStarted Fetching failed - statusCode=" + response.code());
                        return;
                    }
                    GameStatePayload payload;
                    try{
                        payload = new Gson().fromJson(responseBody,GameStatePayload.class);
                    }
                    catch (Exception e){
                        log.error("Failed to parse response on contestStartedUrl, Message=" + e.getMessage());
                        return;
                    }
                    if (response.code() != 200) {
                        log.error("Failed to fetch game status - statusCode=" + response.code() + ", ServerMessage=" + payload.getMessage());
                    }
                    else {
                        log.info("game status - responseCode = 200, ServerMessage=" + payload.getMessage());
                        if(payload.getGameState() != null
                                && payload.getGameState().equals(GameStatus.READY)) {
                            isContestStartedProperty.setValue(true);
                        }
                        if(payload.getGameState() != null && payload.getGameState() != gameStatusProperty.get()){
                            gameStatusProperty.setValue(payload.getGameState());
                            //in comment for test
//                            if(payload.getGameState().equals(GameStatus.READY)){
                                startPullingCandidates();
//                            }
                        }
                    }
                }
            });
        }
    };
    static{
        int poolSize = 2;
        executor = Executors.newScheduledThreadPool(poolSize);
        contestsDataUrl = HttpUrl
                .parse(PropertiesService.getApiContestsInfoUrl())
                .newBuilder()
                .build()
                .toString();
        agentsDataUrl = HttpUrl
                .parse(PropertiesService.getApiAgentsOfAllyUrl())
                .newBuilder()
                .build()
                .toString();
        allyTeamsUrl = HttpUrl
                .parse(PropertiesService.getApiAllyTeamsUrl())
                .newBuilder()
                .build()
                .toString();
        contestDataUrl = HttpUrl
                .parse(PropertiesService.getApiContestInfoUrl())
                .newBuilder()
                .build()
                .toString();
        candidatesUrl = HttpUrl
                .parse(PropertiesService.getApiAllyCandidatesUrl())
                .newBuilder()
                .build()
                .toString();
        contestStatusUrl = HttpUrl
                .parse(PropertiesService.getApiGameStateUrl())
                .newBuilder()
                .build()
                .toString();
    }

    public static void startPullingRoomsData(){
        long timeInterval = 1500;
        executor.scheduleAtFixedRate(contestsDataFetcher, 0, timeInterval, TimeUnit.MILLISECONDS);
        //TODO: implement
    }
    public static void startPullingContestRoomData(){
        long timeInterval = 1500;
        executor.scheduleAtFixedRate(contestDataFetcher, 0, timeInterval, TimeUnit.MILLISECONDS);
        //TODO: implement
    }
    public static void startPullingAgentData(){
        long timeInterval = 1500;
        executor.scheduleAtFixedRate(agentsDataFetcher, 0, timeInterval, TimeUnit.MILLISECONDS);
        //TODO: implement
    }
    public static void startPullingTeamsData(){
        long timeInterval = 1500;
        executor.scheduleAtFixedRate(currTeamsFetcher, 0, timeInterval, TimeUnit.MILLISECONDS);
        //TODO: implement
    }
    public static void startPullingCandidates(){
        long timeInterval = 1500;
        executor.scheduleAtFixedRate(candidatesFetcher, 0, timeInterval, TimeUnit.MILLISECONDS);
        //TODO: implement
    }
    public static void startCheckIsContestStarted(){
        long timeInterval = 1500;
        executor.scheduleAtFixedRate(contestStartedFetcher, 0, timeInterval, TimeUnit.MILLISECONDS);
        //TODO: implement
    }
    public static void fetchInventoryInfo(InventoryInfo inventory) {
        inventoryInfoProperty.setValue(inventory);
    }

    //curr contest


}
