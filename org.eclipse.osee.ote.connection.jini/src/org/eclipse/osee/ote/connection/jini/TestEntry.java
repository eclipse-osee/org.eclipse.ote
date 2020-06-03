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

package org.eclipse.osee.ote.connection.jini;

import net.jini.entry.AbstractEntry;

/**
 * @author Ken J. Aguilar
 */
public class TestEntry extends AbstractEntry {
   private static final long serialVersionUID = -2239353039479522642L;
   public final String data;

   public TestEntry() {
      data = "<none>";
   }

   public TestEntry(String data) {
      super();
      this.data = data;
   }

   /**
    * @return the data
    */
   public String getData() {
      return data;
   }

}
