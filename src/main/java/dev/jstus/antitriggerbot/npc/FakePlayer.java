	/*
	 * Class heavily adapted and re-written in Java from Flyte.gg's Winter Event:
	 * https://github.com/flytegg/christmas-plugin-2024/blob/master/src/main/kotlin/gg/flyte/christmas/npc/WorldNPC.kt
	 */
	package dev.jstus.antitriggerbot.npc;

	import com.github.retrooper.packetevents.PacketEvents;
	import com.github.retrooper.packetevents.protocol.attribute.Attributes;
	import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
	import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
	import com.github.retrooper.packetevents.protocol.npc.NPC;
	import com.github.retrooper.packetevents.protocol.player.TextureProperty;
	import com.github.retrooper.packetevents.protocol.player.UserProfile;
	import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
	import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes;
	import io.github.retrooper.packetevents.util.SpigotConversionUtil;
	import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
	import net.kyori.adventure.text.Component;
	import org.bukkit.Location;
	import org.bukkit.entity.Player;
	import org.jetbrains.annotations.NotNull;

	import java.util.Collections;
	import java.util.List;
	import java.util.UUID;
	import java.util.stream.Collectors;
	import java.util.stream.IntStream;

	public class FakePlayer {
		private final int id;
		private final NPC npc;
		private final Location location;
		private double scale = 0.5;

		private FakePlayer(Component displayName, List<TextureProperty> textureProperties, Location location) {
			this.id = SpigotReflectionUtil.generateEntityId();
			this.location = location;

			String uniqueName = IntStream.iterate(0, i -> i + 1)
					.limit(String.valueOf(id).length())
					.mapToObj(i -> "§" + String.valueOf(id).charAt(i))
					.collect(Collectors.joining());

			this.npc = new NPC(new UserProfile(UUID.randomUUID(), uniqueName, textureProperties), id);
			npc.setPrefixName(displayName);
		}


		public static FakePlayer createFromLive(Component displayName, Player modelAfter, Location location) {
			List<TextureProperty> textureProperties = PacketEvents.getAPI().getPlayerManager().getUser(modelAfter).getProfile().getTextureProperties();
			return new FakePlayer(displayName, textureProperties, location);
		}


		public void spawnFor(Player player, boolean isInvisible) {
			this.npc.setLocation(SpigotConversionUtil.fromBukkitLocation(this.location));
			// Prepare metadata
			EntityData skinLayer = new EntityData(17, EntityDataTypes.BYTE, (byte) 127);
			List<EntityData> metadataList;

			if (isInvisible) {
				// 0x20 = invisible flag
				EntityData invisibleFlag = new EntityData(0, EntityDataTypes.BYTE, (byte) 0x20);
				metadataList = List.of(invisibleFlag, skinLayer);
			} else {
				metadataList = List.of(skinLayer);
			}

			WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata(
					id, metadataList
			);

			try {
				this.npc.spawn(PacketEvents.getAPI().getPlayerManager().getUser(player).getChannel());
			} catch (Exception ignored) {
			}

			PacketEvents.getAPI().getProtocolManager().sendPacket(
					PacketEvents.getAPI().getPlayerManager().getUser(player).getChannel(), metadataPacket
			);

			WrapperPlayServerUpdateAttributes attributesPacket = getWrapperPlayServerUpdateAttributes();
			PacketEvents.getAPI().getProtocolManager().sendPacket(
					PacketEvents.getAPI().getPlayerManager().getUser(player).getChannel(), attributesPacket
			);
		}

		private @NotNull WrapperPlayServerUpdateAttributes getWrapperPlayServerUpdateAttributes() {
			WrapperPlayServerUpdateAttributes.PropertyModifier modifier = new WrapperPlayServerUpdateAttributes.PropertyModifier(
					Attributes.SCALE.getName(),
					scale,
					WrapperPlayServerUpdateAttributes.PropertyModifier.Operation.ADDITION
			);
			WrapperPlayServerUpdateAttributes.Property property = new WrapperPlayServerUpdateAttributes.Property(
					Attributes.GENERIC_SCALE,
					scale,
					Collections.singletonList(modifier)
			);
			WrapperPlayServerUpdateAttributes attributesPacket = new WrapperPlayServerUpdateAttributes(
					id, Collections.singletonList(property)
			);
			return attributesPacket;
		}

		public void remove() {
			this.npc.despawnAll();
		}

		public int getId() {
			return this.id;
		}

		public void setScale(double scale) {
			this.scale = scale;
		}
	}