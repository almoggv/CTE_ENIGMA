package service;

import lombok.Getter;
import okhttp3.*;
import okhttp3.OkHttpClient;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.util.Properties;
import java.util.function.Consumer;

public class HttpClientService {
    private static final Logger log = Logger.getLogger(HttpClientService.class);
    static {
        try {
            Properties p = new Properties();
            p.load(HttpClientService.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + HttpClientService.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + HttpClientService.class.getSimpleName());
        }
    }

    private final static SimpleCookieManager simpleCookieManager = new SimpleCookieManager();

    @Getter
    private final static OkHttpClient HTTP_CLIENT =
            new OkHttpClient.Builder()
                    .cookieJar(simpleCookieManager)
                    .followRedirects(false)
                    .build();

    public static void setCookieManagerLoggingFacility(Consumer<String> logConsumer) {
        simpleCookieManager.setLogData(logConsumer);
    }

    public static void removeCookiesOf(String domain) {
        simpleCookieManager.removeCookiesOf(domain);
    }

    public static void runAsync(String finalUrl, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        log.debug("HttpClientService - new request sent to -" + finalUrl);
        Call call = HttpClientService.HTTP_CLIENT.newCall(request);
        call.enqueue(callback);
    }

    public static void runAsync(Request request, Callback callback) {
        log.info("HttpClientService - new request sent to -" + request.url().toString());
        Call call = HttpClientService.HTTP_CLIENT.newCall(request);

        call.enqueue(callback);
    }

    public static void shutdown() {
        log.info("Shutting down HTTP CLIENT");
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
    }

    public static Response runSync(String finalUrl){
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        Call call = HttpClientService.HTTP_CLIENT.newCall(request);
        try {
            Response response = call.execute();
            return response;
        } catch (IOException e) {
            log.error("Failed to execute request to Url=" + finalUrl + ", exception=" + e.getMessage());
            return null;
        }
    }
}
