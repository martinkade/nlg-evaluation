/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.martin.nlg.evaluation.utils;

import java.util.Arrays;

/**
 * NGram data structure.
 * <p/>
 * @author Martin Kade @version 2015-09-07
 */
public class NGram {

    /**
     * The tokens of the n-gram.
     */
    private String[] tokens;

    /**
     * Constructor.
     *
     * @param tokens The tokens to build the n-gram from
     * @param offset Starting position
     * @param nGramLength The n-gram setting (how long an n-gram to make)
     * @throws NGramException If the n-gram length minus the given offset is too
     * small to be able to build a n-gram with the given length
     */
    public NGram(String[] tokens, int offset, int nGramLength) throws NGramException {
        if (offset + nGramLength > tokens.length) {
            throw new NGramException(
                    String.format("not able to build a %d-gram from %d tokens", nGramLength, tokens.length - offset)
            );
        }
        this.tokens = new String[nGramLength];
        for (int j = offset; j < offset + nGramLength; j++) {
            this.tokens[j - offset] = tokens[j];
        }
    }

    /**
     * Get the prefix {@link NGram}.
     *
     * @return The prefix n-gram
     * @throws de.martin.nlg.evaluation.utils.NGram.NGramException If the n-gram
     * is too short to be able to build a prefix
     */
    public NGram getPrefix() throws NGramException {
        if (tokens.length < 1) {
            return this;
        }
        return new NGram(tokens, 0, tokens.length - 1);
    }

    /**
     * Get the postfix {@link NGram}.
     *
     * @return The postfix n-gram
     * @throws de.martin.nlg.evaluation.utils.NGram.NGramException If the n-gram
     * is too short to be able to build a postfix
     */
    public NGram getPostfix() throws NGramException {
        if (tokens.length < 1) {
            return this;
        }
        return new NGram(tokens, 1, tokens.length - 1);
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
        if (!(obj instanceof NGram)) {
            return false;
        }
        return ((NGram) obj).toString().equals(toString());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Arrays.deepHashCode(this.tokens);
        return hash;
    }

    /**
     * Custom exception when we have problems to build a n-gram.
     */
    public static class NGramException extends Exception {

        /**
         * Constructor.
         *
         * @param msg The message to be displayed
         */
        public NGramException(String msg) {
            super(msg);
        }
    }
}
