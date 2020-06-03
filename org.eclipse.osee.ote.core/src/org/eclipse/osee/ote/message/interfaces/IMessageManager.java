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

package org.eclipse.osee.ote.message.interfaces;

import java.util.Collection;
import java.util.Set;

import org.eclipse.osee.ote.core.TestException;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.listener.DDSDomainParticipantListener;
import org.eclipse.osee.ote.message.listener.IMessageCreationListener;

/**
 * @author Andrew M. Finkbeiner
 */
public interface IMessageManager<T extends MessageData, U extends Message<? extends ITestEnvironmentMessageSystemAccessor, T, U>> {
   void destroy();

   <CLASSTYPE extends U> CLASSTYPE createMessage(Class<CLASSTYPE> messageClass) throws TestException;

   <CLASSTYPE extends U> int getReferenceCount(CLASSTYPE classtype);

   <CLASSTYPE extends U> CLASSTYPE findInstance(Class<CLASSTYPE> clazz, boolean writer);

   Collection<U> getAllMessages();

   Collection<U> getAllReaders();

   Collection<U> getAllWriters();

   Collection<U> getAllReaders(DataType type);

   Collection<U> getAllWriters(DataType type);

   void init() throws Exception;

   boolean isPhysicalTypeAvailable(DataType physicalType);

   IMessageRequestor<T, U> createMessageRequestor(String name);

   Class<? extends U> getMessageClass(String msgClass) throws ClassCastException, ClassNotFoundException;

   DDSDomainParticipantListener getDDSListener(); 
   void addPostCreateMessageListener(IMessageCreationListener listener);

   void addPreCreateMessageListener(IMessageCreationListener listener);

   void addInstanceRequestListener(IMessageCreationListener listener);

   <CLASSTYPE extends U> CLASSTYPE createAndSetUpMessage(Class<CLASSTYPE> messageClass, IMessageRequestor<T, U> requestor,
         boolean writer) throws TestException;

   Set<DataType> getAvailableDataTypes();

   boolean removeRequestorReference(IMessageRequestor<T, U> requestor, U msg);

   <CLASSTYPE extends U> CLASSTYPE getMessageWriter(IMessageRequestor<T, U> messageRequestor, Class<CLASSTYPE> type);
   <CLASSTYPE extends U> CLASSTYPE getMessageReader(IMessageRequestor<T, U> messageRequestor, Class<CLASSTYPE> type);


}
