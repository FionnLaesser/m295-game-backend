# M295 Game Backend

## Voraussetzungen
- Java 17+
- Maven
- Docker und Docker Compose
- Insomnia

---

## Installation und Start
Lade das gesamte Repository in einen Ordner deiner Wahl hoch.

### 1. Datenbank mit Docker starten
Falls die Datenbank neu gestartet werden soll oder Probleme auftreten:

Füre diesen Code in der Konsole in der das Docker-compose.yaml liegt:
```bash
docker compose down -v
docker compose up -d
```

> `down -v` löscht Volumes. Das ist empfohlen, falls bereits Daten verändert wurden.

---

### 2. Tests ausühren
```bash
mvn test
```

Alle Tests sollten erfolgreich durchlaufen.

---

### 3. Applikation starten
```bash
mvn spring-boot:run
```

Die REST-API läuft standardmässig auf `http://localhost:8080`.

---
## Insomnia Tests

1. Insomnia öffnen  
2. **Import / Export → Import Data**
3. Die Datei **InsomniaTest.yaml** auswählen
4. Alle Requests markieren und ausführen

### Erwartetes Ergebnis
- **24 / 24 Tests erfolgreich**

Falls **nicht alle Tests erfolgreich** sind, am besten:

```bash
docker compose down -v
docker compose up -d
```

In den meisten Fällen wurden bereits Daten in der Datenbank verändert.

---

## Hinweise
- Der Ordner `target/` wird automatisch von Maven erzeugt
- Für saubere Tests sollte die Datenbank immer frisch gestartet werden

