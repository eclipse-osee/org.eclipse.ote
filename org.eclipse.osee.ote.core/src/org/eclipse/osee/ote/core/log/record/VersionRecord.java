/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.ote.core.log.record;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.persistence.XmlizableStream;
import org.eclipse.osee.ote.core.VersionSupport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is used to store Version Information to the test script TMO.
 * <br> See {@link VersionSupport}.
 * @author Dominic Leiner
 */
public class VersionRecord implements Xmlizable, XmlizableStream {
   String name;
   String version;
   Boolean isUnderTest;
   
   public VersionRecord() {
   }
   
   public VersionRecord(String name, String version) {
      this.name = name;
      this.version = version;
   }
   
   public VersionRecord(String name, String version, boolean underTest) {
      this.name = name;
      this.version = version;
      this.isUnderTest = underTest;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public void setUnderTest(Boolean underTest) {
      this.isUnderTest = underTest;
   }

   public String getName() {
      return name;
   }

   public String getVersion() {
      return version;
   }
   
   public Boolean isUnderTest() {
      return isUnderTest;
   }

   public String getUnderTestAsString() {
      if(isUnderTest != null) {
         return Boolean.toString(isUnderTest);
      } else {
         return null;
      }
   }

   @Override
   public Element toXml(Document doc) {
      Element recordElement = doc.createElement("VersionInformation");
      recordElement.setAttribute("name", name);
      recordElement.setAttribute("version", version);
      if(isUnderTest != null) {
         recordElement.setAttribute("underTest", getUnderTestAsString());
      }
      return recordElement;
   }

   @Override
   public void toXml(XMLStreamWriter writer) throws XMLStreamException {
      writer.writeStartElement("VersionInformation");
      writer.writeAttribute("name", name);
      writer.writeAttribute("version", version);
      if(isUnderTest != null) {
         writer.writeAttribute("underTest", getUnderTestAsString());
      }
      writer.writeEndElement();
   }
}