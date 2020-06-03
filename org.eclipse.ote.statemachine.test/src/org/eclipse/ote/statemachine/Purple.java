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


public class Purple  extends BaseState {

   private Change internalExit;

   private String name;

   public Purple(Change internalExit) {
      this.internalExit = internalExit;
   }
   
   public Purple(Change internalExit, String name) {
      this.internalExit = internalExit;
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
      internalExit.addToStateMachineQueue();
   }

   public String toString(){
      if(name != null){
         return name;
      } else {
         return super.toString();
      }
   }
}
