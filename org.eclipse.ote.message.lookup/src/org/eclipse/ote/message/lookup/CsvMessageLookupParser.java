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

package org.eclipse.ote.message.lookup;

import java.util.Arrays;
import java.util.List;


/**
 * Utility class for adding a message definition to a lookup operator via CSV. 
 * @author Michael P. Masterson
 */
public class CsvMessageLookupParser {

   /**
    * Parses a formatted CSV string for message attributes and adds those to the lookupOperator passed. 
    * 
    * The string must have the following format:<br>
    * &ltMessageClassQualifiedName&gt,&ltMessageName&gt,&ltMessageTypeStr&gt,&ltMessageIdInt&gt,&ltByteSizeInt&gt&gt,&ltPhaseStr&gt&ltRateStr&gt,&ltScheduledBoolean&gt;;&ltCommaSeparatedElementNameList&gt;;&ltCommaSeparatedSenders&gt;;&ltCommaSeparatedReceivers&gt<br>
    * <br>
    * Example entry:
    * <pre>com.application.sample.SampleMessage,SampleMessage,ETHERNET,42,128,0,4.0,true;ELEMENT_1,ELEMENT_2,ELEMENT_N;SOURCE_1;DEST_1,DEST_2</pre>
    * 
    * @param lookupOperator
    * @param providerId
    * @param line
    */
   public static void addDbEntry(MessageLookupOperator lookupOperator, int providerId, String line) {
      String messageClass = null;
      String messageName = null;
      String messageType = null;
      int messageId = 0;
      int byteSize = 0;
      String phase = null;
      String rate = null;
      String scheduled = null;
      List<String> elements = null;
      List<String> publishers = null;
      List<String> subscribers = null;
      String[] tables = line.split(";");
      if(tables.length > 0 && tables[0].length() > 0){
         String[] messageArray = tables[0].split(",");
         for (int index = 0; index < messageArray.length; index++) {
            switch (index) {
            case 0:
               messageClass = messageArray[index];
               break;
            case 1:
               messageName = messageArray[index];
               break;
            case 2:
               messageType = messageArray[index];
               break;
            case 3:
               messageId = Integer.parseInt(messageArray[index]);
               break;
            case 4:
               byteSize = Integer.parseInt(messageArray[index]);
               break;
            case 5:
               phase = messageArray[index];
               break;
            case 6:
               rate = messageArray[index];
               break;
            case 7:
               scheduled = messageArray[index];
               break;
            default:
               //no action
            }
         }
      }
      if(tables.length > 1 && tables[1].length() > 0){
         String[] elementsArray = tables[1].split(",");
         if(elementsArray.length > 0){
            elements = Arrays.asList(elementsArray);
         }
      }
      if(tables.length > 2 && tables[2].length() > 0){
         String[] publishersArray = tables[2].split(",");
         if(publishersArray.length > 0){
            publishers = Arrays.asList(publishersArray);
         }
      }
      if(tables.length > 3 && tables[3].length() > 0){
         String[] subscribersArray = tables[3].split(",");
         if(subscribersArray.length > 0){
            subscribers = Arrays.asList(subscribersArray);
         }
      }
      if(messageClass != null){
         lookupOperator.addToLookup(providerId, messageClass, messageName, messageType, messageId, byteSize, phase, rate, scheduled, elements, publishers, subscribers);
      }
   }

}
