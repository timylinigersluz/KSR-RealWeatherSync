package ch.ksrminecraft.kSRRealWeatherSync.util;

import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public final class HttpUtil {

    private HttpUtil() {} // Utility class

    /**
     * Führt einen asynchronen GET-Request aus.
     *
     * @param urlString  URL der Anfrage
     * @param timeoutMs  Timeout in Millisekunden
     * @param callback   Wird mit dem Ergebnis (String oder null) aufgerufen
     */
    public static void asyncGet(String urlString, int timeoutMs, Consumer<String> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(
                Bukkit.getPluginManager().getPlugin("KSR-RealWeatherSync"),
                () -> {
                    String response = null;

                    try {
                        Debug.log("HTTP GET → " + urlString);

                        URL url = new URL(urlString);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(timeoutMs);
                        conn.setReadTimeout(timeoutMs);

                        int status = conn.getResponseCode();
                        if (status != HttpURLConnection.HTTP_OK) {
                            Debug.warn("HTTP Fehlercode: " + status);
                            callback.accept(null);
                            return;
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

                        response = sb.toString();
                        Debug.log("HTTP Antwort erfolgreich empfangen (" + response.length() + " Zeichen)");

                    } catch (Exception e) {
                        Debug.warn("Ausnahme während HTTP-Request: " + e.getMessage());
                    }

                    callback.accept(response);
                }
        );
    }
}
