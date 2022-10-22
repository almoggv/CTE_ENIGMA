package servlet;

import com.google.gson.Gson;
import dto.AgentsListPayload;
import dto.ContestAllyTeamsPayload;
import enums.UserType;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.UserManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;
import utils.ServletUtils;
import utils.SessionUtills;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@WebServlet(name = "AgentsDataByAllyNameServlet" ,urlPatterns = {"/agents-by-ally"})
public class AgentsDataByAllyName extends HttpServlet {
    private static final Logger log = Logger.getLogger(AgentsDataByAllyName.class);
    static {
        try {
            Properties p = new Properties();
            p.load(AgentsDataByAllyName.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + AgentsDataByAllyName.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + AgentsDataByAllyName.class.getSimpleName() ) ;
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
        //under assumption - ally wants his agents - agents connected to his name.
        String usernameFromSession = SessionUtills.getUsername(req);
        String usernameFromParameter = req.getParameter(PropertiesService.getUsernameAttributeName());

        if(userManager == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            respWriter.print("no user manager.");
        }
        if(usernameFromSession == null && usernameFromParameter == null/*for debug*/){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            respWriter.print("not a registered user.");
        }
        if(userManager.getUserByName(usernameFromSession).getType() != UserType.ALLY
        && userManager.getUserByName(usernameFromParameter).getType() != UserType.ALLY){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            respWriter.print("not an ally.");
        }

        AgentsListPayload payload = new AgentsListPayload();
        resp.setStatus(SC_OK);
        //todo - fix
        payload.setAgentsList(userManager.getAllieTeamDataByName(usernameFromSession).getAgentsList());
//        payload.setAgentsList(userManager.getAllieTeamDataByName(usernameFromParameter).getAgentsList());

        Gson gson = new Gson();
        resp.setHeader(PropertiesService.getHttpHeaderContentType(),PropertiesService.getJsonHttpContentType());
        respWriter.print(gson.toJson(payload));
    }
}
