/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.martin.nlg.evaluation;

import de.martin.nlg.evaluation.utils.QualityCriteria;

/**
 * ...
 * <p/>
 * @author Martin Kade @version 2015-09-01
 */
public class EvaluationSetup {

    /**
     *
     */
    private QualityCriteria criteria;

    /**
     * Default constructor.
     */
    public EvaluationSetup() {

    }

    public void setCriteria(QualityCriteria criteria) {
        this.criteria = criteria;
    }

    public QualityCriteria getCriteria() {
        return criteria;
    }

    /**
     *
     */
    public void print() {
        System.out.println("###-----Quality criteria-------------");
        System.out.println(String.format("# %-15s%s", "Name", criteria.getName()));
        System.out.println(String.format("# %-15s%s", "Type", criteria.getType()));
        System.out.println(String.format("# %-15s%.1f", "Weight sum", criteria.getWeightSum()));
        System.out.println("###-----Provided methods-------------");

        int i = 0;
        for (QualityCriteria.EvaluationMethod m : criteria.getMethods()) {
            System.out.println("# (" + ++i + ")");
            System.out.println(String.format("# %-15s%s", "Name", m.getName()));
            System.out.println(String.format("# %-15s%s", "Type", m.getType().name()));
            System.out.println(String.format("# %-15s%s", "Execution", m.getExec().name()));
            System.out.println(String.format("# %-15s%.1f", "Weight", m.getWeight()));
            System.out.println(String.format("# %-15s%.1f", "Threshold", m.getThreshold()));
        }
        System.out.println("###----------------------------------");
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * True if there are any automatic metrics included.
     *
     * @return True if there are any automatic metrics included
     */
    public boolean requiresInputTexts() {
        for (QualityCriteria.EvaluationMethod m : criteria.getMethods()) {
            if (m.getExec() == QualityCriteria.MethodExecution.AUTOMATIC) {
                return true;
            }
        }
        return false;
    }
}
