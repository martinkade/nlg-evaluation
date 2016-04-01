/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.martin.nlg.evaluation.tasks.metrics;

import de.martin.nlg.evaluation.DataSource;
import de.martin.nlg.evaluation.utils.NGram;
import de.martin.nlg.evaluation.utils.Sentence;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * ...
 * <p/>
 * @author Martin Kade @version 2015-09-01
 */
public class NIST extends BLEU {

    /**
     * TAG used for identifying output in the console.
     */
    private static final String TAG = NIST.class.getSimpleName();

    /**
     *
     */
    private Map<NGram, Integer>[] globalRefNGramCounts;

    /**
     *
     */
    private int[] globalRefNGramMatchCounts, globalCandNGramCounts;

    /**
     * Default constructor.
     */
    public NIST() {
        super();
    }

    @Override
    public Double call() throws Exception {
        return super.call();
    }

    @Override
    protected Double getScore() {
        final int numRefTexts = DataSource.getInstance().countReferenceTexts();

        // calculate average reference text length
        final double avgRefTextLength = (double) sumRefLenghts / numRefTexts;

        // calculate brevity penalty
        final double brevityPenalty = candidateLength <= avgRefTextLength
                ? Math.exp(1.0d - avgRefTextLength / candidateLength)
                : 1.0d;

        // calculate result
        double tmpResult = 0.0d;
        for (int n = 1; n <= MAX_NGRAM; n++) {
            final double weight = calcInformationWeight(n);
            tmpResult += weight * Math.log(modifiedNGramPrecisions[n - 1]);
        }

        return Math.exp(tmpResult) * brevityPenalty;
    }

    private double calcInformationWeight(int nGramLength) {
        final Map<NGram, Integer> map = globalRefNGramCounts[nGramLength - 1];
        double weight = 0.0d;
        final Iterator entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            final Entry entry = (Entry) entries.next();
            final int refNGramCount = (Integer) entry.getValue();
            if (refNGramCount > 0) {
                weight += (double) refNGramCount / globalRefNGramMatchCounts[nGramLength - 1];
            }
        }
        return Math.exp(Math.log(weight) / Math.log(2));
    }

    @Override
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

                // calculate the maximum number of n-grams co-occuring in this
                // candidate sentence and the reference sentences
                final int canidateCount = candidateMap.get(candidateNGram);
                globalCandNGramCounts[nGramLength - 1] += canidateCount;

                int refCountMax = 0;
                for (List<Map<NGram, Integer>> aRefStats : refStats) {
                    for (Map<NGram, Integer> refMap : aRefStats) {
                        int refCount = refMap.containsKey(candidateNGram)
                                ? refMap.get(candidateNGram)
                                : 0;

                        // a refCount that is greater that is at least 1 means
                        // that we do have co-occurences
                        globalRefNGramMatchCounts[nGramLength - 1] += refCount;
                        globalRefNGramCounts[nGramLength - 1].compute(candidateNGram,
                                (k, v) -> v == null
                                        // add global level stats for n-gram
                                        // if it is not present
                                        ? refCount
                                        // increment old value if n-gram is
                                        // already present
                                        : v + refCount
                        );

                        refCountMax = Math.max(refCountMax, refCount);
                    }
                }

                // store this value by incrementing the entry for the given
                // length {@param nGramLength}
                final double tmpPrecision = (double) Math.min(refCountMax,
                        canidateCount) / numCandidateNGrams;

                modifiedNGramPrecision += tmpPrecision;
            }
        }

        return modifiedNGramPrecision;
    }

    @Override
    protected List<List<Map<NGram, Integer>>> analyzeReferenceTexts(int nGramLength) {
        return super.analyzeReferenceTexts(nGramLength);
    }

    @Override
    protected List<Map<NGram, Integer>> analyzeCandidateText(int nGramLength) {
        return super.analyzeCandidateText(nGramLength);
    }

    @Override
    protected Map<NGram, Integer> updateNGramStats(int nGramLength, Sentence sentence,
            Map<NGram, Integer> nGramStats) {
        return super.updateNGramStats(nGramLength, sentence, nGramStats);
    }

    @Override
    protected int summarizeNGramStats(Map<NGram, Integer> nGramStats) {

        return super.summarizeNGramStats(nGramStats);
    }

    @Override
    public void configure() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void reset() {
        super.reset();
        globalRefNGramCounts = new HashMap[]{new HashMap(), new HashMap(), new HashMap(), new HashMap()};
        globalRefNGramMatchCounts = globalCandNGramCounts = new int[MAX_NGRAM];
    }
}
