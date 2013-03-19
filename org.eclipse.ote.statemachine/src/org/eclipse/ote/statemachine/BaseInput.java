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
