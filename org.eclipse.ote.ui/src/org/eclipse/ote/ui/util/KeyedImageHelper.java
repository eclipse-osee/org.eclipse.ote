/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.ote.ui.util;

import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

/**
 * Put an instance of this in your KeyedImage and reference its overridden methods as pass throughs. Image files should
 * go in <project>/images
 */
public class KeyedImageHelper {

   private final String fileName;
   private final String plugin;

   /**
    * @param plugin The PLUGIN_ID of your activator. NOTE: this should match the actual project pathname.
    * @param fileName The name of the file
    */
   public KeyedImageHelper(String plugin, String fileName) {
      this.fileName = fileName;
      this.plugin = plugin;
   }

   public ImageDescriptor createImageDescriptor() {
      ImageDescriptor imageDesc = null;
      try {
         imageDesc = ImageManager.createImageDescriptor(plugin, fileName);
      } catch (Throwable th) {
         try {
            imageDesc = new ImageDescriptor() {
               @Override
               public ImageData getImageData() {
                  return new ImageData(getPluginFilename());
               }
            };
         } catch (Throwable th2) {
            OseeLog.log(getClass(), Level.SEVERE, th);
         }
      }
      return imageDesc;
   }

   public String getImageKey() {
      return plugin + ".images." + fileName;
   }

   public Image getImage(KeyedImage keyedImage) {
      Image image = null;
      try {
         image = ImageManager.getImage(keyedImage);
      } catch (NullPointerException e) {
         try {
            image = new Image(Display.getDefault(), getPluginFilename());
         } catch (Throwable ignored) {
            throw e;
         }
      }
      return image;
   }

   private String getPluginFilename() {
      return "../" + plugin + "/images/" + fileName;
   }
}
