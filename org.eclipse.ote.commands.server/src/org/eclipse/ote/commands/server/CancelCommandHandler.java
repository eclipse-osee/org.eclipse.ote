package org.eclipse.ote.commands.server;

import org.eclipse.ote.bytemessage.OteByteMessageResponseCallable;
import org.eclipse.ote.commands.messages.CancelCommand;

public class CancelCommandHandler implements OteByteMessageResponseCallable<CancelCommand> {

   private InputCancelCommand inputCancelCommand;

   public CancelCommandHandler(InputCancelCommand inputCancelCommand) {
      this.inputCancelCommand = inputCancelCommand;
   }

   @Override
   public synchronized void call(CancelCommand received) {
      InputCancelCommand newInput = new InputCancelCommand(inputCancelCommand);
      newInput.set(received);
      newInput.addToStateMachineQueue();
   }

}
