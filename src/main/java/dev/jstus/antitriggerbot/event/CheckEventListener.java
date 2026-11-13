package dev.jstus.antitriggerbot.event;

import dev.jstus.antitriggerbot.check.CheckManager;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CheckEventListener implements Listener {
	private final CheckManager checkManager;

	public CheckEventListener(CheckManager checkManager) {
		this.checkManager = checkManager;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPrePlayerAttackEntity(PrePlayerAttackEntityEvent event) {
		checkManager.onEvent(event);
	}
}
