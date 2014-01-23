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
package org.eclipse.ote.ui.message.watch.action;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.nebula.widgets.xviewer.XSubMenuManager;
import org.eclipse.osee.ote.message.tool.MessageMode;
import org.eclipse.ote.ui.message.tree.WatchedMessageNode;
import org.eclipse.ote.ui.message.watch.WatchView;

/**
 * @author Ken J. Aguilar
 */
public class SetMessageModeMenu extends XSubMenuManager implements IMenuListener {
   private final static String NAME = "Set Reader/Writer Buffer";
   private final WatchedMessageNode node;

   private final WatchView watchView;
   
   public static IContributionItem createMenu(WatchView watchView, WatchedMessageNode node) {
      if (node.isEnabled() && node.getSubscription().isResolved()) {
         return new SetMessageModeMenu(watchView, node);
      }
      return new ActionContributionItem(new DisabledAction(NAME));
   }

   protected SetMessageModeMenu(WatchView watchView, WatchedMessageNode node) {
      super(NAME);
      this.watchView = watchView;
      this.node = node;
      setRemoveAllWhenShown(true);
      addMenuListener(this);
   }

   @Override
   public void menuAboutToShow(IMenuManager manager) {
      for (MessageMode mode : MessageMode.values()) {
         add(new SetMessageModeAction(watchView, node, mode));
      }

   }

}
