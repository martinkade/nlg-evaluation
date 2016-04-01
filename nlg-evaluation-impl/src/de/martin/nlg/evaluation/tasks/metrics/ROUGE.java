/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.martin.nlg.evaluation.tasks.metrics;

import de.martin.nlg.evaluation.utils.Sentence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ...
 * <p/>
 * @author Martin Kade @version 2015-09-01
 */
public abstract class ROUGE extends Metric<Double> {

    /**
     * Default constructor.
     */
    public ROUGE() {
        super();
    }

    @Override
    public void reset() {
        // empty
    }

    /**
     * Longest common subsequence at character level.
     *
     * @param sentence
     * @param otherSentence
     * @return
     */
    protected final String calculateLCSCharacterLevel(Sentence sentence, Sentence otherSentence) {
        final String string = sentence.toString(), otherString = otherSentence.toString();
        final int[][] lengths = new int[string.length() + 1][otherString.length() + 1];

        for (int i = 0; i < string.length(); i++) {
            for (int j = 0; j < otherString.length(); j++) {
                if (string.charAt(i) == otherString.charAt(j)) {
                    lengths[i + 1][j + 1] = lengths[i][j] + 1;
                } else {
                    lengths[i + 1][j + 1] = Math.max(lengths[i + 1][j], lengths[i][j + 1]);
                }
            }
        }

        // build the substring from the matrix
        final StringBuilder sb = new StringBuilder();
        for (int x = string.length(), y = otherString.length(); x != 0 && y != 0;) {
            if (lengths[x][y] == lengths[x - 1][y]) {
                x--;
            } else if (lengths[x][y] == lengths[x][y - 1]) {
                y--;
            } else {
                sb.append(string.charAt(x - 1));
                x--;
                y--;
            }
        }
        return sb.reverse().toString();
    }

    /**
     * Longest common subsequence at word level.
     *
     * @param sentence
     * @param otherSentence
     * @return
     */
    protected final Sentence calculateLCSWordLevel(Sentence sentence, Sentence otherSentence) {
        final int[][] lengths = new int[sentence.count() + 1][otherSentence.count() + 1];

        for (int i = 0; i < sentence.count(); i++) {
            for (int j = 0; j < otherSentence.count(); j++) {
                if (sentence.getTokens()[i].equals(otherSentence.getTokens()[j])) {
                    lengths[i + 1][j + 1] = lengths[i][j] + 1;
                } else {
                    lengths[i + 1][j + 1] = Math.max(lengths[i + 1][j], lengths[i][j + 1]);
                }
            }
        }

        // build the sub sentence from the matrix
        final List<String> matchingTokens = new ArrayList<>();
        for (int x = sentence.count(), y = otherSentence.count(); x != 0 && y != 0;) {
            if (lengths[x][y] == lengths[x - 1][y]) {
                x--;
            } else if (lengths[x][y] == lengths[x][y - 1]) {
                y--;
            } else {
                matchingTokens.add(sentence.getTokens()[x - 1]);
                x--;
                y--;
            }
        }

        Collections.reverse(matchingTokens);
        return new Sentence(matchingTokens.toArray(new String[matchingTokens.size()]));
    }
}
