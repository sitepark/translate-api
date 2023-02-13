package com.sitepark.translate.provider.libretranslate;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.sitepark.translate.SupportedLanguages;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationLanguage;

@Disabled
@SuppressWarnings("PMD")
class LibreTranslateTranslationProviderTest {

	private LibreTranslateTranslationProvider createProvider() throws IOException, URISyntaxException {
		LibreTranslateTestConnection con = LibreTranslateTestConnection.get();
		return new LibreTranslateTranslationProvider(
				TranslationConfiguration.builder()
						.translationProviderConfiguration(
								LibreTranslateTranslationProviderConfiguration.builder()
										.url(con.getUrl())
										.apiKey(con.getApiKey())
										.build()
						)
						.build()
		);
	}

	@Test
	void testSupportedLanguages() throws URISyntaxException, IOException, InterruptedException {

		LibreTranslateTranslationProvider translator = this.createProvider();

		SupportedLanguages supportedLanguages = translator.getSupportedLanguages();

		assertTrue(supportedLanguages.getAll().size() > 0, "supportedLanguages should not be empty");
	}

	@Test
	void testTranslate() throws URISyntaxException, IOException, InterruptedException {

		LibreTranslateTranslationProvider translator = this.createProvider();

		TranslationLanguage translationLanguage = TranslationLanguage.builder()
				.source("de")
				.target("en")
				.build();

		String[] res = translator.translate(translationLanguage, new String[] {
				"Hallo", "Welt"
		});

		System.out.println(Arrays.toString(res));

		assertArrayEquals(new String[] {"Hello", "World"}, res, "Unexpected translation");
	}

	@Test
	void testPerformanceTranslate() throws URISyntaxException, IOException, InterruptedException {

		LibreTranslateTranslationProvider translator = this.createProvider();

		TranslationLanguage translationLanguage = TranslationLanguage.builder()
				.source("de")
				.target("en")
				.build();

		String value = this.bytes10000();

		int bytes = value.getBytes(StandardCharsets.UTF_8).length;

		long start = 0;
		long duration = 0;
		String[] res = null;

		System.out.print("as string (" + bytes + " bytes) ... ");
		start = System.currentTimeMillis();
		res = translator.translate(translationLanguage, value);
		duration = (System.currentTimeMillis() - start) / 1000;
		System.out.println(duration + " seconds");

		System.out.println(Arrays.toString(res));
	}

