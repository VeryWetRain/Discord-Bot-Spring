package com.ryankim.DiscordBotSpring;

import com.ryankim.DiscordBotSpring.listeners.music.audio.PlayerManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class BotConfiguration {
    @Value("${token}")
    String token;

    @Bean
    @ConfigurationProperties(value = "discord-api")
    public DiscordApi discordApi(List<MessageCreateListener> eventListeners) {
        DiscordApi api = new DiscordApiBuilder()
                .setToken(token)
                .setAllNonPrivilegedIntentsExcept(Intent.GUILD_WEBHOOKS, Intent.DIRECT_MESSAGE_REACTIONS, Intent.DIRECT_MESSAGE_TYPING, Intent.DIRECT_MESSAGES)
                .login()
                .join();
        api.setMessageCacheSize(10, 60*60);

        System.out.println("~~~~~~~~~~ Initializing Command Listeners ~~~~~~~~~~");
        for(MessageCreateListener listener : eventListeners) {
            api.addListener(listener);
            System.out.println(listener.toString());
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        return api;
    }
}
