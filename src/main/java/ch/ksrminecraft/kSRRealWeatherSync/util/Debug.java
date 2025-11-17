package ch.ksrminecraft.kSRRealWeatherSync.util;

import org.bukkit.Bukkit;

public final class Debug {

    private static boolean debugEnabled = false;
    private static final String PLUGIN_NAME = "KSR-RealWeatherSync";

    private Debug() {} // Utility class

    /**
     * Aktiviert oder deaktiviert den Debug-Modus.
     */
    public static void setDebug(boolean enabled) {
        debugEnabled = enabled;
    }

    /**
     * Gibt eine Debug-Nachricht aus.
     * Automatische Erkennung der aufrufenden Klasse.
     */
    public static void log(String message) {
        if (!debugEnabled) return;

        String caller = getCallerClassName();
        Bukkit.getLogger().info(format(caller, message));
    }

    /**
     * Gibt eine Debug-Warnung aus.
     */
    public static void warn(String message) {
        if (!debugEnabled) return;

        String caller = getCallerClassName();
        Bukkit.getLogger().warning(format(caller, message));
    }

    // ------------------------------------------------------------------------
    // Hilfsmethoden
    // ------------------------------------------------------------------------

    private static String format(String caller, String message) {
        return "[" + PLUGIN_NAME + "] [" + caller + "]: " + message;
    }

    /**
     * Ermittelt automatisch die Klasse, welche Debug.log() oder Debug.warn()
     * aufgerufen hat.
     */
    private static String getCallerClassName() {
        // StackTrace[0] = Thread
        // StackTrace[1] = Debug.java
        // StackTrace[2] = Aufrufer → wir brauchen diesen
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();

        if (stack.length > 3) {
            String fullClass = stack[3].getClassName();
            int lastDot = fullClass.lastIndexOf('.');
            return (lastDot == -1 ? fullClass : fullClass.substring(lastDot + 1));
        }
        return "UnknownClass";
    }
}
