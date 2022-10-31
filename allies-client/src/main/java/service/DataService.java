package service;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dto.*;
import enums.GameStatus;
import generictype.MappingPair;
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
    @Getter private static final ObjectProperty<Set<ContestRoomData>> contestRoomsStateProperty = new SimpleObjectProperty<>();
    @Getter private static final ObjectProperty<ContestRoomData> currentContestRoomStateProperty = new SimpleObjectProperty<>();
    @Getter private static final ObjectProperty<List<AgentData>> agentsListStateProperty = new SimpleObjectProperty<>();
    @Getter private static final ObjectProperty<List<AllyTeamData>> currentTeamsProperty = new SimpleObjectProperty<>();
    @Getter private static final ObjectProperty<MappingPair<Long,Long>> dmProgressProperty = new SimpleObjectProperty<>(new MappingPair<>(0L,1L));

    @Getter private static final ObjectProperty<List<EncryptionCandidate>> lastCandidatesProperty = new SimpleObjectProperty<>();
    @Getter private static final ObjectProperty<GameStatePayload> gameStatusProperty = new SimpleObjectProperty<>(new GameStatePayload());
    private static final ScheduledExecutorService executor;
    private static final String agentsDataUrl;
    private static final String contestsDataUrl;
    private static final String allyTeamsUrl;
    private static final String contestDataUrl;
    private static final String candidatesUrl;
    private static final String gotWinUrl;

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
                        contestRoomsStateProperty.setValue(null);
                    }
                    else {
                        log.debug("all Contests data Successfully Fetched - responseCode = 200, ServerMessage=" + payload.getContestRooms());
                        if(payload.getContestRooms() != null && !payload.getContestRooms().isEmpty() && !payload.getContestRooms().equals(contestRoomsStateProperty.get())){
//                            contestRoomsStateProperty.setValue(null);
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
                        agentsListStateProperty.setValue(null);
                        return;
                    }
                    AgentsListPayload payload;
                    try{
                        payload = new Gson().fromJson(responseBody,AgentsListPayload.class);
                    }
                    catch (Exception e){
                        log.error("Failed to parse response on agentsDataFetcher, Message=" + e.getMessage());
                        agentsListStateProperty.setValue(null);
                        return;
                    }
                    if (response.code() != 200) {
                        log.error("Failed to fetch agents data - statusCode=" + response.code() + ", ServerMessage=" + payload.getMessage());
                        agentsListStateProperty.setValue(null);

                    }
                    else {
                        log.debug("Agents data Successfully Fetched - responseCode = 200, ServerMessage=" + payload.getAgentsList());
                        if(/*payload.getAgentsList() != null &&*//* !payload.getAgentsList().isEmpty() &&*/ !payload.getAgentsList().equals(agentsListStateProperty.get())){
//                            agentsListStateProperty.setValue(null);
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
                            currentTeamsProperty.setValue(null);
                            currentTeamsProperty.setValue(allyTeamsPayload.getAllyTeamsData());
                        }
                    }
                }
            });
        }
    };
    private static final Runnable currContestDataFetcher = new Runnable() {
        @Override
        public void run() {
            HttpClientService.runAsync(contestDataUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    log.error("curr contestDataStateFetcher failed, ExceptionMessage="+e.getMessage());                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (response.code() >= 500) {
                        log.error("curr contestsDataState Fetching failed - statusCode=" + response.code());
                        return;
                    }
                    ContestRoomPayload payload;
                    try{
                        payload = new Gson().fromJson(responseBody,ContestRoomPayload.class);
                    }
                    catch (Exception e){
                        log.error("Failed to parse response on curr contestsDataFetcher, Message=" + e.getMessage());
                        return;
                    }
                    if (response.code() != 200) {
                        log.error("Failed to fetch curr contest data - statusCode=" + response.code() + ", ServerMessage=" + payload.getMessage());
                        currentContestRoomStateProperty.setValue(null);
                        gameStatusProperty.setValue(new GameStatePayload());
                    }
                    else {
                        log.debug("Contest data Successfully Fetched - responseCode = 200, ServerMessage=" + payload.getContestRoom());
//                        currentContestRoomStateProperty.setValue(null);
                        currentContestRoomStateProperty.setValue(payload.getContestRoom());
                            if(payload.getContestRoom() != null  ){
                                if( payload.getContestRoom().getGameStatus()!= gameStatusProperty.get().getGameState() ){
                                    gameStatusProperty.setValue(new GameStatePayload(null, payload.getContestRoom().getGameStatus(), payload.getContestRoom().getWinnerName()));
                                }
                            if(gameStatusProperty.get().getGameState() == GameStatus.DONE){
                                sendGotWin();
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
                    log.error("candidatesFetcher failed, ExceptionMessage="+e.getMessage());
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (response.code() >= 500) {
                        log.warn("candidatesFetcher Fetching failed - statusCode=" + response.code());
                        return;
                    }
                    DecryptionResultPayload payload;
                    try{
                        payload = new Gson().fromJson(responseBody,DecryptionResultPayload.class);
                    }
                    catch (Exception e){
                        log.error("Failed to parse response on candidatesFetcher, Message=" + e.getMessage());
                        return;
                    }
                    if (response.code() != 200) {
                        log.error("Failed to fetch candidates - statusCode=" + response.code() + ", ServerMessage=" + payload.getMessage());
                    }
                    else {
                        log.debug("Candidates Successfully Fetched - responseCode = 200, ServerMessage=" + payload.getEncryptionCandidateList());
//                        if(payload.getEncryptionCandidateList() != null
//                        && !payload.getEncryptionCandidateList().isEmpty()){
                            getLastCandidatesProperty().setValue(payload.getEncryptionCandidateList());
//                        }
                    }
                }
            });
        }
    };
    private static final Runnable dmProgressFetcher = new Runnable() {
        @Override
        public void run() {
            HttpClientService.runAsync(candidatesUrl, new Callback(){
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    AllyWorkProgressPayload payload = new AllyWorkProgressPayload();
                    try{
                        payload = gson.fromJson(responseBody, AllyWorkProgressPayload.class);
                    } catch (JsonSyntaxException e) {
                        log.error("DmProgress Fetching failed - failed to parse response body, status=" + response.code() + ", Exception=" + e.getMessage() + ", Body=" + responseBody);
                    }
                    if (response.code() != 200) {
                        log.warn("dmProgress Fetching Failed - statusCode=" + response.code() + ", Payload=" + payload);
                        return;
                    }
                    MappingPair<Long,Long> newProgress = payload.getProgress();
                    if(newProgress != null){
                        dmProgressProperty.setValue(payload.getProgress());
                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    log.error("dmProgress - onFailure - entire request Failed, ExceptionMessage="+e.getMessage());
                }
            });
        }
    };

    static{
        int poolSize = 3;
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
        gotWinUrl = HttpUrl
                .parse(PropertiesService.getApiGotWinUrl())
                .newBuilder()
                .build()
                .toString();
    }
    public static void sendGotWin() {
        HttpClientService.runAsync(gotWinUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                log.error("gotWin failed, ExceptionMessage="+e.getMessage());
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() >= 500) {
                    log.error("gotWin failed - statusCode=" + response.code());
                    return;
                }
                if (response.code() != 200) {
                    log.error("gotWin failed - statusCode=" + response.code() + ", ServerMessage=" + responseBody);
                }
                else {
                    log.info("sent got win Successfully  - responseCode = 200");
                }
            }
        });
    }
    public static void startPullingRoomsData(){
        long timeInterval = 1500;
        executor.scheduleAtFixedRate(contestsDataFetcher, 0, timeInterval, TimeUnit.MILLISECONDS);
    }
    public static void startPullingContestRoomData(){
        long timeInterval = 1500;
        executor.scheduleAtFixedRate(currContestDataFetcher, 0, timeInterval, TimeUnit.MILLISECONDS);
    }
    public static void startPullingAgentData(){
        long timeInterval = 1500;
        executor.scheduleAtFixedRate(agentsDataFetcher, 0, timeInterval, TimeUnit.MILLISECONDS);
    }
    public static void startPullingTeamsData(){
        long timeInterval = 1500;
        executor.scheduleAtFixedRate(currTeamsFetcher, 0, timeInterval, TimeUnit.MILLISECONDS);
    }
    public static void startPullingCandidates(){
        long timeInterval = 1500;
        executor.scheduleAtFixedRate(candidatesFetcher, 0, timeInterval, TimeUnit.MILLISECONDS);
    }
    public static void startPullingProgress(){
        long timeInterval = 1500;
        executor.scheduleAtFixedRate(dmProgressFetcher, 0, timeInterval, TimeUnit.MILLISECONDS);
    }

}
