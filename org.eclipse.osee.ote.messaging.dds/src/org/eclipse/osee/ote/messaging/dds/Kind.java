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

package org.eclipse.osee.ote.messaging.dds;

/**
 * Base class for the various *Kind enumerations specified in the DDS specification. This provides the basic structure
 * and accessor methods that are common for these enumerations.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public abstract class Kind {
   private final String kindName;
   private final long kindId;

   protected Kind(String kindName, long kindId) {
      super();
      this.kindName = kindName;
      this.kindId = kindId;
   }

   public String getKindName() {
      return kindName;
   }

   public long getKindId() {
      return kindId;
   }
}
