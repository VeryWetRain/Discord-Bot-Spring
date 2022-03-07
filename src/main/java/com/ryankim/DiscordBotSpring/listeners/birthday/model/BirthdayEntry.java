package com.ryankim.DiscordBotSpring.listeners.birthday.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.SimpleDateFormat;
import java.util.Date;

@Document("bdays")
public class BirthdayEntry {
    @Id
    private String name;
    private Date birthday;

    public BirthdayEntry(String name, Date birthday) {
        this.name = name;
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return String.format("%s: %s", this.name, dateFormat.format(this.birthday));
    }
}
