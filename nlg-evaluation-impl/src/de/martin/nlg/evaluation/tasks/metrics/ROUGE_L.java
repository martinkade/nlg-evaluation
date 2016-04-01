/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.martin.nlg.evaluation.tasks.metrics;

import de.martin.nlg.evaluation.DataSource;
import de.martin.nlg.evaluation.utils.Sentence;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 * <p/>
 * @author Martin Kade @version 2015-09-01
 */
public class ROUGE_L extends ROUGE {

    /**
     *
     */
    private List<Double>[] fScores;

    /**
     * Default constructor.
     */
    public ROUGE_L() {
        super();
    }

    @Override
    public Double call() throws Exception {
        final int numRefTexts = DataSource.getInstance().countReferenceTexts();

        Sentence candSentence = DataSource.getInstance().nextSentence(getClass());
        while (candSentence != null) {
            // System.out.println(String.format("%s :: %s", getClass().getName(), candSentence.toString()));

            for (int t = 0; t < numRefTexts; t++) {
                fScores[t] = new ArrayList<>();

                Sentence aRefSentence = DataSource.getInstance().nextRefSentence(getClass(), t);
                while (aRefSentence != null) {
                    // System.out.println(String.format("%s :: %s", getClass().getName(), aRefSentence.toString()));

                    final Sentence lcs = calculateLCSWordLevel(candSentence, aRefSentence);
                    final double rLcs = (double) lcs.count() / aRefSentence.count();
                    final double pLcs = (double) lcs.count() / candSentence.count();
                    final double beta = pLcs / rLcs;
                    final double fLcs = ((1 + Math.pow(beta, 2.0d)) * rLcs * pLcs) / (rLcs + Math.pow(beta, 2.0d) * pLcs);
                    fScores[t].add(fLcs);

                    aRefSentence = DataSource.getInstance().nextSentence(getClass());
                }
            }

            candSentence = DataSource.getInstance().nextSentence(getClass());
        }

        return getScore();
    }

    @Override
    public void configure() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Double getScore() {
        double avgFScore = 0.0f;
        for (List<Double> fScore : fScores) {
            double avgRefFScore = 0.0f;
            for (double refFScore : fScore) {
                avgRefFScore += refFScore;
            }
            avgFScore += avgRefFScore / fScore.size();
        }
        return avgFScore / fScores.length;
    }

    @Override
    public void reset() {
        super.reset();
        final int numRefTexts = DataSource.getInstance().countReferenceTexts();

        fScores = new ArrayList[numRefTexts];
    }
}
