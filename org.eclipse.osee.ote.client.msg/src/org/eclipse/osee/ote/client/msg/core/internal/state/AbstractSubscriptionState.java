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

package org.eclipse.osee.ote.client.msg.core.internal.state;

import org.eclipse.osee.ote.client.msg.core.internal.MessageSubscription;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.tool.MessageMode;

/**
 * @author Ken J. Aguilar
 */
public abstract class AbstractSubscriptionState implements ISubscriptionState {

   private final DataType type;
   private final MessageMode mode;
   private final MessageSubscription subscription;

   protected AbstractSubscriptionState(MessageSubscription subscription, DataType type, MessageMode mode) {
      this.subscription = subscription;
      this.type = type;
      this.mode = mode;
   }

   protected AbstractSubscriptionState(AbstractSubscriptionState otherState) {
      this.subscription = otherState.getSubscription();
      this.type = otherState.getMemType();
      this.mode = otherState.getMode();
   }

   @Override
   public DataType getMemType() {
      return type;
   }

   @Override
   public MessageMode getMode() {
      return mode;
   }

   protected MessageSubscription getSubscription() {
      return subscription;
   }

   @Override
   public void onCanceled() {
   }

}
