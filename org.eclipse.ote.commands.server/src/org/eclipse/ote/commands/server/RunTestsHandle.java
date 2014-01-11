package org.eclipse.ote.commands.server;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.framework.command.ICommandHandle;
import org.eclipse.osee.ote.core.framework.command.ITestCommandResult;
import org.eclipse.osee.ote.core.framework.command.ITestContext;
import org.eclipse.osee.ote.core.framework.command.TestCommandResult;

class RunTestsHandle implements ICommandHandle {
   private final Future<ITestCommandResult> result;
   private final ITestContext context;
   private final RunTestsCommand command;
   private final String guid;

   public RunTestsHandle(Future<ITestCommandResult> result, ITestContext context, RunTestsCommand command) {
      this.result = result;
      this.context = context;
      this.command = command;
      this.guid = GUID.create();
   }

   @Override
   public boolean cancelAll(boolean mayInterruptIfRunning) {
      command.cancel(mayInterruptIfRunning);
      if (command.isRunning()) {
         return context.getRunManager().abort();
      } else {
         result.cancel(mayInterruptIfRunning);
      }
      return true;
   }

   @Override
   public boolean cancelSingle(boolean mayInterruptIfRunning) {
      if (command.isRunning()) {
         context.getRunManager().abort();
      }
      command.cancelSingle(mayInterruptIfRunning);
      return true;
   }

   @Override
   public ITestCommandResult get() {
      try {
         return result.get();
      } catch (InterruptedException e) {
         OseeLog.log(getClass(), Level.SEVERE, String.format(
               "Command [%s] encountered an error while trying to retrieve status.", command.toString()), e);
      } catch (ExecutionException e) {
    	  OseeLog.log(getClass(), Level.SEVERE, String.format(
               "Command [%s] encountered an error while trying to retrieve status.", command.toString()), e);
      }
      return TestCommandResult.FAIL;
   }

   @Override
   public boolean isCancelled() {
      return result.isCancelled();
   }

   @Override
   public boolean isDone() {
      return result.isDone();
   }

   @Override
   public String getCommandKey() {
      return guid;
   }

}
