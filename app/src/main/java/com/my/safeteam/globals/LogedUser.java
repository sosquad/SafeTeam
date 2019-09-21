package com.my.safeteam.globals;

import com.my.safeteam.DB.User;

public class LogedUser {
    private static final LogedUser ourInstance = new LogedUser();
    private User user;
    private String currentUserUid;

    public String getCurrentUserUid() {
        return currentUserUid;
    }

    public void setCurrentUserUid(String currentUserUid) {
        this.currentUserUid = currentUserUid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static LogedUser getInstance() {
        return ourInstance;
    }

    private LogedUser() {
    }
}
