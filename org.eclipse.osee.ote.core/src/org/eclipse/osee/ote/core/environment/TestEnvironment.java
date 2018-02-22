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
package org.eclipse.osee.ote.core.environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.connection.service.LocalConnector;
import org.eclipse.osee.framework.jdk.core.reportdata.ReportDataListener;
import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.OseeLogStatusCallback;
import org.eclipse.osee.ote.core.GCHelper;
import org.eclipse.osee.ote.core.OseeTestThread;
import org.eclipse.osee.ote.core.OteProperties;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.interfaces.IAssociatedObjectListener;
import org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer;
import org.eclipse.osee.ote.core.environment.interfaces.IEnvironmentFactory;
import org.eclipse.osee.ote.core.environment.interfaces.IExecutionUnitManagement;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.osee.ote.core.environment.interfaces.IScriptControl;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentListener;
import org.eclipse.osee.ote.core.environment.interfaces.ITestLogger;
import org.eclipse.osee.ote.core.environment.interfaces.ITestStation;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;
import org.eclipse.osee.ote.core.environment.interfaces.ITimerControl;
import org.eclipse.osee.ote.core.framework.IRunManager;
import org.eclipse.osee.ote.core.framework.command.ICommandHandle;
import org.eclipse.osee.ote.core.framework.command.ITestContext;
import org.eclipse.osee.ote.core.framework.command.ITestServerCommand;
import org.eclipse.osee.ote.core.internal.Activator;
import org.eclipse.osee.ote.properties.OtePropertiesCore;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Andrew M. Finkbeiner
 */
public abstract class TestEnvironment implements TestEnvironmentInterface, ITestEnvironmentAccessor, ITestContext {

   private final List<ITestEnvironmentListener> envListeners = new ArrayList<>(32);
   private IExecutionUnitManagement executionUnitManagement;

   private File outDir = null;
   private final ITestStation testStation;
   private boolean batchMode = false;
   private final HashMap<String, Remote> controlInterfaces = new HashMap<>();
   private final IEnvironmentFactory factory;
   private IServiceConnector connector;
   private final IRuntimeLibraryManager runtimeManager;

   @Deprecated
   private final HashMap<Class<?>, Object> associatedObjects;
   @Deprecated
   private final HashMap<Class<?>, ArrayList<IAssociatedObjectListener>> associatedObjectListeners;
   @Deprecated
   private boolean isEnvSetup = false;
   @Deprecated
   private final List<IScriptCompleteEvent> scriptCompleteListeners = new ArrayList<>();
   @Deprecated
   private final List<IScriptSetupEvent> scriptSetupListeners = new ArrayList<>();

//   private OteServerSideEndprointRecieve oteServerSideEndpointRecieve;
//   private OteServerSideEndpointSender oteServerSideEndpointSender;
//   private final ServiceTracker messagingServiceTracker;

   private volatile boolean isShutdown = false;
//   private NodeInfo oteEmbeddedBroker;
   private ServiceRegistration<TestEnvironmentInterface> myRegistration;

   protected TestEnvironment(IEnvironmentFactory factory) {
      GCHelper.getGCHelper().addRefWatch(this);
//      try {
//         oteEmbeddedBroker = new NodeInfo("OTEEmbeddedBroker", new URI("vm://localhost?broker.persistent=false"));
//      } catch (URISyntaxException ex) {
//         OseeLog.log(TestEnvironment.class, Level.SEVERE, ex);
//      }
      this.factory = factory;
      this.testStation = factory.getTestStation();
      this.runtimeManager = factory.getRuntimeManager();

      this.associatedObjectListeners = new HashMap<>();
      this.associatedObjects = new HashMap<>(100);
      this.batchMode = OteProperties.isOseeOteInBatchModeEnabled();

//      messagingServiceTracker = setupOteMessagingSenderAndReceiver();
   }

   public void init(IServiceConnector connector) {
      this.connector = connector;
   }

