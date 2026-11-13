package dev.jstus.antitriggerbot.check;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import dev.jstus.antitriggerbot.AntiTriggerBotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

public class CheckManager {
	private final List<PeriodicCheck> periodicChecks = new ArrayList<>();
	private final List<EventCheck> eventChecks = new ArrayList<>();
	private final List<PacketEventCheck> packetEventChecks = new ArrayList<>();

	public void registerCheck(Check check) {
		if (check instanceof PeriodicCheck) {
			registerPeriodicCheck((PeriodicCheck) check);
		}

		if (check instanceof EventCheck) {
			eventChecks.add((EventCheck) check);
		}

		if (check instanceof PacketEventCheck) {
			packetEventChecks.add((PacketEventCheck) check);
		}
	}


	public void unregisterAllChecks() {
		for (PeriodicCheck periodicCheck : new ArrayList<>(periodicChecks)) {
			periodicCheck.cancelTask();
		}
		periodicChecks.clear();
		eventChecks.clear();
		packetEventChecks.clear();
	}

	public void onEvent(Event event) {
		for (EventCheck eventCheck : eventChecks) {
			eventCheck.processEvent(event);
		}
	}

	public void onPacketReceive(PacketReceiveEvent event) {
		for (PacketEventCheck packetEventCheck : packetEventChecks) {
			packetEventCheck.processPacketReceived(event);
		}
	}


	private void registerPeriodicCheck(PeriodicCheck check) {
		periodicChecks.add(check);
		schedulePeriodicCheck(check);
	}

	private void schedulePeriodicCheck(PeriodicCheck check) {
		check.setTaskId(Bukkit.getScheduler().scheduleSyncRepeatingTask(AntiTriggerBotPlugin.instance, () -> {

			for (Player player : Bukkit.getOnlinePlayers()) {
				check.processCheck(player);
			}

		}, 0L, check.getInterval()));
	}
}
