package dev.jstus.antitriggerbot.violation;

import dev.jstus.antitriggerbot.AntiTriggerBotPlugin;
import dev.jstus.antitriggerbot.check.Check;
import dev.jstus.antitriggerbot.config.impl.ViolationConfig;
import dev.jstus.antitriggerbot.manager.AlertsManager;
import dev.jstus.antitriggerbot.punishment.PunishmentManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ViolationManager {
	private final PunishmentManager punishmentManager;
	private final AlertsManager alertsManager;
	private final ViolationConfig violationConfig;
	private final List<Violation> violations = new ArrayList<>();
	private final int cleanupTaskId;

	public ViolationManager(PunishmentManager punishmentManager, AlertsManager alertsManager, ViolationConfig violationConfig) {
		this.punishmentManager = punishmentManager;
		this.alertsManager = alertsManager;
		this.violationConfig = violationConfig;
		this.cleanupTaskId = startCleanupTask();
	}

	public void addViolation(Violation violation) {
		this.violations.add(violation);

		Player player = violation.getPlayer();
		Check check = violation.getCheck();

		shouldPunish(player, check);
		alertsManager.broadcastViolation(violation);
	}

	public int getCheckViolationCount(Class<? extends Check> check, Player player) {
		int count = 0;

		for (Violation violation : violations) {
			if (violation.getCheck().getClass().equals(check) && violation.getPlayer().equals(player) && !isExpired(violation)) {
				count++;
			}
		}

		return count;
	}

	public void stopCleanupTask() {
		Bukkit.getScheduler().cancelTask(this.cleanupTaskId);
	}

	public void resetViolations() {
		this.violations.clear();
	}

	private void shouldPunish(Player player, Check check) {
		// Execute the punishment if they failed more than they are allowed to
		if (getCheckViolationCount(check.getClass(), player) >= check.getConfig().getMaxViolations()) {
			punishmentManager.punish(player, check);

			// Reset their violations
			violations.removeIf(violation -> violation.getPlayer().equals(player));
		}
	}

	private boolean isExpired(Violation violation) {
		return (System.currentTimeMillis() - violation.getTimestamp()) > violationConfig.getExpiryTimeMs();
	}

	private int startCleanupTask() {
		return Bukkit.getScheduler().scheduleSyncRepeatingTask(AntiTriggerBotPlugin.instance, () -> {
			violations.removeIf(this::isExpired);
		}, 0L, 1000L);
	}
}
