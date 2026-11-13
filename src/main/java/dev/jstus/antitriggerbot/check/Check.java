package dev.jstus.antitriggerbot.check;

import dev.jstus.antitriggerbot.config.CheckConfig;
import dev.jstus.antitriggerbot.violation.Violation;
import dev.jstus.antitriggerbot.violation.ViolationManager;
import org.bukkit.entity.Player;

public abstract class Check {
	protected final CheckConfig config;
	protected final ViolationManager violationManager;

	public Check(CheckConfig config, ViolationManager violationManager) {
		this.config = config;
		this.violationManager = violationManager;
	}

	public abstract String getName();

	public void fail(Player player) {
		violationManager.addViolation(new Violation(player, this));
	}

	public CheckConfig getConfig() {
		return this.config;
	}
}
