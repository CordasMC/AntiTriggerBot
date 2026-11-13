package dev.jstus.antitriggerbot.check;

import org.bukkit.entity.Player;

public interface PeriodicCheck {
	void processCheck(Player player);

	long getInterval();

	int getTaskId();

	void setTaskId(int TaskId);

	void cancelTask();


}
