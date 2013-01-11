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
package lba.ote.ui.eviewer.view;

import java.util.ArrayList;
import java.util.List;
import lba.ote.ui.eviewer.OteElementImage;
import lba.ote.ui.eviewer.action.EViewerAction;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IXNavigateContainer;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;

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
