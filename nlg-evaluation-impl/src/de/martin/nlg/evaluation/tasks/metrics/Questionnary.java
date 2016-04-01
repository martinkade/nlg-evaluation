/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.martin.nlg.evaluation.tasks.metrics;

import de.martin.nlg.evaluation.utils.QualityCriteria;
import java.util.Map;

/**
 * ...
 * <p/>
 * @author Martin Kade @version 2015-09-01
 */
public class Questionnary {

    /**
     *
     */
    private final QualityCriteria.EvaluationMethod method;

    /**
     *
     */
    private int choice, rating;

    /**
     *
     */
    private String comment;

    /**
     *
     * @param method
     */
    public Questionnary(QualityCriteria.EvaluationMethod method) {
        this.method = method;
        choice = -1;
    }

    /**
     *
     * @param name
     * @param type
     */
    private void printHeader(String name, String type) {
        System.out.println("###-----Bewertungsbogen--------------");
        System.out.printf("Wie bewerten Sie '%s' hinsichtlich '%s'?\n", name, type);
    }

    /**
     *
     * @return
     */
    public QualityCriteria.EvaluationMethod getMethod() {
        return method;
    }

    /**
     *
     * @param command
     * @return Always true for now
     */
    public boolean setChoice(String command) {
        if (command.equalsIgnoreCase("q")) {
            System.exit(0);
        }

        final Map<Integer, String> catalog = method.getCatalog();
        if (command.matches("^-?\\d+$") && catalog != null) {
            final int c = Integer.parseInt(command);
            choice = catalog.containsKey(c) ? c : -1;
            if (choice == -1) {
                comment = command;
                System.out.println("Freitextkommentar: " + comment);
            } else {
                System.out.println("Ihre Einschätzung: " + catalog.get(choice));
            }
        } else {
            comment = command;
            System.out.println("Freitextkommentar: " + comment);
        }
        return true;
    }

    /**
     *
     * @param criteria
     */
    public void printCatalog(QualityCriteria criteria) {
        printHeader(criteria.getName(), criteria.getType());
        final Map<Integer, String> catalog = method.getCatalog();
        if (catalog == null) {
            System.out.println("Freitextkommentar:");
            return;
        }

        catalog.entrySet().stream().forEach((entry) -> {
            System.out.println(String.format("- %-15s(%d)",
                    entry.getValue(), entry.getKey()
            ));
        });

        System.out.println("oder Freitextkommentar:");

    }

    /**
     *
     * @return
     */
    public double getResult() {
        return (method.getCatalog() == null || choice == -1)
                ? 0.0d
                : (double) choice / method.getCatalog().size();
    }
}
