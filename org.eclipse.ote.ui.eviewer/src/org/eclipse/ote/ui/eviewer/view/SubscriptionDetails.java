/*********************************************************************
 * Copyright (c) 2013 Boeing
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

import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.osee.ote.client.msg.core.IMessageSubscription;
import org.eclipse.osee.ote.client.msg.core.ISubscriptionListener;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemException;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.listener.IOSEEMessageListener;

/**
 * @author Ken J. Aguilar
 */
public class SubscriptionDetails implements ISubscriptionListener, IOSEEMessageListener {
   private final CopyOnWriteArrayList<ColumnElement> columnElements = new CopyOnWriteArrayList<ColumnElement>();
   private final IMessageSubscription subscription;
   private Message message;

   private final IUpdateListener listener;

   public SubscriptionDetails(IMessageSubscription subscription, IUpdateListener listener) {
      this.subscription = subscription;
      this.listener = listener;
      subscription.addSubscriptionListener(this);
   }

   public IMessageSubscription getSubscription() {
      return subscription;
   }

   public void addColumn(ColumnElement columnElement) {
      columnElements.add(columnElement);
      subscription.addSubscriptionListener(columnElement);
   }

   public boolean removeColumn(ColumnElement columnElement) {
      if (columnElements.remove(columnElement)) {
         subscription.removeSubscriptionListener(columnElement);
         columnElement.dispose();
      }
      return columnElements.isEmpty();
   }

   public void dispose() {
      subscription.cancel();
      for (ColumnElement column : columnElements) {
         column.dispose();
      }
   }

   @Override
   public void subscriptionActivated(IMessageSubscription subscription) {
   }

   @Override
   public void subscriptionCanceled(IMessageSubscription subscription) {
      if (message != null) {
         message.removeListener(this);
         message = null;
      }
   }

   @Override
   public void subscriptionInvalidated(IMessageSubscription subscription) {
      if (message != null) {
         message.removeListener(this);
         message = null;
      }
   }

   @Override
   public void subscriptionNotSupported(IMessageSubscription subscription) {
   }

   @Override
   public void subscriptionResolved(IMessageSubscription subscription) {
      message = subscription.getMessage();
      message.addListener(this);
   }

   @Override
   public void subscriptionUnresolved(IMessageSubscription subscription) {
      message = null;
   }

   @Override
   public void onDataAvailable(MessageData data, DataType type) throws MessageSystemException {
      if (subscription.getMemType() != type) {
         return;
      }
      boolean changed = false;
      long envTime = 0;
      for (ColumnElement column : columnElements) {
         boolean valueChanged = column.update();
         if (valueChanged) {
            envTime = column.getMessageEnvTime();
         }
         changed |= valueChanged && column.isActive();
      }
      if (changed) {
         listener.update(this, envTime);
      }

   }

   @Override
   public void onInitListener() throws MessageSystemException {
   }
}
