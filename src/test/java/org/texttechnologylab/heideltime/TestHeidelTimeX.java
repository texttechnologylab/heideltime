package org.texttechnologylab.heideltime;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unihd.dbs.uima.annotator.heideltime.resources.Language;
import de.unihd.dbs.uima.types.heideltime.Timex3;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collection;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestHeidelTimeX {
    AnalysisEngine engine;
    JCas jCas;

    @BeforeAll
    public void setUp() throws ResourceInitializationException, CASException {
        jCas = JCasFactory.createJCas();
        engine = AnalysisEngineFactory.createEngine(
                HeidelTimeX.class,
//                HeidelTimeX.PARAM_PARALLEL_SEARCH, true,
                HeidelTimeX.PARAM_LANGUAGE, Language.GERMAN,
                HeidelTimeX.PARAM_DEBUG, false,
                HeidelTimeX.PARAM_TYPE_TO_PROCESS, "narrative",
                HeidelTimeX.PARAM_FIND_DATES, true,
                HeidelTimeX.PARAM_FIND_TIMES, true,
                HeidelTimeX.PARAM_FIND_DURATIONS, true,
                HeidelTimeX.PARAM_FIND_SETS, true,
                HeidelTimeX.PARAM_FIND_TEMPONYMS, true,
                HeidelTimeX.PARAM_GROUP_GRAN, true
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "190 v. Chr.", // EXAMPLE date_historic_1a-BCADhint: (1- to 4-digit year)
            "v. Chr. 190", // EXAMPLE date_historic_1b-BCADhint: (1- to 4-digit year)
            "190 bis 180 v. Chr.", // EXAMPLE date_historic_1c-BCADhint: (find "190 v. Chr."; 1- to 4-digit year)
            "Anfang 190 v. Chr.", // EXAMPLE date_historic_1d-BCADhint: (1- to 4-digit year)
            "Anfang v. Chr. 190 v. Chr.", // EXAMPLE date_historic_1e-BCADhint: (1- to 4-digit year)
            "Anfang 190 bis 180 v. Chr.", // EXAMPLE date_historic_1f-BCADhint: (find "Anfang 190 v. Chr."; 1- to 4-digit year)
            "Januar 190 v. Chr.", // EXAMPLE date_historic_2a-BCADhint: (1- to 4-digit year)
            "Januar 190", // EXAMPLE date_historic_2b: (3-digit year)
            "Januar 90", // EXAMPLE date_historic_2c: (2-digit year)
            "Anfang Januar 190 v. Chr.", // EXAMPLE date_historic_2d-BCADhint: (1- to 4-digit year)
            "Anfang Januar 190", // EXAMPLE date_historic_2e: (3-digit year)
            "Anfang Januar 90", // EXAMPLE date_historic_2f: (2-digit year)
            "1. Januar 190 v. Chr.", // EXAMPLE date_historic_3a-BCADhint: (1- to 4-digit year)
            "1. Januar 190", // EXAMPLE date_historic_3b: (3-digit year)
            "1. Januar 90", // EXAMPLE date_historic_3c: (2-digit year)
            "1. - 15. Januar 90", // EXAMPLE date_historic_3d: (find "1. Januar 90"; 2-digit year)
            "Winter 190 v. Chr.", // EXAMPLE date_historic_4a-BCADhint: (1- to 4-digit year)
            "Mitte Winter 190 v.Chr.", // EXAMPLE date_historic_4b-BCADhint: (1- to 4-digit year)
            "das 5. Jahrhundert v. Chr.", // EXAMPLE date_historic_5a-BCADhint
            "Jahr 90", // EXAMPLE date_historic_6a: (2-digit year)
            "Jahr 190", // EXAMPLE date_historic_6b: (3-digit year)
            "2010-01-29", // EXAMPLE r0a_1
            "10-29-99", // EXAMPLE r0b_1
            "09/26/1999", // EXAMPLE r0c_1
            "09/26/99", // EXAMPLE r0d_1
            "7-14 (AP)", // EXAMPLE r0e_1: (find 7-14)
            "1.3.99", // EXAMPLE r1a_1
            "1.3.1999", // EXAMPLE r1b_1
            "Februar 25, 2009", // EXAMPLE r2a_1
            "Feb. 25, 2009", // EXAMPLE r2a_2
            "25. Februar 2009", // EXAMPLE r3a_1
            "25 Feb 2009", // EXAMPLE r3a_2
            "25 Feb. 2009", // EXAMPLE r3a_3
            "25. November des Jahres 2001", // EXAMPLE r3a_4
            "November 19", // EXAMPLE r4a_1
            "19. November", // EXAMPLE r4b_1
            "November 15 - 18", // EXAMPLE r4c_1: (find November 18)
            "19. und 20. Januar", // EXAMPLE r4d_1: (find 19. Januar)
            "Freitag Oktober 13", // EXAMPLE r5a_1
            "Freitag 13. Oktober", // EXAMPLE r5b_1
            "14. und 15. September 2010", // EXAMPLE r6a_1: (find: 14. September 2010)
            "Friday Oktober 13 2009", // EXAMPLE r7a_1
            "morgen", // EXAMPLE 8a_1
            "Montag", // EXAMPLE r9a_1
            "November 2001", // EXAMPLE r10a_1
            "Nov. 2001", // EXAMPLE r10a_2
            "Mai and Juni 2011", // EXAMPLE r10b_1: (find Mai 2001)
            "November diesen Jahres", // EXAMPLE r11a_1
            "Sommer", // EXAMPLE r12a_1
            "Sommerzeit", // EXAMPLE r12a_2
            "Spätsommerzeit", // EXAMPLE r12a_3
            "Sommer 2001", // EXAMPLE r12b_1
            "Sommer 69", // EXAMPLE r12c_1
            "das erste Quartal 2001", // EXAMPLE r13a_1
            "das erste Quartal", // EXAMPLE r13a_1
            "2009", // EXAMPLE r14a_1
            "Jahr 2009", // EXAMPLE r14a_2
            "1850-58", // EXAMPLE r15a_1: (find: 1858)
            "1850/51", // EXAMPLE r15a_2: (find: 1851)
            "neunzehnsechsundneunzig", // EXAMPLE r16a_1
            "Das 20. Jahrhundert", // EXAMPLE r17a_1
            "Im 18. und 19. Jahrhundert", // EXAMPLE r17b_1: (find: 17. Jahrhundert)
            "das 17. Jahrhundert", // EXAMPLE 2
            "März", // EXAMPLE r18a_1
            "Anfang 1999", // EXAMPLE r18b_1
            "Anfang November 1999", // EXAMPLE r18c_1
            "Anfang November 2000", // EXAMPLE r18d_1
            "die 1920er Jahre", // EXAMPLE r19a_1
            "die 20er Jahre", // EXAMPLE r19b_1
            "die frühen 1920er Jahre", // EXAMPLE r19a_1
            "die frühen 20er Jahre", // EXAMPLE r19b_1
            "dieses Jahr", // EXAMPLE r20a_1
            "gleichen Tag", // EXAMPLE r20b_1
            "diesen November", // EXAMPLE r20c_1
            "diesen Montag", // EXAMPLE r20d_1
            "diesen Sommer", // EXAMPLE r20e_1
            "Anfang diesen Jahres", // EXAMPLE r21a_1
            "Anfang dieses Novembers", // EXAMPLE r21b_1
            "Anfang dieses Montags", // EXAMPLE r21c_1
            "Anfang dieses Sommers", // EXAMPLE r21d_1
            "letztes Wochenende", // EXAMPLE r22a_1
            "das letztjährige Quartal", // EXAMPLE r23a_1
            "das Quartal", // EXAMPLE r23b_1
            "ein Jahr früher", // EXAMPLE r24a_1
            "ein Jahr später", // EXAMPLE r24b_2
            "etwa zehn Tage später", // EXAMPLE r25a_1
            "etwa 20 Jahre später", // EXAMPLE r25b_1
            "etwa ein Jahr später", // EXAMPLE r25c_1
            "etwa zehn Tage früher", // EXAMPLE r25d_1
            "etwa 20 Tage früher", // EXAMPLE r25e_1
            "etwa ein Tag früher", // EXAMPLE r25f_1
            "Neujahr", // EXAMPLE r27a_1
            "Neujahr 2010", // EXAMPLE r27b_1
            "Neujahr 87", // EXAMPLE r27c_1
            "Ostermontag", // EXAMPLE r28a_1
            "Ostermontag 2010", // EXAMPLE r28b_1
            "Ostermontag 87", // EXAMPLE r28c_1
            "Sommerzeit", // EXAMPLE biofid_zeit_r1
            "Mittagszeit", // EXAMPLE biofid_zeit_r1
            "diesjährig", // EXAMPLE biofid_adjunit_r1
            "Vorjahre", // EXAMPLE biofid_vor_r1
            "Herbstferien", // EXAMPLE biofid_ferien_r1
            "Sommer-Ferien", // EXAMPLE biofid_ferien_r2
            "ein Jahr zuvor", // EXAMPLE biofid_relyear_r1
            "13 Jahre später", // EXAMPLE biofid_relyear_r2
            "vor drei Jahren", // EXAMPLE biofid_relyear_r3
            "vor 3 Jahren", // EXAMPLE biofid_relyear_r3
            "vor 4 Monaten", // EXAMPLE biofid_prev_unit_r6_a
            "das Jahr zuvor", // EXAMPLE biofid_prevunit_r3: (see also above "ein Jahr zuvor" and so on)
            "3 Quartale zuvor", // EXAMPLE biofid_prevunit_r4
            "das Jahresende", // EXAMPLE biofid_endof_r1
            "das laufende Quartal", // EXAMPLE biofid_endof_r1
            "Vorjahreszeitraum", // EXAMPLE biofid_prevunit_r1
            "Vorjahresquartal", // EXAMPLE biofid_prevunit_r2
            "vorherigen Freitag", // EXAMPLE biofid_prevunit_r2_a
            "Folgemonat", // EXAMPLE biofid_folge_r1
    })
    public void test_german_daterules(String input) throws ResourceInitializationException, CASException, AnalysisEngineProcessException {
        runSingleSentence(input);
        Collection<Timex3> timex3s = JCasUtil.select(jCas, Timex3.class);
        printAnnotations(timex3s);
        if (timex3s.isEmpty()) {
            Assertions.fail("Expected at least one Timex3 for '%s' but got %d".formatted(input, timex3s.size()));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "MiG-190", // EXAMPLE date_historic_0a_negative: (1- to 4-digit year)
            "90 Menschen", // EXAMPLE date_historic_0b_negative: (2-digit year)
            "Märchen aus 1001 Nacht", // EXAMPLE r1e1_negative
            "1001-Nacht", // EXAMPLE r1e2_negative
            "Sonnenfelsgasse 19, 1010 Wien", // EXAMPLE r2a_negative
            "1010 Wien", // EXAMPLE r2b_negative
            "1600 Pennsylvania Avenue", // EXAMPLE r2c_negative
            "Sitzungssaal 1901", // EXAMPLE r2d_negative
            "1200 davon sind tot", // EXAMPLE r3a_negative
            "mindestens 2000 sind tot", // EXAMPLE r3b_negative
            "von 2000 auf 1800 reduziert", // EXAMPLE r3c_negative
            "UN Resolution 1441", // EXAMPLE r4a_negative
    })
    public void test_german_daterules_negative(String input) throws ResourceInitializationException, CASException, AnalysisEngineProcessException {
        runSingleSentence(input);
        Collection<Timex3> timex3s = JCasUtil.select(jCas, Timex3.class);
        if (!timex3s.isEmpty()) {
            printAnnotations(timex3s);
            Assertions.fail("Expected no Timex3 for '%s' but found %d".formatted(input, timex3s.size()));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "etwa fünf Tage", // EXAMPLE r1a_1
            "etwa 20 Tage", // EXAMPLE r1b_1
            "etwa fünf Stunden", // EXAMPLE r1c_1
            "etwa 20 Stunden", // EXAMPLE r1d_1
            "die nächsten zwanzig Tage", // EXAMPLE r2a_1
            "die nächsten 20 Tage", // EXAMPLE r2b_1
            "die nächsten paar Tage", // EXAMPLE r2c_1
            "die nächsten zwanzig Minuten", // EXAMPLE r2d_1
            "die nächsten 20 Minuten", // EXAMPLE r2e_1
            "die nächsten paar Minuten", // EXAMPLE r2f_1
            "ein Jahr", // EXAMPLE r3a_1
            "eine Stunde", // EXAMPLE r3b_1
            "20-tägig", // EXAMPLE r3c_1
            "20-stündig", // EXAMPLE r3d_1
            "viele Sommer", // EXAMPLE biofid_quantdur_r1
            "mehrere Quartale", // EXAMPLE biofid_quantdur_r2
            "paar Stunden", // EXAMPLE biofid_quantdur_r3
            "halbes Quartal", // EXAMPLE biofid_unitdur_r1
            "Vierteljahrhundert", // EXAMPLE biofid_unitdur_r2
            "zweieinhalb Jahre", // EXAMPLE biofid_unitdur_r3_1
            "2-einhalb Jahre", // EXAMPLE biofid_unitdur_r3_2
            "zweistündig", // EXAMPLE biofid_unitdur_r1_a
            "elfjährig", // EXAMPLE biofid_unitdur_r1_b
            "11-tägig", // EXAMPLE biofid_unitdur_r2_a
            "ganztägig", // EXAMPLE biofid_unitdur_r2_b_1: overgeneralises over UnitAdj
            "ganzjährig", // EXAMPLE biofid_unitdur_r2_b_1
    })
    public void test_german_durationrules(String input) throws ResourceInitializationException, CASException, AnalysisEngineProcessException {
        runSingleSentence(input);
        Collection<Timex3> timex3s = JCasUtil.select(jCas, Timex3.class);
        if (timex3s.isEmpty()) {
            printAnnotations(timex3s);
            Assertions.fail("Expected at least one Timex3 for '%s' but got %d".formatted(input, timex3s.size()));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "zwanzig Jahre alt", // EXAMPLE r1a_negation_1
            "20 Jahre alt", // EXAMPLE r1b_negation_1
            "einige Jahre alt", // EXAMPLE r1c_negation_1
    })
    public void test_german_durationrules_negative(String input) throws ResourceInitializationException, CASException, AnalysisEngineProcessException {
        runSingleSentence(input);
        Collection<Timex3> timex3s = JCasUtil.select(jCas, Timex3.class);
        if (!timex3s.isEmpty()) {
            printAnnotations(timex3s);
            Assertions.fail("Expected no Timex3 for '%s' but found %d".formatted(input, timex3s.size()));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "von 1999 bis 2012", // EXAMPLE interval_01
            "zwischen März und Mai", // EXAMPLE interval_02
            "20.3.2003 - 1.5.2003", // EXAMPLE interval_03
            "20.3.2003 bis 1.5.2003", // EXAMPLE interval_04
            "begann im März 2003 und endete im Mai 2003", // EXAMPLE interval_05
            "2012/2013", // EXAMPLE interval_06
    })
    public void test_german_intervalrules(String input) throws ResourceInitializationException, CASException, AnalysisEngineProcessException {
        runSingleSentence(input);
        Collection<Timex3> timex3s = JCasUtil.select(jCas, Timex3.class);
        if (timex3s.isEmpty()) {
            printAnnotations(timex3s);
            Assertions.fail("Expected at least one Timex3 for '%s' but got %d".formatted(input, timex3s.size()));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "jeden Tag", // EXAMPLE set_r1a
            "jeden Montag", // EXAMPLE set_r1b
            "jeden September", // EXAMPLE set_r1c
            "jeden Sommer", // EXAMPLE set_r1d
            "jährlich", // EXAMPLE r2a
            "Montag vormittags", // EXAMPLE r3a_1
            "Montag und Samstag nachts", // EXAMPLE r3a_1 (find: Montag nachts)
            "beide Jahre", // EXAMPLE biofid_beide_r1
            "monatlichem Rhythmus", // EXAMPLE biofid_turnus_r1
            "zweijähriger Turnus", // EXAMPLE biofid_turnus_r2_a
//            "dreizehnmonatiger Turnus", // EXAMPLE biofid_turnus_r2_b  // FIXME: Not Working!
//            "13-Monatiger Turnus", // EXAMPLE biofid_turnus_r3  // FIXME: Not Working!
            "Wochenbasis", // EXAMPLE biofid_turnus_r4
            "viele Freitage", // EXAMPLE biofid_setday_r1
            "zweimal pro Woche", // EXAMPLE biofid_times_per_unit_r1
            "zweimal innerhalb eines Monats", // EXAMPLE biofid_times_per_unit_r2
            "das zweite Mal innerhalb einer Stunde", // EXAMPLE biofid_times_per_unit_r5
    })
    public void test_german_setrules(String input) throws ResourceInitializationException, CASException, AnalysisEngineProcessException {
        runSingleSentence(input);
        Collection<Timex3> timex3s = JCasUtil.select(jCas, Timex3.class);
        if (timex3s.isEmpty()) {
            printAnnotations(timex3s);
            Assertions.fail("Expected at least one Timex3 for '%s' but got %d".formatted(input, timex3s.size()));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2009-12-19T17:00:00", // EXAMPLE r1a-1:
            "2009-12-19 17:00:00", // EXAMPLE r1a-2:
            "2009-12-19T17:00", // EXAMPLE r1b-1:
            "12/29/2000 20:29", // EXAMPLE r1c-1:
            "12/29/2000 20:29:29", // EXAMPLE r1d-1:
            "12/29/2000 20:29:29.79", // EXAMPLE r1e-1:
            "Montag Mitternacht", // EXAMPLE r2a_1:
            "Montagnacht", // EXAMPLE r2b_1:
            "Mitternacht heute", // EXAMPLE r2c_1:
            "gestern Morgen", // EXAMPLE r2d_1:
            "14:30 Uhr", // EXAMPLE r3a_1:
            "14 Uhr 30", // EXAMPLE r3b_1:
            "15 Uhr", // EXAMPLE r3c_1:
            "Morgen des 1. August 2000", // EXAMPLE r4a_1:
            "Morgen des 1. August", // EXAMPLE r4b_1:
            "am Morgen", // EXAMPLE r5a-1:
            "nächsten Morgen", // EXAMPLE r5b-1:
            "am Morgen desselben Tages", // EXAMPLE r5c-1:
    })
    public void test_german_timerules(String input) throws ResourceInitializationException, CASException, AnalysisEngineProcessException {
        runSingleSentence(input);
        Collection<Timex3> timex3s = JCasUtil.select(jCas, Timex3.class);
        if (timex3s.isEmpty()) {
            printAnnotations(timex3s);
            Assertions.fail("Expected at least one Timex3 for '%s' but got %d".formatted(input, timex3s.size()));
        }
    }

    public void runSingleSentence(String input) throws ResourceInitializationException, CASException, AnalysisEngineProcessException {
        jCas.reset();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(input);

        int offset = 0;
        for (int i = 1; i < input.length(); i++) {
            if (input.charAt(i) == ' ' || i == input.length() - 1) {
                new Token(jCas, offset, i).addToIndexes();
                offset = i + 1;
            }
        }
        new Sentence(jCas, 0, input.length()).addToIndexes();

        SimplePipeline.runPipeline(jCas, engine);
    }

    public static void printAnnotations(Collection<? extends Annotation> annotations) {
        for (Annotation annotation : annotations) {
            StringBuffer stringBuffer = new StringBuffer();
            annotation.prettyPrint(0, 2, stringBuffer, true);
            System.out.print(stringBuffer);
            System.out.printf("%n  text: \"%s\"%n", annotation.getCoveredText());
        }
    }

}
