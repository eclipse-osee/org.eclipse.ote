/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.ui.eviewer.view;

/**
 * @author Ken J. Aguilar
 */
public class ColumnDetails {

   private final ElementColumn column;
   private boolean active;

   public ColumnDetails(ElementColumn column) {
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
