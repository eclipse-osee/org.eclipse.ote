/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ote.endpoint;

import java.net.InetSocketAddress;

import org.eclipse.osee.ote.message.event.OteEventMessage;

public interface OteEndpointSender {

   void send(OteEventMessage sendMessage);

   InetSocketAddress getAddress();

   void stop() throws InterruptedException;

   boolean isClosed();

   void setDebug(boolean debug);

   void start();

}
