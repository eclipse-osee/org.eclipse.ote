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

package org.eclipse.osee.ote.core.log;

/**
 * @author John Butler
 */
public interface ITestPointTally {

   /**
    * Resets the test point pass and fail counts to zero.
    */
   public void reset();

   /**
    * Records test point result.
    * 
    * @param pass test point result. <b>True</b> for passing. <b>False</b> for failing.
    * @param isInteractive <b>True</b> if test point is result of an interactive prompt
    * @return The total number of test points recorded.
    */
   public int tallyTestPoint(boolean pass, boolean isInteractive);

   /**
    * @return The total number of test points recorded.
    */
   public int getTestPointTotal();
}