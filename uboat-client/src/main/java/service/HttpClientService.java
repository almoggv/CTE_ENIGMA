package main.java.service;

import okhttp3.OkHttpClient;

public class HttpClientService {


    private final static OkHttpClient HTTP_CLIENT =
            new OkHttpClient.Builder()
//                    .cookieJar(simpleCookieManager)
                    .followRedirects(false)
                    .build();

    public static void shutdown() {
        System.out.println("Shutting down HTTP CLIENT");
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
    }

}
