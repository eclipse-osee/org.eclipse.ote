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

package org.eclipse.ote.ui.mux.datatable;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ote.ui.mux.msgtable.MessageNode;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ky Komadino
 */
public class DatawordLabelProvider extends LabelProvider implements ITableLabelProvider {
   @Override
   public String getColumnText(Object obj, int index) {
      if (obj != null && obj instanceof RowNode) {
         if (index >= 0 && index <= 7) {
            return String.valueOf(((RowNode) obj).getDataword(index));
         } else {
            return "";
         }
      } else {
         return "";
      }
   }

   @Override
   public Image getColumnImage(Object obj, int index) {
      return getImage(obj);
   }

   @Override
   public String getText(Object obj) {
      return ((MessageNode) obj).getName();
   }

}
