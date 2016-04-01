/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.martin.nlg.evaluation.utils;

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
public class SentenceTest {

    /**
     * Default constructor.
     */
    public SentenceTest() {
        // empty
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of {@link Sentence#getTokens()}.
     */
    @Test
    public void testCleanTokensDot() {
        System.out.println("testCleanTokensDot");
        final Sentence instance = new Sentence(new String[]{"This", "is", "a", "test."});

        final String[] expResult = new String[]{"This", "is", "a", "test"};
        final String[] result = instance.getTokens();

        assertArrayEquals(expResult, result);
    }

    /**
     * Test of {@link Sentence#getTokens()}.
     */
    @Test
    public void testCleanTokensCommaDot() {
        System.out.println("testCleanTokensCommaDot");
        final Sentence instance = new Sentence(new String[]{"This,", "is", "another", "test."});

        final String[] expResult = new String[]{"This", "is", "another", "test"};
        final String[] result = instance.getTokens();

        assertArrayEquals(expResult, result);
    }

    /**
     * Test of {@link Sentence#copy()}.
     */
    @Test
    public void testCopy() {
        System.out.println("testCopy");
        final Sentence instance = new Sentence(new String[]{"This,", "is", "a", "test."});

        final Sentence expResult = new Sentence(new String[]{"This,", "is", "a", "test."});
        final Sentence result = instance.copy();

        assertEquals(expResult, result);
    }

    /**
     * Test of {@link Sentence#toString()}.
     */
    @Test
    public void testToString() {
        System.out.println("testToString");
        final Sentence instance = new Sentence(new String[]{"This,", "is", "a", "test."});

        final String expResult = "This is a test";
        final String result = instance.toString();

        assertEquals(expResult, result);
    }
}
