/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.test.manager.uut.selector.xml;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.ote.test.manager.uut.selector.UutItemCollection;
import org.eclipse.ote.test.manager.uut.selector.UutItemPartition;
import org.eclipse.ote.test.manager.uut.selector.UutItemPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author David N. Phillips
 */
public class TestManagerXmlInterface {
   private static final int NO_VALUE = -1;
   private static final String ATTRIBUTE_NAME = "name";
   private static final String ATTRIBUTE_SELECTED = "selected";
   private static final String ATTRIBUTE_RATE = "rate";
   private static final String ELEMENT_DISTRO = "distributionStatement";
   private static final String ELEMENT_PATH = "path";
   private static final String ELEMENT_PARTITION = "partition";
   private static final String ELEMENT_TEST_MANAGER = "testManager";
   private static final String ELEMENT_BACKWARD_UUT = "uut";
   private static final String ATTRIBUTE_BACKWARD_DEFAULT = "default";

   private UutItemCollection uutItemCollection = new UutItemCollection();
   private String distributionStatement = "";
   private String xmlGiven;
   private int parseErrorLine;
   private String parseErrorText;

   public TestManagerXmlInterface() {
      resetErrorValues();
   }
   
   private void resetErrorValues() {
      parseErrorLine = NO_VALUE;
      parseErrorText = "";
   }

   public UutItemCollection getUutItemCollection() {
      return uutItemCollection;
   }

   public void setUutItemCollection(UutItemCollection uutItemCollection) {
      resetErrorValues();
      this.uutItemCollection = uutItemCollection;
   }

   public String getDistributionStatement() {
      return distributionStatement;
   }

   public void setDistributionStatement(String distributionStatement) {
      this.distributionStatement = distributionStatement;
   }

   public String getXml() {
      String xml = "";
      try {
         DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder docBuilder;
         docBuilder = docFactory.newDocumentBuilder();
         Document doc = docBuilder.newDocument();
         Element rootElement = doc.createElement(ELEMENT_TEST_MANAGER);
         doc.appendChild(rootElement);
         for (UutItemPartition uutPart : uutItemCollection.getPartitions()) {
            Element partitionElement = doc.createElement(ELEMENT_PARTITION);
            rootElement.appendChild(partitionElement);
            partitionElement.setAttribute(ATTRIBUTE_NAME, uutPart.getPartition());
            partitionElement.setAttribute(ATTRIBUTE_SELECTED, asString(uutPart.isSelected()));
            for (UutItemPath pathItem : uutPart.getChildren()) {
               Element pathElement = doc.createElement(ELEMENT_PATH);
               partitionElement.appendChild(pathElement);
               pathElement.setAttribute(ATTRIBUTE_SELECTED, asString(pathItem.isSelected()));
               pathElement.setTextContent(pathItem.getPath());
               pathElement.setAttribute(ATTRIBUTE_RATE, pathItem.getRate());
            }
         }
         rootElement.appendChild(doc.createComment(""));
         rootElement.appendChild(doc.createComment("Please modify the Distribution Statement from the Advanced Page"));
         rootElement.appendChild(doc.createComment(""));
         Element distroElement = doc.createElement(ELEMENT_DISTRO);
         distroElement.setTextContent(distributionStatement);
         rootElement.appendChild(distroElement);
         TransformerFactory transformerFactory = TransformerFactory.newInstance();
         Transformer transformer = transformerFactory.newTransformer();
         DOMSource source = new DOMSource(doc);
         StringWriter stringWriter = new StringWriter();
         StreamResult result = new StreamResult(stringWriter);
         transformer.setOutputProperty(OutputKeys.INDENT, "yes");
         transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
         transformer.transform(source, result);
         xml = stringWriter.toString();
      } catch (Exception e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      }
      return xml;
   }

   public boolean setXml(String xmlString) {
      resetErrorValues();
      this.xmlGiven = xmlString;
      try {
         Document document = PositionalXMLReader.readXML(new ByteArrayInputStream(xmlString.getBytes("UTF-8")));
         parseDocument(document);
         return parseErrorLine == NO_VALUE;
      } catch (SAXException e) {
         OseeLog.log(TestManagerXmlInterface.class, Level.WARNING, e.toString());
         if (e instanceof SAXParseException) {
            parseErrorLine = ((SAXParseException)e).getLineNumber();
         }
         parseErrorText = e.getMessage();
      } catch (Throwable th) {
         OseeLog.log(TestManagerXmlInterface.class, Level.SEVERE, th);
      }
      return false;
   }

