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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.persistence.XmlizableStream;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.log.TestLevel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class ScriptResultRecord extends TestRecord {
    private static final long serialVersionUID = -6132341487630154239L;
    private final List<Xmlizable> childElements;
    private final List<XmlizableStream> childStreamElements;

    /**
     * ScriptConfigRecord Constructor. Constructs test script configuration log
     * message with timestamp.
     * 
     * @param script
     *            The test script who's configuration is to be recorded.
     * @param timeStamp
     *            <b>True </b> if a timestamp should be recorded, <b>False </b>
     *            if not.
     */
    public ScriptResultRecord(TestScript script) {
        super(script.getTestEnvironment(), TestLevel.TEST_POINT, script.getClass().getName(), false);
        childElements = new ArrayList<>(1000);
        childStreamElements = new ArrayList<>(100);
    }

    public void addChildElement(Xmlizable xml) {
        childElements.add(xml);
    }

    public void addChildElement(XmlizableStream xml) {
        childStreamElements.add(xml);
    }

    /**
     * Convert an element to XML format.
     * 
     * @return XML formated config element.
     */
    @Override
    public Element toXml(Document doc) {
        Element result = doc.createElement("ScriptResult");
        for (Xmlizable xml : childElements) {
            result.appendChild(xml.toXml(doc));
        }
        childElements.clear();
        return result;
    }

    @Override
    public void toXml(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("ScriptResult");
        for (XmlizableStream xml : childStreamElements) {
            xml.toXml(writer);
        }
        writer.writeEndElement();
    }

    @JsonProperty
    public List<XmlizableStream> getResults() {
        return childStreamElements;
    }

    @Override
   @JsonProperty("ScriptName")
    public String getMessage() {
        return super.getMessage();
    }
}