/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.martin.nlg.evaluation.tasks.metrics;

import de.martin.nlg.evaluation.DataSource;
import de.martin.nlg.evaluation.utils.NGram;
import de.martin.nlg.evaluation.utils.Sentence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Compares n-grams of the candidate text with n-grams of the reference text(s)
 * counting the number of matches, wich are, by the way, position independent.
 * The more matches, the better the candidate text gets rated.
 * <p/>
 * @author Martin Kade @version 2015-09-01
 */
public class BLEU extends Metric<Double> {

    /**
     * TAG used for identifying output in the console.
     */
    private static final String TAG = BLEU.class.getSimpleName();

    /**
     * We'll consider up to 4-grams in BLEU (BLEU-4).
     */
    protected static final int MAX_NGRAM = 4;

    /**
     * The modified n-gram precision values calculated in
     * {@link #calcModifiedNGramPrecision(int, java.util.Map, java.util.Map...)}
     * for each n-gram length from 1 to {@link #MAX_NGRAM}.
     */
    protected double[] modifiedNGramPrecisions;

    /**
     * Length of the candidate text (num words).
     */
    protected int candidateLength;

    /**
     * Cumulative length of the reference texts (total num words).
     */
    protected int sumRefLenghts;

    /**
     * Default constructor.
     */
    public BLEU() {
        super();
    }

    @Override
    public Double call() throws Exception {

        // for each n-gram length
        for (int n = 1; n <= MAX_NGRAM; n++) {
            final List<Map<NGram, Integer>> candidateStats = analyzeCandidateText(n);
            final List<List<Map<NGram, Integer>>> refStats = analyzeReferenceTexts(n);
            modifiedNGramPrecisions[n - 1] = calcModifiedNGramPrecision(n, candidateStats, refStats);

            // System.out.println("-----------------------");
            // System.out.println(String.format("==> p_%d = %.5f", n, modifiedNGramPrecisions[n - 1]));
            // System.out.println("-----------------------");
            DataSource.getInstance().reset(getClass());
        }

        result = getScore();
        return result;
    }

    @Override
    protected Double getScore() {
        final int numRefTexts = DataSource.getInstance().countReferenceTexts();

        // calculate average reference text length
        // not calculate the average but the closest reference text concerning
        // length
        final double avgRefTextLength = (double) sumRefLenghts / numRefTexts;

        // calculate brevity penalty
        final double brevityPenalty = candidateLength <= avgRefTextLength
                ? Math.exp(1.0d - avgRefTextLength / candidateLength)
                : 1.0d;

        // calculate result
        final double weight = 1.0d / MAX_NGRAM;
        double tmpResult = 0.0d;
        for (int n = 1; n <= MAX_NGRAM; n++) {
            tmpResult += Math.log(modifiedNGramPrecisions[n - 1]);
        }

        return Math.exp(weight * tmpResult) * brevityPenalty;
    }

    /**
     * Calculate the modified n-gram precision p_n.
     *
     * @param nGramLength The n-gram length
     * @param candidateStats
     * @param refStats
     * @return The modified n-gram precision for n-grams with the given length
     */
    public double calcModifiedNGramPrecision(int nGramLength,
            List<Map<NGram, Integer>> candidateStats,
            List<List<Map<NGram, Integer>>> refStats) {

        double modifiedNGramPrecision = 0.0d;

        // for all sentence n-gram statistics in the candidate text (each
        // sentence one)
        for (int i = 0; i < candidateStats.size(); i++) {
            final Map<NGram, Integer> candidateMap = candidateStats.get(i);
            final int numCandidateNGrams = summarizeNGramStats(candidateMap);

            // get all candidate n-grams of length {@param nGramLength}
            final Set<NGram> candidateNGrams = candidateMap.keySet();
            for (NGram candidateNGram : candidateNGrams) {
                // System.out.println("------------------");
                // System.out.println(String.format("%d-GRAM: '%s'", nGramLength, candidateNGram.toString()));

                // calculate the maximum number of n-grams co-occuring in this
                // candidate sentence and the reference sentences
                final int canidateCount = candidateMap.get(candidateNGram);

                int refCountMax = 0;
                for (List<Map<NGram, Integer>> aRefStats : refStats) {
                    for (Map<NGram, Integer> refMap : aRefStats) {
                        int refCount = refMap.containsKey(candidateNGram)
                                ? refMap.get(candidateNGram)
                                : 0;
                        refCountMax = Math.max(refCountMax, refCount);
                    }
                }

                // store this value by incrementing the entry for the given
                // length {@param nGramLength}
                final double tmpPrecision = (double) Math.min(refCountMax,
                        canidateCount) / numCandidateNGrams;
                // System.out.println(String.format("==> p:%.3f", tmpPrecision));

                modifiedNGramPrecision += tmpPrecision;
            }
        }

        return modifiedNGramPrecision;
    }

