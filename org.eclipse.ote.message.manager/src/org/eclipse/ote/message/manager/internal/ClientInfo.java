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

package org.eclipse.ote.message.manager.internal;

import java.net.InetSocketAddress;

/**
 * @author Michael P. Masterson
 */
final class ClientInfo {

      private final InetSocketAddress ipAddress;

      private final int hashcode;

      public ClientInfo(final InetSocketAddress ipAddress) {
         super();
         if (ipAddress == null) {
            throw new IllegalArgumentException("ip address is null");
         }
         this.ipAddress = ipAddress;
         hashcode = 31 * (31 + ipAddress.hashCode());
      }

      public InetSocketAddress getIpAddress() {
         return ipAddress;
      }

      @Override
      public int hashCode() {
         return hashcode;
      }

      @Override
      public boolean equals(Object obj) {
         ClientInfo client = (ClientInfo) obj;
         return ipAddress.equals(client.ipAddress);
      }

   }