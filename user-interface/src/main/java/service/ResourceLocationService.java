package src.main.java.service;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode
public class ResourceLocationService {
//Logger
    @Getter private static final String log4jPropertiesResourcePath = "/main/resources/log4j.properties";
//Fxmls
    @Getter private static final String headerFxmlPath = "/src/main/resources/subcomponents/headerComponent.fxml";
    @Getter private static final String headerWithMenuFxmlPath = "/src/main/resources/subcomponents/headerComponentWithMenu.fxml";
    @Getter private static final String appFxmlPath = "/src/main/resources/app.fxml";
    @Getter private static final String machinePageTemplateFxmlPath = "/src/main/resources/subcomponents/machinePageTemplate.fxml";
    @Getter private static final String setMachineConfigTemplateFxmlPath = "/src/main/resources/subcomponents/setMachineConfigTemplate.fxml";
    @Getter private static final String currMachineConfigTemplateFxmlPath = "/src/main/resources/subcomponents/currMachineConfigTemplate.fxml";
    @Getter private static final String encryptPageTemplateFxmlPath = "/src/main/resources/subcomponents/encryptPageTemplate.fxml";
    @Getter private static final String encryptPageTemplateFxmlPath2 = "/src/main/resources/subcomponents/encryptPageTemplate2.fxml";
    @Getter private static final String statisticsAnchorFxmlPath = "/src/main/resources/subcomponents/statisticsGridController.fxml";
    @Getter private static final String bruteForcePageTemplateFxmlPath = "/src/main/resources/subcomponents/bruteForcePageTemplate.fxml";
    @Getter private static final String decodeCandidatePath = "/src/main/resources/subcomponents/decodeCandidate.fxml";

    //Images
    @Getter private static final String enigmaMachineIllustration = "/src/main/resources/images/enigma-illustration.png";
    @Getter private static final String enigmaMachineIllustration2 = "/src/main/resources/images/enigma2.jpg";
    @Getter private static final String enigmaMachineIllustrationDark = "/src/main/resources/images/darkEnigma.jpg";
//CSSs
    @Getter private static final String darkThemeName = "Dark Theme";
    @Getter private static final String darkThemeCssPath = "/src/main/resources/css/darkTheme.css";
    @Getter private static final String lightThemeName = "Light Theme";
    @Getter private static final String lightThemeCssPath = "/src/main/resources/css/lightTheme.css";
    @Getter private static final Map<String,String> cssThemeToFileMap;

    static{
        Map<String,String> tempCssThemeToFileMap = new HashMap<>();
        tempCssThemeToFileMap.put(darkThemeName,darkThemeCssPath);
        tempCssThemeToFileMap.put(lightThemeName,lightThemeCssPath);

        cssThemeToFileMap = Collections.unmodifiableMap(tempCssThemeToFileMap);
    }
}

