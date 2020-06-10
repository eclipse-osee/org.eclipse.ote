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

package org.eclipse.osee.ote.message.listener;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;
import org.eclipse.osee.ote.message.interfaces.Namespace;

/**
 * @author Andrew M. Finkbeiner
 * @author Michael P. Masterson
 */
public interface IMessageCreationListener<M extends Message> {
   <CLASSTYPE extends M> void onPreCreate(Class<CLASSTYPE> messageClass, IMessageRequestor<M> requestor, boolean writer);

   <CLASSTYPE extends M> void onPostCreate(Class<CLASSTYPE> messageClass, IMessageRequestor< M> requestor, boolean writer, CLASSTYPE message, Namespace namespace);

   <CLASSTYPE extends M> void onInstanceRequest(Class<CLASSTYPE> messageClass, CLASSTYPE message, IMessageRequestor<M> requestor, boolean writer);
}
