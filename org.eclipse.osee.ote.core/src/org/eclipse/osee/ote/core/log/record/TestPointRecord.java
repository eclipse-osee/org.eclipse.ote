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

package org.eclipse.osee.ote.core.log.record;

import java.util.logging.Level;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.jdk.core.util.xml.XMLStreamWriterUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.TestCase;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.OteInternalApi;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.environment.interfaces.ITestPoint;
import org.eclipse.osee.ote.core.log.TestLevel;
import org.eclipse.osee.ote.core.testPoint.CheckPoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 * @author Robert A. Fisher
 * @author Charles Shaw
 */
public class TestPointRecord extends TestRecord {
   private static final long serialVersionUID = 921875066237859323L;
   private final int number;
   protected ITestPoint testPoint;

   public TestPointRecord(ITestEnvironmentAccessor source, ITestPoint testPoint, boolean timeStamp) {
      this(source, source.getTestScript(), source.getTestScript().getTestCase(), testPoint, timeStamp);
   }

   /**
    * TestPointRecord Constructor. Sets up a test point record of the result of the test point.
    * 
    * @param source The object requesting the logging.
    * @param script
    * @param testCase The test case the test point is in.
    * @param testPoint The TestSubPoint object for the test point.
    * @param timeStamp <b>True </b> if a timestamp should be recorded, <b>False </b> if not.
    */
   public TestPointRecord(ITestEnvironmentAccessor source, TestScript script, TestCase testCase, ITestPoint testPoint, boolean timeStamp) {
      super(source, TestLevel.TEST_POINT, "", timeStamp);
      this.testPoint = testPoint;
      script.__addTestPoint(testPoint.isPass(), testPoint.isInteractive());
      if (testCase != null && testCase.getTestScript() == null) {
         OseeLog.log(TestEnvironment.class, Level.INFO, "test script null");
      }
      this.number = script.__recordTestPoint(testPoint.isPass(), testPoint.isInteractive());
   }

   /**
    * TestPointRecord Constructor. Sets up a test point record of the result of the test point
    * 
    * @param source The object requesting the logging.
    * @param script
    * @param testCase
    * @param testPoint The TestPoint object for the test point.
    */
   public TestPointRecord(ITestEnvironmentAccessor source, TestScript script, TestCase testCase, ITestPoint testPoint) {
      this(source, script, testCase, testPoint, true);
      int point = script.getCurrentPointNumber();

      if (!testPoint.isPass()) {
         if (testPoint instanceof CheckPoint) {
            CheckPoint cp = (CheckPoint) testPoint;
            String exp = cp.getExpected();
            String act = cp.getActual();
            String name = cp.getTestPointName();
            Throwable th = new Throwable();
            StringBuilder stack = new StringBuilder();
            boolean printLine = false;
            for (StackTraceElement element : th.getStackTrace()) {
               if (!printLine && !element.toString().startsWith("org.eclipse.osee")) {
                  printLine = true;
               }
               if (printLine) {
                  stack.append(element.toString());
                  stack.append("\n");
               }
            }
            script.pauseScriptOnFail(point, name, exp, act, stack.toString());
            script.printFailure(point, name, exp, act, stack.toString());
         }
         else {
            script.pauseScriptOnFail(point);
            script.printFailure(point);
         }
      }
   }

   /**
    * TestPointRecord Constructor. Sets up a test point record of the result of the test point.
    * 
    * @param source The object requesting the logging.
    * @param script The test script object
    * @param testCase The test case object
    * @param testPointName The name of the item being tested.
    * @param expected The expected value for the test point.
    * @param actual The actual value for the test point.
    * @param passed <b>True </b> if the test point passed, <b>False </b> if not.
    * @param timeStamp <b>True </b> if a timestamp should be recorded, <b>False </b> if not.
    */
   public TestPointRecord(ITestEnvironmentAccessor source, TestScript script, TestCase testCase, String testPointName, String expected, String actual, boolean passed, boolean timeStamp) {
      this(source, script, testCase, new CheckPoint(testPointName, expected, actual, passed), timeStamp);
   }

   /**
    * TestPointRecord Constructor. Sets up a test point record of the result of the test point.
    * 
    * @param source The object requesting the logging.
    * @param accessor The test case the test point is in.
    * @param testPointName The name of the item being tested.
    * @param expected The expected value for the test point.
    * @param actual The actual value for the test point.
    * @param passed <b>True </b> if the test point passed, <b>False </b> if not.
    */
   public TestPointRecord(ITestEnvironmentAccessor source, TestScript script, TestCase accessor, String testPointName, String expected, String actual, boolean passed) {
      this(source, script, accessor, testPointName, expected, actual, passed, true);
   }

   /**
    * @param api
    * @param testPoint
    */
   public TestPointRecord(OteInternalApi api, ITestPoint testPoint) {
      this(api.testEnv(), api.testAccessor().getTestScript(), api.testAccessor().getTestCase(), testPoint);
   }

   /**
    * Converts element to XML formating.
    * 
    * @return Element XML formated element.
    */
   @Override
   public Element toXml(Document doc) {
      Element tpElement = doc.createElement("TestPoint");
      tpElement.appendChild(Jaxp.createElement(doc, "Number", String.valueOf(number)));
      if (testPoint.isPass()) {
         tpElement.appendChild(Jaxp.createElement(doc, "Result", "PASSED"));
      } else {
         tpElement.appendChild(Jaxp.createElement(doc, "Result", "FAILED"));
      }
      tpElement.appendChild(Jaxp.createElement(doc, "isInteractive", String.valueOf(testPoint.isInteractive())));
      tpElement.appendChild(this.getLocation(doc));
      tpElement.appendChild(testPoint.toXml(doc));

      return tpElement;
   }

   @Override
   public void toXml(XMLStreamWriter writer) throws XMLStreamException {
      writer.writeStartElement("TestPoint");
      XMLStreamWriterUtil.writeElement(writer, "Number", String.valueOf(number));
      if (testPoint.isPass()) {
         XMLStreamWriterUtil.writeElement(writer, "Result", "PASSED");
      } else {
         XMLStreamWriterUtil.writeElement(writer, "Result", "FAILED");
      }
      XMLStreamWriterUtil.writeElement(writer, "isInteractive", String.valueOf(testPoint.isInteractive()));

      writeLocation(writer);
      testPoint.toXml(writer);
      writer.writeEndElement();
   }

   @JsonProperty
   public ITestPoint getTestPoint() {
      return testPoint;
   }

   @JsonProperty
   public int getNumber() {
      return number;
   }
}