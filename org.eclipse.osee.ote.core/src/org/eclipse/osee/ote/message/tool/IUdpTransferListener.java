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

package org.eclipse.osee.ote.message.tool;

/**
 * @author Ken J. Aguilar
 */
public interface IUdpTransferListener {
   /**
    * called by the file transfer handler when a file transfer is complete
    * 
    * @param config the transfer configuration
    */
   void onTransferComplete(TransferConfig config);

   /**
    * called when the an error is experienced during transfer operations. The handle for this transfer will be
    * automatically stopped prior to this method being called
    */
   void onTransferException(TransferConfig config, Throwable t);
}
