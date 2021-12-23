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

import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.ote.ui.internal.TestCoreGuiPlugin;

/**
 * @author Andrew M. Finkbeiner
 */
public class OteImage extends OseeImage {
   private static final Long ENUM_ID = 8675309L;
   public static OteImage CHECKOUT = new OteImage("checkout.gif");
   public static OteImage CONNECTED = new OteImage("connected_sm.gif");
   public static OteImage OTE = new OteImage("welcome_item3.gif");

   private OteImage(String fileName) {
      super(fileName);
   }

   @Override
   public String getPluginId() {
      return TestCoreGuiPlugin.PLUGIN_ID;
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

}