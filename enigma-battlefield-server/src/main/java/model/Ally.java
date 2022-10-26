package model;

import dto.AgentData;
import dto.EncryptionCandidate;
import lombok.Getter;
import lombok.Setter;
import manager.AllyClientDM;

import java.util.List;

public class Ally {
    @Getter @Setter private String teamName;
    @Getter @Setter private Integer taskSize = 0;
    @Getter @Setter private List<AgentData> agentsList;
    @Getter @Setter private Integer numOfAgents;
    @Getter @Setter private List<EncryptionCandidate> encryptionCandidateList;
    @Getter @Setter private AllyClientDM decryptionManager;
}
