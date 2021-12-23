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

package org.eclipse.ote.ui.mux.actions;

import java.util.List;

import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemProvider;
import org.eclipse.osee.ote.ui.navigate.OteNavigatorTopFolders;
import org.eclipse.ote.ui.mux.OteMuxImage;

public class MuxViewNavigatorItem implements XNavigateItemProvider {
   private final XNavItemCat navItemCat = new XNavItemCat(OteNavigatorTopFolders.MESSAGING_FOLDER.getName() + ".1553_mux");


   public MuxViewNavigatorItem() {
      // TODO Auto-generated constructor stub
   }

   @Override
   public List<XNavigateItem> getNavigateItems(List<XNavigateItem> items) {
      XNavigateItem action = new XNavigateItemAction(new OpenMuxViewAction(), OteMuxImage.MUX, navItemCat, XNavItemCat.SUBCAT);
      items.add(action);
      return items;
   }

   @Override
   public boolean isApplicable() {
      return true;
   }

}
