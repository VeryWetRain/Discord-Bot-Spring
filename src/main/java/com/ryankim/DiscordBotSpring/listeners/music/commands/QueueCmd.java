package com.ryankim.DiscordBotSpring.listeners.music.commands;

import com.ryankim.DiscordBotSpring.listeners.base.ServerCommand;
import com.ryankim.DiscordBotSpring.listeners.music.audio.AudioManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

public class QueueCmd extends ServerCommand {

    public QueueCmd() {
        super("queue", "");
    }

    @Override
    protected void runCommand(MessageCreateEvent event, Server server, ServerTextChannel channel, User user, String[] args) {
        // check if there is any audio connection
        server.getAudioConnection().ifPresentOrElse(connection -> {
            AudioTrack audioTrack = AudioManager.get(server.getId()).player.getPlayingTrack();
            if(audioTrack != null) {
                // TODO: 3/6/2022 write logic for queue command
            } else {
                channel.sendMessage("There are no songs currently playing.");
            }
        }, () -> channel.sendMessage("The bot doesn't seem to be in any voice channel."));
    }
}

