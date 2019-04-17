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
package org.eclipse.ote.client.ui.core;

import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IXNavigateContainer;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.ote.ui.OteImage;
import org.eclipse.ote.client.ui.actions.HostSelectionAction;

/**
 * @author Andrew M. Finkbeiner
 */
public class ConnectionNavigatorItem implements IXNavigateContainer {

   @Override
   public List<XNavigateItem> getNavigateItems() {

      XNavigateItem item = new XNavigateItemAction(null, new HostSelectionAction(), OteImage.CONNECTED, false);

      return Collections.singletonList(item);
   }

}
