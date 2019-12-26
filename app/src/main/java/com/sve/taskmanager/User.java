package com.sve.taskmanager;

import java.util.UUID;

public class User {
    private UUID mId;
    private String mName;

    public User() {
        this(UUID.randomUUID());
    }

    public User(UUID id) {
        mId = id;
    }

    public UUID getId() {
        return mId;
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
        return mId.equals(user.mId);
    }
}