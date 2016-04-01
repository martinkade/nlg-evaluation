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
 * Test {@link NGram} functionality.
 * <p/>
 * @author Martin Kade @version 2015-09-07
 */
public class NGramTest {

    /**
     * Default constructor.
     */
    public NGramTest() {
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
     * Test of {@link NGram#getPrefix()}.
     *
     * @throws java.lang.Exception We expect this exception not to be thrown
     */
    @Test
    public void testGetPrefix() throws Exception {
        System.out.println("testGetPrefix");
        final NGram instance = new NGram(new String[]{"this", "is", "a", "test"}, 0, 4);

        final NGram expResult = new NGram(new String[]{"this", "is", "a"}, 0, 3);
        final NGram result = instance.getPrefix();

        assertEquals(expResult, result);
    }

    /**
     * Test of {@link NGram#getPostfix()}.
     *
     * @throws java.lang.Exception We expect this exception not to be thrown
     */
    @Test
    public void testGetPostfix() throws Exception {
        System.out.println("testGetPostfix");
        final NGram instance = new NGram(new String[]{"this", "is", "a", "test"}, 0, 4);

        final NGram expResult = new NGram(new String[]{"is", "a", "test"}, 0, 3);
        final NGram result = instance.getPostfix();

        assertEquals(expResult, result);
    }

    /**
     * Test of {@link NGram#getPrefix()} with exception.
     *
     * @throws java.lang.Exception We expect this exception to be thrown
     */
    @Test(expected = NGram.NGramException.class)
    public void testGetPrefixWithException() throws Exception {
        System.out.println("testGetPrefixWithException");
        final NGram instance = new NGram(new String[]{"this"}, 0, 4);

        instance.getPrefix();
    }

    /**
     * Test of {@link NGram#getPostfix()} with exception.
     *
     * @throws java.lang.Exception We expect this exception to be thrown
     */
    @Test(expected = NGram.NGramException.class)
    public void testGetPostfixWithException() throws Exception {
        System.out.println("testGetPostfixWithException");
        final NGram instance = new NGram(new String[]{"this"}, 0, 4);

        instance.getPostfix();
    }

    /**
     * Test of {@link NGram#toString()}.
     *
     * @throws java.lang.Exception We expect this exception not to be thrown
     */
    @Test
    public void testToString() throws Exception {
        System.out.println("toString");
        final NGram instance = new NGram(new String[]{"this", "is", "a", "test"}, 0, 4);

        final String expResult = "this is a test";
        final String result = instance.toString();

        assertEquals(expResult, result);
    }
}
