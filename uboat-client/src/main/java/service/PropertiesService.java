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
    //App Initial size
    @Getter private static final int appWindowWidth = 950;
    @Getter private static final int appWindowHeight = 650;

    //Attributes
    @Getter private static final String usernameAttribute = "username";

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

}

