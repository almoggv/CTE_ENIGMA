package main.java.service;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class PropertiesService {
    @Getter private static final String log4jPropertiesResourcePath = "/main/resources/log4j.properties";
//    @Getter private static final String appFxmlPath = "/src/main/resources/app.fxml";
    @Getter private static final String appFxmlPath = "C:\\Dev\\GitProjects\\CTE_JAVA_ENIGMA\\uboat-client\\src\\main\\resources\\app.fxml";
    @Getter private static final String contestPageTemplateFxmlPath = "/src/main/resources/subcomponents/contestPageTemplate.fxml";
    @Getter private static final String currMachineConfigFxmlPath = "/src/main/resources/subcomponents/currMachineConfigTemplate.fxml";
    @Getter private static final String encryptionComponentFxmlPath = "/src/main/resources/subcomponents/encryptionComponent.fxml";
    @Getter private static final String headerFxmlPath = "/src/main/resources/subcomponents/header.fxml";
    @Getter private static final String loginFxmlPath = "/src/main/resources/subcomponents/login.fxml";
    @Getter private static final String machinePageTemplateFxmlPath = "/src/main/resources/subcomponents/machinePageTemplate.fxml";

}

