/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.ote.core.testPoint;

/**
 * Reserved for those test points that originate from an interactive prompt.
 * 
 * @author Michael P. Masterson
 */
public class InteractiveTestPoint extends CheckPoint {

   public InteractiveTestPoint(String testPointName, String expected, String actual, boolean isPass) {
      super(testPointName, expected, actual, isPass);
   }

   @Override
   public boolean isInteractive() {
      return true;
   }

}
