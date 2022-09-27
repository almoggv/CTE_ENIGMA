package src.main.java.service;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.*;

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
    @Getter private static final String enigmaMachineIllustration = "/src/main/resources/images/enigma.jpg";
    @Getter private static final String enigmaMachineIllustration2 = "/src/main/resources/images/enigma2.jpg";
    @Getter private static final String enigmaMachineIllustrationDark = "/src/main/resources/images/darkEnigma.jpg";
    @Getter private static final String aviadPic = "/src/main/resources/images/aviad.jpg";
    @Getter private static final String aviadPic2 = "/src/main/resources/images/aviad2.jpg";
    @Getter private static final String bisliPic = "/src/main/resources/images/bisli.jpg";
    @Getter private static final String cifliPic = "/src/main/resources/images/cifli.jpg";
//CSSs
    @Getter private static final String darkThemeName = "Dark Theme";
    @Getter private static final String darkThemeCssPath = "/src/main/resources/css/darkTheme.css";
    @Getter private static final String lightThemeName = "Light Theme";
    @Getter private static final String lightThemeCssPath = "/src/main/resources/css/greenTheme.css";
    @Getter private static final String defaultThemeName = "Default Theme";
    @Getter private static final String defaultThemeCssPath = "/src/main/resources/css/defaultTheme.css";
    @Getter private static final Map<String,String> cssThemeToFileMap;
    @Getter private static final List<String> imageListForAnimation;


    static{
        Map<String,String> tempCssThemeToFileMap = new HashMap<>();
        tempCssThemeToFileMap.put(darkThemeName,darkThemeCssPath);
        tempCssThemeToFileMap.put(lightThemeName,lightThemeCssPath);
        tempCssThemeToFileMap.put(defaultThemeName,defaultThemeCssPath);

        cssThemeToFileMap = Collections.unmodifiableMap(tempCssThemeToFileMap);

        List<String> tempImageListForAnimation = new ArrayList<>();
        tempImageListForAnimation.add(enigmaMachineIllustration);
        tempImageListForAnimation.add(aviadPic);
        tempImageListForAnimation.add(aviadPic2);
        tempImageListForAnimation.add(bisliPic);
        tempImageListForAnimation.add(cifliPic);

        imageListForAnimation = Collections.unmodifiableList(tempImageListForAnimation);
    }
}

