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
package org.eclipse.osee.ote.core.framework.saxparse.elements;

/**
 * @author Andrew M. Finkbeiner
 */
public class CheckGroupData {

   private final String mode;

   /**
    * @return the mode
    */
   public String getMode() {
      return mode;
   }

   CheckGroupData(String mode) {
      this.mode = mode;
   }
}
