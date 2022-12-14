/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import java.util.logging.Level;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.ote.core.log.record.TestRecord;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * XML record for the qualification formality information for a test run.
 * 
 * @author Michael P. Masterson
 */
public class FormalityLevelRecord extends TestRecord {

   private final String formalityLevel;
   private final String buildId;
   private final String[] runnerNames;
   private final String[] witnessNames;
   private final String notes;
   private static final long serialVersionUID = 1L;

   public FormalityLevelRecord(String formalityLevel, String buildId, String[] runnerNames,
         String[] witnessNames, String notes) {
      super(null, Level.OFF, "", false);

      this.formalityLevel = formalityLevel;
      this.buildId = buildId;
      this.runnerNames = runnerNames;
      this.witnessNames = witnessNames;
      this.notes = notes;
   }

   @Override
   public Element toXml(Document doc) {
      Element formalityElement = doc.createElement("Qualification");
      formalityElement.setAttribute("level", formalityLevel);
      if (formalityLevel.equalsIgnoreCase("Development") != true) {
         formalityElement.setAttribute("buildId", buildId != null ? buildId : "unknown");

         Element runners = doc.createElement("ExecutedBy");
         for (String runner : this.runnerNames) {
            runners.appendChild(Jaxp.createElement(doc, "Name", runner));
         }
         formalityElement.appendChild(runners);

         Element witnesses = doc.createElement("Witnesses");
         for (String witness : this.witnessNames) {
            witnesses.appendChild(Jaxp.createElement(doc, "Name", witness));
         }
         formalityElement.appendChild(witnesses);

         formalityElement.appendChild(Jaxp.createElement(doc, "Notes", notes != null ? notes : ""));
      }
      return formalityElement;
   }

   @Override
   public void toXml(XMLStreamWriter writer) throws XMLStreamException {
      writer.writeStartElement("Qualification");
      writer.writeAttribute("level", formalityLevel);
      if (formalityLevel.equalsIgnoreCase("Development") != true) {
         writer.writeAttribute("buildId", buildId != null ? buildId : "unknown");
         writer.writeStartElement("ExecutedBy");
         for (String runner : this.runnerNames) {
            writer.writeStartElement("Name");
            writer.writeCharacters(runner);
            writer.writeEndElement();
         }
         writer.writeEndElement();

         writer.writeStartElement("Witnesses");
         for (String witness : this.witnessNames) {
            writer.writeStartElement("Name");
            writer.writeCharacters(witness);
            writer.writeEndElement();
         }
         writer.writeEndElement();

         writer.writeStartElement("Notes");
         writer.writeCharacters(notes != null ? notes : "");
         writer.writeEndElement();
      }
      writer.writeEndElement();
   }

   @JsonProperty
   public String getFormalityLevel() {
      return formalityLevel;
   }

   @JsonProperty
   public String getBuildId() {
      return buildId;
   }

   @JsonProperty
   public String[] getExecutedBy() {
      if (runnerNames.length > 0) {
         return runnerNames;
      }
      else {
         return null;
      }
   }

   @JsonProperty
   public String[] getWitnesses() {
      if (witnessNames.length > 0) {
         return witnessNames;
      }
      else {
         return null;
      }

   }

   @JsonProperty
   public String getNotes() {
      if (notes != null && notes.length() > 0) {
         return notes;
      }
      else {
         return null;
      }
   }

}
