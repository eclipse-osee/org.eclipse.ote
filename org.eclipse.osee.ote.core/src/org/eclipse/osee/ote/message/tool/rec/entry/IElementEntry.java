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
import org.eclipse.osee.ote.message.elements.Element;

public interface IElementEntry {
   public static final byte COMMA = ',';
   public static final byte EQUALS = '=';
   public static final byte LEFT_PAREN = '(';
   public static final byte RIGHT_PAREN = ')';
   public static final byte TICK = '\'';

   public Element getElement();

   public void write(ByteBuffer buffer, MemoryResource mem, int limit);

}
