/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.message.lookup;

import java.util.ArrayList;
import java.util.List;

public class MessageLookupResult {

   private final String messageName;
   private String messageClass;
   private String messageType;
   private int id;
   private int byteSize;
   private String phase;
   private String rate;
   private String scheduled;
   private List<String> elements;

   public MessageLookupResult(String messageClass, String messageName, String messageType, int id){
      this(messageClass, messageName, messageType, id, 0, null, null, null);
   }

   public MessageLookupResult(String messageClass, String messageName, String messageType, int id, int byteSize, String phase, String rate, String scheduled){
      this.messageName = messageName;
      this.messageClass = messageClass;
      this.messageType = messageType;
      this.id = id;
      this.byteSize = byteSize;
      this.phase = phase;
      this.rate = rate;
      this.scheduled = scheduled;
      elements = new ArrayList<String>();
   }

   @Override
   public String toString(){
      return String.format("%s name[%s] type[%s] id[%d], byteSize[%d], phase[%s], rate[%s], scheduled[%s]", messageClass, messageName, messageType, id, byteSize, phase, rate, scheduled);
   }

   public String getMessageName() {
      return messageName;
   }

   public String getClassName() {
      return messageClass;
   }

   public String getMessageType(){
      return messageType;
   }

   public int getByteSize() {
      return byteSize;
   }

   public String getPhase() {
      return phase;
   }

   public String getRate() {
      return rate;
   }

   public String getScheduled() {
      return scheduled;
   }

   public void addElement(String element) {
      elements.add(element);
   }

   public List<String> getElements() {
      return elements;
   }

}
