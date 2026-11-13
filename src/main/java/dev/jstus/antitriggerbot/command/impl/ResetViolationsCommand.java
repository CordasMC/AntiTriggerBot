package dev.jstus.antitriggerbot.command.impl;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jstus.antitriggerbot.AntiTriggerBotPlugin;
import dev.jstus.antitriggerbot.config.ConfigManager;
import org.bukkit.command.CommandSender;

public class ResetViolationsCommand {

	private final AntiTriggerBotPlugin plugin;
	private final ConfigManager configManager;

	public ResetViolationsCommand(AntiTriggerBotPlugin plugin) {
		this.plugin = plugin;
		this.configManager = plugin.getConfigManager();
	}

	public CommandAPICommand load() {
		return new CommandAPICommand("resetviolations")
				.withPermission("antitriggerbot.resetviolations")
				.executes(this::handleCommand);
	}

	private void handleCommand(CommandSender sender, CommandArguments args) {
		plugin.getViolationManager().resetViolations();

		sender.sendRichMessage(configManager.getMessagesConfig().getViolationsResetMessage());
	}
}
