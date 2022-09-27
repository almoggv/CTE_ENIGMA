package main.java.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import main.java.service.PropertiesService;

public class SessionUtills {

    public static String getUsername (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute(PropertiesService.getUsernameAttributeName()) : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    public static void clearSession (HttpServletRequest request) {
        request.getSession().invalidate();
    }
}
