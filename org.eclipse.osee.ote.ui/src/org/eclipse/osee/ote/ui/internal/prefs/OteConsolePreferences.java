/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ote.ui.internal.prefs;

/**
 * @author Michael P. Masterson
 */
public enum OteConsolePreferences {
   BUFFER_LIMIT("org.eclipse.osee.ote.ui.bufferLimit", 1000000), 
   NO_BUFFER_LIMIT("org.eclipse.osee.ote.ui.noLimit", false);

   
   private String propKey;
   private Object defaultValue;

   private OteConsolePreferences(String propKey, Object defaultValue) {
      this.propKey = propKey;
      this.defaultValue = defaultValue;
   }
   
   /**
    * @return the defaultValue
    */
   public Object getDefaultValue() {
      return defaultValue;
   }
   
   /**
    * @return the propKey
    */
   public String getPropKey() {
      return propKey;
   }
   
}
