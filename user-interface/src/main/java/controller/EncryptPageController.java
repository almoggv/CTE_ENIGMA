package src.main.java.controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import main.java.component.MachineHandler;
import main.java.dto.EncryptionInfoHistory;
import main.java.dto.InventoryInfo;
import main.java.dto.MachineState;
import main.java.service.PropertiesService;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.controlsfx.control.textfield.CustomTextField;
import src.main.java.service.DataService;
import src.main.java.service.ResourceLocationService;
import src.main.java.ui.CLIMenu;
import src.main.java.ui.GuiApplication;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;

public class EncryptPageController implements Initializable {

    private static final Logger log = Logger.getLogger(EncryptPageController.class);
    static {
        try {
            Properties p = new Properties();
            p.load(EncryptPageController.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + EncryptPageController.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + EncryptPageController.class.getSimpleName() ) ;
        }
    }

    @Getter @Setter private MachineHandler machineHandler;

    @Getter @Setter @FXML AppController parentController;
    @Getter @FXML private CurrMachineConfigController currMachineConfigComponentController;

    @FXML public CustomTextField encryptTextField;
    @FXML public Button encryptButton;
    @FXML public TableView statisticsTable;
    @FXML public TableColumn statisticsColOriginal;
    @FXML public TableColumn statisticsColEncrypted;
    @FXML public TableColumn statisticsColTime;
    @FXML public CustomTextField resultTextField;
    @FXML public FlowPane inputKeyboardFlowPane;
    @FXML public FlowPane outputKeyboardFlowPane;
    @FXML public Button clearButton;
    @FXML public Button resetMachineStateButton;
    @FXML public ScrollPane scrollOfRightBottomAnchor;
    @FXML public AnchorPane rightAnchorOfBottom;
    @FXML public Accordion statisticsAccordion;
    @FXML public ScrollPane encryptScroll;
    @FXML public HBox encryptHbox;
    @FXML public GridPane currMachineConfigComponent;
    @FXML private GridPane rootGrid;
    @FXML private ScrollPane currMachineConfigWrapperPane;
    @FXML private TextField liveEncryptInputTextField;
    @FXML private TextField liveEncryptOutTextField;
//    @FXML private FlowPane keyboardFlowPane;

    private Map<String,Button> letterToInputKeyboardButtonMap = new HashMap<>();
    private Map<String,Button> letterToOutputKeyboardButtonMap = new HashMap<>();
    private Map<String,Timeline> letterToButtonColorAnimationMap = new HashMap<>();
    private StringProperty liveEncryptionInputProperty = new SimpleStringProperty("");
    private StringProperty liveEncryptionOutputProperty = new SimpleStringProperty("");
    private Color startColor;
    private Color endColor;


    private SimpleBooleanProperty isEncryptionTextFieldEmpty = new SimpleBooleanProperty(true);


    public Parent getRootComponent(){
        return rootGrid;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        liveEncryptInputTextField.textProperty().bindBidirectional(liveEncryptionInputProperty);
        liveEncryptOutTextField.textProperty().bindBidirectional(liveEncryptionOutputProperty);
        liveEncryptInputTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
            try {
                if(!newValue.equals("") && newValue.length()>= oldValue.length()){
                    String newChar = newValue.substring(newValue.length()-1);
//                    String newEncryption = machineHandler.encrypt(newChar);
                    String newEncryption = machineHandler.encryptWithoutHistory(newChar);
                    String newOutText = liveEncryptionOutputProperty.get() + newEncryption;
                    liveEncryptionOutputProperty.setValue(newOutText);
                    DataService.getCurrentMachineStateProperty().setValue(machineHandler.getMachineState().get());
                    //need to set to null - because the machine state thinks it hasent changed - only internal structures changed and it dosent register
//                    DataService.getEncryptionInfoHistoryProperty().setValue(null);
//                    DataService.getEncryptionInfoHistoryProperty().setValue(machineHandler.getMachineStatisticsHistory());
                }
                if(oldValue.length() > newValue.length() && newValue.equals("")){
                    liveEncryptionOutputProperty.setValue("");
                }
            } catch (IOException e) { getParentController().showMessage(e.getMessage());}
        }));
        liveEncryptionOutputProperty.addListener(((observable, oldValue, newValue) -> {
            if(oldValue!=null && !oldValue.equals("") && newValue.equals("") && !liveEncryptionInputProperty.get().equals("")){
                liveEncryptionInputProperty.setValue("");
            }
        }));

        if(currMachineConfigComponentController != null){
            currMachineConfigComponentController.setParentController(this);
            currMachineConfigComponentController.bindToData(DataService.getCurrentMachineStateProperty());
        }
        encryptButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
                        encryptTextField.getText().trim().isEmpty(),
                encryptTextField.textProperty()));
        DataService.getInventoryInfoProperty().addListener(new ChangeListener<InventoryInfo>() {
            @Override
            public void changed(ObservableValue<? extends InventoryInfo> observable, InventoryInfo oldValue, InventoryInfo newValue) {
                if(newValue!= null) {
                    buildKeyboards(newValue);
                }
            }
        });

