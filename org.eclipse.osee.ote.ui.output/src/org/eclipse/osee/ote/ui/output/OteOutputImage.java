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
package org.eclipse.osee.ote.ui.output;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public enum OteOutputImage implements KeyedImage {
   ACCEPT("accept.gif"),
   ARROW_DOWN_YELLOW("down.gif"),
   ARROW_UP_YELLOW("up.gif"),
   GREEN_LIGHT("green_light.gif"),
   EDIT("edit.gif"),
   FAIL("red_light.gif"),
   PASS("green_light.gif"),
   RED_LIGHT("red_light.gif"),
   REFRESH_DIRTY("refreshdirty.gif"),
   REFRESH("refresh.gif");

   private final String fileName;

   private OteOutputImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(Activator.PLUGIN_ID, fileName);
   }

   @Override
   public String getImageKey() {
      return Activator.PLUGIN_ID + "." + fileName;
   }
}
