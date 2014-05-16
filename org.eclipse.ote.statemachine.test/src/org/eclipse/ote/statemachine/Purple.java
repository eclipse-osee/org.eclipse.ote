/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
