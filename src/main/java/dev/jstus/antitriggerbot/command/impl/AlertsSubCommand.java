package dev.jstus.antitriggerbot.command.impl;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jstus.antitriggerbot.AntiTriggerBotPlugin;
import dev.jstus.antitriggerbot.config.ConfigManager;
import dev.jstus.antitriggerbot.manager.AlertsManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AlertsSubCommand {

	private final AlertsManager alertsManager;
	private final ConfigManager configManager;

	public AlertsSubCommand(AntiTriggerBotPlugin plugin) {
		this.alertsManager = plugin.getAlertsManager();
		this.configManager = plugin.getConfigManager();
	}

	public CommandAPICommand load() {
		return new CommandAPICommand("alerts")
				.withPermission("antitriggerbot.alerts")
				.withAliases("verbose")
				.executesPlayer(this::handlePlayerCommand);
	}

	private void handlePlayerCommand(Player player, CommandArguments args) {
		alertsManager.toggleAlerts(player);

		player.sendRichMessage(configManager.getMessagesConfig().getAlertsToggledMessage());
	}
}
