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

package org.eclipse.osee.ote.core.environment.interfaces;

import java.util.List;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.persistence.XmlizableStream;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The TestPoint interface should be implemented by objects that store pass/fail data.
 * 
 * @author Robert A. Fisher
 */
public interface ITestPoint extends Xmlizable, XmlizableStream {
   @JsonProperty
   public boolean isPass();

   /**
    * @return True only if in batch mode and test point is result of an interactive pass/fail
    */
   default boolean isInteractive() {
      return false;
   }
   
   /**
    * Sets the requirement on the Test Point for coverage.
    */
   default void setRequirements(List<String> requirementIds) {
      //Implement in implemented objects
   }
   
}
