package org.eclipse.ote.commands.server;

import org.eclipse.ote.commands.messages.RunTests;
import org.eclipse.ote.statemachine.BaseInput;
import org.eclipse.ote.statemachine.StateMachine;

public class InputRunTests extends BaseInput {

   public final static Object TYPE = new Object();
   private RunTests runTests;
   
   public InputRunTests(StateMachine stateMachine) {
      super(stateMachine);
   }

   public InputRunTests(InputRunTests inputRunTests) {
      super(inputRunTests);
      this.runTests = inputRunTests.runTests;
   }

   @Override
   public Object getType() {
      return TYPE;
   }

   public void set(RunTests received) {
      this.runTests = received;
   }
   
   public RunTests get(){
      return this.runTests;
   }

}
