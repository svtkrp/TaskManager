package com.sve.taskmanager;

import java.util.Date;
import java.util.UUID;

public class Task {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;

    private String mUser;

    public Task() {
        this(UUID.randomUUID());
    }

    public Task(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getUser() {
        return mUser;
    }

    public void setUser(String user) {
        mUser = user;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Task)) return false;
        Task task = (Task) obj;
        return mId.equals(task.mId);
    }
}