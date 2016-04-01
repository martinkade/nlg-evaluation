/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.martin.nlg.evaluation.tasks.metrics;

import de.martin.nlg.evaluation.DataSource;
import de.martin.nlg.evaluation.utils.Sentence;

/**
 * ...
 * <p/>
 * @author Martin Kade @version 2015-09-01
 */
public class SED extends Metric<Double> {

    /**
     * TAG used for identifying output in the console.
     */
    private static final String TAG = SED.class.getSimpleName();

    @Override
    public Double call() throws Exception {
        return getScore();
    }

    @Override
    public void reset() {
        // empty
    }

    @Override
    public void configure() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Double getScore() {
        final int numRefTexts = DataSource.getInstance().countReferenceTexts();
        Sentence candSentence = DataSource.getInstance().nextSentence(getClass());
        double distanceSumCorpusLevel = 0.0d;

        int numSentences = 0;
        while (candSentence != null) {
            // System.out.println(String.format("%s :: %s", getClass().getName(), candSentence.toString()));
            numSentences += 1;

            for (int t = 0; t < numRefTexts; t++) {
                final Sentence aRefSentence = DataSource.getInstance().nextRefSentence(getClass(), t);
                // System.out.println(String.format("%s :: (%d) %s", getClass().getName(), (t + 1), aRefSentence.toString()));
                distanceSumCorpusLevel += calcEditDistance(candSentence, aRefSentence);
            }

            candSentence = DataSource.getInstance().nextSentence(getClass());
        }

        return numSentences == 0
                ? 0.0d
                : distanceSumCorpusLevel / (numSentences * numRefTexts);
    }

    /**
     * Calculates the (normalized) string edit distance from the given
     * sentences.
     *
     * @param sentence
     * @param otherSentence
     * @return
     */
    private double calcEditDistance(Sentence sentence, Sentence otherSentence) {
        final String string = sentence.toString();
        final int strLen = string.length() + 1;

        final String otherString = otherSentence.toString();
        final int otherStrLen = otherString.length() + 1;

        // the array of distances                                                       
        int[] cost = new int[strLen];
        int[] newcost = new int[strLen];

        // initial cost of skipping prefix in {@param sentence}                                
        for (int i = 0; i < strLen; i++) {
            cost[i] = i;
        }

        // dynamically computing the array of distances                             
        // transformation cost for each letter in {@param otherSentence}                                    
        for (int j = 1; j < otherStrLen; j++) {

            // initial cost of skipping prefix in String {@param otherSentence}                              
            newcost[0] = j;

            // transformation cost for each letter in {@param sentence}                                
            for (int i = 1; i < strLen; i++) {

                // matching current letters in both strings                             
                int match = (string.charAt(i - 1) == otherString.charAt(j - 1)) ? 0 : 1;

                // computing cost for each transformation                               
                int costReplace = cost[i - 1] + match;
                int costInsert = cost[i] + 1;
                int costDelete = newcost[i - 1] + 1;

                // keep minimum cost                                                    
                newcost[i] = Math.min(Math.min(costInsert, costDelete), costReplace);
            }

            // swap cost/newcost arrays                                                 
            int[] swap = cost;
            cost = newcost;
            newcost = swap;
        }

        return (double) cost[strLen - 1] / Math.max(strLen, otherStrLen);
    }
}
