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
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.elements.IntegerElement;
import org.eclipse.osee.ote.message.elements.StringElement;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.ote.io.GenericOteIoType;
import org.eclipse.ote.simple.io.SimpleDataType;
import org.eclipse.ote.simple.io.SimpleMessageData;


/**
 * Simple Hello World message class intended as an example for use in the simple test environment. 
 * 
 * <table border="1">
 * <p><tr><td>RATE</td><td>4.0</td></tr>
 * <p><tr><td>DEFAULT_BYTE_SIZE</td><td>64</td></tr>
 * </table>
 * 
 * @author Michael P. Masterson
 */
public class HELLO_WORLD extends Message {

   private static final boolean IS_SCHEDULED = true;
   private static final double RATE = 4.0;
   private static final int PHASE = 0;
   private static final int OFFSET = 0;
   private static final int BYTE_SIZE = 24;
   
   /**
    * The string to print or log when the message is sent
    * 
    *    <table border="1">
    * <p><tr><td>UNIT_ABBR</td><td>String</td></tr>
    * <p><tr><td>START_BYTE</td><td>0</td></tr>
    * <p><tr><td>LENGTH</td><td>5</td></tr>
    * </table>
    */
   public StringElement PRINT_ME;
   /**
    * The distance to the thing.
    * Only accessible in non-mux type.  
    * <table border="1">
    * <p><tr><td>UNIT_ABBR</td><td>Meters</td></tr>
    * <p><tr><td>START_BYTE</td><td>6</td></tr>
    * <p><tr><td>MSB</td><td>0</td></tr>
    * <p><tr><td>LSB</td><td>32</td></tr>
    */
   public IntegerElement ONLY_IN_SIMPLE;
   


   public HELLO_WORLD() {
      super("HELLO_WORLD", BYTE_SIZE, OFFSET, IS_SCHEDULED, PHASE, RATE);
      SimpleMessageData messageData = new SimpleMessageData(this, this.getClass().getName(), getName(), getDefaultByteSize(), SimpleDataType.SIMPLE);
      setDefaultMessageData(messageData);
      messageData.setScheduled(true);
      int bitSize = 5 * 8 - 1;
      PRINT_ME = new StringElement(this, "PRINT_ME", messageData, 0, 0, bitSize);
      ONLY_IN_SIMPLE = new IntegerElement(this, "ONLY_IN_SIMPLE", messageData, 6, 0, 31);
      
      addElements(PRINT_ME, ONLY_IN_SIMPLE);

	  //set the default mem type
      setMemSource(SimpleDataType.SIMPLE);

   }
   
   @Override
   public void switchElementAssociation( Collection<? extends Message> messages ) {
      PRINT_ME = PRINT_ME.switchMessages(messages);
      ONLY_IN_SIMPLE = ONLY_IN_SIMPLE.switchMessages(messages);

   }

   @SuppressWarnings("unchecked")
   @Override
   public Map<DataType, Class<? extends Message>[]> getAssociatedMessages() {
      Map<DataType, Class<? extends Message>[]> o = new LinkedHashMap<DataType, Class<? extends Message>[]>();
      o.put(GenericOteIoType.MUX, new Class[]{SIMPLE_MUX_MSG.class});
      return o;
   }
   
}