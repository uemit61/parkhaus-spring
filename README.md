# Parkhaus-Verwaltung mit Spring Boot

Dieselbe Fachlichkeit wie [parkhaus-swing](https://github.com/uemit61/parkhaus-swing),
neu gebaut auf Spring Boot und JPA — die Swing-Oberfläche des Vorgängers läuft
unverändert darauf weiter.

## Funktionen

- Anzeige freier Parkplätze, berechnet aus der Etagen-Kapazität minus der
  belegten Plätze
- Ein- und Ausfahrt mit automatischer Platzvergabe: jeweils der kleinste freie
  Platz, die Etage ergibt sich aus den aufsummierten Kapazitäten
- Positionsabfrage: auf welcher Etage und welchem Platz steht ein Fahrzeug
- Prüfung des Kennzeichen-Formats; Alarm, wenn ein Kennzeichen ein zweites Mal
  einfahren will
- **Fahrzeugtyp als Teil der Identität**: Erscheint ein bekanntes Kennzeichen
  mit einem anderen Typ als dem registrierten, wird kein Platz vergeben, sondern
  Alarm gemeldet — eines von beiden ist gefälscht. Beim Ausfahren müssen
  Kennzeichen und Typ ebenfalls zusammenpassen
- Administrations-Oberfläche zum Registrieren und Löschen von Fahrzeugen, mit
  Fahrzeug- und Parkplatz-Tabelle

## Technik

- **Spring Boot 4.1.1**, Java 21, Maven Wrapper
- **Spring Data JPA** statt handgeschriebenem SQL. Die Repositories sind leere
  Interfaces; die Implementierung erzeugt Spring Data beim Start aus den
  Methodennamen. Der JOIN, der in der Swing-Fassung noch als SQL-Text dastand,
  entsteht aus einem Unterstrich: `findByFahrzeug_Nummernschild`
- **Entitäten ohne eine einzige `@Column`-Angabe.** Die Felder stehen in
  camelCase, die Spalten in snake_case; die Namensstrategie übersetzt zwischen
  beiden. Fremdschlüssel sind als `@ManyToOne`-Beziehungen abgebildet, nicht als
  `int`/`String` — damit kann ein Konstruktor Etage und Platznummer nicht mehr
  verwechseln
- **Flyway** für das Schema: `V1` legt die Struktur an, `V2` die Stammdaten der
  Etagen. Hibernate baut nichts, es prüft nur (`ddl-auto=validate`) — und bricht
  beim Start ab, wenn Modell und Schema auseinanderlaufen
- **Der `ApplicationContext` ersetzt den Composition Root.** Im Vorgänger wurden
  alle Objekte von Hand in `MainGarage` zusammengesteckt; das macht jetzt der
  Container. Übrig bleibt in `ParkhausConfig` nur, was er nicht allein wissen
  kann
- **Desktop-Anwendung, kein Webserver.** `spring.main.web-application-type=none`;
  ein `CommandLineRunner` startet die Oberfläche auf dem Event Dispatch Thread
- **Die Swing-Oberfläche wurde unverändert übernommen.** Von sechs View-Klassen
  sind fünf bitgleich zum Vorgänger; angepasst werden musste genau eine Stelle,
  weil sich der Typ der Nutzdaten geändert hat. Dass die Oberfläche ohne Umbau
  auf einer völlig anderen Datenschicht weiterläuft, ist der eigentliche Beleg
  dafür, dass die Schichtung des Vorgängers getragen hat

## Tests

42 Tests, bewusst auf verschiedenen Ebenen:

| Ebene | Umfang | prüft |
|---|---|---|
| Unit | `FahrzeugServiceTest` (20), `GarageServiceTest` (12) | Fachlogik, ohne Spring und ohne Datenbank |
| Scheibe | `FahrzeugRepositoryTest` (4), `GarageRepositoryTest` (5) | die abgeleiteten Abfragen gegen echtes SQL |
| Kontext | `ParkhausSpringApplicationTests` (1) | dass der Container hochfährt |

Zwei Entscheidungen, die den Unterschied machen:

**Die Repository-Tests laufen gegen eine echte MySQL**, nicht gegen eine
eingebettete Datenbank. Beim Start bestätigt Spring Data nur, dass ein
Methodenname *auflösbar* ist — nicht, dass er das Richtige tut. Genau diese
Lücke schließen sie.

**Die Service-Tests prüfen nicht nur, welche Meldung kommt, sondern auch welcher
Zugriff dabei stattfindet.** Dass die Formatprüfung *vor* dem Datenbankzugriff
steht, ist eine Fachregel — und in `befahren()` hängt mehr daran als eine
überflüssige Abfrage, weil die Registrierung mitten in der Bedingung steht und
schreibt.

## Projektstruktur

```
de.yildirim.parkhaus
├── ParkhausSpringApplication     Einstiegspunkt, Wurzel der Komponentensuche
├── config                        Aufbau der Anwendung, Start der Oberfläche
├── controller                    Bindeglied zwischen View und Fachschicht
├── model
│   ├── entity                    JPA-Abbildung der Tabellen
│   ├── repository                Datenzugriff, Interfaces ohne Implementierung
│   ├── service                   Fachlogik und Transaktionsgrenze
│   └── event                     Ereignis-Kanal zwischen Model und View
└── view                          Swing-Oberfläche
```

Jedes Paket hat eine `package-info.java`, die seine Grenze beschreibt — wo es
einen gibt, mitsamt Lackmustest. Für `model.service` lautet er: Ein
`import jakarta.persistence` oder `java.sql` in diesem Paket ist ein Zeichen
dafür, dass etwas an der falschen Stelle gelandet ist.

## Datenbank einrichten

Es genügt eine leere Datenbank — Tabellen und Stammdaten legt Flyway beim ersten
Start selbst an.

```sql
CREATE DATABASE parkhaus_spring CHARACTER SET utf8mb4;
```

Die Anwendung erwartet MySQL auf **Port 3307** (siehe
`src/main/resources/application.properties`).

### Stammdaten der Etagen

`V2` legt drei Etagen an: 20, 120 und 120 Plätze. **Im Code steht keine feste
Platzzahl** — aus diesen Werten ergeben sich sowohl die Gesamtzahl der Plätze
als auch die Zuordnung eines Platzes zu seiner Etage. Soll das Parkhaus anders
geschnitten sein, ist das eine neue Migration, keine Codeänderung.

Eine ausgeführte Migration ist unveränderlich; Flyway merkt sich ihre Prüfsumme.
Änderungen danach gehören in ein `V3`.

## Konfiguration

Zugangsdaten stehen **nicht** im Repository, sondern kommen als
Umgebungsvariablen herein:

```powershell
[Environment]::SetEnvironmentVariable("DB_USER", "...", "User")
[Environment]::SetEnvironmentVariable("DB_PASSWORD", "...", "User")
```

Danach IntelliJ und PowerShell neu starten — gesetzte Variablen erreichen nur
neu gestartete Prozesse.

> **Falle:** Ist eine Variable nicht gesetzt, meldet Spring das *nicht*. Der Text
> `${DB_USER}` wird wörtlich durchgereicht und landet als Benutzername bei MySQL.
> Die Meldung lautet dann `Access denied for user '${DB_USER}'` — das sieht nach
> einem falschen Passwort aus, ist aber eine fehlende Variable.

## Starten

```bash
./mvnw spring-boot:run
```

Tests:

```bash
./mvnw test
```

Die Repository-Tests brauchen eine Datenbank `parkhaus_spring_test`; Flyway baut
sie beim ersten Lauf selbst auf.

## Weiterentwicklung

v1.0 ist bewusst klein geschnitten: Swing-Oberfläche auf Spring-Datenschicht,
sonst nichts. Dass die Oberfläche dabei unverändert bleibt, ist der Punkt dieser
Version — erst dadurch ist belegt, dass die Fachschicht für sich steht.

Die folgenden Versionen bauen jeweils eine Schicht darauf auf, ohne die darunter
anzufassen:

### v1.1 — Typisierung und Auswahllisten

Die Ereignisnamen werden von Zeichenketten zu einem `enum`. Heute ist ein
Vertipper in `"ZeigePos"` kein Übersetzungsfehler, sondern eine Meldung, die
niemanden erreicht — vor allem beim *Anmelden* eines Zuhörers fällt das kaum auf,
weil nur eine einzelne Ansicht verstummt.

Dazu bekommt die Kennzeichen-Eingabe eine Auswahlliste mit `Fahrzeug`-Objekten
statt abgetipptem Text. Freie Eingabe bleibt möglich, sonst käme kein neues
Fahrzeug mehr ins Parkhaus.

### v1.2 — Springs eigener Ereignismechanismus

`PropertyChangeHandle` weicht dem `ApplicationEventPublisher`. Der Gewinn liegt
weniger im Austausch des Kanals als in den Nutzdaten: Aus einer `List<Object>`
mit vier Einträgen in fester Reihenfolge wird ein `record` mit benannten Feldern.
Damit entfallen der Cast und das `@SuppressWarnings` in der Oberfläche.

Zusätzlich wird `@TransactionalEventListener` interessant: Heute meldet
`befahren()` die vergebene Position, *bevor* die Transaktion festgeschrieben ist.
Einsträngig in Swing ist das folgenlos, bei parallelen Zugriffen nicht mehr.

### v2.0 — REST-Schnittstelle

Eine HTTP-Schnittstelle **auf demselben Service**, mit DTOs als `record` und
Bean Validation an der Grenze. Kommt sie ohne Änderung an der Fachschicht
obendrauf, ist die Schichtung nicht behauptet, sondern vorgeführt — und der
Commit-Verlauf zeigt es nach.

Die Reihenfolge ist Absicht: erst die Typisierung, dann der Mechanismus, dann die
neue Oberfläche. Jeder Schritt macht den nächsten kleiner.

## Bekannte Grenzen

Bewusst offen gelassen:

- Unerwartete Ausnahmen erreichen noch keinen zentralen Handler. Vorgesehen ist
  `Thread.setDefaultUncaughtExceptionHandler` als Swing-Gegenstück zum
  `@ControllerAdvice` aus v2.0 — dieselbe Idee an derselben Stelle im Aufbau:
  eine Instanz am äußeren Rand, die Ausnahmen in etwas übersetzt, das der
  Aufrufer versteht
- Die Tabellenmodelle geben Fehler auf der Konsole aus, statt sie weiterzureichen
- Ein Alarm wird sofort ausgelöst. Künftig soll eine Rückfrage davor stehen, damit
  ein Vertipper nicht als Fälschung behandelt wird

## Entstehung

Entwickelt im Austausch mit **Claude Code**. Die Aufteilung ist bewusst so
gewählt und sei hier offengelegt:

| | |
|---|---|
| **Quellcode und Tests** | schreibe ich selbst — Zeile für Zeile, auch dann, wenn es länger dauert |
| **Javadoc und Commit-Nachrichten** | entstehen gemeinsam: Wir besprechen die Begründung, Claude formuliert sie aus, ich prüfe und übernehme |
| **Code-Review** | Claude prüft und benennt Alternativen, die Entscheidung treffe ich |

Der Grund für diese Trennung: Ich will den Code **verteidigen** können, nicht nur
vorzeigen. Deshalb tippe ich ihn selbst, auch wenn ein Vorschlag schneller wäre.
Dass ich bei der Dokumentation Unterstützung annehme, ist eine andere Frage — sie
beschreibt, was ich bereits verstanden habe.

Diese Arbeitsweise ist der Grund, warum die Kommentare in diesem Projekt
ungewöhnlich ausführlich sind: Sie halten fest, *warum* etwas so gebaut ist, nicht
nur *was* dasteht. Und mehrere der Fallen unter „Was der Umstieg gezeigt hat" sind
genau so entstanden — erst selbst gebaut, dann im Gespräch nachvollzogen, warum
sie sich anders verhalten als erwartet.

## Lizenz

Alle Rechte vorbehalten — siehe [LICENSE](LICENSE).
Lesen zu Informations- und Bewerbungszwecken ist ausdrücklich gestattet.
