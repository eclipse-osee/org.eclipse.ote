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
package org.eclipse.osee.ote.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.persistence.XmlizableStream;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.log.record.VersionRecord;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Provide support for recording Version information from a property file to test script TMO.
 * @author Dominic Leiner
 */
public class VersionSupport implements Xmlizable, XmlizableStream {
   public static String VERSION_FILE = System.getProperty("ote.version.file");
   private final ArrayList<VersionRecord> versionData;
   
   public VersionSupport() {
      versionData = new ArrayList<VersionRecord>();
      if(VERSION_FILE!=null) {
         processData();         
      }
   }
   
   private void processData() {

      try (InputStream input = new FileInputStream(VERSION_FILE)) {

         Properties prop = new Properties();
         prop.load(input);
         Enumeration<Object> enu = prop.keys();
         
         while(enu.hasMoreElements()) {
            String key = (String) enu.nextElement();
            versionData.add(new VersionRecord(key, prop.getProperty(key)));
         }
         
      }
      catch (IOException e) {
         OseeLog.log(getClass(), Level.SEVERE, "Failure in reading in Version file: " + VERSION_FILE, e);
      }
   }

   @Override
   public Element toXml(Document doc) {
      Element versions = doc.createElement("VersionInformation");
      for (VersionRecord data : versionData) {
         Element el = doc.createElement("Version");
         versions.appendChild(el);
         el.setAttribute("name", data.getName());  
         el.setAttribute("version", data.getVersion()); 
         String underTest = data.getUnderTestAsString();
         if(underTest!=null) {
            el.setAttribute("underTest", underTest);
         }
      }
      return versions;
   }

   @Override
   public void toXml(XMLStreamWriter writer) throws XMLStreamException {
      writer.writeStartElement("VersionInformation");
      for (VersionRecord data : versionData) {
         writer.writeStartElement("Version");
         writer.writeAttribute("name", data.getName());
         writer.writeAttribute("version", data.getVersion());
         String underTest = data.getUnderTestAsString();
         if(underTest!=null) {
            writer.writeAttribute("underTest", underTest);
         }
         writer.writeEndElement();
      }
      writer.writeEndElement();
   }
}
