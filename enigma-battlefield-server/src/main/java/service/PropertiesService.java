package service;

import lombok.Getter;

public class PropertiesService {
    @Getter private static final String log4jPropertiesResourcePath = "/log4j.properties";
    @Getter private static final int defaultTaskSize = 20;
    @Getter private static final int maxThreadPoolSize = 4;
    @Getter private static final int minThreadPoolSize = 1;
    @Getter private static final int maxWorkBatchesQueueSize = 500;
    //server properties
    @Getter private static final String userManagerAttributeName = "userManager";
    @Getter private static final String usernameAttributeName = "username";
    @Getter private static final String usernameErrorAttributeName = "username_error";
    @Getter private static final String tokenAttributeName = "access_token";
    @Getter private static final String messageAttributeName = "message";
    @Getter private static final String inventoryAttributeName = "inventory";
    @Getter private static final String encryptionInputAttributeName = "input";
    @Getter private static final String originalWordAttributeName = "original_word";
    @Getter private static final String encryptedWordAttributeName = "encrypted_word";
    @Getter private static final String roomManagerAttributeName = "roomManager";
    @Getter private static final String roomNameAttributeName = "room_name";
    @Getter private static final String userTypeAttributeName = "user_type";
    @Getter private static final String uboatAttributeName = "uboat";
    @Getter private static final String allyAttributeName = "ally";
    @Getter private static final String allyNameAttributeName = "ally_name";
    @Getter private static final String taskSizeAttributeName = "task_size";
    @Getter private static final String numOfThreadsAttributeName = "thread_num";
    @Getter private static final String batchSizeAttributeName = "size";


    @Getter private static final String jsonHttpContentType = "application/json";
    @Getter private static final String textHttpContentType = "application/text";
    @Getter private static final String textPlainHttpContentType = "text/plain";
    @Getter private static final String HttpHeaderContentType = "Content-Type";

    //server context attributes
    @Getter private static final String machineHandlerAttributeName = "MachineHandler";



}
