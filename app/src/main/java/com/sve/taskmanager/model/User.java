package com.sve.taskmanager.model;

public class User {

    private String mLogin;
    private String mName;

    private User() {}

    public User(String login, String name) {
        mLogin = login;
        mName = name;
    }

    public String getLogin() {
        return mLogin;
    }

    public void setLogin(String login) {
        mLogin = login;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof User)) return false;
        User user = (User) obj;
        return mLogin.equals(user.mLogin);
    }
}