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

package org.eclipse.osee.ote.message.data;

import org.eclipse.osee.ote.message.IMessageHeader;

public class HeaderData extends MessageData {

   public HeaderData(String name, MemoryResource memoryResource) {
      super(name, name, memoryResource, null, null);
   }

   public HeaderData(MemoryResource memoryResource) {
      this("", memoryResource);
   }

   @Override
   public IMessageHeader getMsgHeader() {
      return null;
   }

   @Override
   public void initializeDefaultHeaderValues() {
   }

   @Override
   public void visit(IMessageDataVisitor visitor) {
   }

   @Override
   public void zeroize() {
   }
}
