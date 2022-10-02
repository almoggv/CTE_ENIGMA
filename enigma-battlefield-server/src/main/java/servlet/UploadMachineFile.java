package main.java.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import main.java.component.MachineHandler;
import main.java.component.impl.MachineHandlerImpl;
import main.java.manager.UserManager;
import main.java.service.PropertiesService;
import main.java.utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Scanner;

@WebServlet("/upload-machine-file")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)

public class UploadMachineFile extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //TODO: use this template to authenticate users
        UserManager userManager = ServletUtils.getUserManager(this.getServletContext());
        if(!userManager.isUserRegistered(req.getHeader(PropertiesService.getTokenAttributeName()))){
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        resp.setContentType("text/plain");
        PrintWriter outWriter = resp.getWriter();

        Collection<Part> parts = req.getParts();
        Part uploadedFile = req.getPart("file");

        new Scanner(uploadedFile.getInputStream()).useDelimiter("\\Z").next();
        MachineHandler machineHandler = new MachineHandlerImpl();
        try{
            machineHandler.buildMachinePartsInventory(uploadedFile.getInputStream());
            this.getServletContext().setAttribute(PropertiesService.getMachineHandlerAttributeName(), machineHandler);
            resp.setStatus(HttpServletResponse.SC_OK);
            outWriter.println("Machine built successfully from file.");
        }
        catch (Exception exception){
            resp.setStatus(400);
            outWriter.println(exception.getMessage());
        }

    }
}
