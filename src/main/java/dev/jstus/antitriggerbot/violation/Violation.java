package dev.jstus.antitriggerbot.violation;

import dev.jstus.antitriggerbot.check.Check;
import org.bukkit.entity.Player;

public class Violation {
	private final Player player;
	private final Check check;
	private final long timestamp;

	public Violation(Player player, Check check) {
		this.player = player;
		this.check = check;
		timestamp = System.currentTimeMillis();
	}

	public Player getPlayer() {
		return this.player;
	}

	public Check getCheck() {
		return this.check;
	}

	public long getTimestamp() {
		return this.timestamp;
	}
}
