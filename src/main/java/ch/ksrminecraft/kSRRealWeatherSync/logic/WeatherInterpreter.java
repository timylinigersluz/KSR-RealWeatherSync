package ch.ksrminecraft.kSRRealWeatherSync.logic;

import ch.ksrminecraft.kSRRealWeatherSync.config.PluginConfig;
import ch.ksrminecraft.kSRRealWeatherSync.model.WeatherData;
import ch.ksrminecraft.kSRRealWeatherSync.util.Debug;

public class WeatherInterpreter {

    private final PluginConfig cfg;

    public WeatherInterpreter(PluginConfig cfg) {
        this.cfg = cfg;
    }

    /**
     * Übersetzt WeatherData → Minecraft Wetterzustand
     */
    public WeatherType interpret(WeatherData data) {

        if (data == null) {
            Debug.warn("WeatherInterpreter: WeatherData war null – kein Wetterwechsel.");
            return null;
        }

        int code = data.getWeatherCode();
        Debug.log("WeatherInterpreter: Analysiere Wettercode: " + code);

        // ---------------------------------------
        // THUNDER
        // ---------------------------------------
        if (cfg.thunderCodes.contains(code)) {
            Debug.log("Interpreter: Gewitter erkannt → THUNDER");
            return WeatherType.THUNDER;
        }

        // ---------------------------------------
        // RAIN
        // ---------------------------------------
        if (cfg.rainCodes.contains(code)) {
            Debug.log("Interpreter: Regen-Code erkannt → RAIN");
            return WeatherType.RAIN;
        }

        // ---------------------------------------
        // SNOW
        // (Temperatur < 0°C oder Schneefall > 0 cm)
        // Minecraft setzt Schnee je nach Biom automatisch
        // ---------------------------------------
        if (cfg.snowCodes.contains(code) || data.getSnowfall() > 0.0) {
            Debug.log("Interpreter: Schnee-Code oder Schneefall > 0 erkannt → RAIN (Minecraft regelt Schnee im Biom)");
            return WeatherType.RAIN;
        }

        // ---------------------------------------
        // CLOUDY (keine Wetteränderung)
        // ---------------------------------------
        if (cfg.cloudyCodes.contains(code)) {
            Debug.log("Interpreter: Bewölkt erkannt → CLEAR");
            return WeatherType.CLEAR;
        }

        // ---------------------------------------
        // CLEAR
        // ---------------------------------------
        if (cfg.clearCodes.contains(code)) {
            Debug.log("Interpreter: Klarer Code → CLEAR");
            return WeatherType.CLEAR;
        }

        // ---------------------------------------
        // FALLBACK: Wenn Niederschlag gemessen wird
        // ---------------------------------------
        if (data.getPrecipitation() > 0 ||
                data.getRain() > 0 ||
                data.getShowers() > 0) {

            Debug.log("Interpreter: Niederschlag gemessen → RAIN");
            return WeatherType.RAIN;
        }

        // ---------------------------------------
        // DEFAULT → CLEAR
        // ---------------------------------------
        Debug.log("Interpreter: Kein Code passt → CLEAR");
        return WeatherType.CLEAR;
    }
}
