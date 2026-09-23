package com.example.studenthubapp;

public class Project {

    private final String title;
    private final String dueDate;
    private boolean done;

    public Project(String title, String dueDate, boolean done) {
        this.title = title;
        this.dueDate = dueDate;
        this.done = done;
    }

    public String getTitle() {
        return title;
    }

    public String getDueDate() {
        return dueDate;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
