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
package org.eclipse.ote.ui.eviewer.action;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.ote.ui.eviewer.view.ColumnDetails;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ken J. Aguilar
 */
public class ElementTableLabelProvider implements ITableLabelProvider {

   @Override
   public Image getColumnImage(Object element, int columnIndex) {
      return null;
   }

   @Override
   public String getColumnText(Object element, int columnIndex) {
      if (columnIndex == 0) {
         return ((ColumnDetails) element).getVerboseName();
      }
      return ((ColumnDetails) element).isActive() ? "Active" : "Not Active";
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
   }

   @Override
   public void dispose() {
   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
   }

}
