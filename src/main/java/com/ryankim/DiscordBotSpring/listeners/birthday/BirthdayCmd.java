package com.ryankim.DiscordBotSpring.listeners.birthday;

import com.ryankim.DiscordBotSpring.listeners.base.ServerCommand;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

public class BirthdayCmd extends ServerCommand {

    public BirthdayCmd() {
        super("birthday", "[name] [mm:dd:yyyy]");
    }

    @Override
    protected void runCommand(MessageCreateEvent event, Server server, ServerTextChannel channel, User user, String[] args) {
        // TODO: 3/6/2022 write logic for birthday command and learn mongodb
    }
}
