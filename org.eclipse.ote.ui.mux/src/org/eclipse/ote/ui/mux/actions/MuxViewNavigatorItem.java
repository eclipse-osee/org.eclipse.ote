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

import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IXNavigateContainer;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.ote.ui.mux.OteMuxImage;

public class MuxViewNavigatorItem implements IXNavigateContainer {

   public MuxViewNavigatorItem() {
      // TODO Auto-generated constructor stub
   }

   @Override
   public List<XNavigateItem> getNavigateItems() {
      XNavigateItem action = new XNavigateItemAction(null, new OpenMuxViewAction(), OteMuxImage.MUX, false);
      return Collections.singletonList(action);
   }

}
