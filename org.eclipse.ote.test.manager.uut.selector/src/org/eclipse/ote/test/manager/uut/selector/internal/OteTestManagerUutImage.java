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
package org.eclipse.ote.test.manager.uut.selector.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * @author David N. Phillips
 * @author Andy Jury
 */
public enum OteTestManagerUutImage implements KeyedImage {
   CHECK_GREEN("pinned_ovr@2x.png"),
   COLLAPSE_ALL("collapseAll.gif"),
   EXPAND_ALL("expandAll.gif"),
   DOT("checkedRadioButton.gif"),
   UUT_HELP("uutHelp.png");

   private final String fileName;
   public static final String PLUGIN_ID = "org.eclipse.ote.test.manager.uut.selector";

   private OteTestManagerUutImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(PLUGIN_ID, fileName);
   }

   @Override
   public String getImageKey() {
      return PLUGIN_ID + ".images." + fileName;
   }

   public String getPath() {
      return "images/" + fileName;
   }

   /**
    * Use two different approaches for loading images to support running standalone tests as well as within the eclipse
    * framework
    */
   public static Image loadImage(OteTestManagerUutImage imageEnum) {
      Image image;
      try {
         image = ImageManager.getImage(imageEnum);
      } catch (Exception e) {
         image = new Image(Display.getDefault(), imageEnum.getPath());
      }
      return image;
   }
}
