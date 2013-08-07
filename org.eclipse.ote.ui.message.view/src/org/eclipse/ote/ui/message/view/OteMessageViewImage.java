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
package org.eclipse.ote.ui.message.view;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

public enum OteMessageViewImage implements KeyedImage {
   GLASSES("glasses.gif"), GEAR("gear.png"),
   PIPE("pipe.png"), EXPAND_STATE("expand_state.gif"), COLLAPSE_STATE("collapse_state.gif");
   
   private final String fileName;

   private OteMessageViewImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(MessageSearchView.PLUGIN_ID, "images", fileName);
   }

   @Override
   public String getImageKey() {
      return MessageSearchView.PLUGIN_ID + ".images." + fileName;
   }
}
