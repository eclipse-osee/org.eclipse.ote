/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.ui.message.view.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.osee.framework.ui.plugin.xnavigate.IXNavigateContainer;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.ote.ui.message.view.OteMessageViewImage;

public class MessageNavigateViewItems implements IXNavigateContainer {

   @Override
   public List<XNavigateItem> getNavigateItems() {
      List<XNavigateItem> items = new ArrayList<XNavigateItem>();
      items.add(new XNavigateItemAction(null, new MessageViewAction(), OteMessageViewImage.GLASSES, false));
      return items;
   }

}
