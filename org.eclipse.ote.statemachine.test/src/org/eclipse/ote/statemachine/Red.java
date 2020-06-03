/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.ote.statemachine;


public class Red  extends BaseState {

   private String name;

   public Red(){
      
   }
   
   public Red(String name){
      this.name = name;
   }
   
   @Override
   public void run(BaseInput intput) {
      
   }

   @Override
   public void entry() {
      
   }

   @Override
   public void exit() {
      
   }
   
   public String toString(){
      if(name != null){
         return name;
      } else {
         return super.toString();
      }
   }

}
