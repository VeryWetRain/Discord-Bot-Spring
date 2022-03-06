package com.ryankim.DiscordBotSpring.listeners.music.commands;

import com.ryankim.DiscordBotSpring.listeners.base.ServerCommand;
import com.ryankim.DiscordBotSpring.listeners.music.audio.AudioManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
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
import java.util.stream.Collectors;

@Service
public class NpCmd extends ServerCommand {

    public NpCmd() {
        super("np", "");
    }

    private void sendNpFormatMsg(ServerTextChannel channel, AudioTrack audioTrack) {
        long m_full = TimeUnit.MILLISECONDS.toMinutes(audioTrack.getInfo().length);
        long s_full = m_full % 60;
        String fullLengthFormat = String.format("%d:%02d", m_full, s_full);

        long m_curr = TimeUnit.MILLISECONDS.toMinutes(audioTrack.getPosition());
        long s_curr = m_curr % 60;
        String currLengthFormat = String.format("%d:%02d", m_full, s_full);

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

        char[] progressBar = "|--------------------|".toCharArray();
        char circle = '⬤';
        int idx = (int)Math.round(((double)audioTrack.getPosition() / audioTrack.getDuration())*20) + 1;
        progressBar[idx] = circle;
        String finalProgressBar = String.format("%s %s %s", currLengthFormat, new String(progressBar), fullLengthFormat);

        new MessageBuilder()
                .setEmbed(new EmbedBuilder()
                        .setThumbnail(thumbnailURL)
                        .addField("Currently Playing:", audioTrack.getInfo().title)
                        .setUrl(audioTrack.getInfo().uri)
                        .addField("By", audioTrack.getInfo().author)
                        .addField("Playback Position", finalProgressBar)
                        .setColor(Color.green))
                .send(channel);
    }

    @Override
    protected void runCommand(MessageCreateEvent event, Server server, ServerTextChannel channel, User user, String[] args) {
        // check if there is any audio connection
        server.getAudioConnection().ifPresentOrElse(connection -> {
            AudioTrack audioTrack = AudioManager.get(server.getId()).player.getPlayingTrack();
            if(audioTrack != null) {
                sendNpFormatMsg(channel, audioTrack);
            } else {
                channel.sendMessage("There are no songs currently playing.");
            }
        }, () -> channel.sendMessage("The bot doesn't seem to be in any voice channel."));
    }
}