   private void setupDefaultConnector() {
      EnhancedProperties props = new EnhancedProperties();
      try {
         props.setProperty("station", InetAddress.getLocalHost().getHostName());
      } catch (UnknownHostException ex) {
         OseeLog.log(TestEnvironment.class, Level.SEVERE, ex);
      }
      props.setProperty("date", new Date());
      props.setProperty("group", "OSEE Test Environment");
      props.setProperty("owner", OtePropertiesCore.userName.getValue());
      connector = new LocalConnector(this, Integer.toString(this.getUniqueId()), props);
   }

//   private ServiceTracker setupOteMessagingSenderAndReceiver() {
//      oteServerSideEndpointRecieve = new OteServerSideEndprointRecieve();
//      oteServerSideEndpointSender = new OteServerSideEndpointSender(this);
//      BundleContext context = Platform.getBundle("org.eclipse.osee.ote.core").getBundleContext();
//      return getServiceTracker(MessagingGateway.class.getName(), new OteEnvironmentTrackerCustomizer(context,
//            oteServerSideEndpointRecieve, oteServerSideEndpointSender,
//            OteServerSideEndpointSender.OTE_SERVER_SIDE_SEND_PROTOCOL));
//   }
//
//   public void sendMessageToServer(Message message) {
//      oteServerSideEndpointRecieve.recievedMessage(message);
//   }

   public ServiceTracker getServiceTracker(String clazz, ServiceTrackerCustomizer customizer) {
      return Activator.getInstance().getServiceTracker(clazz, customizer);
   }

   @Override
   public ServiceTracker getServiceTracker(String clazz) {
      return getServiceTracker(clazz, null);
   }

   @Override
   public ICommandHandle addCommand(ITestServerCommand cmd) throws ExportException {
      return factory.getCommandManager().addCommand(cmd, this);
   }

   @Override
   public IRunManager getRunManager() {
      return factory.getRunManager();
   }

   @Override
   public IRuntimeLibraryManager getRuntimeManager() {
      return this.runtimeManager;
   }

   @Override
   public IEnvironmentFactory getEnvironmentFactory() {
      return factory;
   }

   @Override
   public boolean isInBatchMode() {
      return batchMode;
   }

   @Override
   public void setBatchMode(boolean isInBatchMode) {
      if (!OteProperties.isOseeOteInBatchModeEnabled()) {
         this.batchMode = isInBatchMode;
      }
   }

   @Override
   public void addEnvironmentListener(ITestEnvironmentListener listener) {
      envListeners.add(listener);
   }

   @Override
   public boolean addTask(EnvironmentTask task) {
      factory.getTimerControl().addTask(task, this);
      return true;
   }

   public boolean equals(ITestEnvironment testEnvironment) throws RemoteException {
      if (testEnvironment.getUniqueId() == getUniqueId()) {
         return true;
      } else {
         return false;
      }
   }

   @Override
   public long getEnvTime() {
      return getTimerCtrl().getEnvTime();
   }

   @Override
   public IExecutionUnitManagement getExecutionUnitManagement() {
      return this.executionUnitManagement;
   }

   @Override
   public ITestLogger getLogger() {
      return factory.getTestLogger();
   }

   @Override
   public List<String> getQueueLabels() {
      List<String> list = new ArrayList<>();
      list.add("Description");
      return list;
   }

   @Override
   public abstract Object getModel(String modelClassName);

   @Override
   public IScriptControl getScriptCtrl() {
      return factory.getScriptControl();
   }

   @Override
   public byte[] getScriptOutfile(String filepath) throws RemoteException {
      try {
         File file = new File(filepath);
         InputStream is = new FileInputStream(file);
         long length = file.length();
         byte[] bytes = new byte[(int) length];

         int numRead = is.read(bytes);
         if (numRead < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
         }
         is.close();
         OseeLog.log(TestEnvironment.class, Level.FINE, "going to send " + bytes.length + " bytes to the client");

         return bytes;
      } catch (Exception ex) {
         throw new RemoteException("Error retrieving the script output", ex);
      }
   }

   @Override
   public ITestStation getTestStation() {
      return testStation;
   }

   @Override
   public ITimerControl getTimerCtrl() {
      return factory.getTimerControl();
   }

   @Override
   public int getUniqueId() {
      return this.hashCode();
   }

   private final void removeAllTasks() {
      factory.getTimerControl().cancelAllTasks();
   }

   @Override
   public URL setBatchLibJar(byte[] batchJar) throws IOException {
      String path = OtePropertiesCore.userHome.getValue() + File.separator + TestEnvironment.class.getName();

      File dir = new File(path, "batchLibCache");
      if (!dir.isDirectory()) {
         dir.mkdir();
      }
      File jar = File.createTempFile("Batch", ".jar", dir);
      Lib.writeBytesToFile(batchJar, jar);
      return jar.toURI().toURL();
   }

