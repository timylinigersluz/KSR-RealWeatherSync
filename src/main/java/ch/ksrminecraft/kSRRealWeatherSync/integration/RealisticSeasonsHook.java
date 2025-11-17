package ch.ksrminecraft.kSRRealWeatherSync.integration;

import ch.ksrminecraft.kSRRealWeatherSync.config.PluginConfig;
import ch.ksrminecraft.kSRRealWeatherSync.model.WeatherData;
import ch.ksrminecraft.kSRRealWeatherSync.util.Debug;

import me.casperge.realisticseasons.api.SeasonsAPI;
import me.casperge.realisticseasons.api.TemperatureEffect;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Hook zur Integration mit dem RealisticSeasons Plugin.
 * Wendet die reale Temperatur (Open-Meteo API) auf Spieler an,
 * indem die Temperaturdifferenz als Temperatureffekt angewendet wird.
 */
public class RealisticSeasonsHook {

    private final SeasonsAPI api;
    private final PluginConfig cfg;

    // Aktive Temperatureffekte speichern
    private final Map<UUID, TemperatureEffect> activeEffects = new HashMap<>();

    public RealisticSeasonsHook(PluginConfig cfg) {
        this.api = SeasonsAPI.getInstance();
        this.cfg = cfg;

        Debug.log("RealisticSeasonsHook initialisiert.");
    }

    /**
     * Wird beim Plugin-Disable aufgerufen.
     * Entfernt ALLE aktiven Temperatureffekte sauber.
     */
    public void shutdown() {
        Debug.log("RS-Hook: Entferne alle aktiven Temperatureffekte...");

        for (TemperatureEffect eff : activeEffects.values()) {
            try {
                eff.cancel();
            } catch (Exception ignored) {}
        }

        activeEffects.clear();
    }

    /**
     * Entfernt den Temperatureffekt eines einzelnen Spielers.
     */
    public void clearPlayer(Player p) {
        TemperatureEffect old = activeEffects.remove(p.getUniqueId());
        if (old != null) {
            try {
                old.cancel();
                Debug.log("RS-Hook: Temperatur-Effekt für " + p.getName() + " entfernt.");
            } catch (Exception ignored) {}
        }
    }

    /**
     * Wendet die reale Temperatur auf alle Spieler an.
     * Nur aktiv, wenn:
     *  - Integration in config aktiviert ist
     *  - RealisticSeasons installiert ist
     */
    public void applyRealTemperature(WeatherData data) {

        // Check ob in config aktiviert
        if (!cfg.realisticSeasonsEnabled) {
            Debug.log("RS-Hook deaktiviert (config).");
            return;
        }

        if (data == null) {
            Debug.warn("RS-Hook: WeatherData == null");
            return;
        }

        if (api == null) {
            Debug.warn("RS-Hook: SeasonsAPI == null → kein Sync möglich.");
            return;
        }

        double realTemp = data.getTemperature2m();
        Debug.log("RS-Hook: Reale Temperatur = " + realTemp + "°C");

        for (Player p : Bukkit.getOnlinePlayers()) {

            World w = p.getWorld();

            // Nur konfigurierte Welten
            if (!cfg.weatherSyncWorlds.contains(w.getName())) {
                Debug.log("RS-Hook: Welt " + w.getName() + " nicht freigegeben → " + p.getName() + " übersprungen.");
                continue;
            }

            int rsTemp;
            try {
                rsTemp = api.getAirTemperature(p.getLocation());
            } catch (Exception e) {
                Debug.warn("RS-Hook: Fehler bei getAirTemperature für " + p.getName() + ": " + e.getMessage());
                continue;
            }

            int modifier = (int) Math.round(realTemp - rsTemp);

            Debug.log("RS-Hook → " + p.getName()
                    + " RS=" + rsTemp + "°C, REAL=" + realTemp + "°C"
                    + " Δ=" + modifier);

            // Kein Unterschied → nichts tun
            if (modifier == 0) {
                continue;
            }

            // Alten Effekt entfernen
            clearPlayer(p);

            // Neuen Effekt anwenden
            try {
                TemperatureEffect effect = api.applyPermanentTemperatureEffect(p, modifier);
                activeEffects.put(p.getUniqueId(), effect);

                Debug.log("RS-Hook: Neuer Temperatureffekt für " + p.getName()
                        + " angewendet → Modifier=" + modifier);
            } catch (Exception e) {
                Debug.warn("RS-Hook: Fehler bei applyPermanentTemperatureEffect: " + e.getMessage());
            }
        }
    }
}
