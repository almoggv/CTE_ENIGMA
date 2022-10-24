package service;


import com.google.gson.Gson;
import controller.LoginController;
import dto.*;
import javafx.application.Platform;
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

    private static final ScheduledExecutorService executor;
    private static final String fetchInventoryUrl;
    private static final String machineConfigUrl;

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
                        currentTeamsProperty.setValue(null);
                        currentTeamsProperty.setValue(allyTeamsPayload.getAllyTeamsData());
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

    public static void fetchInventoryInfo(InventoryInfo inventory) {
        inventoryInfoProperty.setValue(inventory);
    }
}
