package utils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;
import manager.UserManager;
import service.PropertiesService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

public class ServletUtils {

    private static final Object userManagerLock = new Object();

    public static UserManager getUserManager(ServletContext servletContext) {
        synchronized (userManagerLock) {
            if (servletContext.getAttribute(PropertiesService.getUserManagerAttributeName()) == null) {
                UserManager newUserManager = new UserManager();
                servletContext.setAttribute(PropertiesService.getUserManagerAttributeName(), newUserManager);
            }
        }
        UserManager userManager =(UserManager) servletContext.getAttribute(PropertiesService.getUserManagerAttributeName());
        return userManager;
    }

    public static Optional<PrintWriter> getRespWriter(HttpServletResponse resp){
        PrintWriter respWriter;
        try {
            respWriter = resp.getWriter();
            return Optional.of(respWriter);
        } catch (IOException ignore) {
            return Optional.empty();
        }
    }

}
