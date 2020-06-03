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

package org.eclipse.osee.ote.message.interfaces;

import org.eclipse.osee.ote.message.MessageSystemException;

/**
 * @author Andrew M. Finkbeiner
 */
public interface IOSEEMessageWriter {
   void set(byte[] data);

   byte[] get();

   void set(IOSEEMessageWriterListener listener);

   void send() throws MessageSystemException;
}
