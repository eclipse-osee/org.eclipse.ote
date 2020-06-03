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

package org.eclipse.osee.ote.message.interfaces;

/**
 * @author Andrew M. Finkbeiner
 */
public class Namespace implements INamespace {

   private final String namespace;

   public Namespace(String string) {
      namespace = string;
   }

   @Override
   public String toString() {
      return namespace;
   }

   @Override
   public boolean equals(Object obj) {
      Namespace ns = (Namespace) obj;
      return namespace.equals(ns.namespace);
   }

   @Override
   public int hashCode() {
      return namespace.hashCode();
   }

}