   private void parseDocument(Document document) {
      uutItemCollection.clear();
      Node rootNode = document.getChildNodes().item(0);
      NodeList docNodeList = rootNode.getChildNodes();
      for (int docNodeIndex = 0; docNodeIndex < docNodeList.getLength(); docNodeIndex++) {
         Node docNode = docNodeList.item(docNodeIndex);
         if (!processDocNode(docNode)) {
            processBackwardCompatibility(docNode);
         }
      }
   }

   private boolean processDocNode(Node docNode) {
      boolean success = false;
      if (docNode.getNodeName().equals(ELEMENT_PARTITION)) {
         String partition;
         if ((partition = getAttribute(docNode, ATTRIBUTE_NAME)).isEmpty()) {
            String lineNumber = docNode.getUserData(PositionalXMLReader.LINE_NUMBER_KEY_NAME).toString();
            if (parseErrorLine == NO_VALUE) {
               parseErrorLine = Integer.parseInt(lineNumber);
            }
            parseErrorText += "\nPartition name invalid in partition element at line: "+lineNumber;
            return false;
         }

         UutItemPartition item = uutItemCollection.getPartitionItem(partition);
         item.setSelected(isTrue(getAttribute(docNode, ATTRIBUTE_SELECTED)));

         NodeList pathNodeList = docNode.getChildNodes();
         for (int pathNodeIndex = 0; pathNodeIndex < pathNodeList.getLength(); pathNodeIndex++) {
            Node pathNode = pathNodeList.item(pathNodeIndex);
            processPathNode(pathNode, partition);
         }
         success = true;
      }
      else if (docNode.getNodeName().equals(ELEMENT_DISTRO)) {
         distributionStatement = docNode.getTextContent();
         success = true;
      }

      return success;
   }

   private void processPathNode(Node pathNode, String partition) {
      if (pathNode.getNodeName().equals(ELEMENT_PATH)) {
         String path = pathNode.getTextContent().trim();
         UutItemPath item = uutItemCollection.createItem(partition, path);
         item.setSelected(isTrue(getAttribute(pathNode, ATTRIBUTE_SELECTED)));
         item.setRate(getAttribute(pathNode, ATTRIBUTE_RATE));
      }
   }

   /**
    * Parses test manager config files from before the update to multi UUT selection
    */
   private void processBackwardCompatibility(Node partNode) {
      if (partNode.getNodeName().equals(ELEMENT_BACKWARD_UUT)) {
         String name = getAttribute(partNode, ATTRIBUTE_BACKWARD_DEFAULT);
         String path = partNode.getTextContent().trim();
         String partition = "UNKNOWN";
         if (name.contains("TestScript")) {
            partition = name.substring(0, name.length() - "TestScript".length()).toUpperCase();
         }
         else {
            // Check for common types
            for (String uut : new String[]{"SoftwareUnit1","SoftwareUnit2"}) {
               if (path.toUpperCase().contains("/"+uut)) {
                  partition = uut;
                  break;
               }
            }
         }
         uutItemCollection.createItem(partition, path);
      }
   }

   private String asString(boolean value) {
      return value?"true":"false";
   }

   private boolean isTrue(String textContent) {
      return textContent.trim().toLowerCase().equals("true");
   }

   private String getAttribute(Node node, String name) {
      String value = "";
      Node attributeNode = node.getAttributes().getNamedItem(name);
      if (attributeNode != null) {
         value = attributeNode.getTextContent().trim();
      }
      return value;
   }

   public String getErrorMessage() {
      return parseErrorText;
   }
   
   public Pair<Integer, Integer> getErrorRange() {
      Pair<Integer, Integer> pair = new Pair<>(NO_VALUE, NO_VALUE);
      if (parseErrorLine != NO_VALUE) {
         Pattern p = Pattern.compile("[\\s\\S]");    
         Matcher m = p.matcher(xmlGiven);  
         int lines = 1;
         int chars = 0;
         while (m.find()) {
            if (lines == parseErrorLine && pair.getFirst()==NO_VALUE) {
               pair.setFirst(chars);
            }
            if (lines == (parseErrorLine+1)) {
               pair.setSecond(chars - pair.getFirst());
               break;
            }
            if (m.group().matches("\\n")) {
               lines++;
            }
            chars++;
         }
         if (pair.getFirst() > 0 && pair.getSecond() == 0) {
            pair.setSecond(chars-1);
         }
      }
      return pair;
   }
}
