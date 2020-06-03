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

package org.eclipse.ote.ui.message.search;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.ote.ui.util.IKeyedImageHelped;
import org.eclipse.ote.ui.util.KeyedImageHelper;
import org.eclipse.swt.graphics.Image;

public enum OteMessageViewImage implements KeyedImage, IKeyedImageHelped {
   GLASSES("glasses.gif"),
   GEAR("gear.png"),
   PIPE("pipe.png"),
   EXPAND_STATE("expand_state.gif"),
   COLLAPSE_STATE("collapse_state.gif");

   private final KeyedImageHelper helper;

   private OteMessageViewImage(String fileName) {
      helper = new KeyedImageHelper(MessageSearchView.PLUGIN_ID, fileName);
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return helper.createImageDescriptor();
   }

   @Override
   public String getImageKey() {
      return helper.getImageKey();
   }

   @Override
   public Image getImage() {
      return helper.getImage(this);
   }
}
