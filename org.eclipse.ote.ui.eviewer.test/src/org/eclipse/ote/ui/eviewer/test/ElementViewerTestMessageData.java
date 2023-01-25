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
package org.eclipse.ote.ui.eviewer.test;

import org.eclipse.osee.ote.message.IMessageHeader;
import org.eclipse.osee.ote.message.data.IMessageDataVisitor;
import org.eclipse.osee.ote.message.data.MessageData;
public class ElementViewerTestMessageData extends MessageData{

   public ElementViewerTestMessageData(byte[] data, int dataByteSize, int offset) {
      super("EL_VIEWER", dataByteSize, offset, null);

      this.getMem().setData(data);
   }

   @Override
   public IMessageHeader getMsgHeader() {
      return null;
   }

   @Override
   public void visit(IMessageDataVisitor visitor) {
   }

   @Override
   public void initializeDefaultHeaderValues() {
   }
}
