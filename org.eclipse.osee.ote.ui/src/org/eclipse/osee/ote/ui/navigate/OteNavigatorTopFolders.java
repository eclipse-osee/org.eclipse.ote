/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ote.ui.navigate;

import java.util.List;

import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemFolder;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemProvider;

/**
 * @author Michael P. Masterson
 */
public class OteNavigatorTopFolders implements XNavigateItemProvider {

   public static final XNavigateItemFolder CONNECTIONS_FOLDER = new XNavigateItemFolder("Connections",
                                                                                        XNavItemCat.TOP);
   public static final XNavigateItemFolder MESSAGING_FOLDER = new XNavigateItemFolder("Messaging",
                                                                                      XNavItemCat.TOP);

   @Override
   public List<XNavigateItem> getNavigateItems(List<XNavigateItem> items) {
      items.add(CONNECTIONS_FOLDER);
      items.add(MESSAGING_FOLDER);
      return items;
   }

   @Override
   public boolean isApplicable() {
      return true;
   }

}
