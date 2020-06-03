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

package org.eclipse.osee.ote.core.framework.saxparse.elements;

/**
 * @author Andrew M. Finkbeiner
 */
public class UserData {

   private final String email;
   private final String id;
   private final String name;

   UserData(String email, String id, String name) {
      this.email = email;
      this.id = id;
      this.name = name;
   }

   /**
    * @return the email
    */
   public String getEmail() {
      return email;
   }

   /**
    * @return the id
    */
   public String getId() {
      return id;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

}
