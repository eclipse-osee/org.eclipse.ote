/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.ote.ui.message.watch.action;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.nebula.widgets.xviewer.XSubMenuManager;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.ote.ui.message.tree.WatchedMessageNode;

/**
 * @author Ken J. Aguilar
 */
public class SetDataSourceMenu extends XSubMenuManager implements IMenuListener {
   private final static String NAME = "Set Data Source";
   private final WatchedMessageNode node;

   public static IContributionItem createMenu(WatchedMessageNode node) {
      if (!node.getSubscription().getAvailableTypes().isEmpty()) {
         return new SetDataSourceMenu(node);
      }
      return new ActionContributionItem(new DisabledAction(NAME));
   }

   protected SetDataSourceMenu(WatchedMessageNode node) {
      super(NAME);
      this.node = node;
      setRemoveAllWhenShown(true);
      setEnabled(!node.getSubscription().getAvailableTypes().isEmpty());
      addMenuListener(this);
   }

   @Override
   public void menuAboutToShow(IMenuManager manager) {
      add(new SetDataSourceAction(node, node.getSubscription().getMessage().getDefaultMessageData().getPhysicalIoType()));
      for (DataType type : node.getSubscription().getAvailableTypes()) {
         add(new SetDataSourceAction(node, type));
      }
   }

}
