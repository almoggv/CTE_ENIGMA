package service;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class PropertiesService {
    @Getter
    private static final String log4jPropertiesResourcePath = "/main/resources/log4j.properties";
    @Getter
    private static final int defaultTaskSize = 5;
}