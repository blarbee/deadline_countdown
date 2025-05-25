package com.example.deadline_countdown;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;
@Entity
public class Task implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    private String title;
    private String color;
    private String format;
    private String date_and_time;
    private String description = null;

    public Task(String title, String color, String format, String date_and_time){
        this.title = title;
        this.color = color;
        this.format = format;
        this.date_and_time = date_and_time;
    }

    public String getTitle(){ return title; }

    public String getColor() { return color;}

    public String getFormat(){ return format; }

    public String getDate_and_time(){ return date_and_time; }

    public String getDescription() { return description; }

    public void setDescription(String description) {
        this.description = description;
    }


}
