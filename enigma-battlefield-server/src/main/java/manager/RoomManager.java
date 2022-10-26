package manager;

import dto.*;
import enums.DecryptionDifficultyLevel;
import enums.GameStatus;

import java.util.*;

public class RoomManager {
    private final Map<String, ContestRoom> roomsDataMap = new HashMap<String, ContestRoom>();
    public synchronized void addRoom(String roomName, ContestRoom roomData){
        roomsDataMap.put(roomName, roomData);
    }
    public synchronized void removeRoom(String roomName){
        roomsDataMap.remove(roomName);
    }
    public synchronized Set<ContestRoom> getRooms() {
        Set<ContestRoom> roomsSet = new HashSet(roomsDataMap.values());
        return Collections.unmodifiableSet(roomsSet);
    }
    public synchronized ContestRoom getRoomByName(String roomName){
        return roomsDataMap.get(roomName);
    }
    public boolean isRoomExists(String roomName) {
        return roomsDataMap.containsKey(roomName);
    }
    public void addUserToRoom(AllyTeamData ally ,UserManager userManager, ContestRoom room) {
        if(room.getCurrNumOfTeams() < room.getRequiredNumOfTeams()){
            //add ally to room
            room.getAlliesList().add(ally);
            room.setCurrNumOfTeams(room.getCurrNumOfTeams() + 1);

            //set room in ally info
            userManager.getUserByName(ally.getTeamName()).setContestRoom(room);

            //set room in agents of ally if exist
            for (AgentData agent : ally.getAgentsList()) {
                userManager.getUserByName(agent.getName()).setContestRoom(room);
            }
        }
    }

    public void setUserReady(User user, ContestRoom room){
        if(user.isReady()){
            return;
        }
        user.setReady(true);
        room.setNumOfReady(room.getNumOfReady() + 1);
        boolean isEveryoneReady = room.getNumOfReady() == room.getRequiredNumOfTeams() + 1;
        room.setEveryoneReady(isEveryoneReady);
        if (isEveryoneReady){
            room.setGameStatus(GameStatus.READY);
        }
        //todo: remove - only for test
        room.setGameStatus(GameStatus.READY);
    }

    public void checkWin(ContestRoom contestRoom, String originalWord) {
        for(EncryptionCandidate candidate: contestRoom.getEncryptionCandidateList()){
            if(candidate.getCandidate().equals(originalWord)){
                contestRoom.setGameStatus(GameStatus.DONE);
                contestRoom.setWinnerName(candidate.getAllyTeamName());
            }
        }
    }
}
