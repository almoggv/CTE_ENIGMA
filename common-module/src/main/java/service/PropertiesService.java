package service;


import lombok.Getter;

public class PropertiesService {
    @Getter private static final String log4jPropertiesResourcePath = "/log4j.properties";

    @Getter private static final String tokenAttributeName = "access_token";
    @Getter private static final String messageAttributeName = "message";
    @Getter private static final String inventoryAttributeName = "inventory";



}
