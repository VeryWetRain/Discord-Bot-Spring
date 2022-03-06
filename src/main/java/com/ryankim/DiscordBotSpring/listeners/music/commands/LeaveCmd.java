package com.ryankim.DiscordBotSpring.listeners.music.commands;

import com.ryankim.DiscordBotSpring.listeners.base.ServerCommand;
import com.ryankim.DiscordBotSpring.listeners.music.audio.AudioManager;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.stereotype.Service;

@Service
public class LeaveCmd extends ServerCommand {

    public LeaveCmd() {
        super("leave", "");
    }

    @Override
    protected void runCommand(MessageCreateEvent event, Server server, ServerTextChannel channel, User user, String[] args) {
        // check if bot is connected to any voice channel
        server.getConnectedVoiceChannel(event.getApi().getYourself()).ifPresentOrElse(voiceChannel -> {
            server.getAudioConnection().ifPresentOrElse(connection -> {
                AudioManager.get(server.getId()).player.stopTrack();
                connection.close();
            }, () -> event.getChannel().sendMessage("The bot doesn't seem to be in any voice channel."));
        }, () -> event.getChannel().sendMessage("The bot doesn't seem to be in any voice channel."));
    }
}
