package org.eclipse.ote.commands.server;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.TestEnvironmentInterface;
import org.eclipse.osee.ote.core.framework.command.ICommandHandle;
import org.eclipse.osee.ote.core.framework.command.ITestCommandResult;
import org.eclipse.ote.bytemessage.OteSendByteMessage;
import org.eclipse.ote.commands.messages.CancelCommand;
import org.eclipse.ote.commands.messages.CommandComplete;
import org.eclipse.ote.commands.messages.RunTests;
import org.eclipse.ote.statemachine.BaseInput;
import org.eclipse.ote.statemachine.BaseState;

public class StateRunningCommand extends BaseState {

   private TestEnvironmentInterface env;
   private InputCommandsDone inputCommandsDone;
   private ConcurrentHashMap<String, ICommandHandle> commands;
   private OteSendByteMessage sender;
   
   public StateRunningCommand(TestEnvironmentInterface env, InputCommandsDone inputCommandsDone, OteSendByteMessage sender) {
      this.env = env;
      this.inputCommandsDone = inputCommandsDone;
      this.sender = sender;
      commands = new ConcurrentHashMap<String, ICommandHandle>();
   }

   @Override
   public synchronized void run(BaseInput input) {
      if(InputRunTests.TYPE == input.getType()){
         try{
            RunTests runTests = ((InputRunTests)input).get();
            String cmdId = runTests.getCmdUUID().toString();
            RunTestsCommand command = new RunTestsCommand(cmdId, null, runTests.getGlobalConfig(), runTests.getScripts());
            ICommandHandle handle = env.addCommand(command);
            commands.put(cmdId, handle);
            Thread th = new Thread(new CommandCompleteNotifier(handle, runTests.getSessionUUID(), runTests.getCmdUUID(), this));
            th.setName("Waiting For Command " + cmdId);
            th.setDaemon(true);
            th.start();
         } catch (Throwable th){
            OseeLog.log(getClass(), Level.SEVERE, th);
         }
      } else if (InputCancelCommand.TYPE == input.getType()){
         InputCancelCommand cancel = (InputCancelCommand)input;
         CancelCommand cancelcommand =cancel.get();
         cancelcommand.getSessionUUID();
         String cmdId = cancelcommand.getCmdUUID().toString();
         ICommandHandle handle = commands.remove(cmdId);
         try {
            handle.cancelAll(true);
         } catch (Throwable th) {
            OseeLog.log(getClass(), Level.SEVERE, th);
         }
      } 
   }

   @Override
   public void entry() {

   }
   
   synchronized void commandComplete(UUID sessionId, UUID commandId){
      ICommandHandle handle = commands.remove(commandId.toString());
      String status = "unknown";
      if(handle != null){
         ITestCommandResult result;
         try {
            result = handle.get();
            status = result.getStatus().name();
         } catch (Throwable th) {
            OseeLog.log(getClass(), Level.SEVERE, th);
         }
      }
      CommandComplete complete = new CommandComplete();
      complete.setSessionUUID(sessionId);
      complete.setCmdUUID(commandId);
      complete.setStatus(status);
      sender.asynchSend(complete);
      if(commands.size() == 0){
         inputCommandsDone.addToStateMachineQueue();
      }
   }

}
