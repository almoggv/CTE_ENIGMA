package servlet;

import com.google.gson.Gson;
import dto.DictionaryLoadInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.DictionaryManager;
import manager.UserManager;
import model.User;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Properties;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "GetDictionaryServlet" ,urlPatterns = {"/load-dictionary"})
public class GetDictionary extends HttpServlet {
    private static final Logger log = Logger.getLogger(GetDictionary.class);
    static {
        try {
            Properties p = new Properties();
            p.load(GetDictionary.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + GetDictionary.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + GetDictionary.class.getSimpleName() ) ;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter respWriter;
        try {
            respWriter = resp.getWriter();
        }
        catch (Exception e){
            log.error("Failed to get response Writer, Exception Message=" + e.getMessage());
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            return;
        }
        UserManager userManager = ServletUtils.getUserManager(this.getServletContext());
        User user = userManager.getUserByName((String) req.getSession(false).getAttribute(PropertiesService.getUsernameAttributeName()));   //Username by session
        if(user == null){
            resp.setStatus(SC_UNAUTHORIZED);
            respWriter.print("Please login first");
            return;
        }
        DictionaryManager dictionaryManager = user.getContestRoom().getDictionaryManager();
        DictionaryLoadInfo loadInfo = new DictionaryLoadInfo();
        Gson gson = new Gson();
        if(dictionaryManager == null || dictionaryManager.getDictionary(). isEmpty()){
            log.error("Failed to fetch dictionary , Dictionary of user=" + user.getUsername() + " is null or empty");
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            respWriter.print(gson.toJson(loadInfo));
            return;
        }
        loadInfo.setAbc(dictionaryManager.getAbc());
        loadInfo.setExcludedChars(new HashSet<>(dictionaryManager.getExcludeChars()));
        loadInfo.setWords(dictionaryManager.getDictionary().keySet());
        resp.setStatus(SC_OK);
        respWriter.print(gson.toJson(loadInfo));
        return;
    }
}
