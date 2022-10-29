package service;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class PropertiesService {
    //File Paths:
    @Getter private static final String log4jPropertiesResourcePath = "/log4j.properties";
    @Getter private static final String appFxmlPath = "/app.fxml";
    @Getter private static final String contestPageTemplateFxmlPath = "/subcomponents/contestPageTemplate.fxml";
//    @Getter private static final String currMachineConfigFxmlPath = "/subcomponents/currMachineConfigTemplate.fxml";
//    @Getter private static final String encryptionComponentFxmlPath = "/subcomponents/encryptionComponent.fxml";
    @Getter private static final String headerFxmlPath = "/subcomponents/header.fxml";
    @Getter private static final String loginFxmlPath = "/subcomponents/login.fxml";
//    @Getter private static final String dashboardPageTemplateFxmlPath = "/subcomponents/dashboardPageTemplate.fxml";
    @Getter private static final String contestDataFxmlPath = "/subcomponents/contestData.fxml";
    @Getter private static final String agentDataFxmlPath = "/subcomponents/agentData.fxml";
    @Getter private static final String candidateDataFxmlPath = "/subcomponents/candidate.fxml";
    //App Initial size
    @Getter private static final int appWindowWidth = 950;
    @Getter private static final int appWindowHeight = 650;

    //Attributes
    @Getter private static final String usernameAttribute = "username";
    @Getter private static final String tokenAttributeName = "access_token";
    @Getter private static final String messageAttributeName = "message";
    @Getter private static final String inventoryAttributeName = "inventory";
    @Getter private static final String userTypeAttributeName = "user_type";
    @Getter private static final String agentAttributeName = "agent";
    @Getter private static final String allyNameAttribute = "ally_name";
    @Getter private static final String taskSizeAttributeName = "task_size";
    @Getter private static final String threadCountAttributeName = "thread_num";

    //Api Url
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/enigma_battlefield_server";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    @Getter private static final String apiLoginPageUrl = FULL_SERVER_PATH + "/login-agent";
    @Getter private static final String apiLogoutPageUrl = FULL_SERVER_PATH + "/logout";
    @Getter private static final String apiUploadPageUrl = FULL_SERVER_PATH + "/upload-machine-file";
    @Getter private static final String apiInventoryPageUrl = FULL_SERVER_PATH + "/get-machine-details";
    @Getter private static final String apiAssembleMachineRandomlyPageUrl = FULL_SERVER_PATH + "/assemble-machine-randomly";
    @Getter private static final String apiAssembleMachineManuallyPageUrl = FULL_SERVER_PATH + "/assemble-machine-manually";
    @Getter private static final String apiCurrMachineConfigPageUrl = FULL_SERVER_PATH + "/curr-machine-config";
    @Getter private static final String apiEncryptPageUrl = FULL_SERVER_PATH + "/encrypt";
    @Getter private static final String apiInventoryInfoUrl = FULL_SERVER_PATH + "/get-machine-inventory";
    @Getter private static final String apiContestsInfoUrl = FULL_SERVER_PATH + "/get-rooms-info";
    @Getter private static final String apiContestInfoUrl = FULL_SERVER_PATH + "/get-room-info";
    @Getter private static final String apiJoinContestUrl = FULL_SERVER_PATH + "/join";
    @Getter private static final String apiAgentReadyUrl = FULL_SERVER_PATH + "/agent-ready";
    @Getter private static final String apiAllAlliesReadyUrl = FULL_SERVER_PATH + "/all-ally-teams";
    @Getter private static final String apiGameStateUrl = FULL_SERVER_PATH + "/game-status";
    @Getter private static final String apiAgentsOfAllyUrl = FULL_SERVER_PATH + "/agents-by-ally";
    @Getter private static final String apiSendCandidatesUrl = FULL_SERVER_PATH + "/post-candidates";
    @Getter private static final String apiGotWinUrl = FULL_SERVER_PATH + "/got-win";

}

