/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.ote.ui.eviewer.view;

/**
 * @author Ken J. Aguilar
 */
public class ColumnDetails {

   private final ViewerColumn column;
   private boolean active;

   public ColumnDetails(ViewerColumn column) {
      this.column = column;
      this.active = column.isActive();
   }

   /**
    * @return the active
    */
   public boolean isActive() {
      return active;
   }

   /**
    * @param active the active to set
    */
   void setActive(boolean active) {
      this.active = active;
   }

   void apply() {
      column.setActive(active);
   }

   /**
    * @return the name
    */
   public String getName() {
      return column.getName();
   }

   public String getVerboseName() {
      return column.getVerboseName();
   }
}
