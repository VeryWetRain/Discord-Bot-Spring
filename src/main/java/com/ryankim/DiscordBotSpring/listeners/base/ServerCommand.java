package com.ryankim.DiscordBotSpring.listeners.base;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageDecoration;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.springframework.beans.factory.annotation.Value;

public abstract class ServerCommand implements MessageCreateListener {
    @Value("${prefix}")
    private String prefix;
    private final String command;
    private final String argsFormat;

    protected ServerCommand(String command, String argsFormat) {
        this.command = command;
        this.argsFormat = argsFormat;
    }

    protected void sendErrorFormatMsg(ServerTextChannel channel) {
        new MessageBuilder()
                .append("The correct format for the ")
                .append(command, MessageDecoration.CODE_SIMPLE)
                .append(" command is ")
                .append(String.format("%s%s %s", prefix, command, argsFormat), MessageDecoration.CODE_SIMPLE)
                .send(channel);
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (event.getMessageContent().startsWith(prefix + command)) {
            event.getServer().ifPresent(server -> event.getMessageAuthor().asUser().ifPresent(user ->
                    event.getServerTextChannel().ifPresent(serverTextChannel ->
                            runCommand(event, server, serverTextChannel, user, event.getMessageContent().split(" ")))));
        }
    }

    protected abstract void runCommand(MessageCreateEvent event, Server server, ServerTextChannel channel, User user, String[] args);

    public String toString() {
        return String.format("%s: \t\t\t%s%s %s", command, prefix, command, argsFormat);
    }
}
