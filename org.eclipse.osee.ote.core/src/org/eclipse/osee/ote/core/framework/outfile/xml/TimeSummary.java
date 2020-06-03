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

import java.util.Date;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.persistence.XmlizableStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Andy Jury
 */
public class TimeSummary implements Xmlizable, XmlizableStream {

   @JsonProperty
   private final long elapsedTime;
   @JsonProperty
   private final Date startTime;
   @JsonProperty
   private final Date endTime;
   @JsonProperty
   private final String elapsed;

   public TimeSummary(long elapsedTime, Date startTime, Date endTime, String elapsed) {
      super();
      this.elapsedTime = elapsedTime;
      this.startTime = startTime;
      this.endTime = endTime;
      this.elapsed = elapsed;
   }

   @Override
   public Element toXml(Document doc) {
      Element recordElement = doc.createElement("TimeSummary");
      recordElement.setAttribute("milliseconds", Long.toString(elapsedTime));
      recordElement.setAttribute("startDate", startTime.toString());
      recordElement.setAttribute("endDate", endTime.toString());
      recordElement.setAttribute("elapsed", elapsed);
      return recordElement;
   }

   @Override
   public void toXml(XMLStreamWriter writer) throws XMLStreamException {
      writer.writeStartElement("TimeSummary");
      writer.writeAttribute("milliseconds", Long.toString(elapsedTime));
      writer.writeAttribute("startDate", startTime.toString());
      writer.writeAttribute("endDate", endTime.toString());
      writer.writeAttribute("elapsed", elapsed);
      writer.writeEndElement();
   }

   public long getElapsedTime() {
      return elapsedTime;
   }

   public Date getStartTime() {
      return startTime;
   }

   public Date getEndTime() {
      return endTime;
   }

   public String getElapsed() {
      return elapsed;
   }

}
