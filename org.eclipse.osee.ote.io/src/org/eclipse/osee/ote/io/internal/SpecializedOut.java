/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ote.io.internal;

import java.io.PrintStream;

import org.eclipse.osee.ote.io.SystemOutputListener;

public class SpecializedOut extends PrintStream {

   private SpecializedOutputStream specialOut;

   public SpecializedOut(SpecializedOutputStream out) {
      super(out);
      this.specialOut = out;
   }
   
   public void addListener(SystemOutputListener listener){
      specialOut.add(listener);
   }
   
   public void removeListener(SystemOutputListener listener){
      specialOut.remove(listener);
   }
}
