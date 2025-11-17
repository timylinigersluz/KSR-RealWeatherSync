# 🌦️ KSR-RealWeatherSync
Synchronisiere reales Wetter aus Open-Meteo mit Minecraft – inklusive optionaler RealisticSeasons-Integration

## 🌦️ Projektbeschreibung
KSR-RealWeatherSync verbindet deinen Minecraft-Server mit Echtzeit-Wetterdaten von Open-Meteo und überträgt Regen, Schnee, Gewitter oder Sonne dynamisch in deine Spielwelt.

Optional kann die reale Temperatur auch an RealisticSeasons übergeben werden.

---

## ✨ Features
- 🌍 Reales Wetter basierend auf echten Standortdaten
- 🕐 Automatische Aktualisierung (Intervall einstellbar)
- ⚙️ Vollständig konfigurierbar (API-Felder, Wettercodes, Welten)
- 💨 Asynchrone Requests → Kein Lag
- 🔌 Integration mit RealisticSeasons (optional)
- 🗺 Multi-World-Unterstützung
- 🔍 Debug-Logging
- 📜 Commands mit Tab-Autovervollständigung

---

## 📥 Installation
1. Lade das Plugin **KSR-RealWeatherSync.jar** herunter.
2. Lege es in deinen `/plugins`-Ordner.
3. (Optional) Installiere **RealisticSeasons** und **ProtocolLib**, falls du echte Temperaturen synchronisieren möchtest.
4. Starte den Server – `config.yml` wird automatisch erzeugt.
5. Konfiguriere den Standort, API und Intervall.
6. Fertig 🎉

---

## ⚙️ Beispielkonfiguration (`config.yml`)

```yaml
location:
  latitude: 47.06
  longitude: 8.279999
  timezone: Europe/Berlin

weather-sync:
  enabled: true
  worlds:
    - world

update-interval: 10   # Minuten

realistic-seasons:
  enabled: true    # Reale Temperatur an RealisticSeasons übergeben

logging:
  debug: false
```

---

## 🔁 Commands

| Befehl | Beschreibung |
|--------|---------------|
| `/ksrweather status` | Zeigt aktuellen API-Status |
| `/ksrweather reload` | Lädt config neu |
| `/ksrweather force` | Erzwingt sofortige Aktualisierung |
| `/ksrweather current` | Zeigt aktuelle Wetterdaten an |

---

## 🔑 Permissions

```yaml
permissions:
  ksrrealweathersync.admin:
    description: "Erlaubt Zugriff auf alle Befehle"
    default: op
```

---

## 🔌 RealisticSeasons Integration

Wenn aktiviert und RealisticSeasons installiert ist:
- wird die **reale Temperatur (°C)** an RealisticSeasons übergeben,
- Temperaturdifferenzen werden mit Temperatureffekten ausgeglichen,
- Spieler frieren oder überhitzen entsprechend dem echten Wetter.

---

## 🧱 Build (Entwicklung)

Das Projekt nutzt **Maven**.  
Zum Erstellen des Plugins:

```bash
mvn clean package
```

Das fertige Jar wird nach
```
D:/minecraft/Testserver/plugins
```
kopiert (siehe `pom.xml`).

---

## 🐛 Debugging

Debug-Modus aktivieren:
```yaml
logging:
  debug: true
```

Erzeugt Konsolenmeldungen wie:
```
[DEBUG] WeatherUpdateTask: Temperatur laut API = 13.4°C
[DEBUG] RS-Hook → Player=Timy RS=12°C, REAL=13°C → modifier=+1
```

---

## 📦 Abhängigkeiten

- PaperMC 1.21.4+
- org.json (API Requests)
- RealisticSeasons (optional)
- ProtocolLib (nur für RealisticSeasons)

---

## 👤 Autor
**Timy Liniger**  
📍 Kantonsschule Reussbühl, Luzern  
🌐 https://ksrminecraft.ch/

---

## 🧾 Lizenz
© 2025 Timy Liniger  
Nutzung für schulische oder nicht-kommerzielle Zwecke erlaubt.
