/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.ote.simple.test.environment.outfile.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.persistence.XmlizableStream;
import org.eclipse.osee.ote.core.OteProperties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Andy Jury
 */
public class SystemInfo implements Xmlizable, XmlizableStream {
   @Override
   public Element toXml(Document doc) {
      Element recordElement = doc.createElement("SystemInfo");
      recordElement.setAttribute("osName", System.getProperty("os.name"));
      recordElement.setAttribute("osVersion", System.getProperty("os.version"));
      recordElement.setAttribute("osArch", System.getProperty("os.arch"));
      recordElement.setAttribute("oseeVersion", OseeCodeVersion.getVersion());
      recordElement.setAttribute("javaVersion", System.getProperty("java.version"));
      String title = OteProperties.getOseeOteServerTitle();
      if (title != null) {
         recordElement.setAttribute("oseeServerTitle", title);
      }
      return recordElement;
   }

   @Override
   public void toXml(XMLStreamWriter writer) throws XMLStreamException {
      writer.writeStartElement("SystemInfo");
      writer.writeAttribute("osName", System.getProperty("os.name"));
      writer.writeAttribute("osVersion", System.getProperty("os.version"));
      writer.writeAttribute("osArch", System.getProperty("os.arch"));
      writer.writeAttribute("oseeVersion", OseeCodeVersion.getVersion());
      writer.writeAttribute("javaVersion", System.getProperty("java.version"));
      String title = OteProperties.getOseeOteServerTitle();
      if (title != null) {
         writer.writeAttribute("oseeServerTitle", title);
      }
      writer.writeEndElement();
   }

   @JsonProperty
   public String getOperatingSystem() {
      return System.getProperty("os.name");
   }

   @JsonProperty
   public String getOperatingSystemVersion() {
      return System.getProperty("os.version");
   }

   @JsonProperty
   public String getArchitecture() {
      return System.getProperty("os.arch");
   }

   @JsonProperty
   public String getOseeCodeVersion() {
      return OseeCodeVersion.getVersion();
   }

   @JsonProperty
   public String getJavaVersion() {
      return System.getProperty("java.version");
   }

   @JsonProperty
   public String getOteServerTitle() {
      return OteProperties.getOseeOteServerTitle();
   }
}