	@SuppressWarnings("checkstyle:LineLength")
	private String bytes10000() {
		return "Kurze Stadtgeschichte in 10.000 Zeichen\n"
				+ "Warum Hamburg ist was es ist: Ein Ritt durch 12 Jahrhunderte für alle, die es eilig haben\n"
				+ "\n"
				+ "Es war keine malerische Burg mit Bergfried und dunklem Verlies. Erst seit Anfang 2014 wissen wir überhaupt, wo genau sich die berühmte Hammaburg, der Hamburg seinen Namen verdankt, befunden hat: Sicher ist zwar, dass es schon im achten Jahrhundert eine sächsische Burg gab, doch erst nach der Auswertung neuerer Grabungsbefunde sind sich die Experten jetzt einig, dass die im 9. Jahrhundert erbaute Hammaburg, die eigentliche Keimzelle der Stadt, ziemlich zentral auf dem Domplatz gestanden hat. Als Bischof Ansgar 832 hierher kommt, um die Heiden im Norden zu missionieren, trifft er schon auf adelige Burgherren, die dank der verkehrsgünstigen Lage an Alster, Elbe und Bille über eine prosperierende Handelsstation herrschen. Ungefähr dort, wo heute die Hauptkirche St. Petri steht, lässt der Bischof die erste Missionskirche errichten, ein aus heutiger Sicht ebenfalls recht bescheidenes hölzernes Bauwerk. Lange hat es auch nicht gestanden, denn schon 845 erscheinen die Wikinger mit großer Streitmacht, richten ein Blutbad an, brandschatzen und zerstören Burg und Siedlung. Ansgar kann in letzter Minute fliehen und zieht sich ins vergleichsweise sichere Bremen zurück, das von da an Sitz des Erzbistums ist. Aber da die Lage so verkehrsgünstig und für den Handel zu Wasser und zu Lande so geeignet ist, dauert es nicht lange, bis die verkohlten Trümmer beiseite geräumt werden, um Platz für eine neue Siedlung samt Kirche zu machen, die wiederum von einer Burg geschützt wird.\n"
				+ "￼\n"
				+ "Ausschnitt eines Plans von Hamburg mit dem Großen Grasbrook im Vordergrund, Braun Hogenberg, 1574.\n"
				+ "￼\n"
				+ "Hamburgs wichtigstes Exportgut des 14. und 15. Jahrhunderts war das Bier, Titelbild des Hamburger Stadtrechts von 1497\n"
				+ "Exportschlager Bier\n"
				+ "So kriegerisch und blutig es in Hamburgs früher Geschichte auch zugeht, weit mehr als alle machtpolitischen Ambitionen der Adelsgeschlechter, die im Laufe der Zeit hier herrschen, interessieren sich die Bewohner der wachsenden Stadt fürs Geschäft. Tatsächlich ist Hamburg von Anfang an eine Handelsstadt und der Austausch von Waren über Wasser und über Land – im Spätmittelalter sind das vor allem Bier und Getreide – bleibt das Erfolgsrezept, dem die Stadt zwischen Alster, Bille und Elbe ihren enormen Aufstieg verdankt. Das zeigt sich auch bald im Stadtbild, wo mit dem Mariendom und  den zunächst vier Hauptkirchen gotische Bauwerke errichtet werden, deren hoch aufragende Türme beileibe nicht nur von Gottesfurcht zeugen, sondern durchaus auch vom Stolz wohlhabender Bürger. Aber selbst für reiche Kaufleute kann das Leben im Mittelalter gefährlich sein, zum Beispiel wenn die Pest die Stadt heimsucht, wie im Jahr 1350, in dem 3.000 Menschen – und damit ein Drittel der Stadtbevölkerung – dahingerafft werden.\n"
				+ "\n"
				+ "Mythos Störtebeker\n"
				+ "Auch Piraten stören das Geschäft, kapern die mit Getreide oder Bier, Fellen oder Salz beladenen Hansekoggen, deren Ladung die Hamburger Kaufleute oft als Totalverlust abschreiben müssen. Im Oktober des Jahres 1400 feiert die Hansestadt den kurz zuvor errungenen Sieg über die Vitalienbrüder, eine berüchtigte Piratentruppe, die nun auf dem Grasbrook vor aller Augen hingerichtet wird. Klaus Störtebeker heißt ihr Anführer, der als Erster geköpft werden soll. Er bedingt sich noch aus, dass all jene seiner Kameraden begnadigt werden, an denen er nach der Enthauptung vorbeischreiten kann. Doch der Bürgermeister bricht sein Wort, hingerichtet werden alle, anschließend nagelt man ihre Schädel auf weithin sichtbare Holzpfähle.\n"
				+ "\n"
				+ "Schöne Geschichte, nur sollte man auf ihren Wahrheitsgehalt keine Wetten abschließen. Denn ob es wirklich Störtebeker war, ob es diesen legendären Piraten überhaupt gab, bleibt Spekulation. Aber da Mythen oft einen wahren Kern haben und eben auch zur Geschichte einer Stadt gehören, zeigt das Museum für Hamburgische Geschichte die Schädel von Piraten, die am Grasbrook tatsächlich hingerichtet wurden. Denn es gab Seeräuber, die am Grasbrook enthauptet wurden, auch die Schädel im Museum sind echt, Arbeiter haben sie 1878 beim Bau des Kaispeichers A auf dem Großen Grasbrook entdeckt. Und wenn es Störtebeker gegeben hat, dann befindet sich auch sein Schädel im Museum für Hamburgische Geschichte.\n"
				+ "\n"
				+ "￼\n"
				+ "Hinrichtung am Grasbrook\n"
				+ "Vor den Toren der Stadt lag der so genannte Grasbrook, eine unbebaute, flache Insel, die über schmale Brücken zu erreichen war. Am westlichen Ende, zum Hafen orientiert, war er der Hamburger Richtplatz des Mittelalters und der frühen Neuzeit. Hier fanden zwischen 1390–1625 ca. 600 Hinrichtungen statt. Der 600 Jahre alte Schädel wurde 1878 beim Bau der Speicherstadt auf dem Grasbrook entdeckt. Er wird dem Piraten-Anführer Klaus Störtebeker zugeschrieben. Der Legende nach wurde er am 20. Oktober 1400 mit rund dreißig Gefährten auf dem Grasbrook enthauptet. Die Köpfe der Hingerichteten wurden zur Abschreckung an der Einfahrt zum damaligen Hafen auf Pfähle genagelt. Da der Nagel in diesem Schädel sehr sorgfältig eingeschlagen wurde, vermutet man, dass es sich um den Schädel des berühmt-berüchtigten Störtebeker handeln könnte, den man als möglichst lange erkennbar erhalten wollte.\n"
				+ "￼\n"
				+ "Hamburg von der Elbseite, Ölgemälde von Elias Galli, um 1680\n"
				+ "Anfang des 15. Jahrhunderts ist Hamburg zwar schon eine recht bedeutende Handelsstadt, im mächtigen Bund der Hanse aber leider nicht die Nummer Eins. Das ist Lübeck, dem Hamburg nur kurzzeitig die Führung dieses enorm erfolgreichen Städtebundes streitig machen kann, der vor allem im Ostseeraum operiert. Die Geschichte ist freilich auf Hamburgs Seite, denn nach der Entdeckung der Neuen Welt verliert die Ostsee an Bedeutung und der Schwerpunkt des Handels verlagert sich auf den Atlantik, zu dem die Elbe den idealen Zugang schafft. Auch sonst versteht man zwischen Elbe und Alster die Zeichen der Zeit, zeigt sich empfänglich für die umwälzenden Ideen, mit der der Augustinermönch Martin Luther 1517 in Wittenberg der alten Ordnung den Kampf ansagt und führt schon 1529 die Reformation ein. Das bringt einen Modernisierungsschub für die gesamte Stadtgesellschaft, die sich damit vom Mittelalter verabschiedet und weit über den bisherigen Tellerrand blickt.\n"
				+ "Musikstadt Hamburg\n"
				+ "Zu Beginn des 17. Jahrhunderts erweisen sich die sonst eher knausrigen Ratsherren als weitsichtig, nehmen enorm viel Geld in die Hand und beauftragen einen berühmten Festungsbauer damit, das inzwischen deutlich vergrößerte Stadtgebiet mit einem Wallsystem zu umgeben. Diese Großinvestition lohnt sich, denn dank der uneinnehmbaren Wälle und Bastionen bleibt Hamburg von den Verheerungen des Dreißigjährigen Krieges verschont. Die Stadt blüht auf, was durchaus auch wörtlich zu verstehen ist, angesichts der prächtigen und sündhaft teuren Gärten, die sich manche reiche Handelsherren zum puren Vergnügen leisten und die sogar den einen oder anderen Barockfürsten den Neid ins Gesicht treiben. Auch sonst lässt Hamburg manche Residenz bescheiden aussehen. Etwa Anno 1678, als am Gänsemarkt das erste öffentliche Opernhaus Deutschlands eröffnet wird. Händel, Telemann oder Carl Philipp Emanuel Bach, aber auch Dichter wie Lessing und der junge Heine zeigen, dass es in der Hansestadt eben nicht nur um gute Geschäfte geht, sondern auch um Kunst auf höchstem Niveau.\n"
				+ "\n"
				+ "￼\n"
				+ "Die Hamburgische Staatsoper in der Dammtorstraße, Fotografie 1874, G. Koppmann & Co. Die 1678 eröffnete Oper am Gänsemarkt war das erste öffentliche, jedoch privat finanzierte Theater in Hamburg. Bis 1738 wurden regelmäßig musikalische Werke mit einem festen Ensemble aufgeführt. Hier wirkten berühmte Musiker wie Georg Friedrich Händel (ab 1703) und Georg Philipp Telemann (ab 1722).\n"
				+ "￼\n"
				+ "Altonaer Fischmarkt, um 1900. Nachdem der dänische König Christian IV. 1640 Herzog von Holstein wurde, versuchten die Dänen, der Hansestadt Hamburg Konkurrenz zu machen. 1664 verlieh König Friedrich III. von Dänemark Altona das Stadtrecht. Bereits seit dem frühen 18. Jahrhundert werden auf dem Altonaer Fischmarkt auch Obst, Gemüse und Pflanzen verkauft. Der Markt fand auch sonntags früh statt, damit der mit den Fangbooten angelandete Fisch morgens noch vor dem Kirchgang verkauft werden konnte und er möglichst frisch in die herrschaftlichen Küchen gelangte.\n"
				+ "Stadtrechte für Altona\n"
				+ "Und elbabwärts gleich nebenan gewinnt die zum dänischen Gesamtstaat gehörende frühere Fischersiedlung Altona immer mehr an Bedeutung. 1664 verleiht der dänische König Friedrich III. Altona die Stadtrechte, von da an geht es steil bergauf. Nicht zufällig zeigt das Altonaer Wappen ähnlich wie das Hamburger ein trutziges Stadttor, dessen Flügel aber weit geöffnet sind. Während man in Hamburg nur lutherisch glauben darf, sind in Altona auch Katholiken, Reformierte und Juden willkommen, was erheblich zur Attraktivität und auch zum Reichtum jener Stadt beiträgt, die Anfang des 18. Jahrhunderts nach Kopenhagen zur Nummer 2 im dänischen Gesamtstaat aufsteigt. Erst mit dem Groß-Hamburg-Gesetz von 1937 verliert Altona seine Selbstständigkeit und wächst mit der Hansestadt zusammen. Doch das kann sich Mitte des 19. Jahrhunderts noch niemand vorstellen.\n"
				+ "Der Brand der Nikolaikirche\n"
				+ "Hans Detlev Christian Martens (1795 –1866)\n"
				+ "Der große Brand\n"
				+ "Die Katastrophe kommt, als sich Hamburg gerade von den Wirren der Napoleonischen Kriege erholt hat: In der Nacht vom vierten zum fünften Mai 1842 bricht in einem Speicher auf der Deichstraße ein Feuer aus, das später nur noch der Große Brand genannt wird: 1.700 Häuser, drei bedeutende Kirchen und das Rathaus sinken in Schutt und Asche, etwa 20.000 Menschen werden buchstäblich über Nacht obdachlos. Schon bald danach entsteht die Stadt neu, nicht mehr mit mittelalterlichen Fachwerkhäusern und verwinkelten Gassen, sondern mit großzügigem Straßennetz, Kanalisation, zentraler Gas- und Wasserversorgung und prächtigen Gebäuden, wie zum Beispiel den Alsterarkaden, die fast schon an Venedig erinnern.\n"
				+ "\n"
				+ "Als es 1871 zur Gründung des Deutschen Reichs kommt, muss sich die alte Stadtrepublik schon wieder neu erfi nden. Natürlich geht es bei den Verhandlungen mit der Berliner Reichsregierung vor allem ums Geschäft: um den Handel, um Privilegien, um Zölle. 1881 schließt Hamburg den Zollanschlussvertrag. Als Ausgleich erhält die Stadt das Recht, einen Freihafen einzurichten, in dem Importgüter weiterhin zollfrei umgeschlagen und gelagert werden dürfen. Letzteres geschieht vor allem in der Speicherstadt, die pünktlich zum Zollanschluss am 15. Oktober 1888 eingeweiht wird. Dass für die Lagerhäuser die historische Bebauung der Brookinseln geopfert wird, auf denen 16.000 Menschen gelebt haben, nimmt man als „Kollateralschaden“ in Kauf. Auch sonst hinterlässt die Industrielle Revolution in der Stadt zahlreiche Spuren. Auf der Elbe lösen Dampfer die Segelschiffe ab, an den Ufern rauchen die Schlote neuer Fabriken. Statt der alten Kaufmannshäuser baut man nun Kontore, die elektrisches Licht, Telefone und manchmal sogar Fahrstühle haben. Anfang des 20. Jahrhunderts bekommt Hamburg eine moderne City, die in Europa keinen Vergleich zu scheuen braucht. Dass die vornehmen Hanseaten, die angeblich so viel Wert auf Understatement legen, durchaus für Prunk und Protz anfällig sind, zeigt das neue Rathaus, das 1897 eingeweiht wird und mit seiner aufwendigen Neorenaissancearchitektur und dem 112 Meter hohen Turm eigentlich eher wie ein Schloss anmutet.\n"
				+ "\n"
				+ "￼\n"
				+ "Postkarte vom Jungfernstieg, 1830.\n"
				+ "￼\n"
				+ "St. Pauli Spielbudenplatz, Lithografie,1854-1893. Der Spielbudenplatz ist bis heute eines der Zentren der Unterhaltungs- und Vergnügungskultur St. Paulis. Ende des 18. Jahrhunderts ließen sich hier Künstler und Schaussteller mit Schaubuden nieder. Seit dem ausgehenden 19. Jahrhundert sind hier Theater und Veranstaltungslokale zu finden.\n"
				+ "￼\n"
				+ "Von 1933 bis 1936 wurde in Hamburg mit dem Handbeil gerichtet. Danach kam eine Guillotine zum Einsatz, auf der fast 400 Menschen starben.\n"
				+ "Im Schatten der Weltkriege\n"
				+ "Nur ein paar Jahre später, im Sommer 1914, jubeln Tausende Hamburger den Soldaten zu, die für Kaiser und Vaterland in den Krieg ziehen. Doch der Jubel verfliegt, denn der Krieg erweist sich schon bald als blutiger Ernst. Während die Soldaten an der Front sterben, wird in der Heimat gehungert. Und diejenigen, die nach vier Jahren zurückkehren, erwartet eine unruhige Zeit mit Revolution und Revolte. Die Demokratie, die nun Einzug hält, ist ungeliebt und steht auf wackeligen Füßen. Auch in Hamburg finden die Nationalsozialisten großen Zulauf. Im Frühjahr 1933 zieht ins Hamburger Rathaus ein brauner Bürgermeister ein, doch die wirkliche Macht übt ein anderer aus: der von Hitler eingesetzte Reichsstatthalter Karl Kaufmann. Der sorgt dafür, dass auch in der einst so liberalen Hansestadt Andersdenkende verfolgt und verhaftet und Juden deportiert und ermordet werden. Gleich zweimal zünden SA-Leute 1933 Scheiterhaufen mit unliebsamen Büchern an. Fünf Jahre später werden die Synagogen demoliert und geschändet und müssen auf Kosten der Gemeinden abgerissen oder weit unter Wert verkauft werden. Hamburg entwickelt sich zu einem der größten Einsatzorte für Zwangsarbeiter im Deutschen Reich. Zehn Jahre nach der Machtergreifung durch die Nationalsozialisten versinkt Hamburg im Feuersturm, den die Bombergeschwader der Royal Air Force entzündet haben. Die „Operation Gomorrha“ im Juli und August 1943 fordert 35.000 Todesopfer, ein Drittel der Wohnhäuser, 58 Kirchen, 24 Krankenhäuser und 277 Schulen gehen in Flammen auf.\n"
				+ "Lebenswerte Metropole am Wasser\n"
				+ "Als Hamburg am 3. Mai 1945 kapituliert, fahren die britischen Panzer durch eine Ruinenlandschaft. Doch bald beginnt der Wiederaufbau, bei dem es nicht nur um Straßen, Brücken und Häuser geht, sondern auch um eine neue demokratische Gesellschaft. Als Stadtstaat findet Hamburg seinen Platz im föderalen System der Bundesrepublik, für deren Wirtschaft der Hafen zentrale Bedeutung erlangt. Hamburg wird zur wichtigsten Medienstadt des Landes, gewinnt aber auch als Industrie- und Hochschulstandort immer mehr an Gewicht. Nach der Überwindung der deutschen Teilung entwickelt sich die Hansestadt zu einer der attraktivsten europäischen Metropolen, deren Einwohnerzahl stetig wächst und die mit ihrem liberalen Geist, den ökonomischen Möglichkeiten und einer vielfältigen Kulturszene Menschen aus vielen Ländern der Welt anzieht. Im Lauf von zwölf Jahrhunderten hat Hamburg glänzende und schmachvolle Zeiten durchlebt. Immer blickten die Menschen über die Grenze der Stadt hinaus, pflegten den Austausch mit ihren Nachbarn und oft sogar mit fernen Weltgegenden. Und so lässt sich Hamburgs Geschichte nur als Teil von nationalen und internationalen Entwicklungen verstehen.\n";
	}

}
