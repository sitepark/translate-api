package com.sitepark.translate.provider.libretranslate;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.sitepark.translate.Format;
import com.sitepark.translate.SupportedLanguages;
import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.TranslationProvider;
import com.sitepark.translate.translator.TranslatableText;
import com.sitepark.translate.translator.TranslatableTextListTranslator;

@Disabled
@SuppressWarnings("PMD")
class LibreTranslateTranslationProviderTest {

	private TranslationProvider createProvider() throws IOException, URISyntaxException {
		return new LibreTranslateTranslationProvider(this.createConfiguration());
	}

	private TranslationConfiguration createConfiguration() throws IOException, URISyntaxException {

		LibreTranslateTestConnection con = LibreTranslateTestConnection.get();

		LibreTranslateTranslationProviderConfiguration.Builder builder =
				LibreTranslateTranslationProviderConfiguration.builder()
						.url(con.getUrl());
		if (con.getApiKey() != null) {
			builder.apiKey(con.getApiKey());
		}

		return TranslationConfiguration.builder()
				.translationProviderConfiguration(builder.build())
				.encodePlaceholder(true)
				.build();
	}

	@Test
	void testSupportedLanguages() throws URISyntaxException, IOException, InterruptedException {

		TranslationProvider translator = this.createProvider();

		SupportedLanguages supportedLanguages = translator.getSupportedLanguages();

		assertTrue(supportedLanguages.getAll().size() > 0, "supportedLanguages should not be empty");
	}

