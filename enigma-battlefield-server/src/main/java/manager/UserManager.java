package manager;

import dto.User;
import enums.UserType;

import java.util.*;

public class UserManager {
    private final Map<String, User> usernamesToUserMap;
    private final Map<String, User> authTokenToUserMap;

    public UserManager() {
        usernamesToUserMap = new HashMap<String, User>();
        authTokenToUserMap = new HashMap<String, User>();
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
}
