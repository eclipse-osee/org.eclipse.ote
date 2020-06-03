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

package org.eclipse.osee.ote.messaging.dds;

import java.nio.ByteBuffer;

/**
 * @author Ken J. Aguilar
 */
public interface IOteData extends Data {
   /**
    * signals the end of processing for this data instance. This will return it to the data cache so that it can be
    * reused
    */
   void finish();

   /**
    * gets the data buffer associated with this instance.
    */
   ByteBuffer getDataBuffer();
}
