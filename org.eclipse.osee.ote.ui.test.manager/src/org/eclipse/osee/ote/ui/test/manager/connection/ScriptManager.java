/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.test.manager.connection;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;
import org.eclipse.osee.ote.core.environment.status.TestComplete;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.osee.ote.ui.test.manager.internal.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.jobs.StoreOutfileJob;
import org.eclipse.osee.ote.ui.test.manager.models.OutputModelJob;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTask;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTask.ScriptStatusEnum;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTaskList;

/**
 * @author Andrew M. Finkbeiner
 */
public abstract class ScriptManager implements Runnable {
   private final Map<String, ScriptTask> guidToScriptTask = new HashMap<>();
   private TestManagerStatusListener statusListenerImpl;
   private final TestManagerEditor testManager;

   private volatile boolean updateScriptTable;
   private StructuredViewer stv;
   private ScheduledExecutorService updater;
   private Set<ScriptTask> tasksToUpdate;
   private ITestEnvironment connectedEnv;
   private UUID sessionKey;

   public ScriptManager(TestManagerEditor testManager, StructuredViewer stv) {
      this.testManager = testManager;
      this.stv = stv;

      tasksToUpdate = new HashSet<>();
      updater = Executors.newScheduledThreadPool(1, new ThreadFactory() {

         @Override
         public Thread newThread(Runnable r) {
            Thread th = new Thread(r, "TM Table updater");
            th.setDaemon(true);
            return th;
         }

      });
      updater.scheduleAtFixedRate(this, 0, 2000, TimeUnit.MILLISECONDS);
      OutputModelJob.createSingleton(this);
   }

   public abstract void abortScript(boolean isBatchAbort) throws RemoteException;

   public void notifyScriptDequeued(String className) {
      ScriptTask task = guidToScriptTask.get(className);
      if (task != null) {
         
         ScriptTask value = guidToScriptTask.remove(task);
         if(value == null){
            System.out.println("did not dq");
         }
      }
   }

   /**
    * This should be called after the environment is received in order to configure necessary items.
    * 
    * @return null if successful, otherwise a string describing the error
    */
   public boolean connect(ConnectionEvent event) {

      connectedEnv = event.getEnvironment();
      sessionKey = event.getSessionKey();
      try {
         /*
          * Setup the status listener for commands
          */
         statusListenerImpl = new TestManagerStatusListener(testManager, this);
         return false;
      } catch (Exception e) {
         TestManagerPlugin.log(Level.SEVERE, "failed to connect script manager", e);
         return true;
      }
   }

   /**
    * This should NOT be called directly, users should call the HostDataStore's disconnect.
    */
   public boolean disconnect(ConnectionEvent event) {
      connectedEnv = null;
      sessionKey = null;
      guidToScriptTask.clear();
      statusListenerImpl.unregisterEventListener();
      return false;
   }

   public boolean onConnectionLost() {
      connectedEnv = null;
      sessionKey = null;
      guidToScriptTask.clear();
      statusListenerImpl.unregisterEventListener();
      return false;
   }

   public ScriptTask getScriptTask(String name) {
      ScriptTask t = guidToScriptTask.get(name);
      if(t == null){
         Object obj = stv.getInput();
         if(obj instanceof ScriptTaskList){
            ScriptTaskList stl = (ScriptTaskList)obj;
            for(ScriptTask task:stl.getTasks()){
               String clazz = task.getScriptModel().getTestClass();
               if(clazz.equals(name)){
                  t = task;
                  break;
               }
            }
         }
      }
      return t;
   }

   public void notifyScriptQueued(GUID theGUID, final ScriptTask script) {
      guidToScriptTask.put(script.getScriptModel().getTestClass(), script);
      script.setStatus(ScriptStatusEnum.IN_QUEUE);
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (stv.getControl().isDisposed()) {
               return;
            }
            stv.refresh(script);
         }
      });
   }

   public void updateScriptTableViewer(final ScriptTask task) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (stv.getControl().isDisposed()) {
               return;
            }
            stv.refresh(task);
         }
      });
   }

   public void updateScriptTableViewerTimed(ScriptTask task) {
      updateScriptTable = true;
      synchronized (tasksToUpdate) {
         tasksToUpdate.add(task);
      }
   }

   @Override
   public void run() {
      if (updateScriptTable) {
         updateScriptTable = false;
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               synchronized (tasksToUpdate) {
                  if (stv.getControl().isDisposed()) {
                     return;
                  }
                  for (ScriptTask task : tasksToUpdate) {
                     stv.refresh(task);
                  }
                  tasksToUpdate.clear();
               }
            }
         });
      }
   }

   protected TestManagerEditor getTestManagerEditor() {
      return testManager;
   }

   public abstract void addTestsToQueue(List<ScriptTask> scripts);

   public void notifyScriptStart(final ScriptTask task) {
      task.setStatus(ScriptStatusEnum.RUNNING);
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            stv.refresh(task);
         }
      });
   }

   public void storeOutFile(ScriptTask task, TestComplete testComplete, boolean isValidRun) {
      if (task.getScriptModel() != null) {
         Job job =
            new StoreOutfileJob(connectedEnv, testManager, this, task, testComplete.getClientOutfilePath(),
               testComplete.getServerOutfilePath(), isValidRun);
         StoreOutfileJob.scheduleJob(job);
      }
   }

   protected UUID getSessionKey() {
      return sessionKey;
   }
}