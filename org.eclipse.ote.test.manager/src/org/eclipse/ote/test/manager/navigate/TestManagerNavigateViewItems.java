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
package org.eclipse.ote.test.manager.navigate;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IXNavigateContainer;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.ote.ui.test.manager.TestManagerImage;

/**
 * @author Donald G. Dunne
 */
public class TestManagerNavigateViewItems implements IXNavigateContainer {
   public TestManagerNavigateViewItems() {
      super();
   }

   @Override
   public List<XNavigateItem> getNavigateItems() {
      List<XNavigateItem> items = new ArrayList<>();

      items.add(new XNavigateItemAction(null, new TestManagerAction(), TestManagerImage.TEST_MANAGER, false));

      return items;
   }

}
