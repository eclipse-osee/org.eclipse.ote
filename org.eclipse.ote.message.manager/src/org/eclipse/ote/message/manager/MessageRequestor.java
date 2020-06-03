/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.ote.message.manager;

import java.util.HashSet;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.TestException;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;
import org.eclipse.osee.ote.message.interfaces.ITestEnvironmentMessageSystemAccessor;

/**
 * @author Ken J. Aguilar
 * @author Michael P. Masterson
 */
public class MessageRequestor<D extends MessageData, M extends Message<? extends ITestEnvironmentMessageSystemAccessor, D, M>> implements IMessageRequestor<D,M> {

   private final IMessageManager<D, M> messageManager;
   private final HashSet<M> messagesToDecrementReferenceCount = new HashSet<M>();
   private final String name;

   MessageRequestor(String name, IMessageManager<D, M> messageManager) {
      this.name = name;
      this.messageManager = messageManager;
   }

   @Override
   public synchronized void dispose() {
      for (M msg : messagesToDecrementReferenceCount) {
         try {
            messageManager.removeRequestorReference(this, msg);
         } catch (IllegalStateException ex){//we don't care if the message manager is disposed, it means we're shutting down
         } catch (Exception e) {
            OseeLog.log(MessageRequestor.class, Level.SEVERE, "exception while removing requestor reference for " +  msg.getName(), e);
         }
      }
      messagesToDecrementReferenceCount.clear();
   }

   public String toString()
   {
      return name;
   }

   @Override
   public M getMessageWriter(String msgClass) throws TestException {
      M msg = null;
      try {
         msg = getMessageWriter(messageManager.getMessageClass(msgClass));
      } catch (ClassCastException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      } catch (ClassNotFoundException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      }
      return msg;
   }
   
   @Override
   public synchronized <CLASSTYPE extends M> CLASSTYPE getMessageWriter(Class<CLASSTYPE> type) throws TestException {
      CLASSTYPE msg = messageManager.getMessageWriter(this, type);
      messagesToDecrementReferenceCount.add(msg);
      return msg;
   }

   @Override
   public M getMessageReader(String msgClass) throws TestException {
      M msg = null;
      try {
         msg = getMessageReader(messageManager.getMessageClass(msgClass));
      } catch (ClassCastException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      } catch (ClassNotFoundException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      }
      return msg;
   }

   @Override
   public synchronized <CLASSTYPE extends M> CLASSTYPE getMessageReader(Class<CLASSTYPE> type) throws TestException {
      CLASSTYPE msg = messageManager.getMessageReader(this, type);
      messagesToDecrementReferenceCount.add(msg);
      return msg;
   }

   @Override
   public synchronized void remove(M message) throws TestException {
      if( messagesToDecrementReferenceCount.contains(message)){
         messageManager.removeRequestorReference(this, message);
         messagesToDecrementReferenceCount.remove(message);
      }
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

}
