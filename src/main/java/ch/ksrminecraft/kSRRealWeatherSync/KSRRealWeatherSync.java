package ch.ksrminecraft.kSRRealWeatherSync;

import ch.ksrminecraft.kSRRealWeatherSync.commands.RealWeatherCommand;
import ch.ksrminecraft.kSRRealWeatherSync.commands.RealWeatherTabCompleter;
import ch.ksrminecraft.kSRRealWeatherSync.config.ConfigManager;
import ch.ksrminecraft.kSRRealWeatherSync.config.PluginConfig;
import ch.ksrminecraft.kSRRealWeatherSync.integration.RealisticSeasonsHook;
import ch.ksrminecraft.kSRRealWeatherSync.logic.WeatherInterpreter;
import ch.ksrminecraft.kSRRealWeatherSync.scheduler.TimeSyncTask;
import ch.ksrminecraft.kSRRealWeatherSync.scheduler.WeatherUpdateTask;
import ch.ksrminecraft.kSRRealWeatherSync.services.WeatherService;
import ch.ksrminecraft.kSRRealWeatherSync.util.Debug;
import ch.ksrminecraft.kSRRealWeatherSync.world.WeatherApplier;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.plugin.java.JavaPlugin;

public class KSRRealWeatherSync extends JavaPlugin {

    private ConfigManager configManager;
    private WeatherService weatherService;
    private WeatherInterpreter interpreter;
    private WeatherApplier applier;

    private RealisticSeasonsHook rsHook;

    public ConfigManager getConfigManager() { return configManager; }
    public WeatherService getWeatherService() { return weatherService; }
    public WeatherInterpreter getWeatherInterpreter() { return interpreter; }
    public WeatherApplier getWeatherApplier() { return applier; }
    public RealisticSeasonsHook getRealisticSeasonsHook() { return rsHook; }

    public boolean hasRealisticSeasons() {
        return Bukkit.getPluginManager().getPlugin("RealisticSeasons") != null;
    }

    @Override
    public void onEnable() {
        getLogger().info("Aktiviere KSR-RealWeatherSync...");

        // CONFIG LADEN
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        PluginConfig cfg = configManager.getConfig();
        Debug.setDebug(cfg.debug);

        // DEBUG INFO
        if (cfg.debug) {
            getLogger().info("Debug-Modus aktiviert.");
        }

        // GAME RULE FIX (nur einmal, NICHT in jedem Scheduler-Tick)
        for (String worldName : cfg.weatherSyncWorlds) {
            var world = Bukkit.getWorld(worldName);
            if (world == null) {
                getLogger().warning("⚠ Welt '" + worldName + "' nicht gefunden!");
                continue;
            }

            if (Boolean.TRUE.equals(world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE))) {
                world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                getLogger().info("⏸ DO_DAYLIGHT_CYCLE deaktiviert für '" + worldName + "'");
            }
        }

        // SERVICES
        weatherService = new WeatherService(this, cfg);
        interpreter = new WeatherInterpreter(cfg);
        applier = new WeatherApplier(this, cfg);

        // REALISTIC SEASONS
        if (cfg.realisticSeasonsEnabled && hasRealisticSeasons()) {
            rsHook = new RealisticSeasonsHook(cfg);
            getLogger().info("RealisticSeasons erkannt → Temperatur-Sync aktiviert.");

            // WICHTIGE WARNUNG
            if (cfg.debug) {
                getLogger().warning("⚠ Prüfe RealisticSeasons config.yml → 'modify-daynight-length' muss auf FALSE stehen!");
            }
        } else {
            getLogger().info("RealisticSeasons Integration deaktiviert.");
        }

        // COMMANDS
        if (getCommand("ksrweather") != null) {
            getCommand("ksrweather").setExecutor(new RealWeatherCommand(this));
            getCommand("ksrweather").setTabCompleter(new RealWeatherTabCompleter());
        } else {
            getLogger().warning("⚠ Command /ksrweather konnte nicht registriert werden!");
        }

        // WEATHER SCHEDULER
        if (cfg.weatherSyncEnabled) {
            long intervalTicks = cfg.updateIntervalMinutes * 60L * 20L;

            new WeatherUpdateTask(this, weatherService, interpreter, applier)
                    .runTaskTimerAsynchronously(this, 0, intervalTicks);

            getLogger().info("☁ Wetter-Synchronisation aktiviert → alle "
                    + cfg.updateIntervalMinutes + " Minuten");
        } else {
            getLogger().info("Wetter-Synchronisation ist deaktiviert (config.yml).");
        }

        // TIME SYNC
        if (cfg.timeSyncEnabled) {
            new TimeSyncTask(this, cfg).runTaskTimer(this, 20L, 20L);
            getLogger().info("⏰ Zeit-Synchronisation aktiviert (Realtime, jede Sekunde).");
        }

        getLogger().info("KSR-RealWeatherSync erfolgreich geladen!");
    }


    @Override
    public void onDisable() {
        getLogger().info("Deaktiviere KSR-RealWeatherSync...");
    }
}
