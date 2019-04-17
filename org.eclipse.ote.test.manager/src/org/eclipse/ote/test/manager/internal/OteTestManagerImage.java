/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.test.manager.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public enum OteTestManagerImage implements KeyedImage {
   CHECK_GREEN_SMALL("history_list@2x.png"),
   COLLAPSE_ALL("collapseAll.gif"),
   EXPAND_ALL("expandAll.gif");

   private final String fileName;

   private OteTestManagerImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(OteTestManagerPlugin.PLUGIN_ID, fileName);
   }

   @Override
   public String getImageKey() {
      return OteTestManagerPlugin.PLUGIN_ID + ".images." + fileName;
   }

   public String getPath() {
      return "images/" + fileName;
   }

   /**
    * Use two different approaches for loading images to support running standalone tests as well as within the eclipse
    * framework
    */
   public static Image loadImage(OteTestManagerImage imageEnum) {
      Image image;
      try {
         image = ImageManager.getImage(imageEnum);
      } catch (Exception e) {
         image = new Image(Display.getDefault(), imageEnum.getPath());
      }
      return image;
   }
}
