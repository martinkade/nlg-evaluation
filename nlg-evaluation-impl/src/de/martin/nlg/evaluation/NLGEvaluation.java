/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.martin.nlg.evaluation;

import de.martin.nlg.evaluation.tasks.EvaluationService;
import de.martin.nlg.evaluation.tasks.SetupReaderTask;
import de.martin.nlg.evaluation.tasks.metrics.Metric;
import de.martin.nlg.evaluation.utils.QualityCriteria;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Core class of the evaluation framework scheduling evaluation modules like
 * reading source and reference files, respectively, or evaluation setup file.
 * <p/>
 * @author Martin Kade @version 2015-09-01
 */
public class NLGEvaluation implements DataSource.Delegate, EvaluationService.Delegate {

    /**
     * TAG used for identifying output in the console.
     */
    private static final String TAG = NLGEvaluation.class.getSimpleName();

    /**
     * Executor for {@link SetupReaderTask}.
     */
    private ExecutorService executor;

    /**
     * The data source.
     */
    private final DataSource source;

    /**
     * The evaluation service.
     */
    private EvaluationService service;

    /**
     * The evaluation service being returned from {@link SetupReaderTask}.
     */
    private EvaluationSetup setup;

    /**
     *
     */
    private Scanner scanner;

    /**
     * The evaluation singleton instance.
     */
    private static NLGEvaluation Instance;

    /**
     * Invisible default constructor.
     */
    private NLGEvaluation() {
        source = DataSource.getInstance();
    }

    /**
     * Returns the singleton instance {@link #Instance}.
     *
     * @return The singleton instance
     */
    public static synchronized NLGEvaluation getInstance() {
        if (Instance == null) {
            Instance = new NLGEvaluation();
        }
        return Instance;
    }

    /**
     *
     * @param file
     * @throws java.lang.Exception
     */
    public void config(File file) throws Exception {
        executor = Executors.newFixedThreadPool(1);
        final FutureTask<EvaluationSetup> setupTask = new FutureTask<>(new SetupReaderTask(file));
        executor.execute(setupTask);
        try {
            setup = setupTask.get();
            service = new EvaluationService(setup);
            service.setDelegate(this);
            setup.print();
        } catch (InterruptedException | ExecutionException ex) {
            executor.shutdown();
            throw ex;
        } finally {
            executor.shutdown();
        }
    }

    /**
     * Wait for the setup task to be finished, then return
     * {@link EvaluationSetup#requiresInputTexts()}
     *
     * @return {@link EvaluationSetup#requiresInputTexts()}
     */
    public boolean requiresInputTexts() {
        while (!executor.isTerminated()) {

            // wait
            System.out.println("Beschäftigt ...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(TAG).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Bereit.");
        return setup.requiresInputTexts();
    }

    /**
     * Initialze the data source {@link #source} with given files. If done,
     * {@link #didReadSourceFiles()} gets called and {@link #service} will be
     * executed.
     * <p/>
     * If either there is no original file nor any reference files we call
     * {@link EvaluationService#execute()} derectly because we then assume that
     * there is no need for a {@link DataSource}.
     *
     * @param originalFile The text file we want to be evaluated
     * @param referenceFiles The reference text files as the gold standard
     * @param scanner
     * @throws Exception
     */
    public void initSourceWithFiles(File originalFile, List<File> referenceFiles, Scanner scanner) throws Exception {
        this.scanner = scanner;
        if (originalFile == null || referenceFiles == null || referenceFiles.isEmpty()) {
            try {
                service.execute(scanner);
            } catch (Exception ex) {
                Logger.getLogger(TAG).log(Level.SEVERE, null, ex);
            }
            return;
        }

        try {
            source.setDelegate(this);
            source.init(
                    // original file
                    originalFile,
                    // reference texts
                    referenceFiles.toArray(new File[referenceFiles.size()])
            );
        } catch (IOException | InterruptedException | ExecutionException ex) {
            Logger.getLogger(TAG).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    @Override
    public void didReadSourceFiles() {
        try {
            service.execute(scanner);
        } catch (Exception ex) {
            Logger.getLogger(TAG).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void didReceiveResult(double result, Class<? extends Metric> metricClass, QualityCriteria.EvaluationMethod method) {
        System.out.println("###-----Result-----------------------");
        System.out.println(String.format("# %-15s%.2f [%%] (%.5f) :: w=%.2f",
                metricClass.getSimpleName(), (result * 100), result, method.getWeight()));
        System.out.println("###----------------------------------");
    }

    @Override
    public void didReceiveManualResult(double result, QualityCriteria.EvaluationMethod method) {
        System.out.println("###-----Result-----------------------");
        System.out.println(String.format("# %-15s%.2f [%%] (%.5f) :: w=%.2f",
                "Manuell", (result * 100), result, method.getWeight()));
        System.out.println("###----------------------------------");
    }
}
