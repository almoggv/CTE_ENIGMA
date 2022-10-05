package testservice;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class PropertiesService {
    @Getter
    private static final String log4jPropertiesResourcePath = "/main/resources/log4j.properties";

    @Getter private static final String testSchemaEx2BasicEasy = "C:\\Dev\\GitProjects\\CTE_JAVA_ENIGMA\\decryption-manager\\src\\test\\resources\\ex2-basic-easy.xml";
    @Getter private static final String testSchemaEx2Basic = "C:\\Dev\\GitProjects\\CTE_JAVA_ENIGMA\\decryption-manager\\src\\test\\resources\\ex2-basic.xml";
}