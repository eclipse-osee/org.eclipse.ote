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

package org.eclipse.ote.ui.message.watch.action;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.ote.ui.message.internal.WatchImages;
import org.eclipse.swt.graphics.Image;

public final class ElementLabelProvider extends LabelProvider {
   private static final Image Img = ImageManager.getImage(WatchImages.PIPE);

   @Override
   public Image getImage(Object element) {
      return Img;
   }

   @Override
   public String getText(Object element) {
      final Element elem = (Element) element;
      return String.format("%s: byte=%d, msb=%d, lsb=%d", elem.getName(), elem.getByteOffset(), elem.getMsb(),
         elem.getLsb());
   }
}