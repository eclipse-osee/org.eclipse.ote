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

package org.eclipse.osee.ote.message.tool.rec;

import java.nio.ByteBuffer;

/**
 * @author Ken J. Aguilar
 */
public class RecUtil {
   private static final int ASCII_A_ADDITIVE = 'A' - 10;
   private static final int ASCII_0_ADDITIVE = '0';

   /**
    * a very fast way of converting a byte into a two digit, zero padded hex value that is written directly into a byte
    * buffer
    */
   public static void byteToAsciiHex(byte byteValue, ByteBuffer buffer) {
      int value = byteValue & 0xFF;
      int code = value >>> 4;
      code += code >= 10 ? ASCII_A_ADDITIVE : ASCII_0_ADDITIVE;
      buffer.put((byte) code);
      code = value & 0x0F;
      code += code >= 10 ? ASCII_A_ADDITIVE : ASCII_0_ADDITIVE;
      buffer.put((byte) code);
   }
}
