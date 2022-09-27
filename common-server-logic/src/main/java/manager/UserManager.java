package main.java.manager;

import main.java.dto.User;

import java.util.*;


public class UserManager {
    private final Map<String,User> usernamesToUserMap;

    public UserManager() {
        usernamesToUserMap = new HashMap<>();
    }

    public synchronized void addUser(String username) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setToken(UUID.randomUUID().toString());
        usernamesToUserMap.put(username,newUser);
    }

    public synchronized String getUserToken(String username){
        return usernamesToUserMap.getOrDefault(username,new User()).getToken();
    }

    public synchronized void removeUser(String username) {
        usernamesToUserMap.remove(username);
    }

    public synchronized Set<User> getUsers() {
        Set<User> usersSet = new HashSet<>(usernamesToUserMap.values());
        return Collections.unmodifiableSet(usersSet);
    }

    public boolean isUserExists(String username) {
        return usernamesToUserMap.containsKey(username);
    }

}
