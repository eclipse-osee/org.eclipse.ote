package org.eclipse.ote.commands.server;

import org.eclipse.ote.statemachine.BaseInput;
import org.eclipse.ote.statemachine.BaseState;

public class StateWaitingForCommand extends BaseState{

   public StateWaitingForCommand() {
   }

   @Override
   public void run(BaseInput input) {
      //send the input along to the state that does the real work, this is kind of silly
      input.addToStateMachineQueue();
   }

   @Override
   public void entry() {
      
   }

}
