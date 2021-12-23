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

package org.eclipse.ote.client.ui.core;

import java.util.List;

import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemProvider;
import org.eclipse.osee.ote.ui.OteImage;
import org.eclipse.osee.ote.ui.navigate.OteNavigatorTopFolders;
import org.eclipse.ote.client.ui.actions.HostSelectionAction;

/**
 * @author Andrew M. Finkbeiner
 */
public class ConnectionNavigatorItem implements XNavigateItemProvider {

   private final XNavItemCat navItemCat = new XNavItemCat(OteNavigatorTopFolders.CONNECTIONS_FOLDER.getName() + ".host");

   @Override
   public List<XNavigateItem> getNavigateItems(List<XNavigateItem> items) {

      HostSelectionAction hostSelectionAction = new HostSelectionAction();
      XNavigateItem item = new XNavigateItemAction(hostSelectionAction, OteImage.CONNECTED,
                                                   navItemCat, XNavItemCat.SUBCAT);
      items.add(item);
      return items;
   }

   @Override
   public boolean isApplicable() {
      return true;
   }

}
