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

package org.eclipse.ote.simple.io.message;

import java.util.Collection;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.elements.FixedPointElement;
import org.eclipse.osee.ote.message.elements.RealElement;
import org.eclipse.osee.ote.message.elements.StringElement;
import org.eclipse.ote.io.GenericOteIoType;
import org.eclipse.ote.io.mux.MuxData;
import org.eclipse.ote.io.mux.MuxMessage;
import org.eclipse.ote.io.mux.MuxReceiveTransmit;


/**
 * Simple Hello World message class intended as an example for use in the simple test environment. 
 * 
 * <table border="1">
 * <p><tr><td>RATE</td><td>4.0</td></tr>
 * <p><tr><td>DEFAULT_BYTE_SIZE</td><td>60</td></tr>
 * </table>
 * 
 * @author Michael P. Masterson
 */
public class SIMPLE_MUX_MSG extends MuxMessage {

   /**
    * The string to print or log when the message is sent
    * 
    *    <table border="1">
    * <p><tr><td>UNIT_ABBR</td><td>null</td></tr>
    * <p><tr><td>START_BYTE</td><td>0</td></tr>
    * <p><tr><td>MSB</td><td>0</td></tr>
    * <p><tr><td>LSB</td><td>511</td></tr>
    * </table>
    */
   public StringElement PRINT_ME;
   public RealElement MUX_SPECIFIC_ELEMENT;


   public SIMPLE_MUX_MSG() {
      super("SIMPLE_MUX_MSG", 60, 0, true, 0, 4.0);
      MuxData messageData = new MuxData(this, this.getClass().getName(), getName(), getDefaultByteSize(), 1, 22, MuxReceiveTransmit.TRANSMIT, 4, GenericOteIoType.MUX);
      setDefaultMessageData(messageData);
      messageData.setScheduled(true);
      int bitSize = 32 * 8 - 1;
      PRINT_ME = new StringElement(this, "PRINT_ME", messageData, 0, 0, bitSize);
      MUX_SPECIFIC_ELEMENT = new FixedPointElement(this, "MUX_SPECIFIC_ELEMENT", messageData, 1.0, false, 33, 0, 10);
      addElements(PRINT_ME, MUX_SPECIFIC_ELEMENT);

	  //set up message stuff
      setMemSource(GenericOteIoType.MUX);

   }
   
   @Override
   public void switchElementAssociation( Collection<? extends Message> messages ) {
      PRINT_ME = PRINT_ME.switchMessages(messages);
      MUX_SPECIFIC_ELEMENT = MUX_SPECIFIC_ELEMENT.switchMessages(messages);
   }
   
}