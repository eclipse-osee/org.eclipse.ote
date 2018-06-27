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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.jackson.annotate.JsonProperty;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.jdk.core.util.xml.XMLStreamWriterUtil;
import org.eclipse.osee.ote.core.XmlSupport;
import org.eclipse.osee.ote.core.environment.interfaces.ITestPoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Robert A. Fisher
 */
public class CheckPoint implements ITestPoint {
	private final String testPointName;
	private String expected;
	private final String actual;
	private final boolean pass;
	private final long elpasedTime;
	private final int numTransmissions;

	/**
	 * CheckPoint objects are used for describing the result of a check and can
	 * be logged directly to a the logger as a testPoint or can be added to a
	 * CheckGroup if it is just a part of a larger series of checks being
	 * performed that all constitute one overall check.
	 * 
	 * @param testPointName
	 *            The item being tested. (i.e. TSD Button).
	 * @param expected
	 *            The expected condition for a pass point.
	 * @param actual
	 *            The actual condition during the check.
	 * @param pass
	 *            The result of the check.
	 * @param elapsedTime
	 *            The amount of time elapsed in milliseconds
	 */
	public CheckPoint(String testPointName, String expected, String actual, boolean pass, long elapsedTime) {
		this(testPointName, expected, actual, pass, 0, elapsedTime);
	}

	public CheckPoint(String testPointName, String expected, String actual, boolean pass, int numTransmissions, long elapsedTime) {
		this.testPointName = testPointName;
		this.expected = expected.equals("") ? " " : XmlSupport.convertNonPrintableCharacers(expected);
		this.actual = actual.equals("") ? " " : XmlSupport.convertNonPrintableCharacers(actual);
		this.pass = pass;
		this.elpasedTime = elapsedTime;
		this.numTransmissions = numTransmissions;
	}

	public CheckPoint(String testPointName, Object expected, Object actual, boolean pass, long elapsedTime) {
		this(testPointName, expected.toString(), actual.toString(), pass, elapsedTime);
	}

	public CheckPoint(String testPointName, Object expected, Object actual, boolean pass) {
		this(testPointName, expected.toString(), actual.toString(), pass, 0);
	}

	public CheckPoint(String testPointName, String expected, String actual, boolean pass) {
		this(testPointName, expected, actual, pass, 0);
	}

	public CheckPoint(String testPointName, boolean expected, boolean actual) {
		this(testPointName, expected, actual, expected == actual, 0);
	}

	/**
	 * @return Returns the actual.
	 */
	@JsonProperty
	public String getActual() {
		return actual;
	}

	/**
	 * @return Returns the expected.
	 */
	@JsonProperty
	public String getExpected() {
		return expected;
	}

	/**
	 * @return Returns the pass.
	 */
	@Override
	public boolean isPass() {
		return pass;
	}

	public void setExpected(String expected) {
		this.expected = XmlSupport.convertNonPrintableCharacers(expected);
	}

	@Override
	public Element toXml(Document doc) {
		Element checkPointElement = doc.createElement("CheckPoint");

		checkPointElement.appendChild(Jaxp.createElement(doc, "TestPointName", testPointName));
		checkPointElement.appendChild(Jaxp.createElement(doc, "Expected", expected));
		checkPointElement.appendChild(Jaxp.createElement(doc, "Actual", actual));
		checkPointElement.appendChild(Jaxp.createElement(doc, "Result", pass ? "PASSED" : "FAILED"));
		checkPointElement.appendChild(Jaxp.createElement(doc, "ElapsedTime", Long.toString(this.elpasedTime)));
		checkPointElement.appendChild(Jaxp.createElement(doc, "NumberOfTransmissions", Integer.toString(this.numTransmissions)));

		return checkPointElement;
	}

	@Override
	public void toXml(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("CheckPoint");
		XMLStreamWriterUtil.writeElement(writer, "TestPointName", testPointName);
		XMLStreamWriterUtil.writeElement(writer, "Expected", expected);
		XMLStreamWriterUtil.writeElement(writer, "Actual", actual);
		XMLStreamWriterUtil.writeElement(writer, "Result", pass ? "PASSED" : "FAILED");
		XMLStreamWriterUtil.writeElement(writer, "ElapsedTime", Long.toString(this.elpasedTime));
		XMLStreamWriterUtil.writeElement(writer, "NumberOfTransmissions", Integer.toString(this.numTransmissions));
		writer.writeEndElement();
	}

	@JsonProperty
	public String getTestPointName() {
		return testPointName;
	}

	/**
	 * @return the elpasedTime
	 */
	@JsonProperty
	public long getElpasedTime() {
		return elpasedTime;
	}

	/**
	 * @return the numTransmissions
	 */
	@JsonProperty
	public int getNumTransmissions() {
		return numTransmissions;
	}

}