package manager;

import dto.ContestRoom;
import dto.User;
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

}
