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

import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.client.msg.core.db.AbstractMessageDataBase;
import org.eclipse.osee.ote.client.msg.core.db.MessageInstance;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.enums.DataType;

/**
 * @author Ken J. Aguilar
 */
public class ActivateState extends AbstractSubscriptionState {

   private final MessageInstance instance;
   private final AbstractMessageDataBase msgDb;

   public ActivateState(MessageInstance instance, AbstractMessageDataBase msgDb, AbstractSubscriptionState otherState) {
      super(otherState);
      this.instance = instance;
      this.msgDb = msgDb;
   }

   @Override
   public Message getMessage() {
      return instance.getMessage();
   }

   @Override
   public String getMsgClassName() {
      return instance.getMessage().getClass().getName();
   }

   @Override
   public ISubscriptionState onMessageDbClosing(AbstractMessageDataBase msgDb) {
      getSubscription().notifyUnresolved();
      try {
         msgDb.releaseInstance(instance);
      } catch (Exception e) {
         OseeLog.log(ActivateState.class, Level.SEVERE, "problem releasing instance of " + getMsgClassName());
      }
      return new UnresolvedState(getMsgClassName(), this);
   }

   @Override
   public ISubscriptionState onMessageDbFound(AbstractMessageDataBase msgDB) {
      throw new Error("Unexpected input for this state");
   }

   @Override
   public ISubscriptionState onActivated() {
      throw new Error("Unexpected input for this state");
   }

   @Override
   public ISubscriptionState onDeactivated() {
      return new InactiveState(instance, msgDb, this);
   }

   @Override
   public void onCanceled() {
      super.onCanceled();
      try {
         msgDb.releaseInstance(instance);
      } catch (Exception e) {
         OseeLog.log(ActivateState.class, Level.SEVERE, "problem releasing instance of " + getMsgClassName());
      }
   }

   @Override
   public Set<DataType> getAvailableTypes() {
      return instance.getAvailableTypes();
   }

   @Override
   public boolean isActive() {
      return true;
   }

   @Override
   public boolean isResolved() {
      return true;
   }

}
