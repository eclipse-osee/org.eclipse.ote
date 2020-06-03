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

package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.message.elements.Element;

/**
 * Defines operations for setting and getting the raw bytes that comprise a message header as well as getting the name
 * of the message that the header is attached to
 * 
 * @author Ken J. Aguilar
 */
public interface IMessageHeader {
   public String getMessageName();

   public int getHeaderSize();

   /**
    * Sets the data that backs this header.
    */
   //   public void copyData(byte[] data);
   public byte[] getData();

   public Element[] getElements();

   public void setNewBackingBuffer(byte[] data);

   public String toXml();
}
