package dev.jstus.antitriggerbot.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jstus.antitriggerbot.AntiTriggerBotPlugin;
import dev.jstus.antitriggerbot.command.impl.AlertsSubCommand;
import dev.jstus.antitriggerbot.command.impl.ReloadSubCommand;
import dev.jstus.antitriggerbot.command.impl.ResetViolationsCommand;

public class AntiTriggerBotCommand {

	public AntiTriggerBotCommand(AntiTriggerBotPlugin plugin) {
		load(plugin);
	}

	private void load(AntiTriggerBotPlugin plugin) {
		new CommandAPICommand("antitriggerbot")
				.withAliases("atb")
				.withSubcommands(
						new AlertsSubCommand(plugin).load(),
						new ReloadSubCommand(plugin).load(),
						new ResetViolationsCommand(plugin).load()
				)
				.executes((sender, args) -> {
					sender.sendRichMessage("<green>AntiTriggerBot, written by 'Cordas' on discord!");
				})
				.register();
	}
}
