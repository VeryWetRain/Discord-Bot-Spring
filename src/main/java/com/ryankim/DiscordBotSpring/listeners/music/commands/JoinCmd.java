package com.ryankim.DiscordBotSpring.listeners.music.commands;

import com.ryankim.DiscordBotSpring.listeners.base.ServerCommand;
import com.ryankim.DiscordBotSpring.listeners.music.audio.AudioManager;
import com.ryankim.DiscordBotSpring.listeners.music.audio.LavaplayerAudioSource;
import com.ryankim.DiscordBotSpring.listeners.music.audio.ServerMusicManager;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.stereotype.Service;

@Service
public class JoinCmd extends ServerCommand {

    public JoinCmd() {
        super("join", "");
    }

    @Override
    protected void runCommand(MessageCreateEvent event, Server server, ServerTextChannel channel, User user, String[] args) {
        // check if user is in any voice channel
        event.getMessageAuthor().getConnectedVoiceChannel().ifPresentOrElse(voiceChannel -> {
            // see that user is in channel, but do we have perms for the channel
            if (voiceChannel.canYouConnect() && voiceChannel.canYouSee() && voiceChannel.hasPermission(event.getApi().getYourself(), PermissionType.SPEAK)) {
                // we get ServerMusicManager from the AudioManager class which will create if doesn't exist
                ServerMusicManager serverMusicManager = AudioManager.get(server.getId());

                // we get url or query
                //String query = event.getMessageContent().replace(args[0] + " ", "");

                // if bot not already connected to channel
                if (!voiceChannel.isConnected(event.getApi().getYourself()) && server.getAudioConnection().isEmpty()) {
                    voiceChannel.connect().thenAccept(audioConnection -> {
                        AudioSource audio = new LavaplayerAudioSource(event.getApi(), serverMusicManager.player);
                        audioConnection.setAudioSource(audio);
                        audioConnection.setSelfDeafened(true);
                        serverMusicManager.player.setVolume(10);
                    });
                    //if bot is already connected to a voice channel
                } else if (server.getAudioConnection().isPresent()) {
                    // get audio connection
                    server.getAudioConnection().ifPresent(audioConnection -> {
                        // check to see if user is in same voice channel as bot
                        if (audioConnection.getChannel().getId() == voiceChannel.getId()) {
                            // create audio source and add to audio connection queue
                            AudioSource audio = new LavaplayerAudioSource(event.getApi(), serverMusicManager.player);
                            audioConnection.setAudioSource(audio);
                            audioConnection.setSelfDeafened(true);
                        } else {
                            channel.sendMessage("You are not connected to the same channel as the bot.");
                        }
                    });
                }
            } else {
                channel.sendMessage("I cannot connect, cannot see, or do not have the permissions to speak on the channel.");
            }
        }, () -> channel.sendMessage("You are not connected to any voice channel."));
    }
}
