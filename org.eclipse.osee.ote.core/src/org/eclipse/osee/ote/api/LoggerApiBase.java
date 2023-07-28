/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ote.api;

import org.eclipse.osee.ote.core.environment.TestEnvironment;

/**
 * This is an Api to provide logging features. 
 * 
 * @author Dominic Leiner
 */
public class LoggerApiBase {
   private TestEnvironment testEnv;
   
   protected LoggerApiBase(TestEnvironment testEnv) {
      this.testEnv = testEnv;
   }
   
   /**
    * After invoking this method any TestPoint logs that occur will have the provided rquirementId(s) added.
    * <br> Use {@link #stopRequirementCoverage()} to stop logging of specific requirementId(s).
    * <br> Use {@link #clearRequirementCoverage()} to stop all logging of requirementId(s).
    * 
    * @param requirementIds Series of Strings representing the requirementIds to add to subsequent test points.
    */
   public void addRequirementCoverage(String... requirementIds) {
      testEnv.getLogger().addRequirementCoverage(requirementIds);
   }
   
   /**
    * Stops the recording of requirement(s) to TestPoint Logs.
    * <br> See: {@link #addRequirementCoverage(String)}.
    * 
    * @param requirementIds Series of Strings representing the requirementIds to stop logging.
    */
   public void removeRequirementCoverage(String... requirementIds) {
      testEnv.getLogger().removeRequirementCoverage(requirementIds);
   }
   
   /**
    * Stops all recording of requirements.
    * <br> See: {@link #addRequirementCoverage(String)}.
    */
   public void clearRequirementCoverage() {
      testEnv.getLogger().clearRequirementCoverage();
   }

}
