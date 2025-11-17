package ch.ksrminecraft.kSRRealWeatherSync.config;

import java.util.List;

/**
 * Reines, unveränderbares Datenobjekt, das alle Konfigurationswerte aus der config.yml enthält.
 * Wird vom ConfigManager erstellt und in anderen Klassen weitergegeben.
 */
public class PluginConfig {

    // -------------------------------------------------------------
    // Standort & Zeitzone
    // -------------------------------------------------------------
    public final double latitude;
    public final double longitude;
    public final String timezone;

    // -------------------------------------------------------------
    // API-Konfiguration
    // -------------------------------------------------------------
    public final String apiBaseUrl;
    public final List<String> apiFields;

    public final String unitTemperature;
    public final String unitPrecipitation;
    public final String unitRain;
    public final String unitShowers;
    public final String unitSnowfall;
    public final String unitWeatherCode;
    public final String unitIsDay;

    public final int modelIntervalSeconds;

    public final boolean keepCurrentWeatherOnFail;
    public final boolean logErrors;

    // -------------------------------------------------------------
    // Wetter-/Zeit-Synchronisation
    // -------------------------------------------------------------
    public final boolean weatherSyncEnabled;
    public final List<String> weatherSyncWorlds;
    public final boolean timeSyncEnabled;

    // -------------------------------------------------------------
    // RealisticSeasons
    // -------------------------------------------------------------
    public final boolean realisticSeasonsEnabled;

    // -------------------------------------------------------------
    // Update-Intervall (Minuten)
    // -------------------------------------------------------------
    public final int updateIntervalMinutes;

    // -------------------------------------------------------------
    // WMO Wettercode-Gruppen
    // -------------------------------------------------------------
    public final List<Integer> clearCodes;
    public final List<Integer> cloudyCodes;
    public final List<Integer> rainCodes;
    public final List<Integer> snowCodes;
    public final List<Integer> thunderCodes;

    // DEBUG
    public final boolean debug;

    public PluginConfig(
            double latitude,
            double longitude,
            String timezone,

            String apiBaseUrl,
            List<String> apiFields,

            String unitTemperature,
            String unitPrecipitation,
            String unitRain,
            String unitShowers,
            String unitSnowfall,
            String unitWeatherCode,
            String unitIsDay,

            int modelIntervalSeconds,
            boolean keepCurrentWeatherOnFail,
            boolean logErrors,

            boolean weatherSyncEnabled,
            List<String> weatherSyncWorlds,
            boolean timeSyncEnabled,

            boolean realisticSeasonsEnabled,

            int updateIntervalMinutes,

            List<Integer> clearCodes,
            List<Integer> cloudyCodes,
            List<Integer> rainCodes,
            List<Integer> snowCodes,
            List<Integer> thunderCodes,

            boolean debug
    ) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezone = timezone;

        this.apiBaseUrl = apiBaseUrl;
        this.apiFields = apiFields;

        this.unitTemperature = unitTemperature;
        this.unitPrecipitation = unitPrecipitation;
        this.unitRain = unitRain;
        this.unitShowers = unitShowers;
        this.unitSnowfall = unitSnowfall;
        this.unitWeatherCode = unitWeatherCode;
        this.unitIsDay = unitIsDay;

        this.modelIntervalSeconds = modelIntervalSeconds;
        this.keepCurrentWeatherOnFail = keepCurrentWeatherOnFail;
        this.logErrors = logErrors;

        this.weatherSyncEnabled = weatherSyncEnabled;
        this.weatherSyncWorlds = weatherSyncWorlds;
        this.timeSyncEnabled = timeSyncEnabled;

        this.realisticSeasonsEnabled = realisticSeasonsEnabled;

        this.updateIntervalMinutes = updateIntervalMinutes;

        this.clearCodes = clearCodes;
        this.cloudyCodes = cloudyCodes;
        this.rainCodes = rainCodes;
        this.snowCodes = snowCodes;
        this.thunderCodes = thunderCodes;

        this.debug = debug;
    }
}