   @Override
   public ICancelTimer setTimerFor(ITimeout listener, int time) {
      return getTimerCtrl().setTimerFor(listener, time);
   }

   @Override
   public void setupOutfileDir(String outfileDir) throws IOException {
      if (Strings.isValid(outfileDir)) {
         outDir = new File(outfileDir);
         if (!outDir.isDirectory()) {
            if (!outDir.mkdirs()) {
               throw new IOException("Failed to create the output directory");
            }
            OseeLog.logf(TestEnvironment.class, Level.INFO,
                  "Outfile Dir [%s] created.", outDir.getAbsolutePath());
         } else {
            OseeLog.logf(TestEnvironment.class, Level.FINE,
                  "Outfile Dir [%s] exists.", outDir.getAbsolutePath());
         }
      } else {
         throw new IOException("A valid outfile directory must be specified.");
      }
   }

   @Override
   public void shutdown() {
      if (isShutdown) {
         return;
      }
      isShutdown = true;
      runtimeManager.uninstall(new OseeLogStatusCallback());
      Activator.getInstance().unregisterTestEnvironment();
      // here we remove all environment tasks (emulators)
      removeAllTasks();
      if (associatedObjects != null) {
         this.associatedObjects.clear();// get rid of all models and support
      }

//      messagingServiceTracker.close();

      OseeLog.log(TestEnvironment.class, Level.FINE, "shutting down environment");
      factory.getTimerControl().cancelTimers();
      stop();
      cleanupClassReferences();
      OseeTestThread.clearThreadReferences();
      for (ITestEnvironmentListener listener : envListeners) {
         try {
            listener.onEnvironmentKilled(this);
         } catch (Exception e) {
            OseeLog.log(TestEnvironment.class, Level.SEVERE, "exception during listener notification", e);
         }
      }
      envListeners.clear();
      if (getRunManager() != null) {
         getRunManager().clearAllListeners();
      }
   }

   protected abstract void loadExternalDrivers();

   public void startup(String outfileDir) throws Exception {
      try {
         setupOutfileDir(outfileDir);
      } catch (IOException ex) {
         throw new Exception("Error in directory setup. " + outfileDir, ex);
      }
      if(myRegistration != null){
         myRegistration.unregister();
      }
      myRegistration = FrameworkUtil.getBundle(getClass()).getBundleContext().registerService(TestEnvironmentInterface.class, this, null);
   }

   protected void stop() {
      try{
         myRegistration.unregister();
      } catch (IllegalStateException ex){
         //ignore if it's already unregistered
      }
   }

   protected void cleanupClassReferences() {
      OseeLog.log(TestEnvironment.class, Level.FINE, "cleanupreferences");

      System.out.println("Associated objects that are getting cleaned up.");
      for (Class<?> clazz : associatedObjects.keySet()) {
         System.out.println(clazz.toString());
      }

      if (associatedObjects != null) {
         associatedObjects.clear();
      }
      OseeLog.log(TestEnvironment.class, Level.FINE, "got the other PM REF");
      if (associatedObjectListeners != null) {
         associatedObjectListeners.clear();
      }
      GCHelper.getGCHelper().printLiveReferences();
   }

   public void setExecutionUnitManagement(IExecutionUnitManagement executionUnitManagement) {
      this.executionUnitManagement = executionUnitManagement;
   }

   @Override
   public File getOutDir() {
      return outDir;
   }

   @Override
   public Remote getControlInterface(String id) {
      return controlInterfaces.get(id);
   }

   @Override
   public void registerControlInterface(String id, Remote controlInterface) {
      controlInterfaces.put(id, controlInterface);
   }

   @Override
   public IServiceConnector getConnector() {
      return connector;
   }

   @Deprecated
   public void setEnvSetup(boolean isEnvSetup) {
      this.isEnvSetup = isEnvSetup;
   }

   @Deprecated
   public void addScriptCompleteListener(IScriptCompleteEvent scriptComplete) {
      this.scriptCompleteListeners.add(scriptComplete);
   }

   @Deprecated
   public void removeScriptCompleteListener(IScriptCompleteEvent scriptComplete) {
      this.scriptCompleteListeners.remove(scriptComplete);
   }

   @Deprecated
   public void addScriptSetupListener(IScriptSetupEvent scriptSetup) {
      this.scriptSetupListeners.add(scriptSetup);
   }

   @Deprecated
   public void removeScriptSetupListener(IScriptSetupEvent scriptSetup) {
      this.scriptSetupListeners.remove(scriptSetup);
   }

