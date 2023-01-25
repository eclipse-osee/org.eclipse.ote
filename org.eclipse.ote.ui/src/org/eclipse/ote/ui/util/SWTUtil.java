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
package org.eclipse.ote.ui.util;

import java.lang.reflect.Method;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.widgets.Table;

public class SWTUtil {
   
   /**
    * Use reflection to increase the height of a table item.  This is especially useful when adding buttons or other controls
    * to a table cell that don't fit properly and get clipped, like a a 'g' on a button.
    * 
    * @param table
    * @param sizeIncrease
    */
   public static void increaseTableItemHeight(Table table, int sizeIncrease){
      table.pack();
      try {
          Method setItemHeightMethod = table.getClass().getDeclaredMethod("setItemHeight", int.class);
          setItemHeightMethod.setAccessible(true);
          setItemHeightMethod.invoke(table, table.getItemHeight()+sizeIncrease);
      }
      catch (Exception e) {
         OseeLog.log(SWTUtil.class, Level.WARNING, e);
      }
   }
}
