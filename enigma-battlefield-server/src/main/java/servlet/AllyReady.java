package servlet;

import component.MachineHandler;
import dto.AllyTeamData;
import dto.ContestRoom;
import dto.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.RoomManager;
import manager.UserManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet(name = "AllyReadyServlet" ,urlPatterns = {"/ally-ready"})
public class AllyReady extends HttpServlet {
    private static final Logger log = Logger.getLogger(AllyReady.class);
    static {
        try {
            Properties p = new Properties();
            p.load(AllyReady.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + AllyReady.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + AllyReady.class.getSimpleName() ) ;
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

        RoomManager roomManager = ServletUtils.getRoomManager(this.getServletContext());
        UserManager userManager = ServletUtils.getUserManager(this.getServletContext());

        User user = userManager.getUserByName((String) req.getSession(false).getAttribute(PropertiesService.getUsernameAttributeName()));
        if(user == null){
            resp.setStatus(SC_BAD_REQUEST);
            respWriter.print("user does not exist");
            return;
        }
        ContestRoom contestRoom = user.getContestRoom();
        Integer taskSize;
        try{
            taskSize = Integer.valueOf(req.getParameter(PropertiesService.getTaskSizeAttributeName()));
        }
        catch (Exception e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            respWriter.print("You need to set a task size, a positive integer over 1.");
            return;
        }

        if(contestRoom == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("You are not assigned to a room.");
            return;
        }

        if(taskSize <= 0 ){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            respWriter.print("task size must be a positive integer over 1.");
        }
        else{
            resp.setStatus(SC_OK);
            //todo: after connecting ally to user
            AllyTeamData ally = userManager.getAllieTeamDataByName(user.getUsername());
            ally.setTaskSize(taskSize);
            roomManager.setUserReady(user,contestRoom);
            respWriter.print("Ready with task size: " + taskSize);
        }
    }
}
