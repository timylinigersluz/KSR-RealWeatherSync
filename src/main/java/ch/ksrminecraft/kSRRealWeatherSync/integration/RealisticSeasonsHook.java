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
 * Stabile Integration mit RealisticSeasons:
 *
 * - Wendet reale Temperatur als Temperatureffekt an
 * - Fängt alle RS-Probleme sicher ab
 * - Kein Log-Spam
 * - Temperatureffekt wird nur gesetzt, wenn RS-Daten bereit sind
 */
public class RealisticSeasonsHook {

    private final SeasonsAPI api;
    private final PluginConfig cfg;

    // Speichert aktive Effekte pro Spieler
    private final Map<UUID, TemperatureEffect> activeEffects = new HashMap<>();

    // Warnung nur einmal anzeigen
    private boolean warnedAboutRSTemp = false;

    public RealisticSeasonsHook(PluginConfig cfg) {
        this.api = SeasonsAPI.getInstance();
        this.cfg = cfg;

        Debug.log("[RS-Hook] Initialisiert.");
    }

    /**
     * Entfernt beim Plugin-Disable ALLE Temperatureffekte.
     */
    public void shutdown() {
        Debug.log("[RS-Hook] Entferne sämtliche aktiven Temperatureffekte...");

        for (TemperatureEffect eff : activeEffects.values()) {
            try {
                eff.cancel();
            } catch (Exception ignored) {}
        }
        activeEffects.clear();
    }

    /**
     * Entfernt Temperatureffekt eines einzelnen Spielers
     */
    public void clearPlayer(Player p) {
        TemperatureEffect old = activeEffects.remove(p.getUniqueId());
        if (old != null) {
            try {
                old.cancel();
                Debug.log("[RS-Hook] Effekt für Spieler " + p.getName() + " entfernt.");
            } catch (Exception ignored) {}
        }
    }

    /**
     * Kernmethode: Wendet die reale Temperatur auf passende Spieler an.
     */
    public void applyRealTemperature(WeatherData data) {

        // Sync deaktiviert?
        if (!cfg.realisticSeasonsEnabled) {
            return;
        }

        if (api == null) {
            Debug.warn("[RS-Hook] SeasonsAPI == null → keine Integration möglich.");
            return;
        }

        if (data == null) {
            Debug.warn("[RS-Hook] WeatherData == null → kein Temperatur-Sync.");
            return;
        }

        double realTemp = data.getTemperature2m();
        Debug.log("[RS-Hook] Reale Temperatur laut API = " + realTemp + "°C");

        for (Player p : Bukkit.getOnlinePlayers()) {

            World world = p.getWorld();

            // Nur konfigurierte Welten
            if (!cfg.weatherSyncWorlds.contains(world.getName())) {
                continue;
            }

            // --------------------------------------------------------------------
            // Schritt 1: RS-Temperatur abrufen (sicher!)
            // --------------------------------------------------------------------
            int rsTemp;

            try {
                rsTemp = api.getAirTemperature(p.getLocation());
            } catch (Exception e) {

                if (!warnedAboutRSTemp) {
                    warnedAboutRSTemp = true;

                    Debug.warn(
                            "[RS-Hook] Achtung: RealisticSeasons ist noch nicht bereit! " +
                                    "getAirTemperature liefert Fehler für Spieler " + p.getName() + ". Details: " + e.getMessage()
                    );
                }
                continue;
            }

            // Wenn RealisticSeasons "NONE" Season hat → Temperatur uninitialisiert
            if (rsTemp == 0 && !warnedAboutRSTemp) {
                warnedAboutRSTemp = true;
                Debug.warn("[RS-Hook] RS liefert 0°C (NONE Season oder RS noch uninitialisiert). Sync wird vorübergehend pausiert.");
                continue;
            }

            // --------------------------------------------------------------------
            // Schritt 2: Temperaturdifferenz berechnen
            // --------------------------------------------------------------------
            int modifier = (int) Math.round(realTemp - rsTemp);

            Debug.log("[RS-Hook] " + p.getName() +
                    " RS=" + rsTemp + "°C, REAL=" + realTemp + "°C → Δ=" + modifier);

            // Kein Unterschied → kein Effekt nötig
            if (modifier == 0) {
                clearPlayer(p);
                continue;
            }

            // --------------------------------------------------------------------
            // Schritt 3: Alten Effekt entfernen
            // --------------------------------------------------------------------
            clearPlayer(p);

            // --------------------------------------------------------------------
            // Schritt 4: Neuen Effekt anwenden
            // --------------------------------------------------------------------
            try {
                TemperatureEffect eff =
                        api.applyPermanentTemperatureEffect(p, modifier);

                activeEffects.put(p.getUniqueId(), eff);

                Debug.log("[RS-Hook] Setze Temperatur-Effekt für " + p.getName() +
                        " (Modifier=" + modifier + ")");
            } catch (Exception e) {

                if (!warnedAboutRSTemp) {
                    warnedAboutRSTemp = true;
                    Debug.warn("[RS-Hook] Fehler bei applyPermanentTemperatureEffect: " + e.getMessage());
                }
            }
        }
    }
}
