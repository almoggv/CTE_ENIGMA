package service;


import com.google.gson.Gson;
import dto.*;
import enums.GameStatus;
import javafx.beans.property.*;
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

    @Getter private static final ObjectProperty<InventoryInfo> inventoryInfoProperty = new SimpleObjectProperty<>(null);
    @Getter private static final ObjectProperty<MachineState> originalMachineStateProperty = new SimpleObjectProperty<>();
    @Getter private static final ObjectProperty<MachineState> currentMachineStateProperty = new SimpleObjectProperty<>();
    @Getter private static final ObjectProperty<List<AllyTeamData>> currentTeamsProperty = new SimpleObjectProperty<>();
    //todo: is needed if game status
    @Getter private static final BooleanProperty isContestStartedProperty = new SimpleBooleanProperty(false);

    @Getter private static final ObjectProperty<GameStatus> gameStatusProperty = new SimpleObjectProperty<>();
    @Getter private static final ObjectProperty<List<EncryptionCandidate>> lastCandidatesProperty = new SimpleObjectProperty<>();

    private static final ScheduledExecutorService executor;
    private static final String fetchInventoryUrl;
    private static final String machineConfigUrl;
    private static final String contestStatusUrl;
    private static final String candidatesUrl;
    private static final String allyTeamsUrl;
    private static final Runnable currMachineStateFetcher = new Runnable() {
        @Override
        public void run() {
            HttpClientService.runAsync(machineConfigUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    log.error("currMachineStateFetcher failed, ExceptionMessage="+e.getMessage());                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (response.code() >= 500) {
                        log.error("Current machine state Fetching failed - statusCode=" + response.code());
                        return;
                    }
                    MachineStatePayload machineStatePayload;
                    try{
                        machineStatePayload = new Gson().fromJson(responseBody,MachineStatePayload.class);
                    }
                    catch (Exception e){
                        log.error("Failed to parse response on currMachineStateFetcher, Message=" + e.getMessage());
                        return;
                    }
                    if (response.code() != 200) {
                        log.error("Failed to fetch current machine configuration - statusCode=" + response.code() + ", ServerMessage=" + machineStatePayload.getMessage());
                    }
                    else {
                        log.debug("Current machine state Successfully Fetched - responseCode = 200, ServerMessage=" + machineStatePayload.getMessage());
                        if(originalMachineStateProperty.get() == null){
                            originalMachineStateProperty.setValue(machineStatePayload.getMachineState());
                        }
                        currentMachineStateProperty.setValue(machineStatePayload.getMachineState());
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
                        log.debug("Current teams Fetched - responseCode = 200, ServerMessage=" + allyTeamsPayload.getMessage());
                       if(allyTeamsPayload.getAllyTeamsData() != null &&
                               allyTeamsPayload.getAllyTeamsData() != currentTeamsProperty.get()){

                           if(currentTeamsProperty.get() == null ){
                               startCheckIsContestStarted();
                           }
                           currentTeamsProperty.setValue(null);
                           currentTeamsProperty.setValue(allyTeamsPayload.getAllyTeamsData());
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
                        stopCheckIsContestStarted();
                    }
                    if(payload.getGameState() != null && payload.getGameState() != gameStatusProperty.get()){
                        gameStatusProperty.setValue(payload.getGameState());
                        if(payload.getGameState().equals(GameStatus.READY)){
//                            startPullingCandidates();
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


    static{
        int poolSize = 2;
        executor = Executors.newScheduledThreadPool(poolSize);
        fetchInventoryUrl = HttpUrl
                .parse(PropertiesService.getApiInventoryInfoUrl())
                .newBuilder()
                .build()
                .toString();
        machineConfigUrl = HttpUrl
                .parse(PropertiesService.getApiCurrMachineConfigPageUrl())
                .newBuilder()
                .build()
                .toString();
        allyTeamsUrl = HttpUrl
                .parse(PropertiesService.getApiAllyTeamsUrl())
                .newBuilder()
                .build()
                .toString();
        contestStatusUrl = HttpUrl
                .parse(PropertiesService.getApiGameStateUrl())
                .newBuilder()
                .build()
                .toString();
        candidatesUrl = HttpUrl
                .parse(PropertiesService.getApiUboatCandidatesUrl())
                .newBuilder()
                .build()
                .toString();
    }

    public static void fetchInventoryInfo(){
        HttpClientService.runAsync(fetchInventoryUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                log.error("FetchInventoryInfo failed, ExceptionMessage="+e.getMessage());
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() >= 500) {
                    log.error("Inventory Fetching failed - statusCode=" + response.code());
                    return;
                }
                MachineInventoryPayload inventoryPayload;
                try{
                    inventoryPayload = new Gson().fromJson(responseBody,MachineInventoryPayload.class);
                }
                catch (Exception e){
                    log.error("Failed to parse response on FetchInventoryInfo, Message=" + e.getMessage());
                    return;
                }
                if (response.code() != 200) {
                    log.error("Failed to FetchInventoryInfo - statusCode=" + response.code() + ", ServerMessage=" + inventoryPayload.getMessage());
                }
                else {
                    log.info("Inventory Successfully Fetched - responseCode = 200, ServerMessage=" + inventoryPayload.getMessage());
                    inventoryInfoProperty.setValue(inventoryPayload.getInventory());
                }
            }
        });
    }

    public static void startPullingMachineConfig(){
        long timeInterval = 500;
        executor.scheduleAtFixedRate(currMachineStateFetcher, 0, timeInterval, TimeUnit.MILLISECONDS);
    }
    public static void startPullingTeamsData(){
        long timeInterval = 500;
        executor.scheduleAtFixedRate(currTeamsFetcher, 0, timeInterval, TimeUnit.MILLISECONDS);
    }

    public static void stopPullingMachineConfig(){
        executor.shutdown();
    }

    public static void startCheckIsContestStarted(){
        long timeInterval = 1500;
        executor.scheduleAtFixedRate(contestStartedFetcher, 0, timeInterval, TimeUnit.MILLISECONDS);
        //TODO: implement
    }

    public static void stopCheckIsContestStarted(){
        //todo: will this shut everything down?
        executor.shutdown();
    }
    public static void startPullingCandidates(){
        long timeInterval = 1500;
        executor.scheduleAtFixedRate(candidatesFetcher, 0, timeInterval, TimeUnit.MILLISECONDS);
        //TODO: implement
    }

    public static void fetchInventoryInfo(InventoryInfo inventory) {
        inventoryInfoProperty.setValue(inventory);
    }
}
