package src.main.java.handler;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class PropertiesService {
    @Getter private static final String log4jPropertiesResourcePath = "/main/resources/log4j.properties";
    @Getter private static final String headerFxmlPath = "/src/main/resources/subcomponents/headerGrid.fxml";
    @Getter private static final String appFxmlPath = "/src/main/resources/app.fxml";
    @Getter private static final String primarySceneFxmlPath = headerFxmlPath;
}

