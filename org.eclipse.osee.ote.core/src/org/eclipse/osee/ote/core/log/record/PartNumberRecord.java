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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Dominic Leiner
 */
public class PartNumberRecord implements Xmlizable, XmlizableStream {
   String part;
   String number;
   
   public PartNumberRecord() {
   }
   
   public PartNumberRecord(String part, String number) {
      this.part = part;
      this.number = number;
   }

   public void setPart(String part) {
      this.part = part;
   }

   public void setNumber(String number) {
      this.number = number;
   }

   public String getPart() {
      return part;
   }

   public String getNumber() {
      return number;
   }


   @Override
   public Element toXml(Document doc) {
      Element recordElement = doc.createElement("PartNumber");
      recordElement.setAttribute("part", part);
      recordElement.setAttribute("number", number);
      return recordElement;
   }

   @Override
   public void toXml(XMLStreamWriter writer) throws XMLStreamException {
      writer.writeStartElement("PartNumber");
      writer.writeAttribute("part", part);
      writer.writeAttribute("number", number);
      writer.writeEndElement();
   }
}