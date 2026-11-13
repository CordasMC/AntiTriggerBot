package dev.jstus.antitriggerbot.check;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;

public interface PacketEventCheck {
	void processPacketReceived(PacketReceiveEvent event);
}
