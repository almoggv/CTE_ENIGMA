package main.java.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.java.dto.VersionDto;

import java.io.IOException;

@WebServlet(name = "VersionServlet" ,urlPatterns = {"/version"})
public class Version extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        VersionDto versionDto = new VersionDto();
        versionDto.setMainVersion(1);
        versionDto.setSubVersion(0);
        resp.getWriter().println("Version: " + versionDto);
    }
}
