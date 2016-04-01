/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.martin.nlg.evaluation.utils;

import java.util.Arrays;

/**
 * Sentence data structure.
 * <p/>
 * @author Martin Kade @version 2015-09-07
 */
public class Sentence {

    /**
     * The words of the sentence.
     */
    private final String[] tokens;

    /**
     * Constructor.
     *
     * @param tokens The words of the sentence
     */
    public Sentence(String[] tokens) {
        this.tokens = tokens;
        cleanTokens();
    }

    /**
     * Clean the words removing white spaces and other stuff.
     */
    private void cleanTokens() {
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].replaceAll("\\.|\\,|\\;|\\:|\\-", "").toLowerCase();
        }
    }

    public String[] getTokens() {
        return tokens;
    }

    public int count() {
        return tokens.length;
    }

    public Sentence copy() {
        final String[] tokensCopy = new String[tokens.length];
        System.arraycopy(tokens, 0, tokensCopy, 0, tokens.length);
        return new Sentence(tokensCopy);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(tokens[0]);
        for (int j = 1; j < tokens.length; j++) {
            sb.append(" ");
            sb.append(tokens[j]);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Sentence)) {
            return false;
        }
        return Arrays.equals(((Sentence) obj).tokens, tokens);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Arrays.deepHashCode(this.tokens);
        return hash;
    }
}
