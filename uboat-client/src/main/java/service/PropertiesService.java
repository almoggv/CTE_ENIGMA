package service;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class PropertiesService {
    //File Paths:
    @Getter private static final String log4jPropertiesResourcePath = "/log4j.properties";
    @Getter private static final String appFxmlPath = "/app.fxml";
//    @Getter private static final String appFxmlPath = "C:\\Dev\\GitProjects\\CTE_JAVA_ENIGMA\\uboat-client\\src\\main\\resources\\app.fxml";
    @Getter private static final String contestPageTemplateFxmlPath = "/subcomponents/contestPageTemplate.fxml";
    @Getter private static final String currMachineConfigFxmlPath = "/subcomponents/currMachineConfigTemplate.fxml";
    @Getter private static final String encryptionComponentFxmlPath = "/subcomponents/encryptionComponent.fxml";
    @Getter private static final String headerFxmlPath = "/subcomponents/header.fxml";
    @Getter private static final String loginFxmlPath = "/subcomponents/login.fxml";
    @Getter private static final String machinePageTemplateFxmlPath = "/subcomponents/machinePageTemplate.fxml";
    @Getter private static final String allyTeamComponentFxmlPath = "/subcomponents/allyTeamData.fxml";
    @Getter private static final String candidateDataFxmlPath = "/subcomponents/candidate.fxml";


    //App Initial size
    @Getter private static final int appWindowWidth = 950;
    @Getter private static final int appWindowHeight = 650;

    //Attributes
    @Getter private static final String usernameAttribute = "username";
    @Getter private static final String tokenAttributeName = "access_token";
    @Getter private static final String messageAttributeName = "message";
    @Getter private static final String inventoryAttributeName = "inventory";
    @Getter private static final String inputAttributeName = "input";
    @Getter private static final String uboatAttributeName = "uboat";
    @Getter private static final String userTypeAttributeName = "user_type";
    @Getter private static final String originalWordAttributeName = "original_word";
    @Getter private static final String encryptedWordAttributeName = "encrypted_word";
    //Api Url
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/enigma_battlefield_server";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    @Getter private static final String apiLoginPageUrl = FULL_SERVER_PATH + "/login";
    @Getter private static final String apiLogoutPageUrl = FULL_SERVER_PATH + "/logout";
    @Getter private static final String apiUploadPageUrl = FULL_SERVER_PATH + "/upload-machine-file";
    @Getter private static final String apiInventoryPageUrl = FULL_SERVER_PATH + "/get-machine-details";
    @Getter private static final String apiAssembleMachineRandomlyPageUrl = FULL_SERVER_PATH + "/assemble-machine-randomly";
    @Getter private static final String apiAssembleMachineManuallyPageUrl = FULL_SERVER_PATH + "/assemble-machine-manually";
    @Getter private static final String apiCurrMachineConfigPageUrl = FULL_SERVER_PATH + "/curr-machine-config";
    @Getter private static final String apiEncryptPageUrl = FULL_SERVER_PATH + "/encrypt";
    @Getter private static final String apiInventoryInfoUrl = FULL_SERVER_PATH + "/get-machine-inventory";
    @Getter private static final String apiUboatReadyInfoUrl = FULL_SERVER_PATH + "/uboat-ready";
    @Getter private static final String apiResetMachineStateUrl = FULL_SERVER_PATH + "/reset-machine-state";
    @Getter private static final String apiAllyTeamsUrl = FULL_SERVER_PATH + "/ally-teams";
    @Getter private static final String apiGameStateUrl = FULL_SERVER_PATH + "/game-status";
    @Getter private static final String apiUboatCandidatesUrl = FULL_SERVER_PATH + "/uboat-get-candidates";
    @Getter private static final String apiGotWinUrl = FULL_SERVER_PATH + "/got-win";



}

