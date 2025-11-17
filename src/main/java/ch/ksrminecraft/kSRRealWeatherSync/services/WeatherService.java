package ch.ksrminecraft.kSRRealWeatherSync.services;

import ch.ksrminecraft.kSRRealWeatherSync.KSRRealWeatherSync;
import ch.ksrminecraft.kSRRealWeatherSync.config.PluginConfig;
import ch.ksrminecraft.kSRRealWeatherSync.model.WeatherData;
import ch.ksrminecraft.kSRRealWeatherSync.util.Debug;
import ch.ksrminecraft.kSRRealWeatherSync.util.HttpUtil;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class WeatherService {

    private final KSRRealWeatherSync plugin;
    private final PluginConfig cfg;

    public WeatherService(KSRRealWeatherSync plugin, PluginConfig cfg) {
        this.plugin = plugin;
        this.cfg = cfg;
    }

    // -------------------------------------------------------------------------
    //  URL BUILDER
    // -------------------------------------------------------------------------
    private String buildUrl() {
        StringBuilder fields = new StringBuilder();

        for (String f : cfg.apiFields) {
            if (fields.length() > 0) fields.append(",");
            fields.append(f);
        }

        String url =
                cfg.apiBaseUrl
                        + "?latitude=" + cfg.latitude
                        + "&longitude=" + cfg.longitude
                        + "&current=" + fields
                        + "&timezone=" + cfg.timezone;

        Debug.log("API URL gebaut: " + url);
        return url;
    }

    // -------------------------------------------------------------------------
    //  ASYNCHRONER ABRUF (für Scheduler)
    // -------------------------------------------------------------------------
    public void fetchWeatherAsync(Consumer<WeatherData> callback) {
        String url = buildUrl();

        HttpUtil.asyncGet(url, 8000, (response) -> {
            if (response == null) {
                Debug.warn("API lieferte keine Antwort.");
                callback.accept(null);
                return;
            }

            try {
                WeatherData data = parseWeatherJson(response);
                callback.accept(data);
            } catch (Exception e) {
                Debug.warn("Fehler beim JSON-Parsing: " + e.getMessage());
                callback.accept(null);
            }
        });
    }

    // -------------------------------------------------------------------------
    //  SYNCHRONER ABRUF (für /ksrweather force)
    // -------------------------------------------------------------------------
    public WeatherData fetchWeatherSync() throws Exception {
        String url = buildUrl();
        Debug.log("Synchroner API-Request gestartet…");

        URL u = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);

        int status = conn.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("Fehlercode: " + status);
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
        );

        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        reader.close();
        conn.disconnect();

        Debug.log("Synchron JSON-Länge: " + sb.length());

        return parseWeatherJson(sb.toString());
    }

    // -------------------------------------------------------------------------
    //  JSON → WeatherData
    // -------------------------------------------------------------------------
    private WeatherData parseWeatherJson(String jsonString) {
        JSONObject root = new JSONObject(jsonString);

        if (!root.has("current")) {
            Debug.warn("JSON enthält kein Feld 'current'.");
            return null;
        }

        JSONObject current = root.getJSONObject("current");

        String time = current.getString("time");
        int interval = current.getInt("interval");

        double temperature = current.optDouble("temperature_2m", Double.NaN);
        double precipitation = current.optDouble("precipitation", 0.0);
        double rain = current.optDouble("rain", 0.0);
        double showers = current.optDouble("showers", 0.0);
        double snowfall = current.optDouble("snowfall", 0.0);
        int weatherCode = current.optInt("weather_code", -1);

        boolean isDay = current.optInt("is_day", 0) == 1;

        Debug.log("Parsed Wetterdaten: " +
                "T=" + temperature +
                " | Rain=" + rain +
                " | Snowfall=" + snowfall +
                " | Code=" + weatherCode +
                " | Day=" + isDay
        );

        return new WeatherData(
                time,
                interval,
                temperature,
                precipitation,
                rain,
                showers,
                snowfall,
                weatherCode,
                isDay
        );
    }
}
