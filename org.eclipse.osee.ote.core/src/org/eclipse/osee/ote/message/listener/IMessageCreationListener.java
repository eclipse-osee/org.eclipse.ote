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
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;
import org.eclipse.osee.ote.message.interfaces.ITestEnvironmentMessageSystemAccessor;
import org.eclipse.osee.ote.message.interfaces.Namespace;

/**
 * @author Andrew M. Finkbeiner
 * @author Michael P. Masterson
 */
public interface IMessageCreationListener<D extends MessageData, M extends Message<? extends ITestEnvironmentMessageSystemAccessor, D, M>> {
   <CLASSTYPE extends M> void onPreCreate(Class<CLASSTYPE> messageClass, IMessageRequestor<D, M> requestor, boolean writer);

   <CLASSTYPE extends M> void onPostCreate(Class<CLASSTYPE> messageClass, IMessageRequestor<D, M> requestor, boolean writer, CLASSTYPE message, Namespace namespace);

   <CLASSTYPE extends M> void onInstanceRequest(Class<CLASSTYPE> messageClass, CLASSTYPE message, IMessageRequestor<D, M> requestor, boolean writer);
}
