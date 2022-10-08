package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import component.MachineHandler;
import dto.MachineState;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

@WebServlet("/encrypt")
public class Encrypt extends HttpServlet {
    private static final Logger log = Logger.getLogger(CurrMachineConfiguration.class);
    static {
        try {
            Properties p = new Properties();
            p.load(CurrMachineConfiguration.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + CurrMachineConfiguration.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + CurrMachineConfiguration.class.getSimpleName() ) ;
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(req.getContentType() == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("Please enter a word to encrypt.");
            return;
        }
        if(!req.getHeader(PropertiesService.getHttpHeaderContentType()).equals(PropertiesService.getTextPlainHttpContentType())){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("Expecting text content type");
            return;
        }
        PrintWriter respWriter;
        try {
            respWriter = resp.getWriter();
        }
        catch (Exception e){
            log.error("Failed to get response Writer, Exception Message=" + e.getMessage());
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            return;
        }
        MachineHandler machineHandler = (MachineHandler) req.getSession(false).getAttribute(PropertiesService.getMachineHandlerAttributeName());
        if(machineHandler == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            respWriter.print("Please upload a schema file first. (/upload-machine-file)");
            return;
        }
        Optional<MachineState> machineState = machineHandler.getMachineState();
        if(!machineState.isPresent()){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("Please configure a machine state first. (/assemble-machine-randomly, or //assemble-machine-manually)");
            return;
        }

        BufferedReader bufferedReader = req.getReader();

        if(machineHandler.getInventoryInfo().get().getABC().contains(System.lineSeparator())){
            String input = bufferedReader.lines().collect(Collectors.joining());
            try {
                String output = machineHandler.encrypt(input);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().print("input was encrypted to: "+ output);
            }
            catch (Exception e){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().print("Input contains letters not in machine ABC." + e.getMessage());
            }
        }
        else{
            Stream<String> input = bufferedReader.lines();
            AtomicReference<String> result = new AtomicReference<>("");
            input.forEach((line)->{
                try {
                    result.set(result + machineHandler.encrypt(line) + System.lineSeparator());
                }
                catch (IOException e){
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    try {
                        resp.getWriter().print("Input contains letters not in machine ABC." + e.getMessage());
                    } catch (IOException ignore) {}
                }
            });

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print("input was encrypted to: "+ result);
        }

    }
}
