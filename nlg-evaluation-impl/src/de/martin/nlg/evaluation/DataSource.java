/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.martin.nlg.evaluation;

import de.martin.nlg.evaluation.tasks.SentenceBuilderTask;
import de.martin.nlg.evaluation.utils.Sentence;
import de.martin.nlg.evaluation.tasks.metrics.Metric;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Datasource providing class that follows the singleton pattern.
 * <p/>
 * @author Martin Kade @version 2015-09-01
 */
public class DataSource {

    /**
     * Delegate interface providing notifications.
     */
    public static interface Delegate {

        /**
         * Called when all {@link SentenceBuilderTask}s have finished and the
         * data source is ready for usage.
         */
        void didReadSourceFiles();
    }

    /**
     * TAG used for identifying output in the console.
     */
    private static final String TAG = DataSource.class.getSimpleName();

    /**
     * Administration structure helping to provide the next sentence to be
     * analyzed by a {@link Metric}.
     */
    private Map<String, SentenceHelper> lookupMap;

    /**
     * The {@link Sentence}s of the original text file.
     */
    private List<Sentence> sentences;

    /**
     * Array that stores a list of {@link Sentence}s for each of the given
     * reference text files.
     */
    private List<Sentence>[] refSentences;

    /**
     * Delegate reference.
     */
    private DataSource.Delegate delegate;

    /**
     * The singleton istance.
     */
    private static DataSource Instance;

    /**
     * Invisible default constructor.
     */
    private DataSource() {
        // empty
    }

    /**
     * Returns the singleton instance {@link #Instance} of this class creating
     * it if necessary.
     *
     * @return
     */
    public static synchronized DataSource getInstance() {
        if (Instance == null) {
            Instance = new DataSource();
        }
        return Instance;
    }

    public void setDelegate(DataSource.Delegate delegate) {
        this.delegate = delegate;
    }

    /**
     * Reset {@link #sentences}, {@link #refSentences} and the
     * {@link #lookupMap}.
     *
     * @param numRefTexts The number of available reference texts
     */
    private void reset(int numRefTexts) {
        sentences = new ArrayList<>();
        refSentences = new ArrayList[numRefTexts];
        lookupMap = new HashMap<>();
    }

    /**
     * Initializes the datasource with the given text files.
     *
     * @param file The text file one wants to be evaluated
     * @param referenceFiles The reference text files
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public void init(File file, File... referenceFiles) throws IOException,
            InterruptedException, ExecutionException {

        // reset or init sentence collections, respectively
        reset(referenceFiles.length);

        // one task for each reference file
        final int numTasks = referenceFiles.length;

        final ExecutorService executor = Executors.newFixedThreadPool(numTasks);
        final List<FutureTask<List<Sentence>>> tasks = new ArrayList<>();

        // init and start the single tasks
        final FutureTask<List<Sentence>> originalFileTask = new FutureTask<>(new SentenceBuilderTask(file));
        executor.execute(originalFileTask);
        for (File aRefFile : referenceFiles) {
            final FutureTask<List<Sentence>> aReferenceFileTask = new FutureTask<>(new SentenceBuilderTask(aRefFile));
            tasks.add(aReferenceFileTask);
            executor.execute(aReferenceFileTask);
        }

        // wait for the results to be available
        for (int i = 0; i < numTasks; i++) {
            final FutureTask<List<Sentence>> futureTask = tasks.get(i);
            refSentences[i] = futureTask.get();
        }
        sentences = originalFileTask.get();
        if (delegate != null) {
            delegate.didReadSourceFiles();
        }

        // some clean up
        executor.shutdown();
    }

    /**
     * Request the next sentence for the given metric implementation.
     *
     * @param metricClass The class that requests the next sentence to process
     * @return
     */
    public synchronized Sentence nextSentence(Class<? extends Metric> metricClass) {
        final SentenceHelper helper = lookupMap.containsKey(metricClass.getName())
                ? lookupMap.get(metricClass.getName())
                : new SentenceHelper(refSentences.length);
        final int index = helper.getNextIndex();
        helper.incrementIndex();
        lookupMap.put(metricClass.getName(), helper);

        return sentences.size() > index
                ? sentences.get(index)
                : null;
    }

    /**
     * Request the next reference sentence for the given metric implementation
     * and reference text index in {@link #refSentences}.
     *
     * @param metricClass The class that requests the next reference sentence to
     * process
     * @param refTextIndex The reference text index in {@link #refSentences}
     * @return
     */
    public synchronized Sentence nextRefSentence(Class<? extends Metric> metricClass, int refTextIndex) {
        if (refTextIndex >= refSentences.length || refTextIndex < 0) {
            return null;
        }

        final SentenceHelper helper = lookupMap.containsKey(metricClass.getName())
                ? lookupMap.get(metricClass.getName())
                : new SentenceHelper(refSentences.length);
        final int index = helper.getNextRefIndex(refTextIndex);
        helper.incrementRefIndex(refTextIndex);
        lookupMap.put(metricClass.getName(), helper);

        return refSentences[refTextIndex].size() > index
                ? refSentences[refTextIndex].get(index)
                : null;
    }

    /**
     * Removes a {@link Metric} class from the {@link #lookupMap}.
     *
     * @param metricClass The metric class to be removed from {@link #lookupMap}
     */
    public synchronized void reset(Class<? extends Metric> metricClass) {
        lookupMap.remove(metricClass.getName());
    }

    /**
     * Returns the numnber of reference texts.
     *
     * @return The numnber of reference texts
     */
    public synchronized int countReferenceTexts() {
        return refSentences.length;
    }

    /**
     * Helper structure used in {@link #lookupMap}.
     */
    private static class SentenceHelper {

        /**
         * Index of the next sentence.
         */
        private int nextIndex;

        /**
         * Index of the next reference sentence within one of the provided
         * reference texts.
         */
        private final int[] nextRefIndex;

        /**
         * Default constructor.
         */
        public SentenceHelper(int numRefTexts) {
            nextIndex = 0;
            nextRefIndex = new int[numRefTexts];
        }

        /**
         * Incrememts the index of the next reference sentence.
         *
         * @param refTextIndex
         */
        public void incrementRefIndex(int refTextIndex) {
            nextRefIndex[refTextIndex]++;
        }

        /**
         * Incrememts the index of the next sentence.
         */
        public void incrementIndex() {
            nextIndex++;
        }

        public int getNextIndex() {
            return nextIndex;
        }

        /**
         *
         * @param refTextIndex
         * @return
         */
        public int getNextRefIndex(int refTextIndex) {
            return nextRefIndex[refTextIndex];
        }
    }
}
