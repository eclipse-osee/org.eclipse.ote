/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
