/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.ote.simple.io.message;

import java.util.Collection;

import org.eclipse.osee.ote.message.elements.StringElement;
import org.eclipse.ote.simple.io.SimpleDataType;
import org.eclipse.ote.simple.io.SimpleMessageData;
import org.eclipse.ote.simple.io.SimpleMessageType;


/**
 * Simple Hello World message class intended as an example for use in the simple test environment. 
 * 
 * <table border="1">
 * <p>
 * <tr>
 * <td>RATE</td>
 * <td>50.0</td>
 * </tr>
 * <p>
 * <tr>
 * <td>DEFAULT_BYTE_SIZE</td>
 * <td>64</td>
 * </tr>
 * </table>
 * 
 * @author Michael P. Masterson
 */
public class HELLO_WORLD extends SimpleMessageType {

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


   public HELLO_WORLD() {
      super("HELLO_WORLD", 64, 0, true, 0, 50.0);
      SimpleMessageData messageData = new SimpleMessageData(this, this.getClass().getName(), getName(), getDefaultByteSize(), SimpleDataType.SIMPLE);
      setDefaultMessageData(messageData);
      messageData.setScheduled(true);
      PRINT_ME = new StringElement(this, "PRINT_ME", messageData, 0, 0, 511);
      addElements(PRINT_ME);

	  //set up message stuff
      setMemSource(SimpleDataType.SIMPLE);

   }
   
   @Override
   public void switchElementAssociation( Collection<SimpleMessageType> messages ) {
      PRINT_ME = PRINT_ME.switchMessages(messages);

   }
   
}