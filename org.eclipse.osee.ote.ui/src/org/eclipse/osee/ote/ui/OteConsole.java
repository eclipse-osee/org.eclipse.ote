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
package org.eclipse.osee.ote.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ote.ui.internal.prefs.OteConsolePreferences;
import org.eclipse.osee.ote.ui.internal.prefs.OteConsolePrefsUtil;
import org.eclipse.ui.console.IOConsole;

/**
 * This class provides a way to set the high and low water marks of the console to limit the output buffer. 
 * @author Michael P. Masterson
 */
public class OteConsole extends IOConsole {
   /**
    * Arbitrary number is arbitrary. 
    */
   private static final int HIGH_TO_LOW_DIFF = 100;
   
   private int limit;
   private boolean noLimit;

   /**
    * @param name
    * @param consoleType
    * @param imageDescriptor
    */
   public OteConsole(String name, ImageDescriptor imageDescriptor) {
      super(name, imageDescriptor);
      limit = OteConsolePrefsUtil.getInt(OteConsolePreferences.BUFFER_LIMIT);
      noLimit = OteConsolePrefsUtil.getBoolean(OteConsolePreferences.NO_BUFFER_LIMIT);
      setWaterMarks();
   }
   
   /**
    * When the limit is too small (less than {@value #HIGH_TO_LOW_DIFF}), the low and high mark will be right next to eachother
    * meaning that characters will be deleted from the beginning at the same speed as they are being written
    * to the end.  Otherwise, every time the limit is hit, the buffer will shrink by {@value #HIGH_TO_LOW_DIFF}.  
    */
   private void setWaterMarks() {
      int lowMark, highMark;
      
      if(noLimit) {
         lowMark = -1;
         highMark = -1;
      } else if( limit > HIGH_TO_LOW_DIFF ){
         lowMark = limit - HIGH_TO_LOW_DIFF;
         highMark = limit;
      } else {
         lowMark = limit -1;
         highMark = limit;
      }
      
      setWaterMarks(lowMark, highMark);
   }
   
   /**
    * @param limit the buffer size limit in bytes
    */
   public void setLimit(int limit) {
      this.limit = limit;
      setWaterMarks();
   }
   
   public void setNoLimit(boolean noLimit) {
      this.noLimit = noLimit;
      setWaterMarks();
   }
   
}
