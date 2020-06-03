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

package org.eclipse.ote.ui.mux;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Andrew M. Finkbeiner
 */
public enum OteMuxImage implements KeyedImage {
   MUX("1553.gif");

   private final String fileName;

   private OteMuxImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(MuxToolPlugin.PLUGIN_ID, fileName);
   }

   @Override
   public String getImageKey() {
      return MuxToolPlugin.PLUGIN_ID + ".icons." + fileName;
   }
}
