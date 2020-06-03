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

import java.util.Collection;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.ote.message.tool.MessageMode;
import org.eclipse.ote.ui.message.tree.MessageNode;
import org.eclipse.ote.ui.message.tree.WatchList;
import org.eclipse.ote.ui.message.tree.WatchedMessageNode;
import org.eclipse.ote.ui.message.watch.WatchView;

/**
 * @author Ken J. Aguilar
 */
public class ConvertWritersToReadersAction extends Action {

   private final WatchView watchView;

   public ConvertWritersToReadersAction(WatchView watchView) {
      this.watchView = watchView;
   }

   @Override
   public void run() {
      WatchList watchList = watchView.getWatchList();
      Collection<MessageNode> messages = watchList.getMessages();
      for(MessageNode node : messages ) {
         WatchedMessageNode watchedNode = (WatchedMessageNode) node;
         watchedNode.getSubscription().changeMessageMode(MessageMode.READER);
      }
      watchView.saveWatchFile();
   }
}
