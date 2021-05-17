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

package org.eclipse.osee.ote.core;

import org.eclipse.osee.ote.core.environment.status.OTEStatusBoard;
import org.eclipse.osee.ote.core.log.ITestPointTally;

/**
 * @author Andrew M. Finkbeiner
 */
public class TestPointTally implements ITestPointTally {
   private int testPointSuccesses;
   private int testPointFailures;
   private int testPointInteractives;

   private final String testName;
   private final OTEStatusBoard statusBoard;

   public TestPointTally(String testName) {
      this.testName = testName;
      statusBoard = ServiceUtility.getService(OTEStatusBoard.class);//tracker.getService();
   }

   @Override
   public void reset() {
      this.testPointSuccesses = 0;
      this.testPointFailures = 0;
      sendUpdate();
   }

   @Override
   public int tallyTestPoint(boolean isPass, boolean isInteractive) {
      if(isPass) {
         testPointSuccesses++;
      } else {
         testPointFailures++;
      }
      
      if(isInteractive) {
         testPointInteractives++;
      }
      sendUpdate();
      return getTestPointTotal();
   }

   private void sendUpdate() {
      statusBoard.onTestPointUpdate(testPointSuccesses, testPointFailures, testPointInteractives, testName);
   }

   @Override
   public int getTestPointTotal() {
      return testPointSuccesses + testPointFailures;
   }
}
