package ch.ksrminecraft.kSRRealWeatherSync.model;

/**
 * Repräsentiert die relevanten Echtzeit-Wetterdaten,
 * wie sie über die Open-Meteo API geliefert werden.
 *
 * Alle Felder entsprechen exakt den JSON-Feldern:
 *
 * {
 *   "current": {
 *       "time": "2025-11-14T11:15",
 *       "interval": 900,
 *       "temperature_2m": 10.8,
 *       "precipitation": 0.00,
 *       "rain": 0.00,
 *       "showers": 0.00,
 *       "snowfall": 0.00,
 *       "weather_code": 3,
 *       "is_day": 1
 *   }
 * }
 */
public final class WeatherData {

    // Zeitstempel des Datensatzes
    private final String time;

    // Zeitintervall in Sekunden (Open-Meteo Standard: 900 = 15 Minuten)
    private final int interval;

    // Temperatur in °C
    private final double temperature2m;

    // Gesamtniederschlag (mm)
    private final double precipitation;

    // Regen (mm)
    private final double rain;

    // Schauer (mm)
    private final double showers;

    // Schneefall (cm)
    private final double snowfall;

    // WMO Wettercode: 0–99
    private final int weatherCode;

    // 1 = Tag, 0 = Nacht
    private final boolean isDay;

    public WeatherData(
            String time,
            int interval,
            double temperature2m,
            double precipitation,
            double rain,
            double showers,
            double snowfall,
            int weatherCode,
            boolean isDay
    ) {
        this.time = time;
        this.interval = interval;
        this.temperature2m = temperature2m;
        this.precipitation = precipitation;
        this.rain = rain;
        this.showers = showers;
        this.snowfall = snowfall;
        this.weatherCode = weatherCode;
        this.isDay = isDay;
    }

    // --------------------------------------------------------------------
    // Getter
    // --------------------------------------------------------------------

    public String getTime() {
        return time;
    }

    public int getInterval() {
        return interval;
    }

    public double getTemperature2m() {
        return temperature2m;
    }

    public double getPrecipitation() {
        return precipitation;
    }

    public double getRain() {
        return rain;
    }

    public double getShowers() {
        return showers;
    }

    public double getSnowfall() {
        return snowfall;
    }

    public int getWeatherCode() {
        return weatherCode;
    }

    public boolean isDay() {
        return isDay;
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "time='" + time + '\'' +
                ", interval=" + interval +
                ", temperature2m=" + temperature2m +
                ", precipitation=" + precipitation +
                ", rain=" + rain +
                ", showers=" + showers +
                ", snowfall=" + snowfall +
                ", weatherCode=" + weatherCode +
                ", isDay=" + isDay +
                '}';
    }
}
