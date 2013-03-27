package org.eclipse.ote.commands.server;

import java.io.Serializable;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.status.CommandEndedStatusEnum;
import org.eclipse.osee.ote.core.environment.status.OTEStatusBoard;
import org.eclipse.osee.ote.core.framework.IMethodResult;
import org.eclipse.osee.ote.core.framework.ReturnCode;
import org.eclipse.osee.ote.core.framework.command.ICommandHandle;
import org.eclipse.osee.ote.core.framework.command.ITestCommandResult;
import org.eclipse.osee.ote.core.framework.command.ITestContext;
import org.eclipse.osee.ote.core.framework.command.ITestServerCommand;
import org.eclipse.osee.ote.core.framework.command.TestCommandResult;
import org.eclipse.osee.ote.message.IMessageTestContext;
import org.eclipse.ote.commands.messages.RunTestsKeys;

class RunTestsCommand implements ITestServerCommand, Serializable {

   private static final long serialVersionUID = 7408758537342855854L;
   private final IPropertyStore global;
   private final List<IPropertyStore> scripts;
   private volatile boolean cancel = false;
   private volatile boolean isRunning = false;
   private final UUID sessionKey;
   private final String guid;
   private final List<RunTestsHandle> handles;

   private TestEnvironment environment;

   public RunTestsCommand(String guid, UUID sessionKey, IPropertyStore global, List<IPropertyStore> scripts) {
      handles = new ArrayList<RunTestsHandle>();
      this.global = global;
      this.scripts = scripts;
      this.sessionKey = sessionKey;
      this.guid = guid;
   }

   public UUID getSessionKey() {
      return sessionKey;
   }

   @Override
   public ICommandHandle createCommandHandle(Future<ITestCommandResult> result, ITestContext context) throws ExportException {
      RunTestsHandle handle = new RunTestsHandle(result, context, this);
      handles.add(handle);
      IServiceConnector connector = context.getConnector();
      ICommandHandle toReturn = (ICommandHandle) connector.findExport(handle);
      if (toReturn == null) {
         toReturn = (ICommandHandle) connector.export(handle);
      }
      return toReturn;
   }

   @Override
   public ITestCommandResult execute(TestEnvironment environment, OTEStatusBoard statusBoard) throws Exception {
      environment.setBatchMode(global.getBoolean(RunTestsKeys.batchmode.name()));
      ITestCommandResult retVal = TestCommandResult.SUCCESS;
      isRunning = true;
      IMessageTestContext msgContext = (IMessageTestContext) environment;
      this.environment = environment;
      msgContext.resetScriptLoader(global.getArray(RunTestsKeys.classpath.name()));
      for (IPropertyStore store : scripts) {
         if (cancel) {
            statusBoard.onTestComplete(store.get(RunTestsKeys.testClass.name()),
               store.get(RunTestsKeys.serverOutfilePath.name()),
               store.get(RunTestsKeys.clientOutfilePath.name()), CommandEndedStatusEnum.ABORTED,
               new ArrayList<IHealthStatus>());
            retVal = TestCommandResult.CANCEL;
            continue;

         }
         statusBoard.onTestStart(store.get(RunTestsKeys.testClass.name()), store.get(RunTestsKeys.serverOutfilePath.name()), store.get(RunTestsKeys.clientOutfilePath.name()));
         IMethodResult runResults = environment.getRunManager().run(environment, store);

         CommandEndedStatusEnum status = CommandEndedStatusEnum.RAN_TO_COMPLETION;
         if (runResults.getReturnCode() == ReturnCode.ABORTED) {
            status = CommandEndedStatusEnum.ABORTED;
         }
         if (runResults.getReturnCode() == ReturnCode.ERROR) {
            status = CommandEndedStatusEnum.EXCEPTION;
         }

         statusBoard.onTestComplete(store.get(RunTestsKeys.testClass.name()),
            store.get(RunTestsKeys.serverOutfilePath.name()), store.get(RunTestsKeys.clientOutfilePath.name()),
            status, runResults.getStatus());
      }
      handles.clear();
      isRunning = false;
      return retVal;
   }

   void cancel(boolean mayInterruptIfRunning) {
      cancel = mayInterruptIfRunning;
      environment.getRunManager().abort();
   }

   @Override
   public String getGUID() {
      return guid;
   }

   @Override
   public UUID getUserSessionKey() {
      return sessionKey;
   }

   public void cancelSingle(boolean mayInterruptIfRunning) {

   }

   boolean isRunning() {
      return isRunning;
   }

}
