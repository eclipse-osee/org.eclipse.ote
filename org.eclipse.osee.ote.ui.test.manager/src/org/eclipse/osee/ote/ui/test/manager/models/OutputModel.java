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

package org.eclipse.osee.ote.ui.test.manager.models;

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.ui.test.manager.internal.TestManagerPlugin;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Base Class for all TestManagers
 */
public class OutputModel extends FileModel {

   private static final String OUTPUT_EXTENSION = "tmo";

   private int failedTestPoints = 0;
   private int passedTestPoints = 0;
   private int interactiveTestPoints = 0;
   private boolean aborted = false;
   private boolean exists = false;

   public OutputModel(String rawFilename) {
      super(rawFilename);
   }

   public boolean doesOutfileExist() {
      return exists;
   }

   public void updateTestPointsFromOutfile() {
      try {
         File outfile = getFile();
         exists = outfile.exists();
         if (outfile.exists() && outfile.length() > 0) {
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            xmlReader.setContentHandler(new ParseTestPoints());
            xmlReader.parse(new InputSource(new FileInputStream(outfile)));
         }
      } catch (Exception ex) {
         OseeLog.log(TestManagerPlugin.class, Level.SEVERE, ex);
      }
   }

   public int getFailedTestPoints() {
      return failedTestPoints;
   }

   public int getPassedTestPoints() {
      return passedTestPoints;
   }

   public int getInteractiveTestPoints() {
      return interactiveTestPoints;
   }

   public void setFailedTestPoints(int failedTestPoints) {
      this.failedTestPoints = failedTestPoints;
   }

   public void setPassedTestPoints(int passedTestPoints) {
      this.passedTestPoints = passedTestPoints;
   }

   public void setInteractiveTestPoints(int interactiveTestPoints) {
      this.interactiveTestPoints = interactiveTestPoints;
   }

   private class ParseTestPoints extends AbstractSaxHandler {

      @Override
      public void endElementFound(String uri, String localName, String name) throws SAXException {
      }

      @Override
      public void startElementFound(String uri, String localName, String name, Attributes attributes) throws SAXException {
         if ("TestPointResults".equals(name)) {
            String fail = attributes.getValue("fail");
            String pass = attributes.getValue("pass");
            String interactive = attributes.getValue("interactive");
            String aborted = attributes.getValue("aborted");
            try {
               failedTestPoints = Integer.parseInt(fail);
               passedTestPoints = Integer.parseInt(pass);
               interactiveTestPoints = Integer.parseInt(interactive);
               if (aborted != null && aborted.length() > 0) {
                  OutputModel.this.aborted = Boolean.parseBoolean(aborted);
               }
            } catch (NumberFormatException ex) {

            }
         }
      }
   }

   public String getFileExtension() {
      return OUTPUT_EXTENSION;
   }

   public boolean isAborted() {
      return aborted;
   }

   public void setAborted(boolean b) {
      aborted = b;

   }
}