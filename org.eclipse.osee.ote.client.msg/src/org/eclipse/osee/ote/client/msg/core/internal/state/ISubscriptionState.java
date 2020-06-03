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
import org.eclipse.osee.ote.client.msg.core.db.AbstractMessageDataBase;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.tool.MessageMode;

/**
 * @author Ken J. Aguilar
 */
public interface ISubscriptionState {
   DataType getMemType();

   MessageMode getMode();

   String getMsgClassName();

   Message getMessage();

   Set<DataType> getAvailableTypes();

   ISubscriptionState onMessageDbFound(AbstractMessageDataBase msgDB);

   ISubscriptionState onMessageDbClosing(AbstractMessageDataBase msgDb);

   ISubscriptionState onActivated();

   ISubscriptionState onDeactivated();

   void onCanceled();

   boolean isActive();

   boolean isResolved();

}
