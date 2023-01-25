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
import org.eclipse.osee.ote.core.CopyOnWriteNoIteratorList;

public interface OteUdpEndpoint {

   public void setDebugOutput(boolean enable);

   public InetSocketAddress getLocalEndpoint();

   public OteUdpEndpointSender getOteEndpointSender(InetSocketAddress address);

   OteUdpEndpointInlineSender getOteEndpointInlineSender(InetSocketAddress address);

   public void addBroadcast(OteUdpEndpointSender sender);

   public void removeBroadcast(OteUdpEndpointSender sender);
   
//   public void clearBroadcast();

   public CopyOnWriteNoIteratorList<OteUdpEndpointSender> getBroadcastSenders();

   public void addDataProcessor(EndpointDataProcessor processor);

   public void removeDataProcessor(EndpointDataProcessor processor);

   OteUdpEndpointSender getOteEndpointThreadedSender(InetSocketAddress address);

}
