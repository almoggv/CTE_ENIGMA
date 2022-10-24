package service;


import com.google.gson.Gson;
import dto.*;
import enums.GameStatus;
import javafx.beans.property.ObjectProperty;
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

/*
    @Getter private static final ObjectProperty<MachineState> currentMachineStateProperty = new SimpleObjectProperty<>();
*/
/*
    @Getter private static final ObjectProperty<Set<ContestRoom>> contestRoomsStateProperty = new SimpleObjectProperty<>();
*/
    @Getter private static final ObjectProperty<ContestRoom> currentContestRoomsStateProperty = new SimpleObjectProperty<>();
    @Getter private static final ObjectProperty<List<AllyTeamData>> currentTeamsProperty = new SimpleObjectProperty<>();
    @Getter private static final ObjectProperty<GameStatus> gameStatusProperty = new SimpleObjectProperty<>();



    private static final ScheduledExecutorService executor;
//    private static final String fetchInventoryUrl;
    private static final String machineConfigUrl;
    private static final String contestsDataUrl;
    private static final String allyTeamsUrl;
    private static final String gameStateUrl;
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
    private static final Runnable gameStateFetcher = new Runnable() {
        @Override
        public void run() {
            HttpClientService.runAsync(gameStateUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    log.error("GameStateFetcher failed, ExceptionMessage="+e.getMessage());                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (response.code() >= 500) {
                        log.error("GameStateFetcher Fetching failed - statusCode=" + response.code());
                        return;
                    }
                    GameStatePayload gameStatePayload;
                    try{
                        gameStatePayload = new Gson().fromJson(responseBody,GameStatePayload.class);
                    }
                    catch (Exception e){
                        log.error("Failed to parse response on GameStateFetcher, Message=" + e.getMessage());
                        return;
                    }
                    if (response.code() != 200) {
                        log.error("Failed to fetch game State - statusCode=" + response.code() + ", ServerMessage=" + gameStatePayload.getMessage());
                    }
                    else {
                        log.info("Game state Fetched - responseCode = 200, ServerMessage=" + gameStatePayload.getMessage());
                        gameStatusProperty.setValue(gameStatePayload.getGameState());
                    }
                }
            });
        }
    };
    /* private static final Runnable contestsDataFetcher = new Runnable() {
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
                        log.error("Current machine state Fetching failed - statusCode=" + response.code());
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
                        if(payload.getContestRooms() != null && payload.getContestRooms() != contestRoomsStateProperty.get()){
                            contestRoomsStateProperty.setValue(null);
                            contestRoomsStateProperty.setValue(payload.getContestRooms());
                        }
                    }
                }
            });
        }
    };
*/
    static{
        int poolSize = 2;
        executor = Executors.newScheduledThreadPool(poolSize);
        contestsDataUrl = HttpUrl
                .parse(PropertiesService.getApiContestsInfoUrl())
                .newBuilder()
                .build()
                .toString();
        machineConfigUrl = HttpUrl
                .parse(PropertiesService.getApiCurrMachineConfigPageUrl())
                .newBuilder()
                .build()
                .toString();
        allyTeamsUrl = HttpUrl
                .parse(PropertiesService.getApiAllAlliesReadyUrl())
                .newBuilder()
                .build()
                .toString();
        gameStateUrl = HttpUrl
                .parse(PropertiesService.getApiGameStateUrl())
                .newBuilder()
                .build()
                .toString();
    }
    public static void startPullingTeamsData(){
        long timeInterval = 500;
        executor.scheduleAtFixedRate(currTeamsFetcher, 0, timeInterval, TimeUnit.MILLISECONDS);
    }

    public static void startFetchingGameState(){
        long timeInterval = 250;
        executor.scheduleAtFixedRate(gameStateFetcher,0,timeInterval, TimeUnit.MILLISECONDS);
    }
//    public static void fetchInventoryInfo(){
//        HttpClientService.runAsync(fetchInventoryUrl, new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                log.error("FetchInventoryInfo failed, ExceptionMessage="+e.getMessage());
//            }
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                String responseBody = response.body().string();
//                if (response.code() >= 500) {
//                    log.error("Inventory Fetching failed - statusCode=" + response.code());
//                    return;
//                }
//                MachineInventoryPayload inventoryPayload;
//                try{
//                    inventoryPayload = new Gson().fromJson(responseBody,MachineInventoryPayload.class);
//                }
//                catch (Exception e){
//                    log.error("Failed to parse response on FetchInventoryInfo, Message=" + e.getMessage());
//                    return;
//                }
//                if (response.code() != 200) {
//                    log.error("Failed to FetchInventoryInfo - statusCode=" + response.code() + ", ServerMessage=" + inventoryPayload.getMessage());
//                }
//                else {
//                    log.info("Inventory Successfully Fetched - responseCode = 200, ServerMessage=" + inventoryPayload.getMessage());
//                    inventoryInfoProperty.setValue(inventoryPayload.getInventory());
//                }
//            }
//        });
//    }

/*    public static void startPullingRoomData(){
        long timeInterval = 1500;
        executor.scheduleAtFixedRate(contestsDataFetcher, 0, timeInterval, TimeUnit.MILLISECONDS);
        //TODO: implement
    }

    public static void fetchInventoryInfo(InventoryInfo inventory) {
        inventoryInfoProperty.setValue(inventory);
    }*/
}
