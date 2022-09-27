package main.java.utils;

import jakarta.servlet.ServletContext;
import main.java.manager.UserManager;
import main.java.service.PropertiesService;

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

}
