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

package org.eclipse.osee.ote.core.framework.saxparse.elements;

/**
 * @author Andrew M. Finkbeiner
 */
public class OteLogData {

   private final String level;
   private final String logger;

   OteLogData(String level, String logger) {
      this.level = level;
      this.logger = logger;
   }

   /**
    * @return the level
    */
   public String getLevel() {
      return level;
   }

   /**
    * @return the logger
    */
   public String getLogger() {
      return logger;
   }
}
