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

public abstract class BaseInput{
   private int id = -1;

   private StateMachine stateMachine;
   
   public BaseInput(StateMachine stateMachine){
      this.stateMachine = stateMachine;
      id = this.stateMachine.getIdFactory().getNextInputId();
   }
   
   public BaseInput(BaseInput input) {
      this.stateMachine = input.stateMachine;
      this.id = input.id;
   }

   public void addToStateMachineQueue(){
      this.stateMachine.addToQueue(this);
   }
   
   public abstract Object getType();

   int getId() {
      return id;
   }
}
