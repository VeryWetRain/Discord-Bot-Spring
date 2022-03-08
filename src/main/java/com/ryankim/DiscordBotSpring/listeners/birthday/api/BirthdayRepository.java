package com.ryankim.DiscordBotSpring.listeners.birthday.api;

import com.ryankim.DiscordBotSpring.listeners.birthday.model.BirthdayEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface BirthdayRepository extends MongoRepository<BirthdayEntry, String> {

    @Query("{name:'?0'}")
    BirthdayEntry findBirthdayByName(String name);

    public long count();
}
