package dev.jstus.antitriggerbot.config;

import dev.jstus.antitriggerbot.check.Check;
import dev.jstus.antitriggerbot.check.impl.TriggerBotACheck;
import dev.jstus.antitriggerbot.config.impl.MessagesConfig;
import dev.jstus.antitriggerbot.config.impl.ViolationConfig;
import dev.jstus.antitriggerbot.config.impl.check.TriggerBotACheckConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class ConfigManager {
	private final JavaPlugin plugin;

	private final HashMap<Class<? extends Check>, CheckConfig> checkConfigs = new HashMap<>();
	private MessagesConfig messagesConfig;
	private ViolationConfig violationConfig;
	private FileConfiguration mainConfigurationFile;

	public ConfigManager(JavaPlugin plugin) {
		this.plugin = plugin;

		// Get the config.yml configuration
		this.mainConfigurationFile = plugin.getConfig();
		plugin.saveDefaultConfig();

		// Load all check configs
		loadAllConfigs();
	}

	public void reload() {
		plugin.reloadConfig();
		this.mainConfigurationFile = plugin.getConfig();
		plugin.saveDefaultConfig();

		checkConfigs.clear();

		loadAllConfigs();
	}

	private void loadAllConfigs() {
		loadAllCheckConfigs();
		loadGeneralConfigs();
	}

	private void loadAllCheckConfigs() {
		checkConfigs.put(TriggerBotACheck.class,
				new TriggerBotACheckConfig(mainConfigurationFile.getConfigurationSection("checks.triggerbot.a")));
	}

	private void loadGeneralConfigs() {
		messagesConfig = new MessagesConfig(mainConfigurationFile.getConfigurationSection("settings.messages"));
		violationConfig = new ViolationConfig(mainConfigurationFile.getConfigurationSection("settings.violations"));
	}

	@SuppressWarnings("unchecked")
	public <T extends CheckConfig> T getCheckConfigFor(Class<? extends Check> checkClass) {
		return (T) checkConfigs.get(checkClass);
	}

	public MessagesConfig getMessagesConfig() {
		return this.messagesConfig;
	}

	public ViolationConfig getViolationConfig() {
		return this.violationConfig;
	}
}