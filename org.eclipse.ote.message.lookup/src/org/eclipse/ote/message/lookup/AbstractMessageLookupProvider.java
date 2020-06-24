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
import java.util.List;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.elements.Element;

/**
 * @author Michael P. Masterson
 */
public abstract class AbstractMessageLookupProvider implements MessageLookupProvider {

   @Override
   public void removeFromDb(MessageLookupOperator lookupOperator) {
      lookupOperator.removeFromLookup(getUniqueProviderId());
   }

   public int getUniqueProviderId(){
      return hashCode();
   }

   public void add(MessageLookupOperator lookupOperator, Message message) {
      List<String> strElements = new ArrayList<String>(message.getElements().size());
      for(Element el:message.getElements()){
         strElements.add(el.getName());
      }
      lookupOperator.addToLookup(getUniqueProviderId(), message.getClass().getName(), message.getMessageName(), message.getMemType().name(), 0, message.getDefaultByteSize(), Integer.toString(message.getPhase()), String.format("%.2f", message.getRate()), Boolean.toString(message.isScheduledFromStart()), strElements);
   }

   @Override
   public String getDescriptiveProviderName(){
      return "";
   }
}
