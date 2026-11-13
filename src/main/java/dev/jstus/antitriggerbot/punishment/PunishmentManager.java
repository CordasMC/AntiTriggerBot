package dev.jstus.antitriggerbot.punishment;

import dev.jstus.antitriggerbot.AntiTriggerBotPlugin;
import dev.jstus.antitriggerbot.check.Check;
import dev.jstus.antitriggerbot.manager.AlertsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class PunishmentManager {
	private final AlertsManager alertsManager;

	public PunishmentManager(AlertsManager alertManager) {
		this.alertsManager = alertManager;
	}

	public void punish(Player player, Check check) {
		List<String> punishmentCommands = check.getConfig().getPunishmentCommands();
		applyPlaceholders(punishmentCommands, player);

		for (String command : punishmentCommands) {
			Bukkit.getScheduler().runTask(
					AntiTriggerBotPlugin.getInstance(),
					() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
			);
		}
	}

	private void applyPlaceholders(List<String> commands, Player player) {
		for (String command : commands) {
			commands.remove(command);
			commands.add(command.replace("%player%", player.getName()));
		}
	}
}
