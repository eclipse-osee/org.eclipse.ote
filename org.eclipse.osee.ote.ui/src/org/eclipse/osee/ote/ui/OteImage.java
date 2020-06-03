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

package org.eclipse.osee.ote.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.osee.ote.ui.internal.TestCoreGuiPlugin;

/**
 * @author Andrew M. Finkbeiner
 */
public enum OteImage implements KeyedImage {
   CHECKOUT("checkout.gif"),
   CONNECTED("connected_sm.gif"),
   OTE("welcome_item3.gif");

   private final String fileName;

   private OteImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(TestCoreGuiPlugin.PLUGIN_ID, fileName);
   }

   @Override
   public String getImageKey() {
      return TestCoreGuiPlugin.PLUGIN_ID + ".images." + fileName;
   }
}