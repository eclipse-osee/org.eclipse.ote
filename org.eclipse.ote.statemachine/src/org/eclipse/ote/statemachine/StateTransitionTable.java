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

import java.util.ArrayList;
import java.util.List;


class StateTransitionTable {

   private InputLookup[] inputLookup;
   private List<BaseInput> inputs;
   private List<ChildStateMachineState> children;
   
   StateTransitionTable(int initialInputSize, int initialStateSize){
      inputs = new ArrayList<BaseInput>();
      children = new ArrayList<ChildStateMachineState>();
      inputLookup = new InputLookup[initialInputSize];
      for(int i = 0; i < inputLookup.length; i++){
         inputLookup[i] = new InputLookup(initialStateSize);
      }
   }
   
   BaseState getTransition(BaseInput input, BaseState currentstate) {
      if(input != null && currentstate != null){
         InputLookup lookup = inputLookup[input.getId()];
         if(lookup != null){
            if(currentstate.getId() < 0){
               System.out.println("here");
            }
            return lookup.get(currentstate.getId());
         }
      }
      return null;
   }

   void put(BaseState state, BaseInput input, BaseState nextState) throws Exception {
      if(!inputs.contains(input)){
         inputs.add(input);
      }
      if(state instanceof ChildStateMachineState){
         if(!children.contains(state)){
            children.add((ChildStateMachineState) state);
         }
      }
      if(nextState instanceof ChildStateMachineState){
         if(!children.contains(nextState)){
            children.add((ChildStateMachineState) nextState);
         }
      }
      
      if(!inputLookup[input.getId()].put(state.getId(), nextState)){
         throw new Exception(String.format("State transition collision [%s][%s] already exists{ new[%s] old[%s] }", state.toString(), input.toString(), nextState, getTransition(input, state)));
      }
   }
   
   List<BaseInput> getInputs() {
      return inputs;
   }
   
   List<ChildStateMachineState> getChildren(){
      return children;
   }

   private static class InputLookup {
      BaseState[] nextStateLookup;
      
      public InputLookup(int initialStateSize) {
         nextStateLookup = new BaseState[initialStateSize];
      }

      public boolean put(int id, BaseState nextState) {
         if(nextStateLookup[id] != null){
            return false;
         }
         nextStateLookup[id] = nextState;
         return true;
      }

      public BaseState get(int id) {
         return nextStateLookup[id];
      }
   }


}
