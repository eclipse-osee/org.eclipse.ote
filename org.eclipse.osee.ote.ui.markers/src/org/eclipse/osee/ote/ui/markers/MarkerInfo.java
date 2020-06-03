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

package org.eclipse.osee.ote.ui.markers;

public class MarkerInfo {
   private final String file;
   private final int line;
   private final String message;

   public MarkerInfo(String file, int line, String message) {
      this.file = file;
      this.line = line;
      this.message = message;
   }

   /**
    * @return the file
    */
   public String getFile() {
      return file;
   }

   /**
    * @return the line
    */
   public int getLine() {
      return line;
   }

   /**
    * @return the message
    */
   public String getMessage() {
      return message;
   }
}
