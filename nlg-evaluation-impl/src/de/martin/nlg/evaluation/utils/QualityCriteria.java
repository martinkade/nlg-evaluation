/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.martin.nlg.evaluation.utils;

import de.martin.nlg.evaluation.tasks.metrics.Metric;

import java.lang.reflect.Constructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * ...
 * <p/>
 * @author Martin Kade @version 2015-09-01
 */
public class QualityCriteria {

    /**
     * <li>AUTOMATIC</li>
     * <li>HUMAN_ASSESSED</li>
     */
    public enum MethodExecution {

        /**
         *
         */
        AUTOMATIC,
        /**
         *
         */
        HUMAN_ASSESSED
    }

    /**
     * <li>EXTRINSIC</li>
     * <li>INTRINSIC</li>
     */
    public enum MethodType {

        /**
         *
         */
        EXTRINSIC,
        /**
         *
         */
        INTRINSIC
    }

    /**
     *
     */
    private String type, name;

    /**
     *
     */
    private float weightSum;

    /**
     *
     */
    private Set<EvaluationMethod> methods;

    /**
     * Default constructor.
     */
    public QualityCriteria() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setWeightSum(float weightSum) {
        this.weightSum = weightSum;
    }

    public void addMethod(EvaluationMethod method) {
        if (methods == null) {
            methods = new HashSet<>();
        }
        methods.add(method);
    }

    public Set<EvaluationMethod> getMethods() {
        return methods;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public float getWeightSum() {
        return weightSum;
    }

    public int countMetrics() {
        return methods == null
                ? 0
                : methods.size();
    }

    /**
     *
     */
    public static class EvaluationMethod {

        /**
         *
         */
        private MethodExecution exec;

        /**
         *
         */
        private MethodType type;

        /**
         *
         */
        private String name;

        /**
         *
         */
        private float weight, threshold;

        /**
         *
         */
        private Map<Integer, String> catalog;

        /**
         * Default constructor.
         */
        public EvaluationMethod() {

        }

        public void setName(String name) {
            this.name = name;
        }

        public void setExec(MethodExecution exec) {
            this.exec = exec;
        }

        public void setType(MethodType type) {
            this.type = type;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }

        public void setThreshold(float threshold) {
            this.threshold = threshold;
        }

        public float getWeight() {
            return weight;
        }

        public float getThreshold() {
            return threshold;
        }

        public String getName() {
            return name;
        }

        public MethodType getType() {
            return type;
        }

        public MethodExecution getExec() {
            return exec;
        }

        public void addCatalogEntry(int rating, String name) {
            if (catalog == null) {
                catalog = new HashMap<>();
            }
            catalog.put(rating, name);
        }

        public Map<Integer, String> getCatalog() {
            return catalog;
        }

        /**
         *
         * @return @throws Exception
         */
        public Metric instantiate() throws Exception {
            if (exec != MethodExecution.AUTOMATIC || name == null) {
                throw new Exception();
            }

            final Class<?> metricClass = Class.forName(name);
            final Constructor<?> constructor = metricClass.getConstructor();
            final Object instance = constructor.newInstance();
            if (instance instanceof Metric) {
                final Metric metric = (Metric) instance;
                metric.setMethod(this);
                return metric;
            }
            throw new Exception();
        }
    }
}
