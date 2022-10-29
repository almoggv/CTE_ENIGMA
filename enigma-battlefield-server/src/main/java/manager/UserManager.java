package manager;

import dto.AgentData;
import dto.AllyTeamData;
import manager.impl.AllyClientDMImpl;
import model.Ally;
import model.ContestRoom;
import model.Uboat;
import model.User;
import enums.UserType;

import java.util.*;

public class UserManager {
    private final Map<String, User> usernamesToUserMap;
    private final Map<String, User> authTokenToUserMap;
    private final Map<String, Ally> usernamesToAlliesMap;
    private final Map<String, AgentData> usernamesToAgentsMap;
    private final Map<String, Uboat> usernamesToUboatsMap;
    public UserManager() {
        usernamesToUserMap = new HashMap<String, User>();
        authTokenToUserMap = new HashMap<String, User>();
        usernamesToAlliesMap = new HashMap<String, Ally>();
        usernamesToAgentsMap = new HashMap<String, AgentData>();
        usernamesToUboatsMap = new HashMap<String, Uboat>();
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
        if(UserType.getByName(userType).equals(UserType.UBOAT)){
            Uboat uboat = new Uboat();
            uboat.setName(username);
            usernamesToUboatsMap.put(username,uboat);
        }
        if(UserType.getByName(userType).equals(UserType.ALLY)){
            Ally ally = new Ally();
            ally.setTeamName(username);
            ally.setNumOfAgents(0);
            ally.setAgentsList(new ArrayList<>());
            ally.setEncryptionCandidateList(new ArrayList<>());
            ally.setDecryptionManager(new AllyClientDMImpl());
            usernamesToAlliesMap.put(username, ally);
        }
    }

    public synchronized String getUserToken(String username){
        return usernamesToUserMap.getOrDefault(username,new User()).getToken();
    }

    public synchronized void removeUser(String username, RoomManager roomManager) {
        User userToLogout = usernamesToUserMap.get(username);
        UserType type = userToLogout.getType();

        switch (type){
            case UBOAT:
                if(userToLogout.isInARoom()) {
                    roomManager.resetContestRoom(userToLogout.getContestRoom(), this);
                    roomManager.removeRoom(userToLogout.getContestRoom().getName());
//                    ContestRoom contestRoom
                }
                usernamesToUboatsMap.remove(username);
                break;
            case ALLY:
                Ally ally = usernamesToAlliesMap.get(username);
                if(userToLogout.isInARoom()){
                    userToLogout.getContestRoom().getAlliesList().remove(ally);
                }
                usernamesToAlliesMap.remove(username);
                //need to tell the agents and close them
                break;
            case AGENT:
                AgentData agent = usernamesToAgentsMap.get(username);
                usernamesToAlliesMap.get(agent.getAllyName()).getAgentsList().remove(agent);
                usernamesToAgentsMap.remove(username);
                break;
        }

        usernamesToUserMap.remove(username);
        username = "userrrrrrr";
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
    public Ally getAllyByName(String username) {
        return usernamesToAlliesMap.get(username);
    }
    public AllyTeamData getAllyTeamDTOByName(String username) {
        Ally ally = getAllyByName(username);
        AllyTeamData allyTeamData = new AllyTeamData(ally.getTeamName(), ally.getTaskSize(), ally.getNumOfAgents());
        return allyTeamData;
    }
    public AgentData getAgentByName(String username) {
        return usernamesToAgentsMap.get(username);
    }
    public Uboat getUboatByName(String username) {
        return usernamesToUboatsMap.get(username);
    }
    public void addUser(String username, String userType, String threadNum, String taskSize, String allyName) {
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
            agentData.setAllyName(allyName);
            Ally ally = getAllyByName(allyName);
            if(!getUserByName(allyName).isReady()) {
                newUser.setContestRoom(getUserByName(allyName).getContestRoom());
            }
            if(newUser.getContestRoom() != null){
                newUser.setInARoom(true);
            }
            //connect ally to agent
            ally.getAgentsList().add(agentData);
            ally.setNumOfAgents(ally.getNumOfAgents() + 1);
            usernamesToAgentsMap.put(username, agentData);
        }
    }
    public List<AllyTeamData> getAllALies() {
        List<AllyTeamData> allyTeamDataList = new ArrayList<>();
        for (Ally ally : usernamesToAlliesMap.values()) {
            allyTeamDataList.add(new AllyTeamData(ally.getTeamName(), ally.getTaskSize(), ally.getNumOfAgents()));
        }
        return allyTeamDataList;
    }

    public AllyTeamData makeAllyDataFromAlly(Ally ally){
        return new AllyTeamData(ally.getTeamName(), ally.getTaskSize(), ally.getNumOfAgents());
    }

    public List<AllyTeamData> allyTeamDataFromAllis(List<Ally> allysList){
        List<AllyTeamData> allyTeamDataList = new ArrayList<>();

        for (Ally ally : allysList) {
            allyTeamDataList.add(makeAllyDataFromAlly(ally));
        }
        return allyTeamDataList;

    }

}
