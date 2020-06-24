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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author David N. Phillips
 */
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
   private List<String> sources;
   private List<String> destinations;

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
      sources = new ArrayList<String>();
      destinations = new ArrayList<String>();

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

   public void addSource(String source) {
      if (source != null && !source.isEmpty() && !sources.contains(source)) {
         sources.add(source);
         sorted = false;
      }
   }

   public List<String> getSources() {
      sort();
      return sources;
   }

   public void addDestination(String destination) {
      if (destination != null && !destination.isEmpty() && !destinations.contains(destination)) {
         destinations.add(destination);
         sorted = false;
      }
   }

   public List<String> getDestinations() {
      sort();
      return destinations;
   }

   private void sort() {
      if (!sorted) {
         Collections.sort(elements);
         Collections.sort(sources);
         Collections.sort(destinations);
         sorted = true;
      }
   }

}
