package org.eclipse.ote.ui.eviewer.view;

import org.eclipse.osee.ote.message.ElementPath;

public final class ColumnEntry {
   private final ElementPath path;
   private final boolean isActive;
   
   public ColumnEntry(ElementPath path, boolean isActive) {
      super();
      this.path = path;
      this.isActive = isActive;
   }

   public ElementPath getPath() {
      return path;
   }

   public boolean isActive() {
      return isActive;
   }
   
}