/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.martin.nlg.evaluation.tasks.metrics;

import de.martin.nlg.evaluation.DataSource;
import de.martin.nlg.evaluation.utils.NGram;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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
public class BLEUTest {

    /**
     * {@link BLEU} instance to be tested.
     */
    private final BLEU instance;

    /**
     *
     */
    private DataSource source;

    /**
     * Default constructor.
     */
    public BLEUTest() {
        instance = new BLEU();
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
    }

    /**
     * Test of
     * {@link BLEU#calcModifiedNGramPrecision(int, java.util.List, java.util.List)}
     * with nGramLength = 1.
     */
    @Test
    public void testCalcModifiedNGramPrecisionUnigram() {
        System.out.println("testCalcModifiedNGramPrecisionUnigram");

        final int nGramLength = 1;

        final List<Map<NGram, Integer>> candidateStats = instance.analyzeCandidateText(nGramLength);
        final List<List<Map<NGram, Integer>>> refStats = instance.analyzeReferenceTexts(nGramLength);

        final double expResult = 1.0d;
        final double result = instance.calcModifiedNGramPrecision(nGramLength, candidateStats, refStats);

        assertEquals(expResult, result, 0.01d);
    }
}
