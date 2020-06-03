/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.ote.ui.message.util;

import java.util.Arrays;
import java.util.HashSet;
import org.eclipse.osee.ote.message.elements.Element;

/**
 * @author Ken J. Aguilar
 */
public class ElementFilter {
   private final HashSet<Class<? extends Element>> classes = new HashSet<Class<? extends Element>>();
   private boolean headerElementsAllowed = true;

   /**
    * @param allowHeaderElements the allowHeaderElements to set
    */
   public void setHeaderElementsAllowed(boolean headerElementsAllowed) {
      this.headerElementsAllowed = headerElementsAllowed;
   }

   public void addAllowableClass(Class<? extends Element> elementClass) {
      classes.add(elementClass);
   }

   public void addAllowableClass(Class<? extends Element>... elementClasses) {
      classes.addAll(Arrays.asList(elementClasses));
   }

   public boolean accept(Element e) {
      return classes.contains(e.getClass());
   }

   /**
    * @return the headerElementsAllowed
    */
   public boolean isHeaderElementsAllowed() {
      return headerElementsAllowed;
   }
}