package com.ryankim.DiscordBotSpring.listeners.general;

import com.ryankim.DiscordBotSpring.listeners.base.ServerCommand;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UptimeCmd extends ServerCommand {
    private long startTime;

    public UptimeCmd() {
        super("uptime", "");
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void runCommand(MessageCreateEvent event, Server server, ServerTextChannel channel, User user, String[] args) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;

        long hours = TimeUnit.MILLISECONDS.toHours(elapsedTime);
        elapsedTime = elapsedTime - hours*(60*60*1000);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime);
        elapsedTime = elapsedTime - minutes*(60*1000);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime);

        channel.sendMessage(String.format("Bot Uptime: %d:%02d:%02d", hours, minutes, seconds));
    }
}
