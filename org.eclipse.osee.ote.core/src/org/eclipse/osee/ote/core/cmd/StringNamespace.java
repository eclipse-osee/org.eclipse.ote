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

public class StringNamespace implements Namespace, Serializable {
   private static final long serialVersionUID = -8903438134102328929L;
   private final String namespace;

   public StringNamespace(String namespace) {
      this.namespace = namespace;
   }

   @Override
   public boolean equals(Object arg0) {
      if (arg0 instanceof StringNamespace) {
         return namespace.equals(((StringNamespace) arg0).namespace);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return namespace.hashCode();
   }
}
