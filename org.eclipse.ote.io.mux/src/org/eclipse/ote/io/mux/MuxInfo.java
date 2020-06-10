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

package org.eclipse.ote.io.mux;

/**
 * @author Andrew M. Finkbeiner
 * @author Michael P. Masterson
 */
public class MuxInfo {

   public final int channelNumber;
   public final int remoteTerminalNumber;
   public final MuxReceiveTransmit receiveTransmitFlag;
   public final int subaddressNumber;

   public MuxInfo(int channelNumber, int remoteTerminalNumber, MuxReceiveTransmit receiveTransmitFlag, int subaddressNumber){
      this.channelNumber = channelNumber;
      this.remoteTerminalNumber = remoteTerminalNumber;
      this.receiveTransmitFlag = receiveTransmitFlag;
      this.subaddressNumber = subaddressNumber;
   }

}
