/*********************************************************************
 * Copyright (c) 2020 Boeing
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
package org.eclipse.ote.verify;

import java.util.List;
import java.util.Stack;
import java.util.logging.Handler;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.ote.core.MethodFormatter;
import org.eclipse.osee.ote.core.ReturnFormatter;
import org.eclipse.osee.ote.core.TestCase;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.OteInternalApi;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.environment.interfaces.ITestLogger;
import org.eclipse.osee.ote.core.environment.interfaces.ITestPoint;
import org.eclipse.osee.ote.core.log.record.TestPointRecord;
import org.eclipse.osee.ote.core.log.record.TestRecord;

/**
 * @author Michael P. Masterson
 */
public class MockTestLogger implements ITestLogger {
   
   Stack<ITestPoint> points = new Stack<>();
   
   public MockTestLogger() {
   }
   
   /**
    * @return the points
    */
   public List<ITestPoint> getPoints() {
      return points;
   }
   
   public ITestPoint pop() {
      return points.pop();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#addHandler(java.util.logging.Handler)
    */
   @Override
   public void addHandler(Handler handler) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#debug(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, java.lang.String)
    */
   @Override
   public void debug(ITestEnvironmentAccessor source, String message) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#debug(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, java.lang.String, boolean)
    */
   @Override
   public void debug(ITestEnvironmentAccessor source, String message, boolean timeStamp) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#info(java.lang.String)
    */
   @Override
   public void info(String message) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#log(org.eclipse.osee.ote.core.log.record.TestRecord)
    */
   @Override
   public void log(TestRecord record) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#log(java.util.logging.Level, java.lang.String, java.lang.Throwable)
    */
   @Override
   public void log(Level level, String message, Throwable th) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#log(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, java.util.logging.Level, java.lang.String, java.lang.Throwable)
    */
   @Override
   public void log(ITestEnvironmentAccessor source, Level level, String message, Throwable th) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#methodCalled(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor)
    */
   @Override
   public void methodCalled(ITestEnvironmentAccessor source) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#methodCalled(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, org.eclipse.osee.ote.core.MethodFormatter)
    */
   @Override
   public void methodCalled(ITestEnvironmentAccessor source, MethodFormatter methodFormat) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#methodCalledOnObject(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, java.lang.String)
    */
   @Override
   public void methodCalledOnObject(ITestEnvironmentAccessor source, String objectName) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#methodCalledOnObject(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, java.lang.String, org.eclipse.osee.ote.core.MethodFormatter)
    */
   @Override
   public void methodCalledOnObject(ITestEnvironmentAccessor source, String objectName,
         MethodFormatter methodFormat) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#methodCalledOnObject(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, java.lang.String, org.eclipse.osee.ote.core.MethodFormatter, org.eclipse.osee.framework.jdk.core.persistence.Xmlizable)
    */
   @Override
   public void methodCalledOnObject(ITestEnvironmentAccessor source, String objectName,
         MethodFormatter methodFormat, Xmlizable xmlObject) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#methodEnded(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor)
    */
   @Override
   public void methodEnded(ITestEnvironmentAccessor source) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#methodEnded(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, org.eclipse.osee.ote.core.ReturnFormatter)
    */
   @Override
   public void methodEnded(ITestEnvironmentAccessor source, ReturnFormatter returnFormatter) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#removeHandler(java.util.logging.Handler)
    */
   @Override
   public void removeHandler(Handler handler) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#support(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, java.lang.String)
    */
   @Override
   public void support(ITestEnvironmentAccessor source, String message) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#severe(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, java.lang.String)
    */
   @Override
   public void severe(ITestEnvironmentAccessor source, String message) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#severe(java.lang.Object, java.lang.Throwable)
    */
   @Override
   public void severe(Object source, Throwable thrown) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#severe(java.lang.String)
    */
   @Override
   public void severe(String message) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#testCaseBegan(org.eclipse.osee.ote.core.TestCase)
    */
   @Override
   public void testCaseBegan(TestCase testCase) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#testpoint(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, org.eclipse.osee.ote.core.TestScript, org.eclipse.osee.ote.core.TestCase, boolean, java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public void testpoint(ITestEnvironmentAccessor env, TestScript script, TestCase testCase,
         boolean passed, String testPointName, String expected, String actual) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#testpoint(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, org.eclipse.osee.ote.core.TestScript, org.eclipse.osee.ote.core.TestCase, org.eclipse.osee.ote.core.environment.interfaces.ITestPoint)
    */
   @Override
   public void testpoint(ITestEnvironmentAccessor env, TestScript script, TestCase testCase,
         ITestPoint testPoint) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#testpoint(org.eclipse.osee.ote.core.log.record.TestPointRecord)
    */
   @Override
   public void testpoint(TestPointRecord record) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#testpoint(org.eclipse.osee.ote.core.log.comparator.NewOteApi, org.eclipse.osee.ote.core.environment.interfaces.ITestPoint)
    */
   @Override
   public void testpoint(OteInternalApi api, ITestPoint testPoint) {
      this.points.push(testPoint);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#warning(org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor, java.lang.String)
    */
   @Override
   public void warning(ITestEnvironmentAccessor source, String message) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.environment.interfaces.ITestLogger#warning(java.lang.String)
    */
   @Override
   public void warning(String message) {
      // TODO Auto-generated method stub

   }

   @Override
   public void addRequirementCoverage(String... requirementIds) {
      // TODO Auto-generated method stub
   }

   @Override
   public void removeRequirementCoverage(String... requirementIds) {
      // TODO Auto-generated method stub
   }

   @Override
   public void clearRequirementCoverage() {
      // TODO Auto-generated method stub
   }

}
