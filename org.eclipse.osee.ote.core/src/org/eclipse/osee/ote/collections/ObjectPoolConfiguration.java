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
package org.eclipse.osee.ote.collections;

public abstract class ObjectPoolConfiguration<T> {

   private int maxSize;
   private boolean preallocate;

   public ObjectPoolConfiguration(int maxSize, boolean preallocate){
      this.maxSize = maxSize; 
      this.preallocate = preallocate;
   }
   
   public int getMaxSize(){
      return maxSize;
   }
   
   public boolean preallocate(){
      return preallocate;
   }

   abstract public T make();

}
