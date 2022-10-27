package service;


import com.google.gson.Gson;
import dto.*;
import enums.GameStatus;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import lombok.Getter;
import okhttp3.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
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
    @Getter private static final ObjectProperty<ContestRoomData> currentContestRoomsStateProperty = new SimpleObjectProperty<>();
    @Getter private static final ObjectProperty<List<AgentData>> agentsListStateProperty = new SimpleObjectProperty<>();
    @Getter private static final ObjectProperty<List<AllyTeamData>> currentTeamsProperty = new SimpleObjectProperty<>();

    @Getter private static final BooleanProperty isContestStartedProperty = new SimpleBooleanProperty(false);
    @Getter private static final ObjectProperty<GameStatus> gameStatusProperty = new SimpleObjectProperty<>();
    @Getter private static final ObjectProperty<List<EncryptionCandidate>> lastCandidatesProperty = new SimpleObjectProperty<>();
    private static final ScheduledExecutorService executor;
    private static final String agentsDataUrl;
    private static final String contestDataUrl;
    private static final String allyTeamsUrl;
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
                        log.debug("Contest data Successfully Fetched - responseCode = 200, ServerMessage=" + payload.getContestRoom());
                        if(payload.getContestRoom() != null  ){
                            currentContestRoomsStateProperty.setValue(null);
                            currentContestRoomsStateProperty.setValue(payload.getContestRoom());
//                            if(payload.getContestRoom().getGameStatus()!= GameStatus.WAITING
                                    if( payload.getContestRoom().getGameStatus()!= gameStatusProperty.get() ){
                                gameStatusProperty.setValue(payload.getContestRoom().getGameStatus());
//                                if(getIsContestStartedProperty().get() == false) {
//                                    getIsContestStartedProperty().setValue(true);
//                                }
                            }
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
                        log.debug("Agents data Successfully Fetched - responseCode = 200, ServerMessage=" + payload.getAgentsList());
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
                        log.debug("Current teams Fetched - responseCode = 200, ServerMessage=" + allyTeamsPayload.getMessage());
                        if(allyTeamsPayload.getAllyTeamsData() != null &&
                                allyTeamsPayload.getAllyTeamsData() != currentTeamsProperty.get()){
                            if(currentTeamsProperty.get() == null){
//                                startCheckIsContestStarted();
                            }
                            currentTeamsProperty.setValue(null);
                            currentTeamsProperty.setValue(allyTeamsPayload.getAllyTeamsData());
                        }
                    }
                }
            });
        }
    };
    public static void sendCandidates() {
//        @Override
//        public void run() {
            //todo: return : like this for testing
//            if (getLastCandidatesProperty().get() == null) {
//                return;
//            }
            //for test:
            EncryptionCandidate encryptionCandidate = new EncryptionCandidate();
            encryptionCandidate.setCandidate("atom");
            encryptionCandidate.setAllyTeamName("ally team");
            List<EncryptionCandidate> candidateList = new ArrayList<>();
            candidateList.add(encryptionCandidate);
            candidateList.add(new EncryptionCandidate("something", "ally", new MachineState()));
            lastCandidatesProperty.set(candidateList);

            String sendCandidatesUrl = HttpUrl
                    .parse(PropertiesService.getApiSendCandidatesUrl())
                    .newBuilder()
                    .build()
                    .toString();

            DecryptionResultPayload sendPayload = new DecryptionResultPayload();
            sendPayload.setEncryptionCandidateList(getLastCandidatesProperty().get());

            String payloadString = new Gson().toJson(sendPayload);

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), payloadString);

            Request request = new Request.Builder()
                    .url(sendCandidatesUrl)
                    .post(body)
                    .build();

            log.info("New request is sent for: " + sendCandidatesUrl);
            HttpClientService.runAsync(request, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    log.error("sendCandidatesUrl failed, ExceptionMessage="+e.getMessage());                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (response.code() >= 500) {
                        log.error("sendCandidatesUrl Fetching failed - statusCode=" + response.code());
                        return;
                    }
                    if (response.code() != 200) {
                        log.error("Failed to send candidates - statusCode=" + response.code() + ", ServerMessage=" + responseBody);
                    }
                    else {
                        log.info("Candidates sent - responseCode = 200, ServerMessage=" +responseBody);
                        System.out.println("sentttttttttttttttttttttttttttttttttttttt");
                        System.out.println(responseBody);
                    }
                }
            });
        }

    static{
        int poolSize = 2;
        executor = Executors.newScheduledThreadPool(poolSize);
        allyTeamsUrl = HttpUrl
                .parse(PropertiesService.getApiAllAlliesReadyUrl())
                .newBuilder()
                .build()
                .toString();
        contestDataUrl = HttpUrl
                .parse(PropertiesService.getApiContestInfoUrl())
                .newBuilder()
                .build()
                .toString();
        agentsDataUrl = HttpUrl
                .parse(PropertiesService.getApiAgentsOfAllyUrl())
                .newBuilder()
                .build()
                .toString();

        lastCandidatesProperty.addListener((observable, oldValue, newValue) -> {
            sendCandidates();
        });
    }
    public static void startPullingTeamsData(){
        long timeInterval = 1500;
        executor.scheduleAtFixedRate(currTeamsFetcher, 0, timeInterval, TimeUnit.MILLISECONDS);
        //TODO: implement
    }
    public static void startPullingRoomData(){
        long timeInterval = 1500;
        executor.scheduleAtFixedRate(contestDataFetcher, 0, timeInterval, TimeUnit.MILLISECONDS);
        //TODO: implement
    }
    public static void startPullingAgentData(){
        long timeInterval = 1500;
        executor.scheduleAtFixedRate(agentsDataFetcher, 0, timeInterval, TimeUnit.MILLISECONDS);
        //TODO: implement
    }
}
