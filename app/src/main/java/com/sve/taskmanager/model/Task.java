package com.sve.taskmanager.model;

import java.util.Date;

public class Task {

    private Long id;
    private String title;
    private Date date;
    private boolean solved;

    private String customer;
    private String executor;

    /*public Task() {
        this(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
    }*/

    public Task() {}

    public Task(Long id) {
        this.id = id;
        date = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Task)) return false;
        Task task = (Task) obj;
        return id.equals(task.id);
    }
}
