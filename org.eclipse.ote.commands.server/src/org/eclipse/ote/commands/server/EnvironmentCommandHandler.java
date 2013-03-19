package org.eclipse.ote.commands.server;

import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.TestEnvironmentInterface;
import org.eclipse.ote.bytemessage.OteByteMessageResponseFuture;
import org.eclipse.ote.bytemessage.OteSendByteMessage;
import org.eclipse.ote.commands.messages.CancelCommand;
import org.eclipse.ote.commands.messages.RunTests;
import org.eclipse.ote.statemachine.StateMachine;
import org.osgi.service.event.EventAdmin;

public class EnvironmentCommandHandler {

   private TestEnvironmentInterface env;
   private EventAdmin eventAdmin;
   private StateMachine stateMachine;
   private OteByteMessageResponseFuture<CancelCommand> cancelCommand;
   private OteByteMessageResponseFuture<RunTests> runTestsFuture;
   
   public void start(){
      try{
         OteSendByteMessage sender = new OteSendByteMessage(eventAdmin);
         stateMachine = new StateMachine("ServerCommandProcessor");

         InputRunTests inputRunTests = new InputRunTests(stateMachine);
         InputCancelCommand inputCancelCommand = new InputCancelCommand(stateMachine);
         InputCommandsDone inputCommandsDone = new InputCommandsDone(stateMachine);

         cancelCommand = sender.asynchResponse(CancelCommand.class, CancelCommand.TOPIC, new CancelCommandHandler(inputCancelCommand));
         runTestsFuture = sender.asynchResponse(RunTests.class, RunTests.TOPIC, new HandleRunTests(inputRunTests)); 
         
         StateWaitingForCommand stateWaitingForCommand = new StateWaitingForCommand();
         StateRunningCommand stateRunningCommand = new StateRunningCommand(env, inputCommandsDone, sender);

         stateMachine.setDefaultInitialState(stateWaitingForCommand);
         stateMachine.newTransition(stateWaitingForCommand, inputRunTests, stateRunningCommand);
         stateMachine.newTransition(stateRunningCommand, inputRunTests, stateRunningCommand);
         stateMachine.newTransition(stateRunningCommand, inputCancelCommand, stateRunningCommand);
         stateMachine.newTransition(stateRunningCommand, inputCommandsDone, stateWaitingForCommand);

         stateMachine.initialize();
         stateMachine.start();
         
      } catch (Throwable th){
         OseeLog.log(getClass(), Level.SEVERE, "statemachine initialization failed", th);
      }
   }
   
   public void stop(){
      try{
         cancelCommand.cancel();
         runTestsFuture.cancel();
         stateMachine.stop();
      } catch (Throwable th){
         OseeLog.log(getClass(), Level.SEVERE, "Failed to stop statemachine", th);
      }
   }
   
   public void bindTestEnvironmentInterface(TestEnvironmentInterface env){
      this.env = env;
   }
   
   public void unbindTestEnvironmentInterface(TestEnvironmentInterface env){
      this.env = null;
   }
   
   public void bindEventAdmin(EventAdmin eventAdmin){
      this.eventAdmin = eventAdmin;
   }
   
   public void unbindEventAdmin(EventAdmin eventAdmin){
      this.eventAdmin = null;
   }
}