    /**
     *
     * @param nGramLength
     * @return
     */
    protected List<List<Map<NGram, Integer>>> analyzeReferenceTexts(int nGramLength) {
        // System.out.println("-------------------------------------");
        // System.out.println(String.format("analyzeReferenceTexts(n = %d)", nGramLength));
        // System.out.println("-------------------------------------");
        final int numRefTexts = DataSource.getInstance().countReferenceTexts();
        final List<List<Map<NGram, Integer>>> mResult = new ArrayList<>();

        // for each of the reference texts
        for (int t = 0; t < numRefTexts; t++) {
            final List<Map<NGram, Integer>> refStats = new ArrayList<>();
            Sentence aRefSentence = DataSource.getInstance().nextRefSentence(getClass(), t);
            if (nGramLength == 1) {
                sumRefLenghts += aRefSentence.count();
            }
            while (aRefSentence != null) {
                if (nGramLength == 1) {
                    // System.out.println(String.format("%s :: (%d) %s", getClass().getName(), (t + 1), aRefSentence.toString()));
                }
                refStats.add(updateNGramStats(nGramLength, aRefSentence, null));
                aRefSentence = DataSource.getInstance().nextSentence(getClass());
            }
            mResult.add(refStats);
        }
        return mResult;
    }

    /**
     *
     * @param nGramLength
     * @return
     */
    protected List<Map<NGram, Integer>> analyzeCandidateText(int nGramLength) {
        // System.out.println("#####################################");
        // System.out.println(String.format("analyzeCandidateText(n = %d)", nGramLength));
        // System.out.println("-------------------------------------");
        final List<Map<NGram, Integer>> candidateStats = new ArrayList<>();
        Sentence candSentence = DataSource.getInstance().nextSentence(getClass());
        if (nGramLength == 1) {
            candidateLength += candSentence.count();
        }
        while (candSentence != null) {
            if (nGramLength == 1) {
                // System.out.println(String.format("%s :: %s", getClass().getName(), candSentence.toString()));
            }
            candidateStats.add(updateNGramStats(nGramLength, candSentence, null));
            candSentence = DataSource.getInstance().nextSentence(getClass());
        }
        return candidateStats;
    }

    /**
     *
     * @param nGramLength
     * @param sentence
     * @param nGramStats
     * @return
     */
    protected Map<NGram, Integer> updateNGramStats(int nGramLength, Sentence sentence,
            Map<NGram, Integer> nGramStats) {

        final Map<NGram, Integer> sentenceLevelStats = countNGramAppearences(
                sentence, nGramLength
        );
        return sentenceLevelStats;
    }

    /**
     *
     * @param nGramStats
     * @return
     */
    protected int summarizeNGramStats(Map<NGram, Integer> nGramStats) {
        int totalCount = 0;
        Iterator entries = nGramStats.entrySet().iterator();
        while (entries.hasNext()) {
            totalCount += (Integer) ((Entry) entries.next()).getValue();
        }
        return totalCount;
    }

    @Override
    public void configure() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void reset() {
        result = 0.0d;
        candidateLength = sumRefLenghts = 0;
        modifiedNGramPrecisions = new double[MAX_NGRAM];
    }
}
