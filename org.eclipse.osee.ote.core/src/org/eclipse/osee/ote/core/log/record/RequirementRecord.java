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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.log.TestLevel;

/**
 * @author Ryan D. Brooks
 */
public class RequirementRecord extends TestRecord {

   private static final long serialVersionUID = -3533522844275324310L;

   /**
    * RequirementRecord Constructor. Sets up a Requirement log message.
    * 
    * @param source The object requesting the logging.
    * @param msg The log message.
    * @param timeStamp <b>True </b> if a timestamp should be recorded, <b>False </b> if not.
    */
   public RequirementRecord(ITestEnvironmentAccessor source, String msg, boolean timeStamp) {
      super(source, TestLevel.REQUIREMENT, msg, timeStamp);
   }

   /**
    * RequirementRecord Constructor. Sets up a Requirement log message.
    * 
    * @param source The object requesting the logging.
    * @param msg The log message.
    */
   public RequirementRecord(ITestEnvironmentAccessor source, String msg) {
      this(source, msg, true);
   }
   
   @JsonProperty
   public String getRequirement() {
	   return getMessage();
   }
}