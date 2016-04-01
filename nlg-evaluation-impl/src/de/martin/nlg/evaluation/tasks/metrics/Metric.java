/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.martin.nlg.evaluation.tasks.metrics;

import de.martin.nlg.evaluation.utils.NGram;
import de.martin.nlg.evaluation.utils.QualityCriteria;
import de.martin.nlg.evaluation.utils.Sentence;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Template class for many kinds of automatic evaluation metric implementations.
 * We do the work within a {@link FutureTask}, so implementing the
 * {@link Callable} interface is helpful.
 * <p/>
 * @author Martin Kade @version 2015-09-01
 * @param <T> The result type
 */
public abstract class Metric<T> implements Callable<T> {

    /**
     * TAG used for identifying output in the console.
     */
    private static final String TAG = Metric.class.getSimpleName();

    /**
     * The result of the concrete {@link Metric} implementation.
     */
    protected T result;

    /**
     *
     */
    protected QualityCriteria.EvaluationMethod method;

    /**
     * Default constructor.
     */
    public Metric() {
        reset();
    }

    /**
     * Reset the parameters of the concrete {@link Metric} implementation.
     */
    public abstract void reset();

    /**
     * Make some initial configurations on the concrete {@link Metric}
     * implementation.
     */
    public abstract void configure();

    /**
     * Get the result of the concrete {@link Metric} implementation.
     *
     * @return The result of the concrete {@link Metric} implementation
     */
    protected abstract T getScore();

    /**
     *
     * @param method
     */
    public void setMethod(QualityCriteria.EvaluationMethod method) {
        this.method = method;
    }

    /**
     *
     * @return
     */
    public final QualityCriteria.EvaluationMethod getMethod() {
        return method;
    }

    /**
     * Collects statistics for given n-gram and a sentence (used for computing
     * modified n-gram precision).
     *
     * @param sentence The {@link Sentence} whose statistics are to be computed
     * @param nGramLength The defined n-gram length
     * @return the statistics -- for each possible n-gram as key in the hashmap,
     * collect the number of occurrences within the sentence
     */
    protected final Map<NGram, Integer> countNGramAppearences(Sentence sentence, int nGramLength) {

        // n-gram -> count
        final Map<NGram, Integer> stats = new HashMap<>();

        for (int i = 0; i < sentence.getTokens().length; ++i) {

            NGram nGram;
            try {
                nGram = new NGram(sentence.getTokens(), i, nGramLength);
            } catch (NGram.NGramException ex) {
                break;
            }

            if (stats.containsKey(nGram)) {
                stats.put(nGram, stats.get(nGram) + 1);
                // System.out.println(String.format("%d x '%s'", stats.get(nGram), nGram.toString()));
            } else {
                stats.put(nGram, 1);
                // System.out.println(String.format("%d x '%s'", stats.get(nGram), nGram.toString()));
            }
        }
        return stats;
    }
}
