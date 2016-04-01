/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.martin.nlg.evaluation.tasks;

import de.martin.nlg.evaluation.EvaluationSetup;
import de.martin.nlg.evaluation.utils.QualityCriteria;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Xml parser for setup file.
 * <p/>
 * @author Martin Kade @version 2015-09-01
 */
public class SetupHandler extends DefaultHandler {

    /**
     * The {@link EvaluationSetup} to store the file content in.
     */
    private EvaluationSetup evaluationSetup;

    /**
     * The quality criteria.
     */
    private QualityCriteria criteria;

    /**
     * The current method.
     */
    private QualityCriteria.EvaluationMethod method;

    /**
     * Flags.
     */
    private boolean insideHead, insideMethod, insideCatalog;

    /**
     * Rating in catalog of {@link #method}.
     */
    private int currentRating;

    /**
     * Content from {@link #characters(char[], int, int)}.
     */
    private String content;

    /**
     * Constructor.
     */
    public SetupHandler() {
        insideHead = insideMethod = insideCatalog = false;
        currentRating = -1;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        evaluationSetup = new EvaluationSetup();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (localName.equalsIgnoreCase("quality-criteria")) {
            criteria = new QualityCriteria();
            criteria.setWeightSum(Float.parseFloat(attributes.getValue("weight-sum")));
        } else if (localName.equalsIgnoreCase("head")) {
            insideHead = true;
        } else if (localName.equalsIgnoreCase("item") && insideHead) {
            if (attributes.getValue("name").equalsIgnoreCase("type")) {
                criteria.setName(attributes.getValue("value"));
            } else if (attributes.getValue("name").equalsIgnoreCase("name")) {
                criteria.setType(attributes.getValue("value"));
            }
        } else if (localName.equalsIgnoreCase("eval-method")) {
            method = new QualityCriteria.EvaluationMethod();
            insideMethod = true;
            method.setWeight(Float.parseFloat(attributes.getValue("weight")));
            method.setThreshold(Float.parseFloat(attributes.getValue("threshold")));
            method.setExec(execTypeFromString(attributes.getValue("exec")));
            method.setType(typeFromString(attributes.getValue("type")));
            method.setName(attributes.getValue("name"));
        } else if (localName.equalsIgnoreCase("catalog")) {
            insideCatalog = true;
        } else if (localName.equalsIgnoreCase("rating")) {
            currentRating = Integer.parseInt(attributes.getValue("numeric"));

        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if (insideCatalog) {
            content = new String(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (localName.equalsIgnoreCase("quality-criteria")) {

        } else if (localName.equalsIgnoreCase("head")) {
            insideHead = false;
        } else if (localName.equalsIgnoreCase("item")) {

        } else if (localName.equalsIgnoreCase("eval-method")) {
            criteria.addMethod(method);
            insideMethod = false;
        } else if (localName.equalsIgnoreCase("catalog")) {
            insideCatalog = false;
        } else if (localName.equalsIgnoreCase("rating")) {
            method.addCatalogEntry(currentRating, content);
            currentRating = -1;
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        evaluationSetup.setCriteria(criteria);
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        super.error(e);
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        super.fatalError(e);
    }

    public EvaluationSetup getEvaluationSetup() {
        return evaluationSetup;
    }

    private QualityCriteria.MethodExecution execTypeFromString(String string) {
        if (string.equalsIgnoreCase("automatic")) {
            return QualityCriteria.MethodExecution.AUTOMATIC;
        }
        return QualityCriteria.MethodExecution.HUMAN_ASSESSED;
    }

    private QualityCriteria.MethodType typeFromString(String string) {
        if (string.equalsIgnoreCase("extrinsic")) {
            return QualityCriteria.MethodType.EXTRINSIC;
        }
        return QualityCriteria.MethodType.INTRINSIC;
    }
}
