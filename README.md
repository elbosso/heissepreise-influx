# heissepreise-influx

<!---
[![start with why](https://img.shields.io/badge/start%20with-why%3F-brightgreen.svg?style=flat)](http://www.ted.com/talks/simon_sinek_how_great_leaders_inspire_action)
--->
[![GitHub release](https://img.shields.io/github/release/elbosso/heissepreise-influx/all.svg?maxAge=1)](https://GitHub.com/elbosso/heissepreise-influx/releases/)
[![GitHub tag](https://img.shields.io/github/tag/elbosso/heissepreise-influx.svg)](https://GitHub.com/elbosso/ImageTagManager/tags/)
[![GitHub license](https://img.shields.io/github/license/elbosso/heissepreise-influx.svg)](https://github.com/elbosso/ImageTagManager/blob/master/LICENSE)
[![GitHub issues](https://img.shields.io/github/issues/elbosso/heissepreise-influx.svg)](https://GitHub.com/elbosso/ImageTagManager/issues/)
[![GitHub issues-closed](https://img.shields.io/github/issues-closed/elbosso/heissepreise-influx.svg)](https://GitHub.com/elbosso/ImageTagManager/issues?q=is%3Aissue+is%3Aclosed)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/elbosso/ImageTagManager/issues)
[![GitHub contributors](https://img.shields.io/github/contributors/elbosso/heissepreise-influx.svg)](https://GitHub.com/elbosso/ImageTagManager/graphs/contributors/)
[![Github All Releases](https://img.shields.io/github/downloads/elbosso/heissepreise-influx/total.svg)](https://github.com/elbosso/ImageTagManager)
[![Website elbosso.github.io](https://img.shields.io/website-up-down-green-red/https/elbosso.github.io.svg)]([https://elbosso.github.io/](https://elbosso.github.io/supermarktpreisentwicklung_influxdb.html#content))

## Überblick

Dieses Projekt importiert historische Preisdaten einiger deutscher/österreichischer Supermärkte in eine InfluxDB 
Zeitreihendatenbank und bietet Mittel, diese durch aktuelle Preisinformationen fortzuschreiben.

```
mvn compile package
```

and then starting the resulting monolithic jar file by issuing

```
$JAVA_HOME/bin/java -jar target/heissepreise-influx-<version>-jar-with-dependencies.jar
```
## Hintergrund

[Supermarktpreisentwicklung in InfluxDB](https://elbosso.github.io/supermarktpreisentwicklung_influxdb.html#content)

## Benutzung

Die Anwendung selbst verfügt über zwei Subcommands: `Update` und `Import`. Update funktioniert derzeit nur für Reqe (DE)
Zu beachten ist, dass die Parameter `INFLUXDB_DATA_MAX_VALUES_PER_TAG` und `INFLUXDB_DATA_MAX_SERIES_PER_DATABASE`
der InfluxDB, die die Daten aufnehmen soll auf *0* gesetzt werden müssen (https://docs.influxdata.com/influxdb/v1/administration/config/#data-settings).

Der Import kann beispielsweise mittels des Kommandos

```
$JAVA_HOME/bin/java -jar target/heissepreise-influx-<version>-jar-with-dependencies.jar Import -l INFO -d prices -H influxdb.docker.lab -P 443 -S HTTPS /home/elbosso/temp/heisse-preise.json
```
gestartet werden - wenn vorher die historischen Daten [heruntergeladen](https://github.com/badlogic/heissepreise) wurden.

### Import

```
Usage: HeissePreiseInfluxdbInterface Import [-hV] [-a=<measurementName>]
       -d=<influxDbDatabasename> [-H=<influxDbHost>] [-l=<logLevel>]
       [-p=<influxDbPassword>] [-P=<influxDbPort>] [-S=<influxDbScheme>]
       [-u=<influxDbUsername>] <jsonFile>
Importiert historische Preisdaten von Supermärkten.
      <jsonFile>   Diese Datei enthält die historischen Daten der Supermärkte.
                     Verfügbar zum Beispiel hier: https://heisse-preise.
                     io/data/latest-canonical.json *(Achtung: über 100 MB!).*
                     Das Ursprungsprojekt ist https://github.
                     com/badlogic/heissepreise .
  -a, -measurementName-=<measurementName>
                   measurementName (default: Product). Das ist der Name des
                     Measurement, unter dem die Daten importiert werden.
  -d, --influxDbDatabasename=<influxDbDatabasename>
                   influxDbDatabasename. Der Name der Influx 1.x Datenbank, in
                     die die Daten geschrieben werden sollen.
  -h, --help       Show this help message and exit.
  -H, --influxDbHost=<influxDbHost>
                   influxDbHost (default: localhost). Der Name oder die
                     IP-Adresse zur Verbindung mit der Influx 1.x Datenbank.
  -l, --logLevel=<logLevel>
                   logLevel (default: WARN). Der Loglevel bestimmt die
                     Ausführlichkeit der Ausgaben während der Bearbeitung.
                     Erleubte Werte sind TRACE, DEBUG, INFO, WARN und ERROR.
  -p, --influxDbPassword=<influxDbPassword>
                   influxDbPassword (default: null). Das PAsswort für die
                     Authentifizierung an der Influx 1.x Datenbank - falls
                     benötigt.
  -P, --influxDbPort=<influxDbPort>
                   influxDbPort (default: 8086). Der Port zur Verbindung mit
                     der Influx 1.x Datenbank
  -S, --influxDbScheme=<influxDbScheme>
                   influxDbScheme (default: HTTP). Erlaubte Werte sind HTTP für
                     Verbindungen zur Influx1.x Datenbank ohne TLS und HTTPS
                     für TLS-verschlüsselte Verbindungen.
  -u, --influxDbUsername=<influxDbUsername>
                   influxDbUsername (default: null). Der Nutzername für die
                     Authentifizierung an der Influx 1.x Datenbank - falls
                     benötigt.
  -V, --version    Print version information and exit.
```

### Update

```
Usage: HeissePreiseInfluxdbInterface Update [-hV] [-a=<measurementName>]
       -d=<influxDbDatabasename> [-H=<influxDbHost>] [-l=<logLevel>]
       [-p=<influxDbPassword>] [-P=<influxDbPort>] [-s=<storeNumber>]
       [-S=<influxDbScheme>] [-u=<influxDbUsername>]
Aktualisiert Preisdaten von Rewe Supermärkten. Derzeit wird nur Rewe (DE)
unterstützt. Weiterewerden folgen. Das Update erfolgt so, dass er mit dem
Import historischer Daten (subcommand Update) zusammen funktioniert. Alle
Informationen werden in ein Measurement importiert und die Informationen zu
einzelnen Produkten können anhang des Tags "name" differenziert werden.
  -a, -measurementName-=<measurementName>
                  measurementName (default: Product). Das ist der Name des
                    Measurement, unter dem die Daten importiert werden.
  -d, --influxDbDatabasename=<influxDbDatabasename>
                  influxDbDatabasename. Der Name der Influx 1.x Datenbank, in
                    die die Daten geschrieben werden sollen.
  -h, --help      Show this help message and exit.
  -H, --influxDbHost=<influxDbHost>
                  influxDbHost (default: localhost). Der Name oder die
                    IP-Adresse zur Verbindung mit der Influx 1.x Datenbank.
  -l, --logLevel=<logLevel>
                  logLevel (default: WARN). Der Loglevel bestimmt die
                    Ausführlichkeit der Ausgaben während der Bearbeitung.
                    Erleubte Werte sind TRACE, DEBUG, INFO, WARN und ERROR.
  -p, --influxDbPassword=<influxDbPassword>
                  influxDbPassword (default: null). Das PAsswort für die
                    Authentifizierung an der Influx 1.x Datenbank - falls
                    benötigt.
  -P, --influxDbPort=<influxDbPort>
                  influxDbPort (default: 8086). Der Port zur Verbindung mit der
                    Influx 1.x Datenbank
  -s, --storeNumber=<storeNumber>
                  storeNumber (default: 440405). Das ist die Nummer, die für
                    die Abfrage dere Preisdaten aus der ReweDigital-API
                    benötigt wird. Die Preise der Produkte können nur
                    Store-spezifisch abgefragt werden, da das Angebot sich
                    zwischen den einzelnen Stores unterscheidet. Möchte man
                    einen anderen als den als default hinterlegten Store
                    abfragen, kann man die benötigte Nummer über einen anderen
                    Endpunkt der API herausfinden (https://github.
                    com/foo-git/rewe-discounts/blob/master/rewe_discounts/rewe_d
                    iscounts.py).
  -S, --influxDbScheme=<influxDbScheme>
                  influxDbScheme (default: HTTP). Erlaubte Werte sind HTTP für
                    Verbindungen zur Influx1.x Datenbank ohne TLS und HTTPS für
                    TLS-verschlüsselte Verbindungen.
  -u, --influxDbUsername=<influxDbUsername>
                  influxDbUsername (default: null). Der Nutzername für die
                    Authentifizierung an der Influx 1.x Datenbank - falls
                    benötigt.
  -V, --version   Print version information and exit.
```
