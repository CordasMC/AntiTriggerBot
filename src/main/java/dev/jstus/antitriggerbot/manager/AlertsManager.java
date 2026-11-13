package dev.jstus.antitriggerbot.manager;

import dev.jstus.antitriggerbot.config.ConfigManager;
import dev.jstus.antitriggerbot.violation.Violation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class AlertsManager {
	private final ConfigManager configManager;
	private final HashMap<Player, Boolean> alertsEnabled = new HashMap<>();

	public AlertsManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public void toggleAlerts(Player player) {
		if (!player.hasPermission("antitriggerbot.alerts")) return;

		boolean willEnable;

		// First time running the command
		if (!alertsEnabled.containsKey(player)) {
			// First run will disable alerts = alerts are enabled by default
			willEnable = false;
		} else {
			willEnable = !alertsEnabled.get(player);
		}

		if (willEnable) {
			alertsEnabled.put(player, true);
		} else {
			alertsEnabled.put(player, false);
		}
	}

	public void broadcastViolation(Violation violation) {
		String message = createViolationMessage(violation);

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!player.hasPermission("antitriggerbot.alerts")) continue;
			if (alertsEnabled.containsKey(player) && !alertsEnabled.get(player)) continue;

			player.sendRichMessage(message);
		}
	}

	private String createViolationMessage(Violation violation) {
		String message = configManager.getMessagesConfig().getViolationMessage();
		return applyPlaceholders(message, violation);
	}

	private String applyPlaceholders(String originalMessage, Violation violation) {
		String modifiedMessage;

		modifiedMessage = originalMessage.replace("%player%", violation.getPlayer().getName());
		modifiedMessage = modifiedMessage.replace("%check%", violation.getCheck().getName());

		return modifiedMessage;
	}
}
