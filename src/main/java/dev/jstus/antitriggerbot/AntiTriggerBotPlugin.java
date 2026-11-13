package dev.jstus.antitriggerbot;

import com.github.retrooper.packetevents.PacketEvents;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPILogger;
import dev.jstus.antitriggerbot.check.CheckManager;
import dev.jstus.antitriggerbot.check.impl.TriggerBotACheck;
import dev.jstus.antitriggerbot.command.AntiTriggerBotCommand;
import dev.jstus.antitriggerbot.config.ConfigManager;
import dev.jstus.antitriggerbot.event.CheckEventListener;
import dev.jstus.antitriggerbot.event.CheckManagerPacketListener;
import dev.jstus.antitriggerbot.manager.AlertsManager;
import dev.jstus.antitriggerbot.punishment.PunishmentManager;
import dev.jstus.antitriggerbot.violation.ViolationManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class AntiTriggerBotPlugin extends JavaPlugin {

	public static AntiTriggerBotPlugin instance;

	private ConfigManager configManager;
	private PunishmentManager punishmentManager;
	private ViolationManager violationManager;
	private CheckManager checkManager;
	private CheckEventListener genericEventListener;
	private AlertsManager alertsManager;

	public static AntiTriggerBotPlugin getInstance() {
		return instance;
	}

	@Override
	public void onLoad() {
		AntiTriggerBotPlugin.instance = this;

		CommandAPI.setLogger(CommandAPILogger.fromJavaLogger(getLogger()));
		CommandAPIBukkitConfig config = new CommandAPIBukkitConfig(this);
		CommandAPI.onLoad(config);
	}

	@Override
	public void onEnable() {
		CommandAPI.onEnable();

		this.configManager = new ConfigManager(this);
		this.alertsManager = new AlertsManager(configManager);

		this.punishmentManager = new PunishmentManager(alertsManager);
		this.violationManager = new ViolationManager(punishmentManager, alertsManager, configManager.getViolationConfig());

		this.checkManager = new CheckManager();

		this.genericEventListener = new CheckEventListener(checkManager);


		registerChecks();
		registerListeners();
		PacketEvents.getAPI().getEventManager().registerListener(new CheckManagerPacketListener(checkManager));

		new AntiTriggerBotCommand(this);
	}

	@Override
	public void onDisable() {
		checkManager.unregisterAllChecks();

		this.violationManager.stopCleanupTask();

		this.configManager = null;
		this.alertsManager = null;
		this.punishmentManager = null;
		this.violationManager = null;
		this.checkManager = null;
		this.genericEventListener = null;
	}

	public void reload() {
		configManager.reload();
		checkManager.unregisterAllChecks();

		registerChecks();
	}

	private void registerChecks() {
		// Register the TriggerBotA Check
		checkManager.registerCheck(new TriggerBotACheck(configManager.getCheckConfigFor(TriggerBotACheck.class), violationManager));
	}

	private void registerListeners() {
		getServer().getPluginManager().registerEvents(new CheckEventListener(checkManager), this);
	}

	public AlertsManager getAlertsManager() {
		return alertsManager;
	}

	public ViolationManager getViolationManager() {
		return violationManager;
	}

	public PunishmentManager getPunishmentManager() {
		return punishmentManager;
	}

	public CheckManager getCheckManager() {
		return checkManager;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}
}
