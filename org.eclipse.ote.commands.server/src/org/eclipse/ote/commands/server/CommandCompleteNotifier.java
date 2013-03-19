package org.eclipse.ote.commands.server;

import java.util.UUID;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.framework.command.ICommandHandle;

public class CommandCompleteNotifier implements Runnable {

   private ICommandHandle handle;
   private StateRunningCommand stateRunningCommand;
   private UUID sessionUUID;
   private UUID cmdUUID;

   public CommandCompleteNotifier(ICommandHandle handle, UUID sessionUUID, UUID cmdUUID, StateRunningCommand stateRunningCommand) {
      this.handle = handle;
      this.sessionUUID = sessionUUID;
      this.cmdUUID = cmdUUID;
      this.stateRunningCommand = stateRunningCommand;
   }

   @Override
   public void run() {
      try{
         while(!handle.isDone()){
            try{
               handle.get();
               if(!handle.isDone()){
                  Thread.sleep(1000);
               }
            }catch(Throwable th){
               OseeLog.log(getClass(), Level.SEVERE, th);
            }
         }
      } catch (Throwable th){
         try{
            if(!handle.isDone()){
               handle.cancelAll(true);
            }
         } catch (Throwable th2){
            OseeLog.log(getClass(), Level.SEVERE, th2);
         }
      } finally {
         this.stateRunningCommand.commandComplete(sessionUUID, cmdUUID);
      }
   }

}
