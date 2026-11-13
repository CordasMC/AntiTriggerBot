package dev.jstus.antitriggerbot.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public abstract class CheckConfig {

	protected final ConfigurationSection config;
	private final boolean enabled;
	private final int maxViolations;
	private final List<String> punishmentCommands;

	public CheckConfig(ConfigurationSection section) {
		this.config = section;
		this.enabled = section.getBoolean("enabled", true);
		this.maxViolations = section.getInt("max-violations", 3);
		this.punishmentCommands = section.getStringList("punish");
	}

	public boolean isEnabled() {
		return enabled;
	}

	public int getMaxViolations() {
		return maxViolations;
	}

	public List<String> getPunishmentCommands() {
		return punishmentCommands;
	}
}
