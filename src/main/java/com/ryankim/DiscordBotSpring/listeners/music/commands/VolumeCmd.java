package com.ryankim.DiscordBotSpring.listeners.music.commands;

import com.ryankim.DiscordBotSpring.listeners.base.ServerCommand;
import com.ryankim.DiscordBotSpring.listeners.music.audio.AudioManager;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.stereotype.Service;

@Service
public class VolumeCmd extends ServerCommand {

    public VolumeCmd() {
        super("volume", "[0-100]");
    }
    @Override
    protected void runCommand(MessageCreateEvent event, Server server, ServerTextChannel channel, User user, String[] args) {
        if(args.length > 1) {
            int volume;
            // try to parse arg
            try {
                volume = Integer.parseInt(args[1]);
                if(volume < 0 || volume > 100) throw new Exception("Out of range.");
            } catch(Exception ex) {
                sendErrorFormatMsg(channel);
                return;
            }

            // check if there is any audio connection
            server.getAudioConnection().ifPresent(connection -> {
                AudioManager.get(server.getId()).player.setVolume(volume);
                channel.sendMessage(String.format("The volume has been changed to %d.", volume));
            });
        } else {
            // check if there is any audio connection
            server.getAudioConnection().ifPresent(connection -> {
                channel.sendMessage(String.format("The volume is currently at %d.", AudioManager.get(server.getId()).player.getVolume()));
            });
        }
    }
}
