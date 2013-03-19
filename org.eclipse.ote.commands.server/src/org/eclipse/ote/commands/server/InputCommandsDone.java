package org.eclipse.ote.commands.server;

import org.eclipse.ote.statemachine.BaseInput;
import org.eclipse.ote.statemachine.StateMachine;

public class InputCommandsDone extends BaseInput {

   public final static Object TYPE = new Object();
   
   public InputCommandsDone(StateMachine stateMachine) {
      super(stateMachine);
   }

   @Override
   public Object getType() {
      return TYPE;
   }

}
