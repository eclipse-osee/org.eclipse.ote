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
package org.eclipse.osee.ote.core.testPoint;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.jackson.annotate.JsonProperty;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.persistence.XmlizableStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Robert A. Fisher
 */
public class RetryGroup extends CheckGroup {
   ArrayList<Xmlizable> childElements;
   @JsonProperty("ChildRecords")
   ArrayList<XmlizableStream> childStreamElements;

   public RetryGroup(String groupName) {
      super(Operation.OR, groupName);
      childElements = new ArrayList<>();
      childStreamElements = new ArrayList<>();
   }

   @Override
   public Element toXml(Document doc) {
      Element retVal = buildXml(doc, "RetryGroup");

      for (Xmlizable object : childElements) {
         retVal.appendChild(object.toXml(doc));
      }
      return retVal;
   }
   
   @Override
   public void toXml(XMLStreamWriter writer) throws XMLStreamException {
      buildXml(writer, "RetryGroup");
      for (XmlizableStream object : childStreamElements) {
         object.toXml(writer);
      }
      writer.writeEndElement();
   }

   public void addChildElement(XmlizableStream child) {
      childStreamElements.add(child);
   }

   public void addChildren(List<XmlizableStream> children) {
      childStreamElements.addAll(children);
   }

}
