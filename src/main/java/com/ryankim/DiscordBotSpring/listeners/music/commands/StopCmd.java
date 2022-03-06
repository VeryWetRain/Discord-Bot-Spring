package com.ryankim.DiscordBotSpring.listeners.music.commands;

import com.ryankim.DiscordBotSpring.listeners.base.ServerCommand;
import com.ryankim.DiscordBotSpring.listeners.music.audio.AudioManager;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.stereotype.Service;

@Service
public class StopCmd extends ServerCommand {

    public StopCmd() {
        super("stop", "");
    }

    @Override
    protected void runCommand(MessageCreateEvent event, Server server, ServerTextChannel channel, User user, String[] args) {
        // check if there is any audio connection
        server.getAudioConnection().ifPresentOrElse(connection -> {
            AudioManager.get(server.getId()).player.stopTrack();
            channel.sendMessage("The track has been stopped.");
        }, () -> channel.sendMessage("The bot doesn't seem to be in any voice channel."));
    }
}
