/*
 * Created on Nov 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
