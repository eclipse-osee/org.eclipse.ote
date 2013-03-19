package org.eclipse.ote.commands.server;

import org.eclipse.ote.bytemessage.OteByteMessageResponseCallable;
import org.eclipse.ote.commands.messages.RunTests;

class HandleRunTests implements OteByteMessageResponseCallable<RunTests> {

   private InputRunTests inputRunTests;

   public HandleRunTests(InputRunTests inputRunTests) {
      this.inputRunTests = inputRunTests;
   }

   @Override
   public synchronized void call(RunTests received) {
      InputRunTests newInput = new InputRunTests(inputRunTests);
      newInput.set(received);
      newInput.addToStateMachineQueue();
   }

}
