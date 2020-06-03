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

package org.eclipse.ote.ui.eviewer.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.osee.framework.ui.plugin.xnavigate.IXNavigateContainer;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.ote.ui.eviewer.OteElementImage;
import org.eclipse.ote.ui.eviewer.action.EViewerAction;

/**
 * @author Donald G. Dunne
 */
public class MessageNavigateViewItems implements IXNavigateContainer {
   public MessageNavigateViewItems() {
      super();
   }

   @Override
   public List<XNavigateItem> getNavigateItems() {
      List<XNavigateItem> items = new ArrayList<XNavigateItem>();
      items.add(new XNavigateItemAction(null, new EViewerAction(), OteElementImage.ELEMENT_VIEW, false));
      return items;
   }

}
