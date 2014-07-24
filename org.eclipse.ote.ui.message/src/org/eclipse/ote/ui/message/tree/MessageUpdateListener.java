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
package org.eclipse.ote.ui.message.tree;

import java.nio.channels.IllegalSelectorException;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.client.msg.core.AbstractMessageListener;
import org.eclipse.osee.ote.client.msg.core.IMessageSubscription;
import org.eclipse.osee.ote.message.MessageSystemException;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.tool.MessageMode;
import org.eclipse.ote.ui.message.watch.ElementPath;

/**
 * @author Ken J. Aguilar
 */
public class MessageUpdateListener extends AbstractMessageListener {

   private final WatchedMessageNode node;
   private final TreeViewer viewer;

   private final class NodeUpdate implements Runnable {

      private final AbstractTreeNode[] nodes;

      NodeUpdate(AbstractTreeNode[] nodes) {
         this.nodes = nodes;
      }

      @Override
      public void run() {
         for (AbstractTreeNode node : nodes) {
            viewer.refresh(node, true);
         }
      }

   };

   public MessageUpdateListener(TreeViewer viewer, WatchedMessageNode node) {
      super(node.getSubscription());
      this.viewer = viewer;
      this.node = node;
   }

   @Override
   public void onDataAvailable(MessageData data, DataType type) throws MessageSystemException {
      if (type == getSubscription().getMemType()) {
         node.incrementCounter();
      }
   }

   @Override
   public void subscriptionActivated(IMessageSubscription subscription) {
      node.clearUpdateCounter();
      update(node);
      if (subscription.getMessageMode() == MessageMode.WRITER && node.getRequestedValueMap() != null && node.getRequestedValueMap().size() > 0) {
         for (Entry<ElementPath, String> entry : node.getRequestedValueMap().entrySet()) {
            try {
               subscription.setElementValueNoSend(entry.getKey().getElementPath(), entry.getValue());
            } catch (Exception e) {
               OseeLog.log(getClass(), Level.SEVERE, "Could not set element " + entry.getKey().asString(), e);
            }
         }
         try {
            subscription.send();
         } catch (Exception e) {
            OseeLog.log(getClass(), Level.SEVERE, "Could not send " + subscription.getMessageClassName(), e);
         }
      }
      node.setRequestedValueMap(null);
   }

   @Override
   public void subscriptionInvalidated(IMessageSubscription subscription) {
      String reason = subscription.getMessageClassName() + " does not exist";
      LinkedList<AbstractTreeNode> list = new LinkedList<AbstractTreeNode>();
      list.add(node);
      node.collectDescendants(list);
      OseeLog.log(getClass(), Level.WARNING, subscription.getMessageClassName() +" does  not exists", new IllegalSelectorException());
      for (AbstractTreeNode child : list) {
         child.setEnabled(false);
         child.setDisabledReason(reason);
      }
      update(list.toArray(new AbstractTreeNode[list.size()]));

   }

   @Override
   public void subscriptionNotSupported(IMessageSubscription subscription) {
      update(node);
   }

   @Override
   public void subscriptionResolved(IMessageSubscription subscription) {
      super.subscriptionResolved(subscription);
      node.setResolved(true);
      node.setEnabled(true);
      node.setDisabledReason("");
      update(node);
   }

   @Override
   public void subscriptionUnresolved(IMessageSubscription subscription) {
      super.subscriptionUnresolved(subscription);
      node.setResolved(false);
      update(node);
      subscriptionInvalidated(subscription);
   }

   private void update(AbstractTreeNode[] nodes) {
      Displays.ensureInDisplayThread(new NodeUpdate(nodes));
   }

   private void update(AbstractTreeNode node) {
      Displays.ensureInDisplayThread(new NodeUpdate(new AbstractTreeNode[] {node}));
   }

}
