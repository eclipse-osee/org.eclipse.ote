package org.eclipse.ote.commands.server;

import org.eclipse.ote.commands.messages.CancelCommand;
import org.eclipse.ote.statemachine.BaseInput;
import org.eclipse.ote.statemachine.StateMachine;

public class InputCancelCommand extends BaseInput {

   public final static Object TYPE = new Object();
   private CancelCommand received;
   
   public InputCancelCommand(StateMachine stateMachine) {
      super(stateMachine);
   }
   
   public InputCancelCommand(InputCancelCommand inputCancelCommand){
      super(inputCancelCommand);
      this.received = inputCancelCommand.received;
   }

   @Override
   public Object getType() {
      return TYPE;
   }

   public void set(CancelCommand received) {
      this.received = received;
   }
   
   public CancelCommand get(){
      return this.received;
   }

}
