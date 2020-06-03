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

package org.eclipse.ote.test.manager.panels;

import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Andrew M. Finkbeiner
 */
public final class UserName {
   private final String lastName, firstName, middleInitial;

   public UserName(String lastName, String firstName, String middleInitial) {
      this.lastName = lastName;
      this.firstName = firstName;
      this.middleInitial = middleInitial;
   }

   public String getLastName() {
      return lastName;
   }

   public String getFirstName() {
      return firstName;
   }

   public String getMiddleInitial() {
      return middleInitial;
   }

   @Override
   public String toString() {
      String middle = "";
      if (Strings.isValid(middleInitial)) {
         middle = String.format(" %s.", middleInitial);
      }
      return String.format("%s,%s%s", lastName, firstName, middle);
   }

   @Override
   public boolean equals(Object obj) {
      return this.toString().equalsIgnoreCase(obj.toString());
   }

}