/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
