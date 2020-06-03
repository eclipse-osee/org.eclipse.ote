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

package org.eclipse.osee.ote.message.tool.rec.entry;

import java.nio.ByteBuffer;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.elements.CharElement;

/**
 * @author Ken J. Aguilar
 */
public class CharElementEntry implements IElementEntry {

   private final CharElement element;
   private final byte[] nameAsBytes;

   public CharElementEntry(CharElement element) {
      this.element = element;
      nameAsBytes = element.getName().getBytes();
   }

   @Override
   public CharElement getElement() {
      return element;
   }

   @Override
   public void write(ByteBuffer buffer, MemoryResource mem, int limit) {
      mem.setOffset(element.getMsgData().getMem().getOffset());
      buffer.put(nameAsBytes).put(COMMA).put((byte) element.valueOf(mem).charValue()).put(COMMA);
   }

}
