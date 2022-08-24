package src.main.java.handler;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class PropertiesService {
    @Getter private static final String log4jPropertiesResourcePath = "/main/resources/log4j.properties";
    @Getter private static final String machineSceneFxmlPath = "/src/main/resources/scenebuilder/cte-machine-stage.fxml";
    @Getter private static final String encryptSceneFxmlPath = "/src/main/resources/scenebuilder/cte-encrypt-stage.fxml";
    @Getter private static final String bruteForceSceneFxmlPath = "/src/main/resources/scenebuilder/cte-brute-force-stage.fxml";
    @Getter private static final String primarySceneFxmlPath = machineSceneFxmlPath;
}