//        issue with pressed twice - very noticeable
        DataService.getEncryptionInfoHistoryProperty().addListener(new ChangeListener<Map<MachineState, List<EncryptionInfoHistory>>>() {
            @Override
            public void changed(ObservableValue<? extends Map<MachineState, List<EncryptionInfoHistory>>> observable, Map<MachineState, List<EncryptionInfoHistory>> oldValue, Map<MachineState, List<EncryptionInfoHistory>> newValue) {
                try{
                    if(newValue != null) {
                        showStatistics(newValue);
                    }
                    else{
                        statisticsAccordion.getPanes().clear();
                    }
                }
                catch (Exception e){
                    parentController.showMessage(e.getMessage());
                }
            }
        });

        statisticsAccordion.getPanes().clear();
    }

    private void buildKeyboards(InventoryInfo inventoryInfo) {
        inputKeyboardFlowPane.getChildren().clear();
        outputKeyboardFlowPane.getChildren().clear();
        letterToInputKeyboardButtonMap.clear();
        letterToButtonColorAnimationMap.clear();
        letterToOutputKeyboardButtonMap.clear();

        String abc = inventoryInfo.getABC();
        startColor = Color.web("#E7E7E7");
        endColor = Color.web("#F1C360");

        for (int i = 0; i < abc.length(); i++) {
            String letter = abc.substring(i,i+1);
            Button letterButton = new Button(letter);
            inputKeyboardFlowPane.getChildren().add(letterButton);
            letterToInputKeyboardButtonMap.putIfAbsent(letter,letterButton);
        }
        for (int i = 0; i < abc.length(); i++) {
            String letter = abc.substring(i,i+1);
            Button letterButton = new Button(letter);
            outputKeyboardFlowPane.getChildren().add(letterButton);
            letterToOutputKeyboardButtonMap.putIfAbsent(letter,letterButton);
            ObjectProperty<Color> colorProperty = new SimpleObjectProperty<Color>(startColor);
            StringBinding cssColorSpec = Bindings.createStringBinding(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return String.format("-fx-body-color: rgb(%d, %d, %d);",
                            (int) (256*colorProperty.get().getRed()),
                            (int) (256*colorProperty.get().getGreen()),
                            (int) (256*colorProperty.get().getBlue()));
                }
            }, colorProperty);
            Timeline lightUpAnimation = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(colorProperty, startColor)),
                    new KeyFrame(Duration.seconds(1), new KeyValue(colorProperty, endColor)),
                    new KeyFrame(Duration.seconds(2), new KeyValue(colorProperty, startColor)));
            letterToButtonColorAnimationMap.putIfAbsent(letter,lightUpAnimation);
            letterButton.styleProperty().bind(cssColorSpec);
            liveEncryptionOutputProperty.addListener(((observable, oldValue, newValue) -> {
                if(oldValue.length() < newValue.length() && !newValue.equals("")){
                    String lastLetter = newValue.substring(newValue.length()-1);
                    letterToButtonColorAnimationMap.get(lastLetter).play();
                }
            }));

        }
        for (String letter: letterToInputKeyboardButtonMap.keySet()) {
            Button inputButton = letterToInputKeyboardButtonMap.get(letter);
            inputButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    String oldValue = liveEncryptionInputProperty.get();
                     liveEncryptionInputProperty.setValue(oldValue + inputButton.getText());
                }
            });

        }

    }

    /**
     * binds the root component of the controller to the designated pane.
     */
    public void bindComponent(CurrMachineConfigController currMachineConfigComponentController) {
        this.currMachineConfigComponentController = currMachineConfigComponentController;
        currMachineConfigComponent = currMachineConfigComponentController.getRootGridPane();
    }

    private void showStatistics(Map<MachineState, List<EncryptionInfoHistory>> encryptionInfoHistory) throws IOException {
        statisticsAccordion.getPanes().clear();

        String machineStateMsg = "";
        for (MachineState machineStateHistory : encryptionInfoHistory.keySet()) {
            machineStateMsg = "Machine state:" + CLIMenu.getMachineState(machineStateHistory,
                    DataService.getInventoryInfoProperty().get());

            //create statistics grid
            URL statisticsAnchorUrl = GuiApplication.class.getResource(ResourceLocationService.getStatisticsAnchorFxmlPath());
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(statisticsAnchorUrl);
            Parent statisticsAnchor = fxmlLoader.load(statisticsAnchorUrl.openStream());
            TitledPane statisticsPane = new TitledPane(machineStateMsg, statisticsAnchor);
            StatisticsGridController statisticsGridController = fxmlLoader.getController();
            List<EncryptionInfoHistory> encryptionInfoHistoryList = encryptionInfoHistory.get(machineStateHistory);
            statisticsGridController.addHistory(encryptionInfoHistoryList);
            statisticsAccordion.getPanes().add(statisticsPane);
        }
    }

    public void onEncryptButtonAction(ActionEvent actionEvent) throws IOException {
        try {
            String result = machineHandler.encrypt(encryptTextField.getText());
            resultTextField.setText(result);
            DataService.getCurrentMachineStateProperty().setValue(machineHandler.getMachineState().get());
            //need to set to null - because the machine state thinks it hasent changed - only internal structures changed and it dosent register
            DataService.getEncryptionInfoHistoryProperty().setValue(null);
            DataService.getEncryptionInfoHistoryProperty().setValue(machineHandler.getMachineStatisticsHistory());
            log.debug("EncryptPageController - machine handler's statistics and history" + System.lineSeparator() + machineHandler.getMachineStatisticsHistory());
        }
        catch (Exception e){
            parentController.showMessage(e.getMessage());
        }
    }

    public void onClearButtonAction(ActionEvent actionEvent) {
        resultTextField.clear();
        encryptTextField.clear();
    }

    public void onResetMachineStateButtonAction(ActionEvent actionEvent) {
        machineHandler.resetToLastSetState();
        DataService.getCurrentMachineStateProperty().setValue(machineHandler.getMachineState().get());
        encryptTextField.clear();
        resultTextField.clear();
        liveEncryptionInputProperty.setValue("");
        liveEncryptionOutputProperty.setValue("");
        parentController.showMessage("Reset to last set machine state.");
    }
}
