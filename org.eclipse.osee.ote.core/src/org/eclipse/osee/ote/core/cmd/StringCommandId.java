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

package org.eclipse.osee.ote.core.cmd;

import java.io.Serializable;

/**
 * @author Andrew M. Finkbeiner
 */
public class StringCommandId implements CommandId, Serializable {

   private static final long serialVersionUID = 2236967568467058971L;
   private final Namespace namespace;
   private final Name name;

   public StringCommandId(Namespace namespace, Name name) {
      this.namespace = namespace;
      this.name = name;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof StringCommandId) {
         return namespace.equals(((StringCommandId) obj).namespace) && name.equals(((StringCommandId) obj).name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 31 * hash + namespace.hashCode();
      hash = 31 * hash + name.hashCode();
      return hash;
   }

   @Override
   public Name getName() {
      return name;
   }

   @Override
   public Namespace getNamespace() {
      return namespace;
   }
}
