package com.ryankim.DiscordBotSpring.listeners.music.commands;

import com.ryankim.DiscordBotSpring.listeners.base.ServerCommand;
import com.ryankim.DiscordBotSpring.listeners.music.audio.AudioManager;
import com.ryankim.DiscordBotSpring.listeners.music.audio.LavaplayerAudioSource;
import com.ryankim.DiscordBotSpring.listeners.music.audio.PlayerManager;
import com.ryankim.DiscordBotSpring.listeners.music.audio.ServerMusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.stereotype.Service;


import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
public class PlayCmd extends ServerCommand {
    private final AudioPlayerManager manager = PlayerManager.getManager();

    public PlayCmd() {
        super("play", "<youtube url | search terms>");
    }

    @Override
    protected void runCommand(MessageCreateEvent event, Server server, ServerTextChannel channel, User user, String[] args) {
        //play https://youtube.com/...
        //play darude sandstorm
        if (args.length > 1) {

            // check if user is in any voice channel
            event.getMessageAuthor().getConnectedVoiceChannel().ifPresentOrElse(voiceChannel -> {
                // see that user is in channel, but do we have perms for the channel
                if (voiceChannel.canYouConnect() && voiceChannel.canYouSee() && voiceChannel.hasPermission(event.getApi().getYourself(), PermissionType.SPEAK)) {
                    // we get ServerMusicManager from the AudioManager class which will create if doesn't exist
                    ServerMusicManager serverMusicManager = AudioManager.get(server.getId());

                    // we get url or query
                    String query = event.getMessageContent().replace(args[0] + " ", "");

                    // if bot not already connected to channel
                    if (!voiceChannel.isConnected(event.getApi().getYourself()) && server.getAudioConnection().isEmpty()) {
                        voiceChannel.connect().thenAccept(audioConnection -> {
                            AudioSource audio = new LavaplayerAudioSource(event.getApi(), serverMusicManager.player);
                            audioConnection.setAudioSource(audio);
                            audioConnection.setSelfDeafened(true);
                            serverMusicManager.player.setVolume(10);

                            // plays music
                            play(query, channel, serverMusicManager);
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

                                //plays music
                                play(query, channel, serverMusicManager);
                            } else {
                                channel.sendMessage("You are not connected to the same channel as the bot.");
                            }
                        });
                    }
                } else {
                    channel.sendMessage("I cannot connect, cannot see, or do not have the permissions to speak on the channel.");
                }
            }, () -> channel.sendMessage("You are not connected to any voice channel."));
        } else {
            sendErrorFormatMsg(channel);
        }
    }

    private void sendFormattedSong(ServerTextChannel channel, AudioTrack audioTrack, int queueNumber) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(audioTrack.getInfo().length);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(audioTrack.getInfo().length) % 60;
        String lengthFormat = String.format("%d:%02d", minutes, seconds);

        //https://img.youtube.com/vi/<insert-youtube-video-id-here>/mqdefault.jpg
        String thumbnailURL;
        try {
            List<NameValuePair> params = URLEncodedUtils.parse(new URI(audioTrack.getInfo().uri), StandardCharsets.UTF_8);
            Map<String, String> paramsMapped = params.stream().collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
            String v = paramsMapped.get("v");
            thumbnailURL = "https://img.youtube.com/vi/" + v + "/mqdefault.jpg";
        } catch (URISyntaxException e) {
            e.printStackTrace();
            thumbnailURL = "https://img.youtube.com/vi/";
        }

        new MessageBuilder()
                .setEmbed(new EmbedBuilder()
                        .setAuthor("Added to queue")
                        .setTitle(audioTrack.getInfo().title)
                        .setUrl(audioTrack.getInfo().uri)
                        .setThumbnail(thumbnailURL)
                        .addInlineField("Channel", audioTrack.getInfo().author)
                        .addInlineField("Song Duration", lengthFormat)
                        .addInlineField("Position in Queue", Integer.toString(queueNumber))
                        .setColor(Color.red))
                .send(channel);
    }

    private void play(String query, ServerTextChannel channel, ServerMusicManager serverMusicManager) {
        // load the track, check if url, otherwise use youtube search on the query
        manager.loadItemOrdered(serverMusicManager, isYoutubeUrl(query) ? query : "ytsearch: " + query, new FunctionalResultHandler(audioTrack -> {
            sendFormattedSong(channel, audioTrack, serverMusicManager.scheduler.queueSize());
            serverMusicManager.scheduler.queue(audioTrack);
        }, audioPlaylist -> {
            // if url is just a search result, just load first song
            if (audioPlaylist.isSearchResult()) {
                sendFormattedSong(channel, audioPlaylist.getTracks().get(0), serverMusicManager.scheduler.queueSize());
                serverMusicManager.scheduler.queue(audioPlaylist.getTracks().get(0));
            } else {
                // else just queue every track
                audioPlaylist.getTracks().forEach(serverMusicManager.scheduler::queue);
                sendFormattedSong(channel, audioPlaylist.getTracks().get(0), serverMusicManager.scheduler.queueSize());
                channel.sendMessage(String.format("Enqueued %d songs", audioPlaylist.getTracks().size()));
            }
        }, () -> {
            channel.sendMessage("We could not find the track.");
        }, e -> {
            channel.sendMessage("We could not play the track: " + e.getMessage());
        }));
    }

    private boolean isYoutubeUrl(String arg) {
        return Pattern.compile("^((?:https?:)?\\/\\/)?((?:www|m)\\.)?((?:youtube\\.com|youtu.be))(\\/(?:[\\w\\-]+\\?v=|embed\\/|v\\/)?)([\\w\\-]+)(\\S+)?").matcher(arg).matches();
    }
}
