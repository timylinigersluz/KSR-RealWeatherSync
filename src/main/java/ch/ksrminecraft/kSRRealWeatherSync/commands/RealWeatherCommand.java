package ch.ksrminecraft.kSRRealWeatherSync.commands;

import ch.ksrminecraft.kSRRealWeatherSync.KSRRealWeatherSync;
import ch.ksrminecraft.kSRRealWeatherSync.model.WeatherData;
import ch.ksrminecraft.kSRRealWeatherSync.util.Debug;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RealWeatherCommand implements CommandExecutor {

    private final KSRRealWeatherSync plugin;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public RealWeatherCommand(KSRRealWeatherSync plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!sender.hasPermission("ksrrealweathersync.admin")) {
            sender.sendMessage(ChatColor.RED + "Keine Berechtigung.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> reloadConfig(sender);
            case "status" -> showStatus(sender);
            case "force" -> forceUpdate(sender);
            case "current" -> showCurrentWeather(sender);
            default -> sendHelp(sender);
        }

        return true;
    }

    // ------------------------------------------------------------------------
    // /ksrweather current
    // ------------------------------------------------------------------------
    private void showCurrentWeather(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Rufe aktuelle Wetterdaten ab...");

        Debug.log("Befehl /ksrweather current ausgelöst.");

        plugin.getWeatherService().fetchWeatherAsync((WeatherData data) -> {

            if (data == null) {
                sender.sendMessage(ChatColor.RED + "Konnte aktuelle Wetterdaten nicht abrufen.");
                return;
            }

            // Formatierte Zeit (Leerzeichen statt T)
            String formattedTime;
            try {
                LocalDateTime time = LocalDateTime.parse(data.getTime());
                formattedTime = time.format(FORMATTER);
            } catch (Exception e) {
                // Fallback: einfach ein Leerzeichen vor T setzen
                formattedTime = data.getTime().replace("T", " ");
            }

            sender.sendMessage(ChatColor.AQUA + "==== Aktuelles Wetter ====");
            sender.sendMessage(ChatColor.GRAY + "Zeit:        " + ChatColor.WHITE + formattedTime);
            sender.sendMessage(ChatColor.GRAY + "Temperatur:  " + ChatColor.WHITE + data.getTemperature2m() + " °C");
            sender.sendMessage(ChatColor.GRAY + "Niederschlag:" + ChatColor.WHITE + data.getPrecipitation() + " mm");
            sender.sendMessage(ChatColor.GRAY + "Regen:       " + ChatColor.WHITE + data.getRain() + " mm");
            sender.sendMessage(ChatColor.GRAY + "Schauer:     " + ChatColor.WHITE + data.getShowers() + " mm");
            sender.sendMessage(ChatColor.GRAY + "Schnee:      " + ChatColor.WHITE + data.getSnowfall() + " cm");
            sender.sendMessage(ChatColor.GRAY + "Wettercode:  " + ChatColor.WHITE + data.getWeatherCode());
            sender.sendMessage(ChatColor.GRAY + "Tag/Nacht:   " + ChatColor.WHITE +
                    (data.isDay() ? "Tag" : "Nacht"));
            sender.sendMessage(ChatColor.AQUA + "==========================");
        });
    }

    // ------------------------------------------------------------------------
    // Bestehende Commands
    // ------------------------------------------------------------------------
    private void reloadConfig(CommandSender sender) {
        plugin.getConfigManager().loadConfig();
        sender.sendMessage(ChatColor.GREEN + "Konfiguration neu geladen.");
        Debug.log("Config via /ksrweather reload neu geladen.");
    }

    private void showStatus(CommandSender sender) {
        var cfg = plugin.getConfigManager().getConfig();

        sender.sendMessage(ChatColor.AQUA + "--- KSR-RealWeatherSync Status ---");
        sender.sendMessage(ChatColor.GRAY + "Aktiv:              " + ChatColor.WHITE + cfg.weatherSyncEnabled);
        sender.sendMessage(ChatColor.GRAY + "Welten:             " + ChatColor.WHITE + cfg.weatherSyncWorlds);
        sender.sendMessage(ChatColor.GRAY + "Koordinaten:        " + ChatColor.WHITE + cfg.latitude + ", " + cfg.longitude);
        sender.sendMessage(ChatColor.GRAY + "Timezone:           " + ChatColor.WHITE + cfg.timezone);
        sender.sendMessage(ChatColor.GRAY + "Update-Intervall:   " + ChatColor.WHITE + cfg.updateIntervalMinutes + " Minuten");
        sender.sendMessage(ChatColor.GRAY + "RealisticSeasons:   " + ChatColor.WHITE + cfg.realisticSeasonsEnabled);
        sender.sendMessage(ChatColor.GRAY + "Debug:              " + ChatColor.WHITE + cfg.debug);
    }

    private void forceUpdate(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Starte sofortigen Wetterabgleich...");
        Debug.log("Force-Update via /ksrweather force ausgelöst.");

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                WeatherData data = plugin.getWeatherService().fetchWeatherSync();
                var type = plugin.getWeatherInterpreter().interpret(data);

                plugin.getServer().getScheduler().runTask(plugin,
                        () -> plugin.getWeatherApplier().applyWeather(type));

                sender.sendMessage(ChatColor.GREEN + "Wetter erfolgreich aktualisiert.");
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Fehler: " + e.getMessage());
                Debug.warn("Fehler bei forceUpdate: " + e.getMessage());
            }
        });
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "KSR-RealWeatherSync Befehle:");
        sender.sendMessage(ChatColor.YELLOW + "/ksrweather status" + ChatColor.GRAY + " - Plugin-Status anzeigen");
        sender.sendMessage(ChatColor.YELLOW + "/ksrweather reload" + ChatColor.GRAY + " - Config neu laden");
        sender.sendMessage(ChatColor.YELLOW + "/ksrweather force" + ChatColor.GRAY + " - Wetter sofort setzen");
        sender.sendMessage(ChatColor.YELLOW + "/ksrweather current" + ChatColor.GRAY + " - Live-Wetter anzeigen");
    }
}
