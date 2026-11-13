package dev.jstus.antitriggerbot.check.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import dev.jstus.antitriggerbot.AntiTriggerBotPlugin;
import dev.jstus.antitriggerbot.check.Check;
import dev.jstus.antitriggerbot.check.EventCheck;
import dev.jstus.antitriggerbot.check.PacketEventCheck;
import dev.jstus.antitriggerbot.check.PeriodicCheck;
import dev.jstus.antitriggerbot.config.impl.check.TriggerBotACheckConfig;
import dev.jstus.antitriggerbot.npc.FakePlayer;
import dev.jstus.antitriggerbot.violation.ViolationManager;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class TriggerBotACheck extends Check implements PeriodicCheck, EventCheck, PacketEventCheck {

	private final TriggerBotACheckConfig config;
	private final HashMap<Player, EntityBaitData> entityBaits = new HashMap();
	private final HashMap<Player, PlayerBaitData> playerBaits = new HashMap<>();

	int taskId = -1;

	public TriggerBotACheck(TriggerBotACheckConfig config, ViolationManager violationManager) {
		super(config, violationManager);

		this.config = config;
	}

	@Override
	public String getName() {
		return "TriggerBotA";
	}

	@Override
	public void processCheck(Player player) {
		if (!(isHoldingWeapon(player))) return;

		// They already have an active bait
		if (entityBaits.get(player) != null) return;

		// Is the entity to be spawned a player?
		if (config.getEntity().equals("PLAYER")) {
			handlePlayerCheck(player);
		} else {
			handleEntityCheck(player);
		}
	}

	@Override
	public void processPacketReceived(PacketReceiveEvent event) {
		// Is it an interact packet? If not, return
		if (!(event.getPacketType().equals(PacketType.Play.Client.INTERACT_ENTITY))) return;

		Player player = event.getPlayer();

		// Get the attacked entity
		WrapperPlayClientInteractEntity entity = new WrapperPlayClientInteractEntity(event);
		int entityId = entity.getEntityId();

		if (!isBaitFakePlayerOf(player, entityId)) return;

		// They attacked a bait player - fail them
		fail(player);
		removeBaitOf(player);
	}

	@Override
	public long getInterval() {
		return this.config.getInterval();
	}

	@Override
	public int getTaskId() {
		return this.taskId;
	}

	@Override
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	@Override
	public void cancelTask() {
		if (this.taskId != -1) {
			Bukkit.getScheduler().cancelTask(taskId);
			this.taskId = -1;
		}
	}

	@Override
	public void processEvent(Event event) {
		if (isFailedCheckEvent(event)) {
			handleFailedCheckEvent(event);
		}
	}

	private void handleFailedCheckEvent(Event event) {
		Player player = ((PrePlayerAttackEntityEvent) event).getPlayer();

		// Fail them, remove the bait
		fail(player);
		removeBaitOf(player);
	}

	private void handleEntityCheck(Player player) {
		// Spawn the bait and store it
		Entity bait = spawnBaitEntity(player);
		entityBaits.put(player, new EntityBaitData(bait, System.currentTimeMillis()));

		// Schedule for the bait to be removed
		Bukkit.getScheduler().runTaskLater(AntiTriggerBotPlugin.instance, () -> {
			if (bait.isValid()) {
				bait.remove();
				entityBaits.remove(player);
			}
		}, config.getDuration());

	}

	private void handlePlayerCheck(Player player) {
		if (shouldDisregardCheck(player)) return;
		// Get the location where the center of the spawned player needs to be at
		Location targetCenter = calculateBaitLocation(player);

		// Spawn the fake player with adjusted position
		double hitboxHeight = 1.8 * config.getPlayerScale();
		double adjustmentNeeded = (hitboxHeight / 2) * -1;

		FakePlayer playerBait = FakePlayer.createFromLive(Component.text(""), player, targetCenter.add(0, adjustmentNeeded, 0));

		playerBait.setScale(config.getPlayerScale());
		playerBait.spawnFor(player, true);

		playerBaits.put(player, new PlayerBaitData(playerBait, System.currentTimeMillis()));
		Bukkit.getScheduler().runTaskLater(AntiTriggerBotPlugin.instance, () -> {
			playerBait.remove();
			playerBaits.remove(player);
		}, config.getDuration());
	}

	private boolean shouldDisregardCheck(Player player) {
		Location eyeLoc = player.getEyeLocation();
		Vector direction = eyeLoc.getDirection().normalize();

		double offsetAngle = Math.toRadians(5);

		Vector left = direction.clone().rotateAroundY(-offsetAngle);
		Vector right = direction.clone().rotateAroundY(offsetAngle);

		double reach = config.getValidationRange();
		double checkRadius = 0.5;

		boolean foundLeft = isEntityInDirection(player, eyeLoc, left, reach, checkRadius);
		boolean foundRight = isEntityInDirection(player, eyeLoc, right, reach, checkRadius);

		return foundLeft || foundRight;
	}

	private boolean isEntityInDirection(Player player, Location origin, Vector direction, double reach, double radius) {
		Location checkLoc = origin.clone();
		for (double d = 0.5; d <= reach; d += 0.5) {
			checkLoc = origin.clone().add(direction.clone().multiply(d));
			for (Entity entity : player.getWorld().getNearbyEntities(checkLoc, radius, radius, radius)) {
				if (entity != player && !(entity instanceof FakePlayer)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isFailedCheckEvent(Event event) {
		if (!(event instanceof PrePlayerAttackEntityEvent attackEntityEvent)) return false;

		Player player = attackEntityEvent.getPlayer();
		Entity entity = attackEntityEvent.getAttacked();

		return isBaitEntityOf(player, entity);
	}

	private boolean isHoldingWeapon(Player player) {
		if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) return false;

		Material itemType = player.getInventory().getItemInMainHand().getType();

		for (Material material : config.getValidMaterials()) {
			if (itemType.equals(material)) return true;
		}

		return false;
	}

	private boolean isBaitEntityOf(Player player, Entity entity) {
		if (entityBaits.containsKey(player)) {
			EntityBaitData baitData = entityBaits.get(player);
			return baitData.entity.equals(entity);
		}
		return false;
	}

	private boolean isBaitFakePlayerOf(Player player, int entityId) {
		if (playerBaits.containsKey(player)) {
			PlayerBaitData playerBaitData = playerBaits.get(player);
			FakePlayer bait = playerBaitData.bait;

			return bait.getId() == entityId;
		}

		return false;
	}

	private void removeBaitOf(Player player) {
		if (entityBaits.containsKey(player)) {
			EntityBaitData baitData = entityBaits.get(player);
			baitData.entity.remove();
			entityBaits.remove(player);
		}
		if (playerBaits.containsKey(player)) {
			PlayerBaitData baitData = playerBaits.get(player);
			baitData.bait.remove();
			playerBaits.remove(player);
		}
	}

	private Location calculateBaitLocation(Player player) {
		Location eyeLocation = player.getEyeLocation().clone();
		Vector direction = eyeLocation.getDirection().normalize();
		Location baitLocation = eyeLocation.add(direction.multiply(config.getDistance()));
		return baitLocation;
	}

	private Entity spawnBaitEntity(Player player) {
		Location spawnLoc = calculateBaitLocation(player);
		Entity bait = player.getWorld().spawnEntity(spawnLoc, EntityType.valueOf(config.getEntity()));

		// Apply all the correct settings to the entity
		configureBaitEntity(bait);

		return bait;
	}

	private void configureBaitEntity(Entity bait) {
		bait.setInvisible(true);
		bait.setInvulnerable(true);
		bait.setNoPhysics(true);
		bait.setCustomNameVisible(false);
		bait.setGravity(false);
	}

	private record EntityBaitData(Entity entity, long spawnTimeStamp) {
	}

	private record PlayerBaitData(FakePlayer bait, long spawnTimeStamp) {
	}
}
