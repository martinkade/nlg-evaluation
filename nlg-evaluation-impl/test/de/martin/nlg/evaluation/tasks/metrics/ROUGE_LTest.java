/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.martin.nlg.evaluation.tasks.metrics;

import de.martin.nlg.evaluation.DataSource;
import de.martin.nlg.evaluation.utils.Sentence;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * ...
 * <p/>
 * @author Martin Kade @version 2015-09-01
 */
public class ROUGE_LTest {

    /**
     * {@link ROUGE} instance to be tested.
     */
    private final ROUGE instance;

    /**
     *
     */
    private DataSource source;

    /**
     * Default constructor.
     */
    public ROUGE_LTest() {
        instance = new ROUGE_L();
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        source = DataSource.getInstance();

        try {
            source.init(
                    // origonal file
                    new File("res/test/out.txt"),
                    // first reference text
                    new File("res/test/ref1.txt"),
                    // an optional second reference text
                    new File("res/test/ref2.txt")
            );
        } catch (IOException | InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
        }
    }

    @After
    public void tearDown() {
        source.reset(ROUGE_L.class);
    }

    /**
     * Test of {@link ROUGE#calculateLCSCharacterLevel(Sentence, Sentence)}.
     */
    @Test
    public void testCalculateLCS() {
        System.out.println("calculateLCS");

        final Sentence sentence = source.nextSentence(ROUGE_L.class);
        final Sentence refSentence = source.nextRefSentence(ROUGE_L.class, 0);
        final Sentence refSentence2 = source.nextRefSentence(ROUGE_L.class, 1);

        final String expResult = "the cat";
        final String result = instance.calculateLCSCharacterLevel(sentence, refSentence);

        assertEquals(expResult, result);

        final String expResult2 = "the cat";
        final String result2 = instance.calculateLCSCharacterLevel(sentence, refSentence2);

        assertEquals(expResult2, result2);
    }

    /**
     * Test of {@link ROUGE#calculateLCSWordLevel(Sentence, Sentence)}.
     */
    @Test
    public void testCalculateLCSWordLevel() {
        System.out.println("calculateLCSWordLevel");

        final Sentence sentence = source.nextSentence(ROUGE_L.class);
        final Sentence refSentence = source.nextRefSentence(ROUGE_L.class, 0);
        final Sentence refSentence2 = source.nextRefSentence(ROUGE_L.class, 1);

        final String expResult = "the cat";
        final String result = instance.calculateLCSWordLevel(sentence, refSentence).toString();

        assertEquals(expResult, result);

        final String expResult2 = "the";
        final String result2 = instance.calculateLCSWordLevel(sentence, refSentence2).toString();

        assertEquals(expResult2, result2);
    }

}
