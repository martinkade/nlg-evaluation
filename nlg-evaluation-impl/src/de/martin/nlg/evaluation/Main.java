/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.martin.nlg.evaluation;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entry point of the application containing the {@link #main(java.lang.String[])
 * class method.
 * <p/>
 * @author Martin Kade @version 2015-09-01
 */
public class Main {

    /**
     * Main method of the application.
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        final NLGEvaluation evaluation = NLGEvaluation.getInstance();
        try (Scanner scanner = new Scanner(System.in)) {

            // read the configuration file
            readSetupFile(scanner, evaluation);

            // if there are any automatic metrics within the setup that require
            // input texts to preceed
            if (evaluation.requiresInputTexts()) {

                // read file to be evaluated
                System.out.println("Zu evaluierende Textdatei (*.txt):");
                final File originalFile = initFile(scanner);
                final List<File> referenceFiles = new ArrayList<>();

                // read necessary reference texts
                boolean additionalReferenceText = true;
                while (additionalReferenceText) {
                    System.out.println("Referenztext (*.txt):");
                    referenceFiles.add(initFile(scanner));

                    System.out.println("Weiteren Referenztext [j/n]:");
                    additionalReferenceText = scanner.nextLine().equalsIgnoreCase("j");
                }

                // start evaluation with source files
                startEvaluation(scanner, evaluation, originalFile, referenceFiles);

            } else {

                // start evaluation without source files
                startEvaluation(scanner, evaluation, null, null);
            }

            // exit program
            if (scanner.nextLine().equalsIgnoreCase("q")) {
                System.exit(0);
            }

            // close scanner
            scanner.close();
        }
    }

    /**
     * Read and parse the setup file.
     *
     * @param scanner Console input scanner
     * @param evaluation Reference to the current evaluation
     */
    private static void readSetupFile(Scanner scanner, NLGEvaluation evaluation) {
        System.out.println("Konfigurationsdatei (*.xml):");
        boolean invalidSetupFile = true;
        while (invalidSetupFile) {
            final File setupFile = initFile(scanner);
            try {
                evaluation.config(setupFile);
                invalidSetupFile = false;
            } catch (Exception ex) {
                System.out.println("Fehlerhafte Datei: " + ex.getMessage());
                System.out.println("Programm beenden [q] oder neuer Versuch:");
            }
        }
    }

    /**
     * Start evaluation calling
     * {@link NLGEvaluation#initSourceWithFiles(File, List)}.
     *
     * @param scanner Console input scanner
     * @param evaluation Reference to the current evaluation
     * @param originalFile The original file or null if not required
     * @param referenceFiles The reference files or null of not required
     */
    private static void startEvaluation(Scanner scanner, NLGEvaluation evaluation, File originalFile, List<File> referenceFiles) {
        System.out.println("Die Konfiguration ist abgeschlossen.");
        System.out.println("Evaluation starten [r] oder Programm beenden [q]?");
        if (scanner.nextLine().equalsIgnoreCase("q")) {
            System.exit(0);
        }

        try {
            evaluation.initSourceWithFiles(originalFile, referenceFiles, scanner);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getSimpleName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Create a {@link File} from user input in console or exit program if
     * console input equals 'q'.
     *
     * @param scanner The console scanner reading user input
     * @return A file from the typed system file path
     */
    private static File initFile(Scanner scanner) {
        final String path = scanner.nextLine();
        if (path.equalsIgnoreCase("q")) {
            System.exit(0);
        }
        final File f = new File(path);
        if (f.exists() && !f.isDirectory()) {
            return f;
        } else {
            System.out.println(String.format("Die Datei '%s' existiert nicht.", path));
            System.out.println("Programm beenden [q] oder neuer Versuch:");
            return initFile(scanner);
        }
    }
}
