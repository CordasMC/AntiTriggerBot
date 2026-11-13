package dev.jstus.antitriggerbot.command.impl;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jstus.antitriggerbot.AntiTriggerBotPlugin;
import dev.jstus.antitriggerbot.config.ConfigManager;
import org.bukkit.command.CommandSender;

public class ReloadSubCommand {

	private final AntiTriggerBotPlugin plugin;
	private final ConfigManager configManager;

	public ReloadSubCommand(AntiTriggerBotPlugin plugin) {
		this.plugin = plugin;
		this.configManager = plugin.getConfigManager();
	}

	public CommandAPICommand load() {
		return new CommandAPICommand("reload")
				.withPermission("antitriggerbot.reload")
				.executes(this::handleCommand);
	}

	private void handleCommand(CommandSender sender, CommandArguments args) {
		plugin.reload();

		sender.sendRichMessage(configManager.getMessagesConfig().getPluginReloadedMessage());
	}
}
