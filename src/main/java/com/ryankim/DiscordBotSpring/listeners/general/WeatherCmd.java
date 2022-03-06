package com.ryankim.DiscordBotSpring.listeners.general;

import com.ryankim.DiscordBotSpring.listeners.base.ServerCommand;
import net.aksingh.owmjapis.api.APIException;
import net.aksingh.owmjapis.core.OWM;
import net.aksingh.owmjapis.model.CurrentWeather;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WeatherCmd extends ServerCommand {
    @Value("${owm}")
    private OWM owm;
    private final static Pattern pattern = Pattern.compile("!weather ([a-zA-Z ]*)");

    public WeatherCmd() {
        super("weather", "<city name>");
    }

    @Override
    public void runCommand(MessageCreateEvent event, Server server, ServerTextChannel channel, User user, String[] args) {
        Matcher matcher = pattern.matcher(event.getMessageContent());
        if (matcher.matches()) {
            String city = matcher.group(1);
            //System.out.println("city: " + city);
            CurrentWeather cwd;
            try {
                owm.setUnit(OWM.Unit.IMPERIAL);
                cwd = owm.currentWeatherByCityName(city, OWM.Country.UNITED_STATES);
            } catch (APIException e) {
                e.printStackTrace();
                channel.sendMessage("City not found.");
                sendErrorFormatMsg(channel);
                return;
            }

            StringBuilder weatherData = new StringBuilder();
            weatherData.append(String.format("Temperature: %.2f/%.2f °F\n",
                    cwd.getMainData().getTempMax(),
                    cwd.getMainData().getTempMin()));
            weatherData.append(String.format("Humidity: %.2f %%\n", cwd.getMainData().getHumidity()));
            weatherData.append(String.format("Wind: %.2f mph\n", cwd.getWindData().getSpeed()));

            if (cwd.hasCloudData()) {
                weatherData.append(String.format("Cloud: %.2f\n", cwd.getCloudData().getCloud()));
            }
            if (cwd.hasRainData() && cwd.getRainData().hasPrecipVol3h()) {
                weatherData.append(String.format("Rain: %.2f\n", cwd.getRainData().getPrecipVol3h()));
            }
            if (cwd.hasSnowData()) {
                weatherData.append(String.format("Snow: %.2f in\n", cwd.getSnowData().getSnowVol3h()));
            }

            new MessageBuilder()
                    .setEmbed(new EmbedBuilder()
                            .setTitle("Weather for " + cwd.getCityName())
                            .setDescription(weatherData.toString())
                            .setFooter("pls don't spam me")
                            .setColor(Color.WHITE))
                    .send(channel);
        } else {
            sendErrorFormatMsg(channel);
        }
    }
}
