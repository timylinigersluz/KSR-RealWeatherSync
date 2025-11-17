package ch.ksrminecraft.kSRRealWeatherSync.scheduler;

import ch.ksrminecraft.kSRRealWeatherSync.KSRRealWeatherSync;
import ch.ksrminecraft.kSRRealWeatherSync.config.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class TimeSyncTask extends BukkitRunnable {

    private final KSRRealWeatherSync plugin;
    private final PluginConfig cfg;
    private final ZoneId zoneId;

    // Anti-Spam-Speicher
    private long lastWarnTime = 0;
    private long lastWarnTick = -9999;
    private static final long WARN_INTERVAL_MS = 5 * 60_000; // 5 Minuten

    public TimeSyncTask(KSRRealWeatherSync plugin, PluginConfig cfg) {
        this.plugin = plugin;
        this.cfg = cfg;
        this.zoneId = ZoneId.of(cfg.timezone);
    }

    private boolean shouldWarn(long afterTick) {
        if (!cfg.debug) return false; // Nur wenn debug = true

        long now = System.currentTimeMillis();

        if (now - lastWarnTime > WARN_INTERVAL_MS) {
            lastWarnTime = now;
            lastWarnTick = afterTick;
            return true;
        }

        if (lastWarnTick != afterTick) {
            lastWarnTick = afterTick;
            return true;
        }

        return false;
    }

    private long toMinecraftDaytimeTicks(ZonedDateTime now) {
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();

        double minutesOfDay = hour * 60.0 + minute + (second / 60.0);
        double minutesFromSix = minutesOfDay - 360.0;
        if (minutesFromSix < 0) minutesFromSix += 1440.0;

        long ticks = Math.round(minutesFromSix * (24000.0 / 1440.0)) % 24000;
        return ticks < 0 ? ticks + 24000 : ticks;
    }

    @Override
    public void run() {
        if (!cfg.timeSyncEnabled) return;

        ZonedDateTime now = ZonedDateTime.now(zoneId).truncatedTo(ChronoUnit.SECONDS);
        long ticks = toMinecraftDaytimeTicks(now);

        for (String worldName : cfg.weatherSyncWorlds) {
            World w = plugin.getServer().getWorld(worldName);
            if (w == null) continue;

            w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            w.setTime(ticks);

        }
    }
}
