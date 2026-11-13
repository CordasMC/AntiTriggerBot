package dev.jstus.antitriggerbot.config.impl;

import dev.jstus.antitriggerbot.config.Config;
import org.bukkit.configuration.ConfigurationSection;

public class MessagesConfig extends Config {

	private final String violationMessage;
	private final String alertsToggledMessage;
	private final String pluginReloadedMessage;
	private final String violationsResetMessage;

	public MessagesConfig(ConfigurationSection section) {
		super(section);

		this.violationMessage = section.getString("violation");
		this.alertsToggledMessage = section.getString("alerts-toggled");
		this.pluginReloadedMessage = section.getString("reloaded");
		this.violationsResetMessage = section.getString("violations-reset");
	}

	public String getViolationMessage() {
		return this.violationMessage;
	}

	public String getAlertsToggledMessage() {
		return this.alertsToggledMessage;
	}

	public String getPluginReloadedMessage() {
		return this.pluginReloadedMessage;
	}

	public String getViolationsResetMessage() {
		return this.violationsResetMessage;
	}
}
