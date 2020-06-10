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
 * 
 * @author Michael P. Masterson
 */
public enum MuxReceiveTransmit {
   RECEIVE(0),
   TRANSMIT(1);

   private int value;

   private MuxReceiveTransmit(int value) {
      this.value = value;
   }

   public int getValue() {
      return value;
   }
}
