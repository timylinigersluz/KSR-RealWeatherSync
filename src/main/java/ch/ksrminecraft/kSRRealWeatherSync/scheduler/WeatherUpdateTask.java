package ch.ksrminecraft.kSRRealWeatherSync.scheduler;

import ch.ksrminecraft.kSRRealWeatherSync.KSRRealWeatherSync;
import ch.ksrminecraft.kSRRealWeatherSync.logic.WeatherInterpreter;
import ch.ksrminecraft.kSRRealWeatherSync.logic.WeatherType;
import ch.ksrminecraft.kSRRealWeatherSync.model.WeatherData;
import ch.ksrminecraft.kSRRealWeatherSync.services.WeatherService;
import ch.ksrminecraft.kSRRealWeatherSync.util.Debug;
import ch.ksrminecraft.kSRRealWeatherSync.world.WeatherApplier;
import org.bukkit.scheduler.BukkitRunnable;

public class WeatherUpdateTask extends BukkitRunnable {

    private final KSRRealWeatherSync plugin;
    private final WeatherService weatherService;
    private final WeatherInterpreter interpreter;
    private final WeatherApplier applier;

    public WeatherUpdateTask(KSRRealWeatherSync plugin,
                             WeatherService weatherService,
                             WeatherInterpreter interpreter,
                             WeatherApplier applier) {

        this.plugin = plugin;
        this.weatherService = weatherService;
        this.interpreter = interpreter;
        this.applier = applier;
    }

    @Override
    public void run() {

        Debug.log("WeatherUpdateTask gestartet — API-Abfrage läuft...");

        weatherService.fetchWeatherAsync((WeatherData data) -> {

            if (data == null) {
                Debug.warn("WeatherUpdateTask: API lieferte null – kein Wetterwechsel.");
                return;
            }

            Debug.log("WeatherUpdateTask: API-Temperatur = " + data.getTemperature2m() + "°C");

            WeatherType type = interpreter.interpret(data);

            if (type == null) {
                Debug.warn("WeatherUpdateTask: interpretierter WeatherType war null.");
                return;
            }

            // MainThread → Wetter setzen + RS-Hook aufrufen
            plugin.getServer().getScheduler().runTask(plugin, () -> {

                // Minecraft-Wetter setzen
                Debug.log("WeatherUpdateTask: Setze Wetter: " + type);
                applier.applyWeather(type);

                // Realistic Seasons Hook
                if (plugin.hasRealisticSeasons()) {
                    Debug.log("WeatherUpdateTask: Übergabe der Temperatur an RealisticSeasons...");
                    plugin.getRealisticSeasonsHook().applyRealTemperature(data);
                }
            });
        });
    }
}
