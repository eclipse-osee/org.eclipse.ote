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

package org.eclipse.ote.simple.io.message.lookup;

import java.util.List;

import org.eclipse.osee.ote.message.MessageDefinitionProvider;
import org.eclipse.osee.ote.message.MessageSink;
import org.eclipse.ote.message.lookup.MessageLookup;
import org.eclipse.ote.message.lookup.MessageLookupResult;

/**
 * @author Michael P. Masterson
 */
public class SimpleMessageDefinitionProvider implements MessageDefinitionProvider {

   private static final String MAJOR_VERSION = "SIMP_MAJOR";
   private static final String MINOR_VERSION = "SIMP_MIN";
   private static final String SINGLETON_ID = "SIMPLE_DEF_PROVIDER";
   
   private MessageLookup lookup;
   
   public SimpleMessageDefinitionProvider() {
   }
   
   /**
    * @param lookup the lookup to set
    */
   public void setLookup(MessageLookup lookup) {
      this.lookup = lookup;
   }
   
   /**
    * @param lookup unused
    */
   public void unsetLookup(MessageLookup lookup) {
      this.lookup = null;
   }

   @Override
   public String singletonId() {
      return SINGLETON_ID;
   }

   @Override
   public String majorVersion() {
      return MAJOR_VERSION;
   }

   @Override
   public String minorVersion() {
      return MINOR_VERSION;
   }

   public void generateMessageIndex(MessageSink sink) throws Exception {
      List<MessageLookupResult> allMsgs = lookup.lookup("*");
      
      for (MessageLookupResult result : allMsgs) {
         sink.absorbMessage(result.getClassName());
         List<String> elements = result.getElements();
         for (String el : elements) {
            sink.absorbElement(el);
         }
      }
   }

}
