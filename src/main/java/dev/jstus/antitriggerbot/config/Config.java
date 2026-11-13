package dev.jstus.antitriggerbot.config;

import org.bukkit.configuration.ConfigurationSection;

public abstract class Config {
	protected final ConfigurationSection section;

	public Config(ConfigurationSection section) {
		this.section = section;
	}
}
