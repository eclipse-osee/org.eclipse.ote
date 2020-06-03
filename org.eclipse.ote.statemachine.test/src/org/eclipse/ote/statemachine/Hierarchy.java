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


public class Hierarchy extends ChildStateMachineState {

   private String name;

   public Hierarchy(StateMachine sm) {
      super(sm);
   }
   
   public Hierarchy(StateMachine sm, String name) {
      super(sm, name);
      this.name = name;
   }

   @Override
   public void exit() {
      // TODO Auto-generated method stub

   }

   @Override
   public void preRunStateMachine(BaseInput input) {
      // TODO Auto-generated method stub
   }

   @Override
   public void postRunStateMachine(BaseInput input) {
      // TODO Auto-generated method stub
      
   }
   
   public String toString(){
      if(name != null){
         return name;
      } else {
         return super.toString();
      }
   }

}
