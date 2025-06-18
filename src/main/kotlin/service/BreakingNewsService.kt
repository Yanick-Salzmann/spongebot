package service

import kotlin.random.Random
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentLinkedQueue
import config.BotConfig

class BreakingNewsService {
    private val logger = LoggerFactory.getLogger(BreakingNewsService::class.java)
    private val random = Random
    
    // Cooldown mechanism to avoid repeating recent breaking news
    private val recentBreakingNews = ConcurrentLinkedQueue<Int>()
    
    private val breakingNews = listOf(
        "🚨 **EILMELDUNG: LOKALER MANN ENTDECKT PERFEKTE RAUMTEMPERATUR** 🚨\n" +
        "Ein 34-jähriger Büroangestellter aus Düsseldorf hat heute Morgen die revolutionäre Entdeckung gemacht, dass 22,3°C die absolut perfekte Raumtemperatur ist. Experten sind schockiert, Klimaanlagen in ganz Deutschland haben bereits begonnen zu zittern.",
        
        "🔴 **BREAKING: KAFFEEMASCHINE WEIGERT SICH MONTAG MORGEN ZU ARBEITEN** 🔴\n" +
        "Eine Bürokaffeemaschine in Hamburg hat heute um 07:45 Uhr den Dienst verweigert und protestiert gegen 'unmenschliche Arbeitsbedingungen am Montagmorgen'. Die Polizei ermittelt wegen Verweigerung der Grundversorgung der Bevölkerung.",
        
        "⚠️ **SCHOCKIERENDE NEUIGKEITEN: MANN FINDET BEIDE SOCKEN NACH DER WÄSCHE** ⚠️\n" +
        "In einem beispiellosen Ereignis, das Physiker weltweit sprachlos macht, hat ein Kölner Familienvater heute beide Socken eines Paares in derselben Waschladung wiedergefunden. Das Phänomen wird als 'Wunder der modernen Wissenschaft' bezeichnet.",
        
        "🚨 **EILMELDUNG: LOKALE KATZE IGNORIERT BESITZER ERFOLGREICH FÜR 47 STUNDEN** 🚨\n" +
        "Eine dreijährige Hauskatze aus München hat einen neuen Weltrekord im Ignorieren ihres Besitzers aufgestellt. Trotz ständiger Rufe, Leckerli-Angeboten und verzweifelten Lockversuchen blieb die Katze standhaft und würdigte ihren Menschen keines Blickes.",
        
        "🔴 **BREAKING: STUDENT BEGINNT HAUSARBEIT VOR ABLAUF DER DEADLINE** 🔴\n" +
        "In einer schockierenden Wendung der Ereignisse hat ein Berliner Student seine Semesterarbeit bereits drei Tage vor Abgabetermin begonnen. Universitätsprofessoren sind alarmiert und haben eine Notfallsitzung einberufen.",
        
        "⚠️ **DRAMATISCHE ENTWICKLUNG: MANN FINDET PARKPLATZ IM ERSTEN VERSUCH** ⚠️\n" +
        "Ein 42-jähriger Autofahrer aus Stuttgart hat heute das Unmögliche geschafft und auf Anhieb einen Parkplatz direkt vor seinem Zielort gefunden. Zeugen sprechen von einem 'Wunder des städtischen Verkehrswesens'.",
        
        "🚨 **EILMELDUNG: SMARTPHONE-AKKU HÄLT GANZEN TAG OHNE NACHLADEN** 🚨\n" +
        "In einer sensationellen Entwicklung hat der Akku eines drei Jahre alten Smartphones erstmals seit dem Kauf einen ganzen Tag ohne Aufladen überstanden. Technikexperten sind ratlos und sprechen von 'unmöglichen physikalischen Gesetzmäßigkeiten'.",
        
        "🔴 **BREAKING: AUFZUG KOMMT SOFORT NACHDEM KNOPF GEDRÜCKT WURDE** 🔴\n" +
        "Bewohner eines Frankfurter Hochhauses stehen unter Schock, nachdem der Aufzug heute Morgen innerhalb von 3 Sekunden erschien, nachdem der Rufknopf betätigt wurde. Aufzugtechniker können das Phänomen nicht erklären.",
        
        "⚠️ **SENSATIONELLE MELDUNG: KASSENSCHLANGE IM SUPERMARKT BEWEGT SICH FLÜSSIG** ⚠️\n" +
        "Kunden in einem Hamburger Supermarkt erlebten heute das Undenkbare: Eine Kassenschlange, die sich kontinuierlich und ohne Verzögerung bewegte. Niemand suchte ewig nach Kleingeld, niemand hatte vergessen etwas aus dem obersten Regal zu holen.",
        
        "🚨 **EILMELDUNG: WETTER-APP SAGT WETTER KORREKT VORAUS** 🚨\n" +
        "In einem historischen Moment hat eine Wetter-App heute das tatsächliche Wetter zu 100% korrekt vorhergesagt. Meteorologen weltweit sind in heller Aufregung und sprechen von einer 'Revolution der Wettervorhersage'.",
        
        "🔴 **BREAKING: DEUTSCHER PÜNKTLICH ZU TERMIN ERSCHIENEN** 🔴\n" +
        "Ein Münchener Geschäftsmann hat heute internationale Schlagzeilen gemacht, indem er nicht nur pünktlich, sondern sogar 2 Minuten zu früh zu seinem Termin erschien. Das Phänomen wird als 'Triumph der deutschen Ingenieurskunst' gefeiert.",
        
        "⚠️ **SCHOCKIERENDE NEUIGKEITEN: ALLE AMPELN AUF ARBEITSWEG ZEIGEN GRÜN** ⚠️\n" +
        "Eine Pendlerin aus Köln erlebte auf ihrem Weg zur Arbeit das Unvorstellbare: Jede einzelne Ampel zeigte grünes Licht. Verkehrsexperten sprechen von einem 'statistischen Unmöglichkeitsphänomen' und haben eine Untersuchungskommission eingesetzt.",
        
        "🚨 **EILMELDUNG: MANN ERINNERT SICH AN ALLE PUNKTE AUF EINKAUFSLISTE** 🚨\n" +
        "Ein Familienvater aus Essen hat Geschichte geschrieben, indem er alle Artikel seiner mentalen Einkaufsliste kaufte, ohne etwas zu vergessen. Seine Frau ist in psychotherapeutischer Behandlung wegen des Schocks.",
        
        "🔴 **BREAKING: INTERNETVERBINDUNG FUNKTIONIERT WÄHREND WICHTIGER VIDEOKONFERENZ** 🔴\n" +
        "In einer beispiellosen Wendung der Ereignisse blieb eine Internetverbindung während einer dreistündigen Videokonferenz stabil. IT-Experten sind verwirrt und prüfen, ob das Universum noch korrekt funktioniert.",
        
        "⚠️ **DRAMATISCHE ENTWICKLUNG: DRUCKERTINTE REICHT FÜR GESAMTES DOKUMENT** ⚠️\n" +
        "Ein Büroarbeiter in Nürnberg konnte ein 50-seitiges Dokument drucken, ohne dass die Druckertinte während des Vorgangs zur Neige ging. Der Drucker wird nun von Wissenschaftlern untersucht.",
        
        "🚨 **EILMELDUNG: ALLE USB-STECKER PASSEN BEIM ERSTEN VERSUCH** 🚨\n" +
        "In einem physikalisch unmöglichen Ereignis passten heute alle USB-Stecker eines Hamburger Programmierers beim ersten Einsteckversuch. Die Gesetze der Quantenphysik werden derzeit überprüft.",
        
        "🔴 **BREAKING: BAHN KOMMT PÜNKTLICH UND FÄHRT OHNE VERZÖGERUNG** 🔴\n" +
        "Fahrgäste der Deutschen Bahn stehen unter Schock: Ein ICE erreichte nicht nur pünktlich sein Ziel, sondern fuhr die gesamte Strecke ohne eine einzige Verspätungsansage. Die DB-Zentrale hat einen Krisenstab einberufen.",
        
        "⚠️ **SENSATIONELLE MELDUNG: HANDY-AKKU LÄDT IN ANGEKÜNDIGTER ZEIT** ⚠️\n" +
        "Ein Smartphone hat heute tatsächlich in der vom Hersteller angegebenen Zeit von 30 Minuten auf 80% geladen. Verbraucherschützer sind alarmiert und prüfen, ob hier Betrug im Spiel ist.",
        
        "🚨 **EILMELDUNG: MANN FINDET FERNBEDIENUNG IM ERSTEN SOFA-POLSTER** 🚨\n" +
        "In einem Haushaltswunder sondergleichen fand ein Düsseldorfer die verschwundene TV-Fernbedienung bereits im ersten durchsuchten Sofa-Polster. Möbelexperten sprechen von einem 'Durchbruch in der Fernbedienungsforschung'.",
        
        "🔴 **BREAKING: SUPERMARKT-EINKAUFSWAGEN FÄHRT GERADEAUS** 🔴\n" +
        "Kunden eines Bremer Supermarkts erlebten das Unmögliche: Ein Einkaufswagen, der sich geradeaus lenken ließ, ohne ständig nach links zu ziehen. Die Physik, wie wir sie kennen, steht vor dem Kollaps.",
        
        "⚠️ **SCHOCKIERENDE NEUIGKEITEN: ALLE KÜCHENGERÄTE PASSEN IN SPÜLMASCHINE** ⚠️\n" +
        "Eine Hausfrau aus Leipzig schaffte es, alle dreckigen Töpfe, Pfannen und Teller nach dem Kochen in einem einzigen Spülmaschinengang unterzubringen. Tetris-Weltmeister sind neidisch auf diese Leistung.",
        
        "🚨 **EILMELDUNG: WLAN-PASSWORT FUNKTIONIERT BEIM ERSTEN EINGABEVERSUCH** 🚨\n" +
        "Ein Besucher konnte sich heute ohne Probleme beim ersten Versuch ins Gäste-WLAN einloggen. IT-Techniker sind ratlos und untersuchen, ob hier übernatürliche Kräfte im Spiel sind.",
        
        "🔴 **BREAKING: KELLNER BRINGT BESTELLUNG OHNE NACHFRAGEN** 🔴\n" +
        "In einem Restaurant in Heidelberg brachte ein Kellner die komplette Bestellung einer 8-köpfigen Familie korrekt und ohne Rückfragen an den Tisch. Gastronomen sprechen von einem 'Meilenstein der Servicequalität'.",
        
        "⚠️ **DRAMATISCHE ENTWICKLUNG: STAU LÖST SICH OHNE ERKENNBAREN GRUND AUF** ⚠️\n" +
        "Autofahrer auf der A1 erlebten ein Verkehrswunder: Ein kilometerlanger Stau löste sich plötzlich auf, ohne dass ein Unfall, eine Baustelle oder ein Geisterfahrer die Ursache gewesen wäre. Verkehrspsychologen sind sprachlos.",
        
        "🚨 **EILMELDUNG: AKKU-ANZEIGE ZEIGT TATSÄCHLICHE RESTLAUFZEIT AN** 🚨\n" +
        "Das Laptop eines Studenten zeigte heute eine Akku-Restlaufzeit von 2 Stunden an - und hielt tatsächlich exakt 2 Stunden durch. Computerhersteller dementieren jegliche Beteiligung an diesem Phänomen."    )
      /**
     * Get a random breaking news message in German, avoiding recently shown ones
     */
    fun getRandomBreakingNews(): String {
        val maxRecentNews = BotConfig.getBreakingNewsCooldownSize()
        val availableIndices = breakingNews.indices.filter { index -> 
            !recentBreakingNews.contains(index)
        }
        
        val selectedIndex = if (availableIndices.isNotEmpty()) {
            // Select from non-recent breaking news
            availableIndices.random(random)
        } else {
            // If all breaking news have been shown recently, clear the history and pick any
            logger.debug("All breaking news recently shown, clearing history")
            recentBreakingNews.clear()
            breakingNews.indices.random(random)
        }
        
        // Add to recent breaking news
        recentBreakingNews.offer(selectedIndex)
        
        // Keep only the most recent breaking news in memory
        while (recentBreakingNews.size > maxRecentNews) {
            recentBreakingNews.poll()
        }
        
        val news = breakingNews[selectedIndex]
        logger.debug("Selected breaking news #$selectedIndex: ${news.take(50)}... (${recentBreakingNews.size}/$maxRecentNews recent news in memory)")
        return news
    }
    
    /**
     * Determine if a breaking news should be shown based on configured probability
     * @param probability The probability (0.0 to 1.0) of showing breaking news
     */
    fun shouldShowBreakingNews(probability: Double = 0.03): Boolean { // 3% default chance
        val shouldShow = random.nextDouble() < probability
        logger.debug("Breaking news probability check: $probability, result: $shouldShow")
        return shouldShow
    }
      /**
     * Get the total number of available breaking news
     */
    fun getBreakingNewsCount(): Int = breakingNews.size
    
    /**
     * Clear the recent breaking news history to reset cooldown
     */
    fun clearCooldownHistory() {
        recentBreakingNews.clear()
        logger.info("Breaking news cooldown history cleared")
    }
    
    /**
     * Get the current cooldown status
     */
    fun getCooldownStatus(): String {
        val maxRecentNews = BotConfig.getBreakingNewsCooldownSize()
        return "Recent breaking news: ${recentBreakingNews.size}/$maxRecentNews"
    }
}
