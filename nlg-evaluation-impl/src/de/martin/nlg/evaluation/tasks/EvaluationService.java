/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.martin.nlg.evaluation.tasks;

import de.martin.nlg.evaluation.tasks.metrics.Metric;
import de.martin.nlg.evaluation.EvaluationSetup;
import de.martin.nlg.evaluation.tasks.metrics.Questionnary;
import de.martin.nlg.evaluation.utils.QualityCriteria;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * The evaluation service that schedules evaluation method tasks like
 * {@link Metric}s, for instance.
 * <p/>
 * @author Martin Kade @version 2015-09-01
 */
public class EvaluationService {

    /**
     * Delegate interface providing notifications.
     */
    public static interface Delegate {

        /**
         *
         * @param result
         * @param metricClass
         * @param method
         */
        void didReceiveResult(double result, Class<? extends Metric> metricClass, QualityCriteria.EvaluationMethod method);

        /**
         *
         * @param result
         * @param method
         */
        void didReceiveManualResult(double result, QualityCriteria.EvaluationMethod method);
    }

    /**
     * TAG used for identifying output in the console.
     */
    private static final String TAG = EvaluationService.class.getSimpleName();

    /**
     * The executor service for {@link Metric} tasks.
     */
    private ExecutorService executor;

    /**
     * Delegate reference.
     */
    private EvaluationService.Delegate delegate;

    /**
     * The evaluation setup.
     */
    private final EvaluationSetup setup;

    /**
     * Constructor.
     *
     * @param setup The evaluation setup
     */
    public EvaluationService(EvaluationSetup setup) {
        this.setup = setup;
    }

    public void setDelegate(EvaluationService.Delegate delegate) {
        this.delegate = delegate;
    }

    /**
     * Execute or trigger, respectively, the automatic {@link Metric}s.
     *
     * @param scanner
     * @throws Exception
     */
    public void execute(Scanner scanner) throws Exception {

        // queue containing questionnaires
        final Queue<Questionnary> humanQueue = new LinkedList<>();

        // automatic metric tasks
        final Map<Class<? extends Metric>, FutureTask<Double>> tasks = new HashMap<>();
        final Map<Class<? extends Metric>, QualityCriteria.EvaluationMethod> methodMap = new HashMap<>();

        for (QualityCriteria.EvaluationMethod m : setup.getCriteria().getMethods()) {
            if (m.getExec() == QualityCriteria.MethodExecution.AUTOMATIC
                    && m.getName() != null) {

                final Metric metric = m.instantiate();
                tasks.put(metric.getClass(), new FutureTask<>(metric));
                methodMap.put(metric.getClass(), metric.getMethod());

            } else if (m.getExec() == QualityCriteria.MethodExecution.HUMAN_ASSESSED) {

                final Questionnary questionnary = new Questionnary(m);
                humanQueue.add(questionnary);
            }
        }

        // iterate through questionnaires
        while (!humanQueue.isEmpty()) {
            final Questionnary questionnary = humanQueue.poll();
            questionnary.printCatalog(setup.getCriteria());
            questionnary.setChoice(scanner.nextLine());
            if (delegate != null) {
                delegate.didReceiveManualResult(questionnary.getResult(), questionnary.getMethod());
            }
        }

        // init thread pool
        executor = Executors.newFixedThreadPool(tasks.size());

        // start tasks
        for (Map.Entry<Class<? extends Metric>, FutureTask<Double>> entry : tasks.entrySet()) {
            executor.execute(entry.getValue());
        }

        // wait for results
        for (Map.Entry<Class<? extends Metric>, FutureTask<Double>> entry : tasks.entrySet()) {
            final double result = entry.getValue().get();
            if (delegate != null) {
                delegate.didReceiveResult(result, entry.getKey(), methodMap.get(entry.getKey()));
            }
        }

        // shutdown executor
        executor.shutdown();
    }
}
