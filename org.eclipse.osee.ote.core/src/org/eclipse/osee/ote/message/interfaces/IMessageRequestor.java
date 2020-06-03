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

import org.eclipse.osee.ote.core.TestException;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;

/**
 * @author Ken J. Aguilar
 * 
 * @param <T> The specific MessageData type
 * @param <U> The specific Message type
 */
public interface IMessageRequestor<T extends MessageData, U extends Message<? extends ITestEnvironmentMessageSystemAccessor, T, U>> {
   <CLASSTYPE extends U> CLASSTYPE getMessageReader(Class<CLASSTYPE> type) throws TestException;
   <CLASSTYPE extends U> CLASSTYPE getMessageWriter(Class<CLASSTYPE> type) throws TestException;
   U getMessageWriter(String msgClass) throws TestException;
   U getMessageReader(String msgClass) throws TestException;
   String getName();
   void remove(U message) throws TestException;
   void dispose();
}
