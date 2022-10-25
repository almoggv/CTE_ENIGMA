package controller;

import dto.AgentData;
import javafx.scene.control.Label;

public class AgentDataController {
    public Label agentNameLabel;
    public Label numOfThreadsLabel;
    public Label taskSizeLabel;

    private void setAgentNameLabel(String value){
        agentNameLabel.setText(value);
    }

    private void setTaskSizeLabel(Integer value){
        taskSizeLabel.setText(String.valueOf(value));
    }

    private void setNumOfThreadsLabel(Integer value){
        numOfThreadsLabel.setText(String.valueOf(value));
    }

    public void setData(AgentData agentData){
        this.setAgentNameLabel(agentData.getName());
        this.setNumOfThreadsLabel(agentData.getNumberOfThreads());
        this.setTaskSizeLabel(agentData.getNumberOfTasksThatTakes());
    }
}
