package dev.jstus.antitriggerbot.event;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.ConnectionState;
import dev.jstus.antitriggerbot.check.CheckManager;

public class CheckManagerPacketListener extends PacketListenerAbstract {

	private final CheckManager checkManager;

	public CheckManagerPacketListener(CheckManager checkManager) {
		this.checkManager = checkManager;
	}

	@Override
	public void onPacketReceive(final PacketReceiveEvent event) {
		if (event.getConnectionState() != ConnectionState.PLAY) {
			if (event.getConnectionState() != ConnectionState.CONFIGURATION) return;
			checkManager.onPacketReceive(event);
			return;
		}

		checkManager.onPacketReceive(event);
	}
}
