/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.martin.nlg.evaluation.tasks;

import de.martin.nlg.evaluation.EvaluationSetup;

import java.io.File;
import java.io.FileInputStream;

import java.util.concurrent.Callable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Xml reader task via {@link SetupHandler}.
 * <p/>
 * @author Martin Kade @version 2015-09-01
 */
public class SetupReaderTask implements Callable<EvaluationSetup> {

    /**
     * The xml file containing the evaluation setup.
     */
    private final File file;

    /**
     * Custom xml handler.
     */
    private final SetupHandler setupHandler;

    /**
     * Constructor.
     *
     * @param file The xml file containing the evaluation setup.
     */
    public SetupReaderTask(File file) {
        this.file = file;
        setupHandler = new SetupHandler();
    }

    @Override
    public EvaluationSetup call() throws Exception {
        final XMLReader xmlReader = buildReader();
        final InputSource source = new InputSource(new FileInputStream(file));
        xmlReader.parse(source);
        xmlReader.getContentHandler();
        return setupHandler.getEvaluationSetup();
    }

    /**
     * Build the {@link XMLReader} parsing the xml file with the custom
     * {@link #setupHandler}.
     *
     * @return The xml reader with {@link #setupHandler}
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    private XMLReader buildReader() throws ParserConfigurationException, SAXException {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        final SAXParser saxParser = factory.newSAXParser();
        final XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setContentHandler(setupHandler);
        return xmlReader;
    }
}
