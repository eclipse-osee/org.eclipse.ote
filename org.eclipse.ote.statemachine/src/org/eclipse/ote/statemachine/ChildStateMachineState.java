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

import java.util.List;

public abstract class ChildStateMachineState extends BaseState{
   
   private StateMachine stateMachine;

   public ChildStateMachineState(StateMachine sm){
      stateMachine = new StateMachine(sm.getIdFactory(), getClass().getSimpleName(), sm.getRunInInputThread());
   }
   
   public ChildStateMachineState(StateMachine sm, String name){
      stateMachine = new StateMachine(sm.getIdFactory(), name, sm.getRunInInputThread());
   }
   
   public abstract void preRunStateMachine(BaseInput input);
   public abstract void postRunStateMachine(BaseInput input);
   
   @Override
   public final void run(BaseInput intput) {
      try {
         preRunStateMachine(intput);
         addToQueue(intput);
         postRunStateMachine(intput);
      } catch (InterruptedException e) {
         e.printStackTrace();
      } catch (Throwable e) {
         e.printStackTrace();
      }
   }
   
   @Override
   public void entry(){
      stateMachine.resetToInitialState();
      getCurrentState().entry();
   }
   
   void addToQueue(BaseInput input) throws Exception {
      stateMachine.addToQueue(input);
      stateMachine.processInput();           
   }
   
   public void setDefaultInitialState(BaseState initialState) {
      stateMachine.setDefaultInitialState(initialState);
   }
   
   public BaseState getCurrentState(){
      return stateMachine.getCurrentState();
   }

   public void newTransition(BaseState state, BaseInput input, BaseState nextState) throws Exception{
      stateMachine.newTransition(state, input, nextState);
   }

   List<BaseInput> getInputs() {
      return stateMachine.getInputs();
   }

   void setupChildStateMachines() throws Exception {
      stateMachine.setupChildStateMachines();
   }
}
