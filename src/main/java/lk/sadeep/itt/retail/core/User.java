package lk.sadeep.itt.retail.core;

import lk.sadeep.itt.retail.core.constants.UserType;

import java.util.*;

public class User {

    private Long userId;
    private String username;
    private String password;

    private UserType userType;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    private static List<User> userList = new ArrayList<>();

    public static Optional<User> createUser(User newUser) {
        Long newUserId = 1l;

        newUser.setUserId(newUserId);

        if(!userList.isEmpty()) {
            User userWithMaxId = Collections.max(userList, Comparator.comparing(user -> user.getUserId()));

            if(userWithMaxId != null) {
                newUserId = userWithMaxId.getUserId() + 1;
                newUser.setUserId(newUserId);
            }
        }

        userList.add(newUser);

        return findUserById(newUserId);
    }

    public static Optional<User> findUserById(Long userId) {
        return userList.stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst();
    }

    public static Optional<User> checkCredentials(String username, String password) {
        return userList.stream()
                .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
                .findFirst();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}
