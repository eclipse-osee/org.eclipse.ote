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

package org.eclipse.osee.ote.client.msg.core;

import org.eclipse.osee.ote.message.MessageSystemException;
import org.eclipse.osee.ote.message.listener.IOSEEMessageListener;

/**
 * @author Ken J. Aguilar
 */
public abstract class AbstractMessageListener implements ISubscriptionListener, IOSEEMessageListener {

   private final IMessageSubscription subscription;

   protected AbstractMessageListener(IMessageSubscription subscription) {
      this.subscription = subscription;
   }

   @Override
   public void subscriptionCanceled(IMessageSubscription subscription) {
      if (subscription.isResolved()) {
         subscription.getMessage().removeListener(this);
      }
   }

   @Override
   public void subscriptionResolved(IMessageSubscription subscription) {
      if (subscription.isResolved()) {
         subscription.getMessage().addListener(this);
      }
   }

   @Override
   public void subscriptionUnresolved(IMessageSubscription subscription) {
   }

   @Override
   public void onInitListener() throws MessageSystemException {
   }

   public IMessageSubscription getSubscription() {
      return subscription;
   }
}
