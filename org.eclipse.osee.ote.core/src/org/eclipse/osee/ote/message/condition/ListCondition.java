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

package org.eclipse.osee.ote.message.condition;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.ote.message.elements.DiscreteElement;

public class ListCondition<T extends Comparable<T>> extends AbstractCondition implements IDiscreteElementCondition<T> {
   private final DiscreteElement<T> element;
   private final HashSet<T> set;
   private final boolean inList;
   private T lastValue;

   public ListCondition(DiscreteElement<T> element, boolean inList, T... list) {
      this.element = element;
      this.inList = inList;
      for (int i = 0; i < list.length; i++) {
         list[i] = element.elementMask(list[i]);
      }
      this.set = new HashSet<>(Arrays.asList(list));

   }

   public ListCondition(DiscreteElement<T> element, boolean inList, Collection<T> list) {
      this.element = element;
      this.inList = inList;
      this.set = new HashSet<>();
      for (T item : list) {
         set.add(element.elementMask(item));
      }

   }

   @Override
   public T getLastCheckValue() {
      return lastValue;
   }

   @Override
   public boolean check() {
      lastValue = element.getValue();
      return !(inList ^ set.contains(lastValue));
   }

   public DiscreteElement<T> getElement() {
      return element;
   }

   public Collection<T> getSet() {
      return set;
   }

   public boolean isInList() {
      return inList;
   }

}
