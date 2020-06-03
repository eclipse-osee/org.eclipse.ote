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

package org.eclipse.ote.message.lookup;

import java.util.ArrayList;
import java.util.Collections;
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
   private List<String> publishers;
   private List<String> subscribers;

   private boolean sorted;

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
      publishers = new ArrayList<String>();
      subscribers = new ArrayList<String>();

      sorted = true;
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

   public int getMessageId() {
      return id;
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
      if (element != null && !element.isEmpty() && !elements.contains(element)) {
         elements.add(element);
         sorted = false;
      }
   }

   public List<String> getElements() {
      sort();
      return elements;
   }

   public void addPublisher(String publisher) {
      if (publisher != null && !publisher.isEmpty() && !publishers.contains(publisher)) {
         publishers.add(publisher);
         sorted = false;
      }
   }

   public List<String> getPublishers() {
      sort();
      return publishers;
   }

   public void addSubscriber(String subscriber) {
      if (subscriber != null && !subscriber.isEmpty() && !subscribers.contains(subscriber)) {
         subscribers.add(subscriber);
         sorted = false;
      }
   }

   public List<String> getSubscribers() {
      sort();
      return subscribers;
   }

   private void sort() {
      if (!sorted) {
         Collections.sort(elements);
         Collections.sort(publishers);
         Collections.sort(subscribers);
         sorted = true;
      }
   }

}
