package manager;

import dto.*;
import enums.GameStatus;
import model.Ally;
import model.ContestRoom;
import model.User;

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
    public ContestRoomData addUserToRoom(Ally ally , UserManager userManager, ContestRoom room) {
        if(room.getCurrNumOfTeams() < room.getRequiredNumOfTeams()){
            //add ally to room
            room.getAlliesList().add(ally);
            room.setCurrNumOfTeams(room.getCurrNumOfTeams() + 1);
            ally.setEncryptionCandidateList(new ArrayList<>());

            //set room in ally info
            userManager.getUserByName(ally.getTeamName()).setContestRoom(room);
            userManager.getUserByName(ally.getTeamName()).setInARoom(true);

            //set room in agents of ally if exist
            for (AgentData agent : ally.getAgentsList()) {
                userManager.getUserByName(agent.getName()).setContestRoom(room);
            }
        }
        return makeRoomDataFromRoom(room);
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
            for (Ally ally: room.getAlliesList() ) {
                //todo: start DM when exsist
                ally.getDecryptionManager();
            }
        }
        //todo: remove - only for test
//        room.setGameStatus(GameStatus.READY);
    }

    public void checkWin(ContestRoom contestRoom, String originalWord, UserManager userManager) {
        List<EncryptionCandidate> encryptionCandidateList = contestRoom.getEncryptionCandidateList();
        for(EncryptionCandidate candidate: encryptionCandidateList){
            if(candidate.getCandidate().toUpperCase().equals(originalWord)){
                contestRoom.setGameStatus(GameStatus.DONE);
                contestRoom.setWinnerName(candidate.getAllyTeamName());
            }
        }
    }

    public Set<ContestRoomData> getRoomsData() {
        Set<ContestRoomData> roomsSet = new HashSet();
        for (ContestRoom room : roomsDataMap.values()) {
            roomsSet.add(new ContestRoomData(room.getName(), room.getCreatorName(), room.getGameStatus(), room.getDifficultyLevel(), room.getCurrNumOfTeams(), room.getRequiredNumOfTeams(), room.getWordToDecrypt()));
        }
        return Collections.unmodifiableSet(roomsSet);
    }

    public ContestRoomData makeRoomDataFromRoom(ContestRoom room){
        return new ContestRoomData(room.getName(), room.getCreatorName(), room.getGameStatus(), room.getDifficultyLevel(), room.getCurrNumOfTeams(), room.getRequiredNumOfTeams(),room.getWordToDecrypt());
    }

    //todo - change to private - this is for check
    public void resetContestRoom(ContestRoom room, UserManager userManager){
        List<Ally> allies = room.getAlliesList();
        //reset room itself
        room.setGameStatus(GameStatus.WAITING);
        room.setCurrNumOfTeams(0);
        room.setWordToDecrypt(null);
        room.setNumOfReady(0);
        room.setEveryoneReady(false);
        room.setWinnerName(null);
        room.setEncryptionCandidateList(new ArrayList<>());

        //reset ally teams
        for (Ally ally : allies) {
            //reset room in ally info
            User allyuser = userManager.getUserByName(ally.getTeamName());
            allyuser.setContestRoom(null);
            allyuser.setInARoom(false);
            allyuser.setSentGotWin(false);
            ally.setEncryptionCandidateList(null);

            //reset room in agents of ally if exist
            for (AgentData agent : ally.getAgentsList()) {
                userManager.getUserByName(agent.getName()).setContestRoom(null);
                userManager.getUserByName(agent.getName()).setSentGotWin(false);
                userManager.getUserByName(agent.getName()).setInARoom(false);
            }
        }
        room.getAlliesList().clear();
        room.setNumOfGotWinCount(0);
    }

    public void updateGotWon(ContestRoom contestRoom, UserManager userManager) {
        contestRoom.setNumOfGotWinCount(contestRoom.getNumOfGotWinCount() + 1);
//        if(contestRoom.getNumOfGotWinCount() >= 3){
            resetContestRoom(contestRoom, userManager);
//        }
    }
}
