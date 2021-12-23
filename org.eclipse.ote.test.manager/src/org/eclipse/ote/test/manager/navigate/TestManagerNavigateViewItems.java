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

package org.eclipse.ote.test.manager.navigate;

import java.util.List;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemProvider;
import org.eclipse.osee.ote.ui.test.manager.TestManagerImage;

/**
 * @author Donald G. Dunne
 */
public class TestManagerNavigateViewItems implements XNavigateItemProvider {
   public TestManagerNavigateViewItems() {
      super();
   }

   @Override
   public List<XNavigateItem> getNavigateItems(List<XNavigateItem> items) {
      items.add(new XNavigateItemAction(new TestManagerAction(), TestManagerImage.TEST_MANAGER, XNavItemCat.TOP));

      return items;
   }

   @Override
   public boolean isApplicable() {
      return true;
   }

}
