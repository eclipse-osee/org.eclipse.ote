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
package org.eclipse.osee.ote.core.log.record;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.persistence.XmlizableStream;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.jdk.core.util.xml.XMLStreamWriterUtil;
import org.eclipse.osee.ote.core.MethodFormatter;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.log.TestLevel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Ryan D. Brooks
 */
public class TraceRecord extends TestRecord implements Xmlizable {
   private static final long serialVersionUID = 8567378567805515775L;
   private final String objectName;
   private final String methodName;
   private final MethodFormatter methodArguments;
   private final ArrayList<Xmlizable> additionalElements;
   private final ArrayList<XmlizableStream> additionalStreamElements;

   private static final String additionalString = "AdditionalInfo";

   /**
    * TraceRecord Constructor. Sets up a Trace log message.
    * 
    * @param source The object requesting the logging.
    * @param timeStamp <b>True </b> if a timestamp should be recorded, <b>False </b> if not.
    */
   public TraceRecord(ITestEnvironmentAccessor source, String objectName, String methodName, MethodFormatter methodArguments, boolean timeStamp, boolean startFlag) {
      this(source, objectName, methodName, methodArguments, timeStamp);
   }

   public TraceRecord(ITestEnvironmentAccessor source, String objectName, String methodName, MethodFormatter methodArguments, boolean timeStamp) {
      super(source, TestLevel.TRACE, "", timeStamp);
      this.objectName = objectName;
      this.methodName = methodName;
      this.methodArguments = methodArguments;
      this.additionalElements = new ArrayList<>();
      this.additionalStreamElements = new ArrayList<>();
   }

   public TraceRecord(ITestEnvironmentAccessor source, String objectName2, String methodName2, MethodFormatter methodArguments2) {
      this(source, objectName2, methodName2, methodArguments2, true);
   }

   public void addAdditionalElement(Xmlizable object) {
      if (object != null) {
         additionalElements.add(object);
      }
   }

   public void addAdditionalElement(XmlizableStream object) {
      if (object != null) {
         additionalStreamElements.add(object);
      }
   }

   /**
    * Converts element to XML formating.
    * 
    * @return Element XML formated element.
    */
   @Override
   public Element toXml(Document doc) {
      Element trElement = doc.createElement("Trace");
      trElement.appendChild(Jaxp.createElement(doc, "ObjectName", objectName));
      trElement.appendChild(Jaxp.createElement(doc, "MethodName", methodName));
      trElement.appendChild(methodArguments.toXml(doc));
      if (!additionalElements.isEmpty()) {
         Element additional = doc.createElement(additionalString);
         trElement.appendChild(additional);
         for (Xmlizable object : additionalElements) {
            additional.appendChild(object.toXml(doc));
         }
      }
      if (TestRecord.getLocationLoggingOn()) {
         trElement.appendChild(getLocation(doc));
      }
      return trElement;
   }

   @Override
   public void toXml(XMLStreamWriter writer) throws XMLStreamException {
      writer.writeStartElement("Trace");
      XMLStreamWriterUtil.writeElement(writer, "ObjectName", objectName);
      XMLStreamWriterUtil.writeElement(writer, "MethodName", methodName);
      methodArguments.toXml(writer);
      if (!additionalElements.isEmpty()) {
         writer.writeStartElement(additionalString);
         for (XmlizableStream object : additionalStreamElements) {
            object.toXml(writer);
         }
         writer.writeEndElement();
      }
      writeLocationCheckLocationLoggingOn(writer);
   }
   
   @JsonProperty
   public String getArguments() {
	   return nonEmptyString(methodArguments.toString());
   }
   
   @JsonProperty
   public List<?> getAdditionalInfo() {
	   return nonEmptyList(additionalStreamElements);
   }
   
   @JsonProperty
   public String getObjectName() {
	   return nonEmptyString(objectName);
   }
   
   @JsonProperty
   public String getMethodName() {
	   return nonEmptyString(methodName);
   }
}