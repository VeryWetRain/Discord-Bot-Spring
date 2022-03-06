package com.ryankim.DiscordBotSpring;

import com.ryankim.DiscordBotSpring.listeners.music.audio.PlayerManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiscordBotSpringApplication implements CommandLineRunner {

	public static void main(String[] args) {
		PlayerManager.init();
		SpringApplication.run(DiscordBotSpringApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Thread.currentThread().join();
	}
}
