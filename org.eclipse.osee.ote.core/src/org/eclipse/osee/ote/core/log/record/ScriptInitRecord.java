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
package org.eclipse.osee.ote.core.log.record;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.jackson.annotate.JsonProperty;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.log.TestLevel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class ScriptInitRecord extends TestRecord {

   private static final long serialVersionUID = -8573603873316659237L;
   private final boolean startFlag;

   /**
    * ScriptInitRecord Constructor. Constructs test script configuration log message with timestamp.
    * 
    * @param script The test script who's configuration is to be recorded.
    * @param timeStamp <b>True</b> if a timestamp should be recorded, <b>False</b> if not.
    */
   public ScriptInitRecord(TestScript script, boolean timeStamp, boolean startFlag) {
      super(script.getTestEnvironment(), TestLevel.TEST_POINT, script.getClass().getName(), timeStamp);
      this.startFlag = startFlag;
   }

   /**
    * ScriptConfigRecord Constructor. Constructs test script configuration log message.
    * 
    * @param script The test script who's configuration is to be recorded.
    * @param startFlag true for the beginning of init, false for the end.
    */
   public ScriptInitRecord(TestScript script, boolean startFlag) {
      this(script, true, startFlag);
   }

   /**
    * Returns whether this is the start of the init or end.
    * 
    * @return <b>true</b> if start of the script init, <b>false</b> if the end.
    */
   public boolean getStartFlag() {
      return startFlag;
   }

   @Override
   public Element toXml(Document doc) {
      //This element is not added to the output xml.
      //It is here because ScriptLogHandler creates an
      //element out of the record and adds it to the xml.
      //We don't add it, but it was necessary to create the
      //element so that we could test for ScriptInitRecord.
      return doc.createElement("ScriptInit");
   }

   @Override
   public void toXml(XMLStreamWriter writer) throws XMLStreamException {
      writer.writeStartElement("ScriptInit");
   }

   @Override
   @JsonProperty("Script")
   public String getMessage() {
	   return super.getMessage();
   }
}