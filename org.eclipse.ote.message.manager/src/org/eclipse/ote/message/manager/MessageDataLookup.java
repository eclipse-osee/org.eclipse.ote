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

import java.util.Collection;

import org.eclipse.osee.ote.message.data.MessageData;

/**
 * @author Andrew M. Finkbeiner
 * @author Michael P. Masterson
 */
public interface MessageDataLookup {
   void put(MessageData data);

   MessageData getByName(String Name);
   
   MessageData getById(int id);

   Collection<MessageData> allValues();
}
