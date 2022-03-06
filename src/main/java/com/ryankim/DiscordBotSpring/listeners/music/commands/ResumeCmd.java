package com.ryankim.DiscordBotSpring.listeners.music.commands;

import com.ryankim.DiscordBotSpring.listeners.base.ServerCommand;
import com.ryankim.DiscordBotSpring.listeners.music.audio.AudioManager;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.stereotype.Service;

@Service
public class ResumeCmd extends ServerCommand {

    public ResumeCmd() {
        super("resume", "");
    }

    @Override
    protected void runCommand(MessageCreateEvent event, Server server, ServerTextChannel channel, User user, String[] args) {
        // check if there is any audio connection
        server.getAudioConnection().ifPresentOrElse(connection -> {
            if(AudioManager.get(server.getId()).player.isPaused()) {
                AudioManager.get(server.getId()).player.setPaused(false);
                channel.sendMessage("The song has been resumed.");
            } else {
                channel.sendMessage("The song is already playing.");
            }
        }, () -> channel.sendMessage("The bot doesn't seem to be in any voice channel."));
    }
}
