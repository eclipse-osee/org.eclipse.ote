/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ote.core.framework.outfile.xml;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.persistence.XmlizableStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Andy Jury
 */
public class TestPointResults implements Xmlizable, XmlizableStream {

   @JsonProperty
   private final int passes;
   @JsonProperty
   private final int fails;
   @JsonProperty
   private final int interactives;
   @JsonProperty
   private final boolean aborted;
   @JsonProperty
   private final int total;

   public TestPointResults(int passes, int fails, int interactives, boolean isAborted) {
      super();
      this.passes = passes;
      this.fails = fails;
      this.interactives = interactives;
      this.aborted = isAborted;
      this.total = passes + fails;
   }

   @Override
   public Element toXml(Document doc) {
      Element recordElement = doc.createElement("TestPointResults");
      recordElement.setAttribute("pass", Integer.toString(passes));
      recordElement.setAttribute("fail", Integer.toString(fails));
      recordElement.setAttribute("interactive", Integer.toString(interactives));
      recordElement.setAttribute("aborted", Boolean.toString(aborted));
      recordElement.setAttribute("total", Integer.toString(total));
      return recordElement;
   }

   @Override
   public void toXml(XMLStreamWriter writer) throws XMLStreamException {
      writer.writeStartElement("TestPointResults");
      writer.writeAttribute("pass", Integer.toString(passes));
      writer.writeAttribute("fail", Integer.toString(fails));
      writer.writeAttribute("interactive", Integer.toString(interactives));
      writer.writeAttribute("aborted", Boolean.toString(aborted));
      writer.writeAttribute("total", Integer.toString(total));
      writer.writeEndElement();
   }

   public int getPasses() {
      return passes;
   }

   public int getFails() {
      return fails;
   }

   public int getInteractives() {
      return interactives;
   }

   public boolean isAborted() {
      return aborted;
   }

   public int getTotal() {
      return total;
   }

}
