/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.ote.ui.message.watch;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.RecordElement;
import org.eclipse.osee.ote.message.elements.RecordMap;
import org.eclipse.ote.ui.message.internal.WatchImages;
import org.eclipse.swt.graphics.Image;

/**
 * @author Andrew M. Finkbeiner
 */
public class ChildElementNodeLabelProvider extends LabelProvider {
   private static final Image recordImg = ImageManager.getImage(WatchImages.DATABASE);
   private static final Image elementImg = ImageManager.getImage(WatchImages.PIPE);

   @Override
   public Image getImage(Object element) {
      if (element instanceof RecordElement) {
         return recordImg;
      } else if (element instanceof Element) {
         return elementImg;
      }
      return null;
   }

   @Override
   public String getText(Object element) {
      if (element instanceof Element) {
         if (element instanceof RecordMap<?>) {
            return ((Element) element).getDescriptiveName();
         } else {
            return String.format("%s: byte=%d, msb=%d, lsb=%d", ((Element) element).getDescriptiveName(),
               ((Element) element).getByteOffset(), ((Element) element).getMsb(), ((Element) element).getLsb());
         }
      }
      return "<UNKNOWN>";
   }
}
