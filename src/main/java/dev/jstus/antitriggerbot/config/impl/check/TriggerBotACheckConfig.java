package dev.jstus.antitriggerbot.config.impl.check;

import dev.jstus.antitriggerbot.AntiTriggerBotPlugin;
import dev.jstus.antitriggerbot.config.CheckConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class TriggerBotACheckConfig extends CheckConfig {
	private final double distance;
	private final long interval;
	private final long duration;
	private final String entity;
	private final double playerScale;
	private final double validationRange;
	private final List<Material> validMaterials = new ArrayList<>();

	public TriggerBotACheckConfig(ConfigurationSection section) {
		super(section);

		this.distance = section.getDouble("distance", 2.9);
		this.interval = section.getLong("interval", 400L);
		this.duration = section.getLong("duration");
		this.playerScale = section.getDouble("scale");
		this.entity = section.getString("type", "ARMOR_STAND");
		this.validationRange = section.getDouble("validation-range");

		List<String> validMaterialStrings = section.getStringList("materials");
		for (String materialString : validMaterialStrings) {
			try {
				Material material = Material.matchMaterial(materialString);

				validMaterials.add(material);
			} catch (IllegalArgumentException e) {
				Bukkit.getLogger().severe("Your configuration is invalid. I could not recognise the material: " + materialString + " you provided for TriggerBotA.");
				Bukkit.getPluginManager().disablePlugin(AntiTriggerBotPlugin.instance);
			}
		}

		boolean isValidEntity = validateEntity(entity);
		if (!isValidEntity) {
			Bukkit.getLogger().severe("Your configuration is invalid. I could not recognise the Entity Type you provided for TriggerBotA.");
			Bukkit.getPluginManager().disablePlugin(AntiTriggerBotPlugin.instance);
		}
	}

	private boolean validateEntity(String entity) {
		try {
			EntityType validEntity = EntityType.valueOf(entity);

			return true;
		} catch (IllegalArgumentException e) {
			// The entity is not a valid entity. Is it a player instead? If not either, return false;
			return entity.equals("PLAYER");
			// It's a player, that's valid
		}
	}

	public double getDistance() {
		return this.distance;
	}

	public long getInterval() {
		return this.interval;
	}

	public long getDuration() {
		return this.duration;
	}

	public String getEntity() {
		return this.entity;
	}

	public double getPlayerScale() {
		return this.playerScale;
	}

	public List<Material> getValidMaterials() {
		return this.validMaterials;
	}

	public double getValidationRange() {
		return this.validationRange;
	}
}
