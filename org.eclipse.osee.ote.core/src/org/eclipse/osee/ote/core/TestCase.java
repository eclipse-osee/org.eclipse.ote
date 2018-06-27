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
package org.eclipse.osee.ote.core;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.jackson.annotate.JsonProperty;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.persistence.XmlizableStream;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.jdk.core.util.xml.XMLStreamWriterUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.enums.PromptResponseType;
import org.eclipse.osee.ote.core.environment.EnvironmentTask;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer;
import org.eclipse.osee.ote.core.environment.interfaces.IExecutionUnitManagement;
import org.eclipse.osee.ote.core.environment.interfaces.IScriptControl;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.environment.interfaces.ITestLogger;
import org.eclipse.osee.ote.core.environment.interfaces.ITestStation;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;
import org.eclipse.osee.ote.core.environment.interfaces.ITimerControl;
import org.eclipse.osee.ote.core.log.record.RequirementRecord;
import org.eclipse.osee.ote.core.log.record.TestCaseRecord;
import org.eclipse.osee.ote.core.log.record.TestDescriptionRecord;
import org.eclipse.osee.ote.core.log.record.TestRecord;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * TestCase is the abstract base class for all test cases. This class provides the interfaces necessary for a TestCase
 * to be part of a {@link org.eclipse.osee.ote.core.TestScript TestScript}.
 * <p>
 * The TestCase class contains the following information:
 * <ul>
 * <li><b>Standalone</b> - This is used to determine if a TestCase can be used in a selective run list.
 * <ul>
 * <li><b>true</b> - The TestCase runs as expected regardless of order.
 * <li><b>false</b> - The TestCase must be run in a given order with other test cases to work as expected.
 * </ul>
 * <li><b>Test Case Number</b> - A serial number automatically assigned at the time of construction.
 * <li><b>Tracability</b> - The requirements that are tested by this TestCase.
 * </ul>
 * <p>
 * When building a new TestCase object it is important to remember that the object contains information describing the
 * test case in addition to the run time code. It is also important to understand that the when writing classes which
 * within a TestScript that the object must be instantiated (typically done in the constructor) with a reference to the
 * TestScript it is within or else it will not be executed.
 * <p>
 * The following sample should be followed closely when building TestCase objects. <br>
 * <br>
 * <code>
 * public class OipCase extends TestCase {
 * 		<ul style="list-style: none">
 * <li>		public OipCase(TestScript parent) {
 * 			<ul style="list-style: none">
 * <li>			</code><i>Use <b>one</b> of the following constructors based on if this TestCase is standalone</i><code>
 * <li>			super(parent);</code> <i>Standalone defaulted to <b>false</b></i><code>
 * <li>			super(parent, true);</code><i>Standalone explicitly set to <b>true</b></i><code>
 * <li>
 * <li>			</code><i>All requirements tested in the test case should be noted here with the </i>
 * <code>{@link org.eclipse.osee.ote.core.TestCase#addTracability(String) addTracability}</code><i> method.</i> <code>
 * 			</ul>
 * <li>		}
 * <li>
 * <li>		</code><i><b>Note:</b>It is very important that </i><code>doTestCase</code><i> always has the </i>
 * <code>throws InterrupedException</code><i> in the method declaration. The ability to abort a script relies on this
 * statement, and without it many calls would have to be wrapped with a try/catch block.</i><code>
 * <li>		public void doTestCase(ITestEnvironmentAccessor environment, ITestLogger logger) throws InterruptedException {
 * 			<ul style="list-style: none">
 * <li>			</code><i>Place all of the runtime code for the test case here.</i><code>
 * 			</ul>
 * <li>		}
 * 		</ul>
 * }
 * </code>
 * 
 * @see org.eclipse.osee.ote.core.TestScript
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 */
public abstract class TestCase implements ITestEnvironmentAccessor, Xmlizable, XmlizableStream {
   protected ITestLogger logger;
   private final ITestEnvironmentAccessor environment;
   private final boolean standAlone;
   private final WeakReference<TestScript> testScript;
   private final TestDescriptionRecord testDescription;
   @JsonProperty
   public int number;
   protected List<RequirementRecord> traceability = new ArrayList<>();

   /**
    * TestCase Constructor.
    */
   public TestCase(TestScript testScript) {
      this(testScript, false);
   }

   /**
    * TestCase Constructor.
    */
   public TestCase(TestScript testScript, boolean standAlone) {
      this(testScript, standAlone, true);
   }

   /**
    * TestCase Constructor.
    */
   protected TestCase(TestScript testScript, boolean standAlone, boolean addToRunList) {
      super();
      this.testDescription = new TestDescriptionRecord(testScript.getTestEnvironment());
      this.standAlone = standAlone;
      if (addToRunList) {
         this.number = testScript.addTestCase(this);
      }
      this.testScript = new WeakReference<>(testScript);
      this.environment = testScript.getTestEnvironment();
      GCHelper.getGCHelper().addRefWatch(this);
   }

   public TestCase(ITestEnvironmentAccessor accessor) {
      this.testDescription = new TestDescriptionRecord(accessor);

      // TODO we have two different traceability tags here.... we need to combine these or get rid
      // of them all together since define and the artifact framework specifies traceability
      // this.tracability = new ArrayList();
      this.traceability = new ArrayList<>();

      this.standAlone = false;
      ;
      this.number = 1;
      this.testScript = null;
      this.environment = accessor;

   }

   /**
    * Called by baseDoTestCase(). This is implemented by the tester's in each test case in the test script.
    * 
    * @param environment The Test environment.
    */
   public abstract void doTestCase(ITestEnvironmentAccessor environment, ITestLogger logger) throws InterruptedException;

   public Element getTastCaseNumberXml(Document doc) {
      return Jaxp.createElement(doc, "Number", String.valueOf(number));
   }

   public void writeTestCaseNumber(XMLStreamWriter writer) throws XMLStreamException {
      XMLStreamWriterUtil.writeElement(writer, "Number", String.valueOf(number));
   }

   public void writeTestCaseClassName(XMLStreamWriter writer) throws XMLStreamException {
      String name = this.getClass().getName();
      if (name == null || name.length() == 0) {
         name = "";
      }
      XMLStreamWriterUtil.writeElement(writer, "Name", name);
   }
   
   @JsonProperty
   public String getName() {
       return this.getClass().getName();
   }

   public void writeTracability(XMLStreamWriter writer) throws XMLStreamException {
      writer.writeStartElement("Tracability");
      for (RequirementRecord record : traceability) {
         record.toXml(writer);
      }
      writer.writeEndElement();
   }

   public Element getTestCaseClassName(Document doc) {
      String name = this.getClass().getName();
      if (name == null || name.length() == 0) {
         name = "";
      }
      return Jaxp.createElement(doc, "Name", name);
   }
   
   public String getTestCaseClassName() {
	   return this.getClass().getName();
   }

   public TestCase getTestCase() {
      return this;
   }

   /**
    * @return Returns the testCaseNumber.
    */
   public int getTestCaseNumber() {
      return number;
   }

   public ITestEnvironmentAccessor getTestEnvironment() {
      return environment;
   }

   public TestRecord getTestRecord() {
      return new TestCaseRecord(getTestEnvironment(), this);
   }

   /**
    * @return Returns the testScript.
    */
   @Override
   public TestScript getTestScript() {
      return testScript.get();
   }

   public Element getTracabilityXml(Document doc) {
      Element traceElement = doc.createElement("Tracability");
      for (RequirementRecord record : traceability) {
         traceElement.appendChild(record.toXml(doc));

      }
      return traceElement;
   }

   /**
    * @return Returns the standAlone.
    */
   public boolean isStandAlone() {
      return standAlone;
   }

   public void prompt() throws InterruptedException {
      getTestScript().prompt();
   }

   public void prompt(String message) throws InterruptedException {
      getTestScript().prompt(new TestPrompt(message, PromptResponseType.NONE));
   }

   public void promptPassFail(String message) throws InterruptedException {
      getTestScript().getLogger().methodCalled(getTestScript().getTestEnvironment(), new MethodFormatter().add(message));

      getTestScript().promptPassFail(message);

      getTestScript().getLogger().methodEnded(getTestScript().getTestEnvironment());
   }

   public void promptPause(String message) throws InterruptedException {
      getTestScript().promptPause(message);
   }

   public void promptStep(String message) throws InterruptedException {
      getTestScript().prompt(new TestPrompt(message, PromptResponseType.SCRIPT_STEP));
   }

   public String promptInput(String message) throws InterruptedException {
      return getTestScript().prompt(new TestPrompt(message, PromptResponseType.USER_INPUT));
   }

   /**
    * Logs the results of a test point.
    * 
    * @param passed boolean T/F
    * @param expected The expected information
    * @param actual The actual information
    */
   public void testpoint(boolean passed, String testPointName, String expected, String actual) {
      logger.testpoint(this.getTestEnvironment(), this.getTestScript(), this, passed, testPointName, expected, actual);
   }

   @Override
   public String toString() {
      String description = getTestScript().getClass().getName() + "Test Case " + number + ":";
      if (traceability != null) {
         for (RequirementRecord record : traceability) {
            description += "\n\t" + record;
         }
      }
      return description;
   }

   @Override
   public Element toXml(Document doc) {
      Element testCaseElement = doc.createElement("TestCase");
      testCaseElement.appendChild(getTastCaseNumberXml(doc));
      testCaseElement.appendChild(getTestCaseClassName(doc));
      testCaseElement.appendChild(getTracabilityXml(doc));
      return testCaseElement;
   }

   @Override
   public void toXml(XMLStreamWriter writer) throws XMLStreamException {
      writer.writeStartElement("TestCase");
      writeTestCaseNumber(writer);
      writeTestCaseClassName(writer);
      writeTracability(writer);
   }

   /**
    * Starts running the test case. Calls doTestCase(), which is implemented by the tester in each test case.
    * 
    * @param environment The Test Enviornment.
    */
   public void baseDoTestCase(ITestEnvironmentAccessor environment) throws InterruptedException {
      this.logger = environment.getLogger();

      logger.testCaseBegan(this); // This is required for valid outfile.
      //This creates the test case outfile logging.

      environment.getTestScript().setTestCase(this);
      OseeLog.logf(TestEnvironment.class, OteLevel.TEST_EVENT, "Starting Test Case %s.%s",
         this.getTestScript().getClass().getSimpleName(), this.getClass().getSimpleName());
      doTestCase(environment, environment.getLogger());
   }

   @Override
   public void abortTestScript() {
      environment.abortTestScript();
   }

   @Override
   public boolean addTask(EnvironmentTask task) {
      return environment.addTask(task);
   }

   @Override
   public void associateObject(Class<?> c, Object obj) {
      environment.associateObject(c, obj);
   }

   @Override
   public Object getAssociatedObject(Class<?> c) {
      return environment.getAssociatedObject(c);
   }

   @Override
   public Set<Class<?>> getAssociatedObjects() {
      return environment.getAssociatedObjects();
   }

   /*
    * public ITestEnvironmentCommandCallback getClientCallback() { return environment.getClientCallback(); }
    */

   // public EnvironmentType getEnvironmentType() {
   // return environment.getEnvironmentType();
   // }
   @Override
   public long getEnvTime() {
      return environment.getEnvTime();
   }

   @Override
   public IExecutionUnitManagement getExecutionUnitManagement() {
      return environment.getExecutionUnitManagement();
   }

   @Override
   public ITestLogger getLogger() {
      return environment.getLogger();
   }

   @Override
   public IScriptControl getScriptCtrl() {
      return environment.getScriptCtrl();
   }

   /*
    * public StatusBoard getStatusBoard() { return environment.getStatusBoard(); }
    */

   @Override
   public ITestStation getTestStation() {
      return environment.getTestStation();
   }

   @Override
   public ITimerControl getTimerCtrl() {
      return environment.getTimerCtrl();
   }

   @Override
   public void onScriptComplete() throws InterruptedException {
      environment.onScriptComplete();
   }

   @Override
   public void onScriptSetup() {
      environment.onScriptSetup();
   }

   @Override
   public ICancelTimer setTimerFor(ITimeout listener, int time) {
      return environment.setTimerFor(listener, time);
   }

   public void logTestPoint(boolean isPassed, String testPointName, String expected, String actual) {
      this.getLogger().testpoint(this.getTestEnvironment(), this.getTestScript(), this, isPassed, testPointName,
         expected, actual);
   }

   public void setPurpose(String purpose) {
      testDescription.setPurpose(purpose);
   }

   public void setPreCondition(String preCondition) {
      testDescription.setPreCondition(preCondition);
   }

   public void setPostCondition(String postCondition) {
      testDescription.setPostCondition(postCondition);
   }

   public List<RequirementRecord> getScriptReqRecordTraceability() {
      return traceability;
   }

   public void addTraceability(String description) {
      this.traceability.add(new RequirementRecord(this.getTestEnvironment(), description));
   }

   public void writeToConsole(String message) {
      TestPrompt p = new TestPrompt(message);

      try {
         testScript.get().prompt(p);
      } catch (Throwable e) {
         OseeLog.log(TestEnvironment.class, Level.INFO, e.getMessage(), e);
      }
   }

   public void setTestScript(TestScript script) {
      throw new IllegalStateException("Why are you calling this one?!?!?!?");
   }

   @Override
   public void abortTestScript(Throwable t) {
      testScript.get().abortDueToThrowable(t);
   }
   
   @JsonProperty
   public List<RequirementRecord> getTraceability() {
       if (traceability.isEmpty()) {
           return null;
       } else {
           return traceability;
       }
   }
}