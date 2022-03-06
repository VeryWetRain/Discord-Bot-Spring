package com.ryankim.DiscordBotSpring.listeners.general;

import com.ryankim.DiscordBotSpring.listeners.base.ServerCommand;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.stereotype.Service;

@Service
public class PingCmd extends ServerCommand {

    public PingCmd() {
        super("ping", "");
    }

    @Override
    public void runCommand(MessageCreateEvent event, Server server, ServerTextChannel channel, User user, String[] args) {
        channel.sendMessage("Pong!");
    }
}
