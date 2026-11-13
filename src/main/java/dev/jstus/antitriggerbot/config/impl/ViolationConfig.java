package dev.jstus.antitriggerbot.config.impl;

import dev.jstus.antitriggerbot.config.Config;
import org.bukkit.configuration.ConfigurationSection;

public class ViolationConfig extends Config {

	private final long expiryTimeMs;

	public ViolationConfig(ConfigurationSection section) {
		super(section);

		this.expiryTimeMs = section.getLong("expiry");
	}

	public long getExpiryTimeMs() {
		return this.expiryTimeMs;
	}
}
