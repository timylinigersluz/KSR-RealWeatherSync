package ch.ksrminecraft.kSRRealWeatherSync.world;

import ch.ksrminecraft.kSRRealWeatherSync.KSRRealWeatherSync;
import ch.ksrminecraft.kSRRealWeatherSync.config.PluginConfig;
import ch.ksrminecraft.kSRRealWeatherSync.logic.WeatherType;
import ch.ksrminecraft.kSRRealWeatherSync.util.Debug;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class WeatherApplier {

    private final KSRRealWeatherSync plugin;
    private final PluginConfig cfg;

    public WeatherApplier(KSRRealWeatherSync plugin, PluginConfig cfg) {
        this.plugin = plugin;
        this.cfg = cfg;
    }

    /**
     * Setzt das Wetter in den konfigurierten Welten.
     * Muss im Main-Thread ausgeführt werden!
     */
    public void applyWeather(WeatherType type) {

        if (type == null) {
            Debug.warn("WeatherApplier: WeatherType war null – kein Wetterwechsel.");
            return;
        }

        for (String worldName : cfg.weatherSyncWorlds) {

            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                Debug.warn("WeatherApplier: Welt nicht gefunden: " + worldName);
                continue;
            }

            Debug.log("Wetter wird gesetzt in Welt: " + worldName + " → " + type);

            switch (type) {
                case CLEAR -> {
                    world.setStorm(false);
                    world.setThundering(false);
                    world.setWeatherDuration(20 * 60 * 10); // 10 Minuten
                }
                case RAIN -> {
                    world.setStorm(true);
                    world.setThundering(false);
                    world.setWeatherDuration(20 * 60 * 10);
                }
                case THUNDER -> {
                    world.setStorm(true);
                    world.setThundering(true);
                    world.setThunderDuration(20 * 60 * 10);
                }
            }
        }
    }
}