	@Test
	void testTranslate() throws URISyntaxException, IOException, InterruptedException {

		TranslationProvider translator = this.createProvider();

		TranslationLanguage translationLanguage = TranslationLanguage.builder()
				.providerType(SupportedProvider.LIBRE_TRANSLATE)
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
	void testTranslateHtml() throws URISyntaxException, IOException, InterruptedException {

		TranslationConfiguration translatorConfiguration = this.createConfiguration();

		List<TranslatableText> translatableTextList = new ArrayList<>();
		translatableTextList.add(new TranslatableText("Hallo Welt & <test>  \"Universum\""));
		translatableTextList.add(new TranslatableText("Hallo Welt &amp; &lt;test&gt; \"Universum\"", Format.HTML));

		TranslationLanguage language = TranslationLanguage.builder()
				.providerType(SupportedProvider.LIBRE_TRANSLATE)
				.source("de")
				.target("en")
				.build();

		TranslatableTextListTranslator translator = TranslatableTextListTranslator.builder()
				.translatorConfiguration(translatorConfiguration)
				.build();

		translator.translate(language, translatableTextList);

		translator.translate(language, translatableTextList);

		System.out.println(translatableTextList.get(0).getTargetText());
		System.out.println(translatableTextList.get(1).getTargetText());

		//assertArrayEquals(new String[] {"Hello", "World"}, res, "Unexpected translation");
	}

	@Test
	void testPerformanceTranslate() throws URISyntaxException, IOException, InterruptedException {

		TranslationProvider translator = this.createProvider();

		TranslationLanguage translationLanguage = TranslationLanguage.builder()
				.providerType(SupportedProvider.LIBRE_TRANSLATE)
				.source("de")
				.target("en")
				.build();

		String[] values = this.bytes10000AsArray();

		int bytes = 0;
		for (String value : values) {
			bytes += value.getBytes(StandardCharsets.UTF_8).length;
		}

		long start = 0;
		long duration = 0;
		String[] res = null;

		System.out.print("as string (" + bytes + " bytes) ... ");
		start = System.currentTimeMillis();
		res = translator.translate(translationLanguage, values);
		duration = (System.currentTimeMillis() - start) / 1000;
		System.out.println(duration + " seconds");

		System.out.println(Arrays.toString(res));
	}

	@SuppressWarnings("checkstyle:LineLength")
	private String bytes10000() {
		return "Kurze Stadtgeschichte in 10.000 Zeichen\n"
				+ "Warum Hamburg ist was es ist: Ein Ritt durch 12 Jahrhunderte f??r alle, die es eilig haben\n"
				+ "\n"
				+ "Es war keine malerische Burg mit Bergfried und dunklem Verlies. Erst seit Anfang 2014 wissen wir ??berhaupt, wo genau sich die ber??hmte Hammaburg, der Hamburg seinen Namen verdankt, befunden hat: Sicher ist zwar, dass es schon im achten Jahrhundert eine s??chsische Burg gab, doch erst nach der Auswertung neuerer Grabungsbefunde sind sich die Experten jetzt einig, dass die im 9. Jahrhundert erbaute Hammaburg, die eigentliche Keimzelle der Stadt, ziemlich zentral auf dem Domplatz gestanden hat. Als Bischof Ansgar 832 hierher kommt, um die Heiden im Norden zu missionieren, trifft er schon auf adelige Burgherren, die dank der verkehrsg??nstigen Lage an Alster, Elbe und Bille ??ber eine prosperierende Handelsstation herrschen. Ungef??hr dort, wo heute die Hauptkirche St. Petri steht, l??sst der Bischof die erste Missionskirche errichten, ein aus heutiger Sicht ebenfalls recht bescheidenes h??lzernes Bauwerk. Lange hat es auch nicht gestanden, denn schon 845 erscheinen die Wikinger mit gro??er Streitmacht, richten ein Blutbad an, brandschatzen und zerst??ren Burg und Siedlung. Ansgar kann in letzter Minute fliehen und zieht sich ins vergleichsweise sichere Bremen zur??ck, das von da an Sitz des Erzbistums ist. Aber da die Lage so verkehrsg??nstig und f??r den Handel zu Wasser und zu Lande so geeignet ist, dauert es nicht lange, bis die verkohlten Tr??mmer beiseite ger??umt werden, um Platz f??r eine neue Siedlung samt Kirche zu machen, die wiederum von einer Burg gesch??tzt wird.\n"
				+ "???\n"
				+ "Ausschnitt eines Plans von Hamburg mit dem Gro??en Grasbrook im Vordergrund, Braun Hogenberg, 1574.\n"
				+ "???\n"
				+ "Hamburgs wichtigstes Exportgut des 14. und 15. Jahrhunderts war das Bier, Titelbild des Hamburger Stadtrechts von 1497\n"
				+ "Exportschlager Bier\n"
				+ "So kriegerisch und blutig es in Hamburgs fr??her Geschichte auch zugeht, weit mehr als alle machtpolitischen Ambitionen der Adelsgeschlechter, die im Laufe der Zeit hier herrschen, interessieren sich die Bewohner der wachsenden Stadt f??rs Gesch??ft. Tats??chlich ist Hamburg von Anfang an eine Handelsstadt und der Austausch von Waren ??ber Wasser und ??ber Land ??? im Sp??tmittelalter sind das vor allem Bier und Getreide ??? bleibt das Erfolgsrezept, dem die Stadt zwischen Alster, Bille und Elbe ihren enormen Aufstieg verdankt. Das zeigt sich auch bald im Stadtbild, wo mit dem Mariendom und  den zun??chst vier Hauptkirchen gotische Bauwerke errichtet werden, deren hoch aufragende T??rme beileibe nicht nur von Gottesfurcht zeugen, sondern durchaus auch vom Stolz wohlhabender B??rger. Aber selbst f??r reiche Kaufleute kann das Leben im Mittelalter gef??hrlich sein, zum Beispiel wenn die Pest die Stadt heimsucht, wie im Jahr 1350, in dem 3.000 Menschen ??? und damit ein Drittel der Stadtbev??lkerung ??? dahingerafft werden.\n"
				+ "\n"
				+ "Mythos St??rtebeker\n"
				+ "Auch Piraten st??ren das Gesch??ft, kapern die mit Getreide oder Bier, Fellen oder Salz beladenen Hansekoggen, deren Ladung die Hamburger Kaufleute oft als Totalverlust abschreiben m??ssen. Im Oktober des Jahres 1400 feiert die Hansestadt den kurz zuvor errungenen Sieg ??ber die Vitalienbr??der, eine ber??chtigte Piratentruppe, die nun auf dem Grasbrook vor aller Augen hingerichtet wird. Klaus St??rtebeker hei??t ihr Anf??hrer, der als Erster gek??pft werden soll. Er bedingt sich noch aus, dass all jene seiner Kameraden begnadigt werden, an denen er nach der Enthauptung vorbeischreiten kann. Doch der B??rgermeister bricht sein Wort, hingerichtet werden alle, anschlie??end nagelt man ihre Sch??del auf weithin sichtbare Holzpf??hle.\n"
				+ "\n"
				+ "Sch??ne Geschichte, nur sollte man auf ihren Wahrheitsgehalt keine Wetten abschlie??en. Denn ob es wirklich St??rtebeker war, ob es diesen legend??ren Piraten ??berhaupt gab, bleibt Spekulation. Aber da Mythen oft einen wahren Kern haben und eben auch zur Geschichte einer Stadt geh??ren, zeigt das Museum f??r Hamburgische Geschichte die Sch??del von Piraten, die am Grasbrook tats??chlich hingerichtet wurden. Denn es gab Seer??uber, die am Grasbrook enthauptet wurden, auch die Sch??del im Museum sind echt, Arbeiter haben sie 1878 beim Bau des Kaispeichers A auf dem Gro??en Grasbrook entdeckt. Und wenn es St??rtebeker gegeben hat, dann befindet sich auch sein Sch??del im Museum f??r Hamburgische Geschichte.\n"
				+ "\n"
				+ "???\n"
				+ "Hinrichtung am Grasbrook\n"
				+ "Vor den Toren der Stadt lag der so genannte Grasbrook, eine unbebaute, flache Insel, die ??ber schmale Br??cken zu erreichen war. Am westlichen Ende, zum Hafen orientiert, war er der Hamburger Richtplatz des Mittelalters und der fr??hen Neuzeit. Hier fanden zwischen 1390???1625 ca. 600 Hinrichtungen statt. Der 600 Jahre alte Sch??del wurde 1878 beim Bau der Speicherstadt auf dem Grasbrook entdeckt. Er wird dem Piraten-Anf??hrer Klaus St??rtebeker zugeschrieben. Der Legende nach wurde er am 20. Oktober 1400 mit rund drei??ig Gef??hrten auf dem Grasbrook enthauptet. Die K??pfe der Hingerichteten wurden zur Abschreckung an der Einfahrt zum damaligen Hafen auf Pf??hle genagelt. Da der Nagel in diesem Sch??del sehr sorgf??ltig eingeschlagen wurde, vermutet man, dass es sich um den Sch??del des ber??hmt-ber??chtigten St??rtebeker handeln k??nnte, den man als m??glichst lange erkennbar erhalten wollte.\n"
				+ "???\n"
				+ "Hamburg von der Elbseite, ??lgem??lde von Elias Galli, um 1680\n"
				+ "Anfang des 15. Jahrhunderts ist Hamburg zwar schon eine recht bedeutende Handelsstadt, im m??chtigen Bund der Hanse aber leider nicht die Nummer Eins. Das ist L??beck, dem Hamburg nur kurzzeitig die F??hrung dieses enorm erfolgreichen St??dtebundes streitig machen kann, der vor allem im Ostseeraum operiert. Die Geschichte ist freilich auf Hamburgs Seite, denn nach der Entdeckung der Neuen Welt verliert die Ostsee an Bedeutung und der Schwerpunkt des Handels verlagert sich auf den Atlantik, zu dem die Elbe den idealen Zugang schafft. Auch sonst versteht man zwischen Elbe und Alster die Zeichen der Zeit, zeigt sich empf??nglich f??r die umw??lzenden Ideen, mit der der Augustinerm??nch Martin Luther 1517 in Wittenberg der alten Ordnung den Kampf ansagt und f??hrt schon 1529 die Reformation ein. Das bringt einen Modernisierungsschub f??r die gesamte Stadtgesellschaft, die sich damit vom Mittelalter verabschiedet und weit ??ber den bisherigen Tellerrand blickt.\n"
				+ "Musikstadt Hamburg\n"
				+ "Zu Beginn des 17. Jahrhunderts erweisen sich die sonst eher knausrigen Ratsherren als weitsichtig, nehmen enorm viel Geld in die Hand und beauftragen einen ber??hmten Festungsbauer damit, das inzwischen deutlich vergr????erte Stadtgebiet mit einem Wallsystem zu umgeben. Diese Gro??investition lohnt sich, denn dank der uneinnehmbaren W??lle und Bastionen bleibt Hamburg von den Verheerungen des Drei??igj??hrigen Krieges verschont. Die Stadt bl??ht auf, was durchaus auch w??rtlich zu verstehen ist, angesichts der pr??chtigen und s??ndhaft teuren G??rten, die sich manche reiche Handelsherren zum puren Vergn??gen leisten und die sogar den einen oder anderen Barockf??rsten den Neid ins Gesicht treiben. Auch sonst l??sst Hamburg manche Residenz bescheiden aussehen. Etwa Anno 1678, als am G??nsemarkt das erste ??ffentliche Opernhaus Deutschlands er??ffnet wird. H??ndel, Telemann oder Carl Philipp Emanuel Bach, aber auch Dichter wie Lessing und der junge Heine zeigen, dass es in der Hansestadt eben nicht nur um gute Gesch??fte geht, sondern auch um Kunst auf h??chstem Niveau.\n"
				+ "\n"
				+ "???\n"
				+ "Die Hamburgische Staatsoper in der Dammtorstra??e, Fotografie 1874, G. Koppmann & Co. Die 1678 er??ffnete Oper am G??nsemarkt war das erste ??ffentliche, jedoch privat finanzierte Theater in Hamburg. Bis 1738 wurden regelm????ig musikalische Werke mit einem festen Ensemble aufgef??hrt. Hier wirkten ber??hmte Musiker wie Georg Friedrich H??ndel (ab 1703) und Georg Philipp Telemann (ab 1722).\n"
				+ "???\n"
				+ "Altonaer Fischmarkt, um 1900. Nachdem der d??nische K??nig Christian IV. 1640 Herzog von Holstein wurde, versuchten die D??nen, der Hansestadt Hamburg Konkurrenz zu machen. 1664 verlieh K??nig Friedrich III. von D??nemark Altona das Stadtrecht. Bereits seit dem fr??hen 18. Jahrhundert werden auf dem Altonaer Fischmarkt auch Obst, Gem??se und Pflanzen verkauft. Der Markt fand auch sonntags fr??h statt, damit der mit den Fangbooten angelandete Fisch morgens noch vor dem Kirchgang verkauft werden konnte und er m??glichst frisch in die herrschaftlichen K??chen gelangte.\n"
				+ "Stadtrechte f??r Altona\n"
				+ "Und elbabw??rts gleich nebenan gewinnt die zum d??nischen Gesamtstaat geh??rende fr??here Fischersiedlung Altona immer mehr an Bedeutung. 1664 verleiht der d??nische K??nig Friedrich III. Altona die Stadtrechte, von da an geht es steil bergauf. Nicht zuf??llig zeigt das Altonaer Wappen ??hnlich wie das Hamburger ein trutziges Stadttor, dessen Fl??gel aber weit ge??ffnet sind. W??hrend man in Hamburg nur lutherisch glauben darf, sind in Altona auch Katholiken, Reformierte und Juden willkommen, was erheblich zur Attraktivit??t und auch zum Reichtum jener Stadt beitr??gt, die Anfang des 18. Jahrhunderts nach Kopenhagen zur Nummer 2 im d??nischen Gesamtstaat aufsteigt. Erst mit dem Gro??-Hamburg-Gesetz von 1937 verliert Altona seine Selbstst??ndigkeit und w??chst mit der Hansestadt zusammen. Doch das kann sich Mitte des 19. Jahrhunderts noch niemand vorstellen.\n"
				+ "Der Brand der Nikolaikirche\n"
				+ "Hans Detlev Christian Martens (1795 ???1866)\n"
				+ "Der gro??e Brand\n"
				+ "Die Katastrophe kommt, als sich Hamburg gerade von den Wirren der Napoleonischen Kriege erholt hat: In der Nacht vom vierten zum f??nften Mai 1842 bricht in einem Speicher auf der Deichstra??e ein Feuer aus, das sp??ter nur noch der Gro??e Brand genannt wird: 1.700 H??user, drei bedeutende Kirchen und das Rathaus sinken in Schutt und Asche, etwa 20.000 Menschen werden buchst??blich ??ber Nacht obdachlos. Schon bald danach entsteht die Stadt neu, nicht mehr mit mittelalterlichen Fachwerkh??usern und verwinkelten Gassen, sondern mit gro??z??gigem Stra??ennetz, Kanalisation, zentraler Gas- und Wasserversorgung und pr??chtigen Geb??uden, wie zum Beispiel den Alsterarkaden, die fast schon an Venedig erinnern.\n"
				+ "\n"
				+ "Als es 1871 zur Gr??ndung des Deutschen Reichs kommt, muss sich die alte Stadtrepublik schon wieder neu erfi nden. Nat??rlich geht es bei den Verhandlungen mit der Berliner Reichsregierung vor allem ums Gesch??ft: um den Handel, um Privilegien, um Z??lle. 1881 schlie??t Hamburg den Zollanschlussvertrag. Als Ausgleich erh??lt die Stadt das Recht, einen Freihafen einzurichten, in dem Importg??ter weiterhin zollfrei umgeschlagen und gelagert werden d??rfen. Letzteres geschieht vor allem in der Speicherstadt, die p??nktlich zum Zollanschluss am 15. Oktober 1888 eingeweiht wird. Dass f??r die Lagerh??user die historische Bebauung der Brookinseln geopfert wird, auf denen 16.000 Menschen gelebt haben, nimmt man als ???Kollateralschaden??? in Kauf. Auch sonst hinterl??sst die Industrielle Revolution in der Stadt zahlreiche Spuren. Auf der Elbe l??sen Dampfer die Segelschiffe ab, an den Ufern rauchen die Schlote neuer Fabriken. Statt der alten Kaufmannsh??user baut man nun Kontore, die elektrisches Licht, Telefone und manchmal sogar Fahrst??hle haben. Anfang des 20. Jahrhunderts bekommt Hamburg eine moderne City, die in Europa keinen Vergleich zu scheuen braucht. Dass die vornehmen Hanseaten, die angeblich so viel Wert auf Understatement legen, durchaus f??r Prunk und Protz anf??llig sind, zeigt das neue Rathaus, das 1897 eingeweiht wird und mit seiner aufwendigen Neorenaissancearchitektur und dem 112 Meter hohen Turm eigentlich eher wie ein Schloss anmutet.\n"
				+ "\n"
				+ "???\n"
				+ "Postkarte vom Jungfernstieg, 1830.\n"
				+ "???\n"
				+ "St. Pauli Spielbudenplatz, Lithografie,1854-1893. Der Spielbudenplatz ist bis heute eines der Zentren der Unterhaltungs- und Vergn??gungskultur St. Paulis. Ende des 18. Jahrhunderts lie??en sich hier K??nstler und Schaussteller mit Schaubuden nieder. Seit dem ausgehenden 19. Jahrhundert sind hier Theater und Veranstaltungslokale zu finden.\n"
				+ "???\n"
				+ "Von 1933 bis 1936 wurde in Hamburg mit dem Handbeil gerichtet. Danach kam eine Guillotine zum Einsatz, auf der fast 400 Menschen starben.\n"
				+ "Im Schatten der Weltkriege\n"
				+ "Nur ein paar Jahre sp??ter, im Sommer 1914, jubeln Tausende Hamburger den Soldaten zu, die f??r Kaiser und Vaterland in den Krieg ziehen. Doch der Jubel verfliegt, denn der Krieg erweist sich schon bald als blutiger Ernst. W??hrend die Soldaten an der Front sterben, wird in der Heimat gehungert. Und diejenigen, die nach vier Jahren zur??ckkehren, erwartet eine unruhige Zeit mit Revolution und Revolte. Die Demokratie, die nun Einzug h??lt, ist ungeliebt und steht auf wackeligen F????en. Auch in Hamburg finden die Nationalsozialisten gro??en Zulauf. Im Fr??hjahr 1933 zieht ins Hamburger Rathaus ein brauner B??rgermeister ein, doch die wirkliche Macht ??bt ein anderer aus: der von Hitler eingesetzte Reichsstatthalter Karl Kaufmann. Der sorgt daf??r, dass auch in der einst so liberalen Hansestadt Andersdenkende verfolgt und verhaftet und Juden deportiert und ermordet werden. Gleich zweimal z??nden SA-Leute 1933 Scheiterhaufen mit unliebsamen B??chern an. F??nf Jahre sp??ter werden die Synagogen demoliert und gesch??ndet und m??ssen auf Kosten der Gemeinden abgerissen oder weit unter Wert verkauft werden. Hamburg entwickelt sich zu einem der gr????ten Einsatzorte f??r Zwangsarbeiter im Deutschen Reich. Zehn Jahre nach der Machtergreifung durch die Nationalsozialisten versinkt Hamburg im Feuersturm, den die Bombergeschwader der Royal Air Force entz??ndet haben. Die ???Operation Gomorrha??? im Juli und August 1943 fordert 35.000 Todesopfer, ein Drittel der Wohnh??user, 58 Kirchen, 24 Krankenh??user und 277 Schulen gehen in Flammen auf.\n"
				+ "Lebenswerte Metropole am Wasser\n"
				+ "Als Hamburg am 3. Mai 1945 kapituliert, fahren die britischen Panzer durch eine Ruinenlandschaft. Doch bald beginnt der Wiederaufbau, bei dem es nicht nur um Stra??en, Br??cken und H??user geht, sondern auch um eine neue demokratische Gesellschaft. Als Stadtstaat findet Hamburg seinen Platz im f??deralen System der Bundesrepublik, f??r deren Wirtschaft der Hafen zentrale Bedeutung erlangt. Hamburg wird zur wichtigsten Medienstadt des Landes, gewinnt aber auch als Industrie- und Hochschulstandort immer mehr an Gewicht. Nach der ??berwindung der deutschen Teilung entwickelt sich die Hansestadt zu einer der attraktivsten europ??ischen Metropolen, deren Einwohnerzahl stetig w??chst und die mit ihrem liberalen Geist, den ??konomischen M??glichkeiten und einer vielf??ltigen Kulturszene Menschen aus vielen L??ndern der Welt anzieht. Im Lauf von zw??lf Jahrhunderten hat Hamburg gl??nzende und schmachvolle Zeiten durchlebt. Immer blickten die Menschen ??ber die Grenze der Stadt hinaus, pflegten den Austausch mit ihren Nachbarn und oft sogar mit fernen Weltgegenden. Und so l??sst sich Hamburgs Geschichte nur als Teil von nationalen und internationalen Entwicklungen verstehen.\n";
	}

	@SuppressWarnings("checkstyle:LineLength")
	private String[] bytes10000AsArray() {
		return new String[] {"Kurze Stadtgeschichte in 10.000 Zeichen\n"
				+ "Warum Hamburg ist was es ist: Ein Ritt durch 12 Jahrhunderte f??r alle, die es eilig haben\n"
				+ "\n"
				+ "Es war keine malerische Burg mit Bergfried und dunklem Verlies. Erst seit Anfang 2014 wissen wir ??berhaupt, wo genau sich die ber??hmte Hammaburg, der Hamburg seinen Namen verdankt, befunden hat: Sicher ist zwar, dass es schon im achten Jahrhundert eine s??chsische Burg gab, doch erst nach der Auswertung neuerer Grabungsbefunde sind sich die Experten jetzt einig, dass die im 9. Jahrhundert erbaute Hammaburg, die eigentliche Keimzelle der Stadt, ziemlich zentral auf dem Domplatz gestanden hat. Als Bischof Ansgar 832 hierher kommt, um die Heiden im Norden zu missionieren, trifft er schon auf adelige Burgherren, die dank der verkehrsg??nstigen Lage an Alster, Elbe und Bille ??ber eine prosperierende Handelsstation herrschen. Ungef??hr dort, wo heute die Hauptkirche St. Petri steht, l??sst der Bischof die erste Missionskirche errichten, ein aus heutiger Sicht ebenfalls recht bescheidenes h??lzernes Bauwerk. Lange hat es auch nicht gestanden, denn schon 845 erscheinen die Wikinger mit gro??er Streitmacht, richten ein Blutbad an, brandschatzen und zerst??ren Burg und Siedlung. Ansgar kann in letzter Minute fliehen und zieht sich ins vergleichsweise sichere Bremen zur??ck, das von da an Sitz des Erzbistums ist. Aber da die Lage so verkehrsg??nstig und f??r den Handel zu Wasser und zu Lande so geeignet ist, dauert es nicht lange, bis die verkohlten Tr??mmer beiseite ger??umt werden, um Platz f??r eine neue Siedlung samt Kirche zu machen, die wiederum von einer Burg gesch??tzt wird.\n"
				+ "???\n",
				"Ausschnitt eines Plans von Hamburg mit dem Gro??en Grasbrook im Vordergrund, Braun Hogenberg, 1574.\n"
				+ "???\n",
				"Hamburgs wichtigstes Exportgut des 14. und 15. Jahrhunderts war das Bier, Titelbild des Hamburger Stadtrechts von 1497\n"
				+ "Exportschlager Bier\n"
				+ "So kriegerisch und blutig es in Hamburgs fr??her Geschichte auch zugeht, weit mehr als alle machtpolitischen Ambitionen der Adelsgeschlechter, die im Laufe der Zeit hier herrschen, interessieren sich die Bewohner der wachsenden Stadt f??rs Gesch??ft. Tats??chlich ist Hamburg von Anfang an eine Handelsstadt und der Austausch von Waren ??ber Wasser und ??ber Land ??? im Sp??tmittelalter sind das vor allem Bier und Getreide ??? bleibt das Erfolgsrezept, dem die Stadt zwischen Alster, Bille und Elbe ihren enormen Aufstieg verdankt. Das zeigt sich auch bald im Stadtbild, wo mit dem Mariendom und  den zun??chst vier Hauptkirchen gotische Bauwerke errichtet werden, deren hoch aufragende T??rme beileibe nicht nur von Gottesfurcht zeugen, sondern durchaus auch vom Stolz wohlhabender B??rger. Aber selbst f??r reiche Kaufleute kann das Leben im Mittelalter gef??hrlich sein, zum Beispiel wenn die Pest die Stadt heimsucht, wie im Jahr 1350, in dem 3.000 Menschen ??? und damit ein Drittel der Stadtbev??lkerung ??? dahingerafft werden.\n"
				+ "\n",
				"Mythos St??rtebeker\n",
				"Auch Piraten st??ren das Gesch??ft, kapern die mit Getreide oder Bier, Fellen oder Salz beladenen Hansekoggen, deren Ladung die Hamburger Kaufleute oft als Totalverlust abschreiben m??ssen. Im Oktober des Jahres 1400 feiert die Hansestadt den kurz zuvor errungenen Sieg ??ber die Vitalienbr??der, eine ber??chtigte Piratentruppe, die nun auf dem Grasbrook vor aller Augen hingerichtet wird. Klaus St??rtebeker hei??t ihr Anf??hrer, der als Erster gek??pft werden soll. Er bedingt sich noch aus, dass all jene seiner Kameraden begnadigt werden, an denen er nach der Enthauptung vorbeischreiten kann. Doch der B??rgermeister bricht sein Wort, hingerichtet werden alle, anschlie??end nagelt man ihre Sch??del auf weithin sichtbare Holzpf??hle.\n"
				+ "\n",
				"Sch??ne Geschichte, nur sollte man auf ihren Wahrheitsgehalt keine Wetten abschlie??en. Denn ob es wirklich St??rtebeker war, ob es diesen legend??ren Piraten ??berhaupt gab, bleibt Spekulation. Aber da Mythen oft einen wahren Kern haben und eben auch zur Geschichte einer Stadt geh??ren, zeigt das Museum f??r Hamburgische Geschichte die Sch??del von Piraten, die am Grasbrook tats??chlich hingerichtet wurden. Denn es gab Seer??uber, die am Grasbrook enthauptet wurden, auch die Sch??del im Museum sind echt, Arbeiter haben sie 1878 beim Bau des Kaispeichers A auf dem Gro??en Grasbrook entdeckt. Und wenn es St??rtebeker gegeben hat, dann befindet sich auch sein Sch??del im Museum f??r Hamburgische Geschichte.\n"
				+ "\n"
				+ "???\n",
				"Hinrichtung am Grasbrook\n",
				"Vor den Toren der Stadt lag der so genannte Grasbrook, eine unbebaute, flache Insel, die ??ber schmale Br??cken zu erreichen war. Am westlichen Ende, zum Hafen orientiert, war er der Hamburger Richtplatz des Mittelalters und der fr??hen Neuzeit. Hier fanden zwischen 1390???1625 ca. 600 Hinrichtungen statt. Der 600 Jahre alte Sch??del wurde 1878 beim Bau der Speicherstadt auf dem Grasbrook entdeckt. Er wird dem Piraten-Anf??hrer Klaus St??rtebeker zugeschrieben. Der Legende nach wurde er am 20. Oktober 1400 mit rund drei??ig Gef??hrten auf dem Grasbrook enthauptet. Die K??pfe der Hingerichteten wurden zur Abschreckung an der Einfahrt zum damaligen Hafen auf Pf??hle genagelt. Da der Nagel in diesem Sch??del sehr sorgf??ltig eingeschlagen wurde, vermutet man, dass es sich um den Sch??del des ber??hmt-ber??chtigten St??rtebeker handeln k??nnte, den man als m??glichst lange erkennbar erhalten wollte.\n"
				+ "???\n",
				"Hamburg von der Elbseite, ??lgem??lde von Elias Galli, um 1680\n",
				"Anfang des 15. Jahrhunderts ist Hamburg zwar schon eine recht bedeutende Handelsstadt, im m??chtigen Bund der Hanse aber leider nicht die Nummer Eins. Das ist L??beck, dem Hamburg nur kurzzeitig die F??hrung dieses enorm erfolgreichen St??dtebundes streitig machen kann, der vor allem im Ostseeraum operiert. Die Geschichte ist freilich auf Hamburgs Seite, denn nach der Entdeckung der Neuen Welt verliert die Ostsee an Bedeutung und der Schwerpunkt des Handels verlagert sich auf den Atlantik, zu dem die Elbe den idealen Zugang schafft. Auch sonst versteht man zwischen Elbe und Alster die Zeichen der Zeit, zeigt sich empf??nglich f??r die umw??lzenden Ideen, mit der der Augustinerm??nch Martin Luther 1517 in Wittenberg der alten Ordnung den Kampf ansagt und f??hrt schon 1529 die Reformation ein. Das bringt einen Modernisierungsschub f??r die gesamte Stadtgesellschaft, die sich damit vom Mittelalter verabschiedet und weit ??ber den bisherigen Tellerrand blickt.\n",
				"Musikstadt Hamburg\n",
				"Zu Beginn des 17. Jahrhunderts erweisen sich die sonst eher knausrigen Ratsherren als weitsichtig, nehmen enorm viel Geld in die Hand und beauftragen einen ber??hmten Festungsbauer damit, das inzwischen deutlich vergr????erte Stadtgebiet mit einem Wallsystem zu umgeben. Diese Gro??investition lohnt sich, denn dank der uneinnehmbaren W??lle und Bastionen bleibt Hamburg von den Verheerungen des Drei??igj??hrigen Krieges verschont. Die Stadt bl??ht auf, was durchaus auch w??rtlich zu verstehen ist, angesichts der pr??chtigen und s??ndhaft teuren G??rten, die sich manche reiche Handelsherren zum puren Vergn??gen leisten und die sogar den einen oder anderen Barockf??rsten den Neid ins Gesicht treiben. Auch sonst l??sst Hamburg manche Residenz bescheiden aussehen. Etwa Anno 1678, als am G??nsemarkt das erste ??ffentliche Opernhaus Deutschlands er??ffnet wird. H??ndel, Telemann oder Carl Philipp Emanuel Bach, aber auch Dichter wie Lessing und der junge Heine zeigen, dass es in der Hansestadt eben nicht nur um gute Gesch??fte geht, sondern auch um Kunst auf h??chstem Niveau.\n"
				+ "\n"
				+ "???\n",
				"Die Hamburgische Staatsoper in der Dammtorstra??e, Fotografie 1874, G. Koppmann & Co. Die 1678 er??ffnete Oper am G??nsemarkt war das erste ??ffentliche, jedoch privat finanzierte Theater in Hamburg. Bis 1738 wurden regelm????ig musikalische Werke mit einem festen Ensemble aufgef??hrt. Hier wirkten ber??hmte Musiker wie Georg Friedrich H??ndel (ab 1703) und Georg Philipp Telemann (ab 1722).\n"
				+ "???\n",
				"Altonaer Fischmarkt, um 1900. Nachdem der d??nische K??nig Christian IV. 1640 Herzog von Holstein wurde, versuchten die D??nen, der Hansestadt Hamburg Konkurrenz zu machen. 1664 verlieh K??nig Friedrich III. von D??nemark Altona das Stadtrecht. Bereits seit dem fr??hen 18. Jahrhundert werden auf dem Altonaer Fischmarkt auch Obst, Gem??se und Pflanzen verkauft. Der Markt fand auch sonntags fr??h statt, damit der mit den Fangbooten angelandete Fisch morgens noch vor dem Kirchgang verkauft werden konnte und er m??glichst frisch in die herrschaftlichen K??chen gelangte.\n",
				"Stadtrechte f??r Altona\n",
				"Und elbabw??rts gleich nebenan gewinnt die zum d??nischen Gesamtstaat geh??rende fr??here Fischersiedlung Altona immer mehr an Bedeutung. 1664 verleiht der d??nische K??nig Friedrich III. Altona die Stadtrechte, von da an geht es steil bergauf. Nicht zuf??llig zeigt das Altonaer Wappen ??hnlich wie das Hamburger ein trutziges Stadttor, dessen Fl??gel aber weit ge??ffnet sind. W??hrend man in Hamburg nur lutherisch glauben darf, sind in Altona auch Katholiken, Reformierte und Juden willkommen, was erheblich zur Attraktivit??t und auch zum Reichtum jener Stadt beitr??gt, die Anfang des 18. Jahrhunderts nach Kopenhagen zur Nummer 2 im d??nischen Gesamtstaat aufsteigt. Erst mit dem Gro??-Hamburg-Gesetz von 1937 verliert Altona seine Selbstst??ndigkeit und w??chst mit der Hansestadt zusammen. Doch das kann sich Mitte des 19. Jahrhunderts noch niemand vorstellen.\n",
				"Der Brand der Nikolaikirche\n",
				"Hans Detlev Christian Martens (1795 ???1866)\n",
				"Der gro??e Brand\n",
				"Die Katastrophe kommt, als sich Hamburg gerade von den Wirren der Napoleonischen Kriege erholt hat: In der Nacht vom vierten zum f??nften Mai 1842 bricht in einem Speicher auf der Deichstra??e ein Feuer aus, das sp??ter nur noch der Gro??e Brand genannt wird: 1.700 H??user, drei bedeutende Kirchen und das Rathaus sinken in Schutt und Asche, etwa 20.000 Menschen werden buchst??blich ??ber Nacht obdachlos. Schon bald danach entsteht die Stadt neu, nicht mehr mit mittelalterlichen Fachwerkh??usern und verwinkelten Gassen, sondern mit gro??z??gigem Stra??ennetz, Kanalisation, zentraler Gas- und Wasserversorgung und pr??chtigen Geb??uden, wie zum Beispiel den Alsterarkaden, die fast schon an Venedig erinnern.\n"
				+ "\n",
				"Als es 1871 zur Gr??ndung des Deutschen Reichs kommt, muss sich die alte Stadtrepublik schon wieder neu erfi nden. Nat??rlich geht es bei den Verhandlungen mit der Berliner Reichsregierung vor allem ums Gesch??ft: um den Handel, um Privilegien, um Z??lle. 1881 schlie??t Hamburg den Zollanschlussvertrag. Als Ausgleich erh??lt die Stadt das Recht, einen Freihafen einzurichten, in dem Importg??ter weiterhin zollfrei umgeschlagen und gelagert werden d??rfen. Letzteres geschieht vor allem in der Speicherstadt, die p??nktlich zum Zollanschluss am 15. Oktober 1888 eingeweiht wird. Dass f??r die Lagerh??user die historische Bebauung der Brookinseln geopfert wird, auf denen 16.000 Menschen gelebt haben, nimmt man als ???Kollateralschaden??? in Kauf. Auch sonst hinterl??sst die Industrielle Revolution in der Stadt zahlreiche Spuren. Auf der Elbe l??sen Dampfer die Segelschiffe ab, an den Ufern rauchen die Schlote neuer Fabriken. Statt der alten Kaufmannsh??user baut man nun Kontore, die elektrisches Licht, Telefone und manchmal sogar Fahrst??hle haben. Anfang des 20. Jahrhunderts bekommt Hamburg eine moderne City, die in Europa keinen Vergleich zu scheuen braucht. Dass die vornehmen Hanseaten, die angeblich so viel Wert auf Understatement legen, durchaus f??r Prunk und Protz anf??llig sind, zeigt das neue Rathaus, das 1897 eingeweiht wird und mit seiner aufwendigen Neorenaissancearchitektur und dem 112 Meter hohen Turm eigentlich eher wie ein Schloss anmutet.\n"
				+ "\n"
				+ "???\n",
				"Postkarte vom Jungfernstieg, 1830.\n"
				+ "???\n",
				"St. Pauli Spielbudenplatz, Lithografie,1854-1893. Der Spielbudenplatz ist bis heute eines der Zentren der Unterhaltungs- und Vergn??gungskultur St. Paulis. Ende des 18. Jahrhunderts lie??en sich hier K??nstler und Schaussteller mit Schaubuden nieder. Seit dem ausgehenden 19. Jahrhundert sind hier Theater und Veranstaltungslokale zu finden.\n"
				+ "???\n",
				"Von 1933 bis 1936 wurde in Hamburg mit dem Handbeil gerichtet. Danach kam eine Guillotine zum Einsatz, auf der fast 400 Menschen starben.\n",
				"Im Schatten der Weltkriege\n",
				"Nur ein paar Jahre sp??ter, im Sommer 1914, jubeln Tausende Hamburger den Soldaten zu, die f??r Kaiser und Vaterland in den Krieg ziehen. Doch der Jubel verfliegt, denn der Krieg erweist sich schon bald als blutiger Ernst. W??hrend die Soldaten an der Front sterben, wird in der Heimat gehungert. Und diejenigen, die nach vier Jahren zur??ckkehren, erwartet eine unruhige Zeit mit Revolution und Revolte. Die Demokratie, die nun Einzug h??lt, ist ungeliebt und steht auf wackeligen F????en. Auch in Hamburg finden die Nationalsozialisten gro??en Zulauf. Im Fr??hjahr 1933 zieht ins Hamburger Rathaus ein brauner B??rgermeister ein, doch die wirkliche Macht ??bt ein anderer aus: der von Hitler eingesetzte Reichsstatthalter Karl Kaufmann. Der sorgt daf??r, dass auch in der einst so liberalen Hansestadt Andersdenkende verfolgt und verhaftet und Juden deportiert und ermordet werden. Gleich zweimal z??nden SA-Leute 1933 Scheiterhaufen mit unliebsamen B??chern an. F??nf Jahre sp??ter werden die Synagogen demoliert und gesch??ndet und m??ssen auf Kosten der Gemeinden abgerissen oder weit unter Wert verkauft werden. Hamburg entwickelt sich zu einem der gr????ten Einsatzorte f??r Zwangsarbeiter im Deutschen Reich. Zehn Jahre nach der Machtergreifung durch die Nationalsozialisten versinkt Hamburg im Feuersturm, den die Bombergeschwader der Royal Air Force entz??ndet haben. Die ???Operation Gomorrha??? im Juli und August 1943 fordert 35.000 Todesopfer, ein Drittel der Wohnh??user, 58 Kirchen, 24 Krankenh??user und 277 Schulen gehen in Flammen auf.\n",
				"Lebenswerte Metropole am Wasser\n",
				"Als Hamburg am 3. Mai 1945 kapituliert, fahren die britischen Panzer durch eine Ruinenlandschaft. Doch bald beginnt der Wiederaufbau, bei dem es nicht nur um Stra??en, Br??cken und H??user geht, sondern auch um eine neue demokratische Gesellschaft. Als Stadtstaat findet Hamburg seinen Platz im f??deralen System der Bundesrepublik, f??r deren Wirtschaft der Hafen zentrale Bedeutung erlangt. Hamburg wird zur wichtigsten Medienstadt des Landes, gewinnt aber auch als Industrie- und Hochschulstandort immer mehr an Gewicht. Nach der ??berwindung der deutschen Teilung entwickelt sich die Hansestadt zu einer der attraktivsten europ??ischen Metropolen, deren Einwohnerzahl stetig w??chst und die mit ihrem liberalen Geist, den ??konomischen M??glichkeiten und einer vielf??ltigen Kulturszene Menschen aus vielen L??ndern der Welt anzieht. Im Lauf von zw??lf Jahrhunderten hat Hamburg gl??nzende und schmachvolle Zeiten durchlebt. Immer blickten die Menschen ??ber die Grenze der Stadt hinaus, pflegten den Austausch mit ihren Nachbarn und oft sogar mit fernen Weltgegenden. Und so l??sst sich Hamburgs Geschichte nur als Teil von nationalen und internationalen Entwicklungen verstehen.\n"
		};
	}
}
