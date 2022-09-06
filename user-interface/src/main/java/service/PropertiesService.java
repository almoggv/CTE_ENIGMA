package src.main.java.service;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class PropertiesService {
    @Getter private static final String log4jPropertiesResourcePath = "/main/resources/log4j.properties";

    @Getter private static final String headerFxmlPath = "/src/main/resources/subcomponents/headerComponent.fxml";
    @Getter private static final String appFxmlPath = "/src/main/resources/app.fxml";
    @Getter private static final String machinePageTemplateFxmlPath = "/src/main/resources/subcomponents/machinePageTemplate.fxml";
    @Getter private static final String setMachineConfigTemplateFxmlPath = "/src/main/resources/subcomponents/setMachineConfigTemplate.fxml";
    @Getter private static final String currMachineConfigTemplateFxmlPath = "/src/main/resources/subcomponents/currMachineConfigTemplate.fxml";
    @Getter private static final String encryptPageTemplateFxmlPath = "/src/main/resources/subcomponents/encryptPageTemplate.fxml";
    @Getter private static final String encryptPageTemplateFxmlPath2 = "/src/main/resources/subcomponents/encryptPageTemplate2.fxml";
    @Getter private static final String statisticsTableFxmlPath = "/src/main/resources/subcomponents/statisticsTable.fxml";
}

