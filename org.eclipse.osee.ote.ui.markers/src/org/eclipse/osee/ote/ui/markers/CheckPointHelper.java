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

package org.eclipse.osee.ote.ui.markers;

import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.w3c.dom.Element;

public class CheckPointHelper implements Comparable<CheckPointHelper> {

   @Override
   public String toString() {
      return String.format("%s[%s, %s]", testPointName, expected, actual);
   }

   public void increment() {
      count++;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof CheckPointHelper) {
         return key.equals(((CheckPointHelper) obj).key);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return key.hashCode();
   }

   private final String testPointName;
   private final String expected;
   private final String actual;
   private final String key;
   private int count = 1;

   public CheckPointHelper(Element el) {
      testPointName = Jaxp.getChildText(el, "TestPointName");
      expected = Jaxp.getChildText(el, "Expected");
      actual = Jaxp.getChildText(el, "Actual");
      key = testPointName + expected + actual;
   }

   public CheckPointHelper(CheckPointData data) {
      testPointName = data.getName();
      expected = data.getExpected();
      actual = data.getActual();
      key = testPointName + expected + actual;
   }

   @Override
   public int compareTo(CheckPointHelper o) {
      return o.count - this.count;
   }

}
