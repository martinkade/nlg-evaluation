/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.martin.nlg.evaluation.tasks;

import de.martin.nlg.evaluation.utils.Sentence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Task that reads the input text and reference files converting them each to a
 * list of {@link Sentence}s.
 * <p/>
 * @author Martin Kade @version 2015-09-01
 */
public class SentenceBuilderTask implements Callable<List<Sentence>> {

    /**
     * TAG used for identifying output in the console.
     */
    private static final String TAG = SentenceBuilderTask.class.getSimpleName();

    /**
     * The text file to be decompounded.
     */
    private final File textFile;

    /**
     * Constructor.
     *
     * @param textFile The text file to be decompounded
     */
    public SentenceBuilderTask(File textFile) {
        this.textFile = textFile;
    }

    @Override
    public List<Sentence> call() throws Exception {
        // Logger.getLogger(TAG).log(Level.INFO, String.format("decompounding '%s'...", textFile.getPath()));

        final List<Sentence> sentences = new ArrayList<>();
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(textFile));
        } catch (FileNotFoundException e) {
            Logger.getLogger(TAG).log(Level.SEVERE, null, e);
            throw new IOException(e.getMessage());
        }

        for (;;) {
            String line = null;

            try {
                line = reader.readLine();
            } catch (IOException ex) {
                throw ex;
            }

            // test for end of file
            if (line == null) {
                break;
            }

            sentences.add(new Sentence(line.trim().split("\\s+")));
        }

        return sentences;
    }
}
