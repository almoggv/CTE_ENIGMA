package manager;

import dto.AgentData;
import dto.AllyTeamData;
import dto.User;
import enums.UserType;

import java.util.*;

public class UserManager {
    private final Map<String, User> usernamesToUserMap;
    private final Map<String, User> authTokenToUserMap;
    private final Map<String, AllyTeamData> usernamesToAlliesMap;
    private final Map<String, AgentData> usernamesToAgentsMap;

    public UserManager() {
        usernamesToUserMap = new HashMap<String, User>();
        authTokenToUserMap = new HashMap<String, User>();
        usernamesToAlliesMap = new HashMap<String, AllyTeamData>();
        usernamesToAgentsMap = new HashMap<String, AgentData>();
    }

    public boolean isUserRegistered(String userToken){
        //Actual implementation
//        return authTokenToUserMap.containsKey(userToken);
        //Used to save development time:
        return true;
    }

    public synchronized void addUser(String username, String userType) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setType(UserType.getByName(userType));
        usernamesToUserMap.put(username,newUser);
        authTokenToUserMap.put(newUser.getToken(),newUser);
        newUser.setInARoom(false);
        if(UserType.getByName(userType).equals(UserType.ALLY)){
            AllyTeamData allyTeamData = new AllyTeamData();
            allyTeamData.setTeamName(username);
            allyTeamData.setNumOfAgents(0);
            usernamesToAlliesMap.put(username, allyTeamData);
        }
        if(UserType.getByName(userType).equals(UserType.AGENT)){
            AgentData agentData = new AgentData();
            agentData.setName(username);
            agentData.setNumberOfTasksThatTakes(0);
            agentData.setNumberOfThreads(0);
            usernamesToAgentsMap.put(username, agentData);
        }
    }

    public synchronized String getUserToken(String username){
        return usernamesToUserMap.getOrDefault(username,new User()).getToken();
    }

    public synchronized void removeUser(String username) {
        usernamesToUserMap.remove(username);
    }

    public synchronized Set<User> getUsers() {
        Set<User> usersSet = new HashSet(usernamesToUserMap.values());
        return Collections.unmodifiableSet(usersSet);
    }

    public boolean isUserExists(String username) {
        return usernamesToUserMap.containsKey(username);
    }

    public User getUserByName(String username) {
        return usernamesToUserMap.get(username);
    }
    public AllyTeamData getAllieTeamDataByName(String username) {
        return usernamesToAlliesMap.get(username);
    }
    public void addUser(String username, String userType, String threadNum, String taskSize) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setType(UserType.getByName(userType));
        usernamesToUserMap.put(username,newUser);
        authTokenToUserMap.put(newUser.getToken(),newUser);
        newUser.setInARoom(false);
        if(UserType.getByName(userType).equals(UserType.AGENT)){
            AgentData agentData = new AgentData();
            agentData.setName(username);
            agentData.setNumberOfTasksThatTakes(Integer.valueOf(taskSize));
            agentData.setNumberOfThreads(Integer.valueOf(threadNum));
            usernamesToAgentsMap.put(username, agentData);
        }
    }
    public List<AllyTeamData> getAllALies() {
        List<AllyTeamData> allyTeamDataList = new ArrayList<>();
        allyTeamDataList.addAll(usernamesToAlliesMap.values());
        return allyTeamDataList;
    }

}
