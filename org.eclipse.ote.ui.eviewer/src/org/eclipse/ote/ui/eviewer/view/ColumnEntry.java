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