   @Deprecated
   protected boolean isEnvSetup() {
      return isEnvSetup;
   }

   @Deprecated
   /**
    * alerts the environment of an exception. The environment will take any necessary actions and alert any interested
    * entities of the problem. Any runing test script will be terminated
    * 
    */
   public void handleException(Throwable t, Level logLevel) {
      handleException(t, "An exception has occurred in the environment", logLevel, true);
   }

   @Deprecated
   /**
    * @param abortScript true will cause the currently running script to abort
    */
   public void handleException(Throwable t, Level logLevel, boolean abortScript) {
      handleException(t, "An exception has occurred in the environment", logLevel, abortScript);
   }

   @Deprecated
   /**
    * alerts the environment of an exception. The environment will take any necessary actions and alert any interested
    * entities of the problem
    * 
    * @param t the exception
    * @param logLevel the severity of the exception. Specifing a Level.OFF will
    * @param abortScript cause the exception to not be logged
    */
   public void handleException(Throwable t, String message, Level logLevel, boolean abortScript) {
      if (logLevel != Level.OFF) {
         OseeLog.log(TestEnvironment.class, logLevel, message, t);
      }
      if (getTestScript() != null && abortScript) {
         getTestScript().abortDueToThrowable(t);
      }
      Iterator<ITestEnvironmentListener> iter = envListeners.iterator();
      while (iter.hasNext()) {
         final ITestEnvironmentListener listener = iter.next();
         listener.onException(message, t);
      }
   }

   @Deprecated
   public void testEnvironmentCommandComplete(ICommandHandle handle) {
      for (ITestEnvironmentListener listener : envListeners) {
         try {
            listener.onTestServerCommandFinished(this, handle);
         } catch (Throwable th) {
            System.out.println(listener.getClass().getName());
            th.printStackTrace();
         }
      }
   }

   @Override
   @Deprecated
   /**
    * marks the script as ready as well as clears any objects that are associated with the environment.
    */
   public synchronized void onScriptSetup() {

      for (IScriptSetupEvent listeners : scriptSetupListeners) {
         listeners.scriptSetup();
      }

      this.associatedObjects.clear();
   }

   @Deprecated
   public void removeQueueListener(ReportDataListener listener) throws RemoteException {
      factory.getReportDataControl().removeQueueListener(listener);
   }

   @Override
   @Deprecated
   public void onScriptComplete() throws InterruptedException {
      factory.getScriptControl().setScriptReady(false);

      for (int i = 0; i < scriptCompleteListeners.size(); i++) {
         try {
            scriptCompleteListeners.get(i).scriptComplete();
         } catch (Exception e) {
            OseeLog.log(TestEnvironment.class, Level.SEVERE, "problem with script complete listener", e);
         }
      }

      // here we remove all environment tasks (emulators)
      if (associatedObjects != null) {
         this.associatedObjects.clear();// get rid of all models and support
      }
   }

   @Override
   @Deprecated
   public void associateObject(Class<?> c, Object obj) {
      associatedObjects.put(c, obj);
      ArrayList<IAssociatedObjectListener> listeners = this.associatedObjectListeners.get(c);
      if (listeners != null) {
         for (int i = 0; i < listeners.size(); i++) {
            try {
               listeners.get(i).updateAssociatedListener();
            } catch (RemoteException e) {
               OseeLog.log(TestEnvironment.class, Level.SEVERE, e.getMessage(), e);
            }

         }
      }
   }

   @Override
   @Deprecated
   public Object getAssociatedObject(Class<?> c) {
      return associatedObjects.get(c);
   }

   @Override
   @Deprecated
   public Set<Class<?>> getAssociatedObjects() {
      return associatedObjects.keySet();
   }

   @Override
   @Deprecated
   /**
    * Use getRunManager().getCurrentScript() instead of this method.
    */
   public TestScript getTestScript() {
      return getRunManager().getCurrentScript();
   }

   @Override
   @Deprecated
   public void abortTestScript() {
      getRunManager().abort();
   }

   @Override
   @Deprecated
   public void abortTestScript(Throwable t) {
      getRunManager().abort(t, false);
   }

//   public void setOteNodeInfo(NodeInfo oteEmbeddedBroker) {
//      this.oteEmbeddedBroker = oteEmbeddedBroker;
//   }
//
//   public NodeInfo getOteNodeInfo() {
//      return oteEmbeddedBroker;
//   }
}
