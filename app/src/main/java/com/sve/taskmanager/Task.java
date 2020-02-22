package com.sve.taskmanager;

import java.util.Date;
import java.util.UUID;

public class Task {

    private Long mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;

    private String mCustomer;
    private String mExecutor;

    public Task() {
        this(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
    }

    public Task(Long id) {
        mId = id;
        mDate = new Date();
    }

    public Long getId() {
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

    public String getCustomer() {
        return mCustomer;
    }

    public void setCustomer(String customer) {
        mCustomer = customer;
    }

    public String getExecutor() {
        return mExecutor;
    }

    public void setExecutor(String executor) {
        mExecutor = executor;
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
