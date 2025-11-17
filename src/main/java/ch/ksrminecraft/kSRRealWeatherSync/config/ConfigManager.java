package ch.ksrminecraft.kSRRealWeatherSync.config;

import ch.ksrminecraft.kSRRealWeatherSync.KSRRealWeatherSync;
import ch.ksrminecraft.kSRRealWeatherSync.util.Debug;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {

    private final KSRRealWeatherSync plugin;
    private PluginConfig config;

    public ConfigManager(KSRRealWeatherSync plugin) {
        this.plugin = plugin;
    }

    /**
     * Lädt config.yml und erstellt ein PluginConfig-Objekt.
     */
    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        FileConfiguration cfg = plugin.getConfig();

        // ----------------------------------------------------
        // Null-sichere Sections laden
        // ----------------------------------------------------
        ConfigurationSection loc = section(cfg, "location");
        ConfigurationSection api = section(cfg, "api");
        ConfigurationSection unitsSec = section(api, "units");
        ConfigurationSection fail = section(api, "fail-behavior");
        ConfigurationSection sync = section(cfg, "weather-sync");
        ConfigurationSection time = section(cfg, "time-sync");
        ConfigurationSection rs = section(cfg, "realistic-seasons");
        ConfigurationSection codesSec = section(cfg, "weather-codes");

        // ----------------------------------------------------
        // LOCATION
        // ----------------------------------------------------
        double latitude = loc.getDouble("latitude");
        double longitude = loc.getDouble("longitude");
        String timezone = loc.getString("timezone", "Europe/Berlin");

        // ----------------------------------------------------
        // API-Konfiguration
        // ----------------------------------------------------
        String baseUrl = api.getString("base-url");
        List<String> fields = api.getStringList("fields");

        String unitTemperature = unitsSec.getString("temperature_2m", "°C");
        String unitPrecipitation = unitsSec.getString("precipitation", "mm");
        String unitRain = unitsSec.getString("rain", "mm");
        String unitShowers = unitsSec.getString("showers", "mm");
        String unitSnowfall = unitsSec.getString("snowfall", "cm");
        String unitWeatherCode = unitsSec.getString("weather_code", "wmo");
        String unitIsDay = unitsSec.getString("is_day", "binary");

        int modelInterval = api.getInt("model-interval-seconds", 900);

        boolean keepWeather = fail.getBoolean("keep-current-weather", true);
        boolean logErrors = fail.getBoolean("log-errors", true);

        // ----------------------------------------------------
        // WEATHER-SYNC
        // ----------------------------------------------------
        boolean weatherSyncEnabled = sync.getBoolean("enabled", true);
        List<String> worlds = sync.getStringList("worlds");

        // ----------------------------------------------------
        // TIME-SYNC (NEU)
        // ----------------------------------------------------
        boolean timeSyncEnabled = time.getBoolean("enabled", false);

        // ----------------------------------------------------
        // REALISTIC SEASONS
        // ----------------------------------------------------
        boolean realisticSeasonsEnabled = rs.getBoolean("enabled", true);

        // ----------------------------------------------------
        // UPDATE
        // ----------------------------------------------------
        int updateInterval = cfg.getInt("update-interval", 10);

        // ----------------------------------------------------
        // WMO WEATHER CODES
        // ----------------------------------------------------
        List<Integer> clearCodes = codesSec.getIntegerList("clear");
        List<Integer> cloudyCodes = codesSec.getIntegerList("cloudy");
        List<Integer> rainCodes = codesSec.getIntegerList("rain");
        List<Integer> snowCodes = codesSec.getIntegerList("snow");
        List<Integer> thunderCodes = codesSec.getIntegerList("thunder");

        // ----------------------------------------------------
        // DEBUG
        // ----------------------------------------------------
        boolean debug = cfg.getBoolean("logging.debug", false);
        Debug.setDebug(debug);

        // ----------------------------------------------------
        // PluginConfig erzeugen
        // ----------------------------------------------------
        this.config = new PluginConfig(
                latitude,
                longitude,
                timezone,

                baseUrl,
                fields,

                unitTemperature,
                unitPrecipitation,
                unitRain,
                unitShowers,
                unitSnowfall,
                unitWeatherCode,
                unitIsDay,

                modelInterval,
                keepWeather,
                logErrors,

                weatherSyncEnabled,
                worlds,
                timeSyncEnabled,

                realisticSeasonsEnabled,

                updateInterval,

                clearCodes,
                cloudyCodes,
                rainCodes,
                snowCodes,
                thunderCodes,

                debug
        );

        Debug.log("Config erfolgreich geladen.");
    }

    /**
     * Gibt PluginConfig zurück.
     */
    public PluginConfig getConfig() {
        return config;
    }

    // Null-sichere Section-Methode
    private ConfigurationSection section(ConfigurationSection parent, String key) {
        ConfigurationSection sec = parent.getConfigurationSection(key);
        if (sec == null) {
            throw new IllegalStateException("config.yml fehlt Section: " + key);
        }
        return sec;
    }
}
