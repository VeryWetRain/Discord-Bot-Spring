package com.ryankim.DiscordBotSpring.listeners.birthday;

import com.ryankim.DiscordBotSpring.listeners.base.ServerCommand;
import com.ryankim.DiscordBotSpring.listeners.birthday.crud.BirthdayRepository;
import com.ryankim.DiscordBotSpring.listeners.birthday.model.BirthdayEntry;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class BirthdayCmd extends ServerCommand {

    @Autowired
    BirthdayRepository birthdayRepo;

    //private final static Pattern choicePattern = Pattern.compile("\b(list|add|del)\b");
    //private final static Pattern namePattern = Pattern.compile("([^\\d\\W])");

    public BirthdayCmd() {
        super("birthday", "<list|add|del> [<name> <YYYY-MM-DD>]");
    }

    @Override
    protected void runCommand(MessageCreateEvent event, Server server, ServerTextChannel channel, User user, String[] args) {
        if (args.length > 1) {
            String choice = args[1];
            if (choice.equals("list")) {
                list(channel);
            } else if (choice.equals("add") && args.length >= 4) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    String name = args[2];
                    Date date = dateFormat.parse(args[3]);
                    add(channel, new BirthdayEntry(name, date));
                } catch (ParseException e) {
                    e.printStackTrace();
                    sendErrorFormatMsg(channel);
                }
            } else if (choice.equals("del") && args.length >= 3) {
                String name = args[2];
                delete(channel, name);
            } else {
                sendErrorFormatMsg(channel);
            }
        } else {
            sendErrorFormatMsg(channel);
        }
    }

    private void list(ServerTextChannel channel) {
        List<BirthdayEntry> birthdays = birthdayRepo.findAll();
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Birthday List");
        StringBuilder bdayList = new StringBuilder();
        birthdays.forEach(bday -> {
            bdayList.append(bday.toString());
            bdayList.append("\n");
        });
        embed.setDescription(bdayList.toString());
        channel.sendMessage(embed);
    }

    private void add(ServerTextChannel channel, BirthdayEntry birthdayEntry) {
        birthdayRepo.save(birthdayEntry);
        channel.sendMessage(String.format("%s's birthday has been added to the database if it doesn't already exist.", birthdayEntry.getName()));
    }

    private void delete(ServerTextChannel channel, String name) {
        birthdayRepo.deleteById(name);
        channel.sendMessage(String.format("%s's birthday has been removed from the database if it existed.", name));
    }
}
