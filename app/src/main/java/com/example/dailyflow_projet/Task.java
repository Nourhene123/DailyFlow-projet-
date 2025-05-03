package com.example.dailyflow_projet;

public class Task {
    private String title;
    private String dueDate;
    private String priority;
    private boolean completed;
    private String userId;


    public Task() {
        this.title = "";
        this.dueDate = "";
        this.priority = "";
        this.completed = false;
        this.userId = "";
    }


    public Task(String title, String dueDate, String priority, boolean completed, String userId) {
        this.title = title;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = completed;
        this.userId = userId;
    }


    public String getTitle() {
        return title;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getUserId() {
        return userId;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}