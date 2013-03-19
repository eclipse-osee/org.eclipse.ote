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

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;


public final class StateMachine {

   private StateTransitionTable stateTransitionTable;
   private BaseState currentState;
   private LinkedBlockingQueue<BaseInput> queue;
   private String name;
   private KillInput killInput;
   
   private static class KillInput extends BaseInput{
      
      public KillInput(StateMachine stateMachine) {
         super(stateMachine);
      }

      @Override
      public Object getType() {
         return 0;
      }
   };
   
   private Thread th;
   private StateMachineIdFactory factory;
   private BaseState defaultInitialState;
   private int nextStateId = 0;
   private boolean debug = true;
   
   public StateMachine(String name){
      this(new StateMachineIdFactory(), name);
   }
   
   StateMachine(StateMachineIdFactory factory, String name){
      this.name = name;
      this.factory = factory;
      stateTransitionTable = new StateTransitionTable(50,50);
      queue = new LinkedBlockingQueue<BaseInput>();
      killInput = new KillInput(this);
   }
   
   BaseInput processInput() throws Exception{
      BaseInput input = getInput();
      if(input != killInput){
         if(currentState == null){
            throw new Exception("CurrentState is null, statemachine was not properly initialized.  Please ensure that if any child state machines were created and entry() was implemented that you call super.entry().");
         }
         BaseState nextState = stateTransitionTable.getTransition(input, currentState);
         if(debug ) {
            System.out.println("======================"+name+"========================");
            System.out.println("INPUT: " + input.toString());
            System.out.println("CURRENTSTATE: " + currentState.toString());
            System.out.println("NEXTSTATE: " + (nextState == null ? "" : nextState.toString()));
         }
         if(nextState != null){
            currentState.run(input);
            if(nextState != currentState){
               currentState.exit();
               nextState.entry();
               currentState = nextState;
            }
         }
      }
      return input;
   }
   
   public void start(){
      th = new Thread(new Runnable(){
         @Override
         public void run() {
            BaseInput input = null;
            currentState.entry();
            while(input != killInput){
               try{
                  input = processInput();
               } catch (Throwable th){
                  th.printStackTrace();
               }
            }
         }
      });
      th.setName("StateMachine " + name);
      th.start();
   }

   public void setDefaultInitialState(BaseState initialState) {
      this.defaultInitialState = initialState;
   }
   
   public void initialize() throws Exception{
      resetToInitialState();
      setupChildStateMachines();
   }
   
   void setupChildStateMachines() throws Exception{
      for(ChildStateMachineState child:stateTransitionTable.getChildren()){
         child.setupChildStateMachines();
         this.addChildMachine(child);
      }
   }
   
   public void resetToInitialState(){
      this.currentState = defaultInitialState;
   }
   
   public BaseState getCurrentState(){
      return currentState;
   }

   private BaseInput getInput() throws InterruptedException {
      return queue.take();
   }
   
   public void newTransition(BaseState state, BaseInput input, BaseState nextState) throws Exception{
      if(state.getId() == -1){
         state.setId(getNextStateId());
      }
      if(nextState.getId() == -1){
         nextState.setId(getNextStateId());
      }
      stateTransitionTable.put(state, input, nextState);
   }

   private int getNextStateId() {
      return nextStateId ++;
   }

   void addToQueue(BaseInput input) {
      try {
         queue.put(input);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

   public void stop() throws InterruptedException {
      killInput.addToStateMachineQueue();
   }
   
   public void waitForCompletion() throws InterruptedException{
      if(th != null && th.isAlive()){
         th.join();
      }
   }
   
   public boolean waitForCompletion(long timeout) throws InterruptedException{
      if(th != null && th.isAlive()){
         th.join(timeout);
         return !th.isAlive();
      }
      return true;
   }
   
   public void murder() throws InterruptedException{
      stop();
      if(th != null){
         th.join(1000);
         if(th.isAlive()){
            th.interrupt();
            th.join(1000);
         }
      }
   }

   void addChildMachine(ChildStateMachineState child) throws Exception {
      for(BaseInput input:child.getInputs()){
         newTransition(child, input, child);
      }
   }

   List<BaseInput> getInputs() {
      return stateTransitionTable.getInputs();
   }
   
   int queueSize(){
      return queue.size();
   }

   StateMachineIdFactory getIdFactory() {
      return this.factory;
   }
      
}
