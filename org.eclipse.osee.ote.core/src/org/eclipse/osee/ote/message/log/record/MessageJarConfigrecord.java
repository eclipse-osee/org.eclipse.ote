/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ote.message.log.record;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.log.record.TestRecord;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class MessageJarConfigrecord extends TestRecord {

   private final String[] jarVersions;

   private static final long serialVersionUID = 6919229589873467398L;

   /**
    * ScriptConfigRecord Constructor. Constructs test script configuration log message with timestamp.
    * 
    * @param script The test script who's configuration is to be recorded.
    */
   public MessageJarConfigrecord(TestScript script, String[] jarVersions, Map<String, File> availableJars) {
      super(script.getTestEnvironment(), Level.CONFIG, script.getClass().getName(), false);
      this.jarVersions = jarVersions;
   }

   /**
    * Convert an element to XML format.
    * 
    * @return XML formated config element.
    */
   @Override
   public Element toXml(Document doc) {
      Element jarConfig = doc.createElement("JarConfig");
      doc.appendChild(jarConfig);

      for (String version : jarVersions) {
         Element el = doc.createElement("Jar");
         el.setTextContent(version);
         jarConfig.appendChild(el);
      }
      return jarConfig;
   }
}