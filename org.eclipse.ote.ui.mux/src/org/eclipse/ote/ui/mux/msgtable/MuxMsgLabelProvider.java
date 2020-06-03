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

package org.eclipse.ote.ui.mux.msgtable;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ky Komadino
 */
public class MuxMsgLabelProvider extends LabelProvider implements ITableLabelProvider {
   @Override
   public String getColumnText(Object obj, int index) {
      if (obj != null && obj instanceof MessageNode) {
         switch (index) {
            case 0:
               return ((MessageNode) obj).getName();
            case 1:
               return ((MessageNode) obj).getRtRt();
            case 2:
               return String.valueOf(((MessageNode) obj).getWordCount());
            case 3:
               return ((MessageNode) obj).getStatusWord();
            case 4:
               return ((MessageNode) obj).getEmulation();
            case 5:
               return ((MessageNode) obj).getBus();
            case 6:
               return String.valueOf(((MessageNode) obj).getActivity());
            case 7:
               return String.valueOf(((MessageNode) obj).getErrCount());
            case 8:
               return ((MessageNode) obj).getErrType();
            default:
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
