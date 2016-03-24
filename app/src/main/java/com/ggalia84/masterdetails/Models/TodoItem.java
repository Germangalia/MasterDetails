package com.ggalia84.masterdetails.Models;

/**
 * Created by ggalia84 on 24/03/16.
 */
public class TodoItem {
    public String id;
    public String name;
    public String description;
    public boolean done;
    public int priority;

    public TodoItem(String id, String name, boolean done, int priority, String description) {
        this.id = id;
        this.name = name;
        this.done = done;
        this.priority = priority;
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }
}
