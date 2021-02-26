/*********************************************************************
 * Copyright (c) 2021 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.ote.ci.test_server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.eclipse.osee.ote.HostServerProperties;
import org.eclipse.osee.ote.NonRemoteUserSession;
import org.eclipse.osee.ote.OTETestEnvironmentClient;
import org.eclipse.osee.ote.core.ConnectionRequestResult;
import org.eclipse.osee.ote.core.OSEEPerson1_4;
import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.framework.command.RunTestsKeys;
import org.eclipse.osee.ote.endpoint.OteEndpointUtil;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.ote.ci.test_server.internal.BatchInfo;
import org.eclipse.ote.ci.test_server.internal.MyServiceStatusDataVisitor;
import org.eclipse.ote.ci.test_server.internal.MyStatusListener;
import org.eclipse.ote.ci.test_server.internal.RunTestArguments;
import org.eclipse.ote.ci.test_server.internal.results.TestOuputFolderResulter;
import org.eclipse.ote.services.core.BundleUtility;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

/**
 * @author Andy Jury
 */
public class CiTestRunner {

   private IHostTestEnvironment env;
   private EventAdmin eventAdmin;
   private OteUdpEndpoint endpointService;
   private RunTestArguments args;
   private UUID userSessionId = UUID.randomUUID();
   private boolean forceShutdown = false;
   private ReentrantLock lock;
   private Condition condition;

   /**
    * OSGI
    */
   public void start() {
      args = new RunTestArguments(Platform.getCommandLineArgs());
      new Thread(new RunTests()).start();
      new Thread(new Shutdown()).start();
   }

   /**
    * OSGI
    */
   public void stop() {
      // Intentionally empty
   }

   /**
    * OSGI
    */
   public void bindIHostTestEnvironment(IHostTestEnvironment env) {
      this.env = env;
   }

   /**
    * OSGI
    */
   public void unbindIHostTestEnvironment(IHostTestEnvironment env) {
      this.env = null;
   }

   private class Shutdown implements Runnable {

      @Override
      public void run() {
         Double hoursToRun = args.getHoursToRun();
         if (hoursToRun != null && hoursToRun > 0.0) {
            System.out.format("\n\nStarting shutdown thread for %.1f hours\n\n\n", hoursToRun);
            Double sleep = hoursToRun * 60.0 * 60.0 * 1000.0;
            try {
               Thread.sleep(sleep.longValue());
            } catch (InterruptedException ex) {
               return;
            }
            System.out.println("\n\nTimer expired, shutting down!\n\n");
            forceShutdown = true;
            lock.lock();
            try {
               System.out.println("Signalling run thread.");
               condition.signalAll();
            } finally {
               lock.unlock();
            }
         }
      }

   }

   private class RunTests implements Runnable {

      @Override
      public void run() {

         acquireServices();
         try {
            if (checkInitialConditions(args)) {
               EnhancedProperties properties = env.getProperties();
               if (properties != null) {
                  String endpointURI = properties.getProperty(HostServerProperties.oteUdpEndpoint.name()).toString();
                  Boolean isSim =
                     Boolean.parseBoolean(properties.getProperty(HostServerProperties.isSim.name()).toString());
                  InetSocketAddress destinationAddress = OteEndpointUtil.getAddress(endpointURI);
                  OTETestEnvironmentClient client = new OTETestEnvironmentClient(endpointService, destinationAddress);

                  lock = new ReentrantLock();
                  condition = lock.newCondition();
                  MyServiceStatusDataVisitor visitor =
                     new MyServiceStatusDataVisitor(client, new URI(endpointURI), lock, condition);
                  ServiceRegistration<EventHandler> statusEventRegistration = registerForStatusBoardData(visitor);

                  BatchInfo batchInfo = new BatchInfo(args);
                  UUID cmdId = UUID.randomUUID();

                  List<String> tests = batchInfo.getTests();
                  PropertyStore globalProperties = new PropertyStore("global");
                  List<IPropertyStore> scriptProperties = new ArrayList<IPropertyStore>();

                  loadPropertyStores(globalProperties, scriptProperties, args, cmdId, userSessionId, "", tests, isSim);

                  if (Conditions.hasValues(scriptProperties)) {
                     NonRemoteUserSession nonRemoteUserSession = new NonRemoteUserSession(
                        new OSEEPerson1_4(System.getProperty("user.name"), "unknown", userSessionId.toString()),
                        "localhost");

                     startAllBundles();
                     ConnectionRequestResult requestEnvironment =
                        env.requestEnvironment(nonRemoteUserSession, userSessionId, null);
                     if (requestEnvironment != null) {
                        visitor.reset();
                        client.setBatchMode(true);
                        client.runScripts(userSessionId, globalProperties, scriptProperties, false, null, null, "1",
                           null);
                        lock.lock();
                        try {
                           while (!(visitor.isCommandComplete() || forceShutdown)) {
                              condition.await(10, TimeUnit.MINUTES);
                              System.out.println("waiting ");
                           }
                        } finally {
                           lock.unlock();
                        }
                     } else {
                        System.out.println("failed to communicate with test environment");
                     }
                  } else {
                     System.out.println("\nNO SCRIPT TO RUN!!!");
                     args.printUsage();
                  }

                  client.disconnect(userSessionId);
                  statusEventRegistration.unregister();
                  new TestOuputFolderResulter().processOutput(args.getOutfileDirectory());

                  System.out.println("\n\nShutting down the server\n");
                  client.shutdownServer((String) properties.getProperty("id", "unknown"));
               }
            }
         } catch (Throwable th) {
            th.printStackTrace();
         }

         // jic
         try {
            Thread.sleep(5000);// give the shutdown command a chance
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
         System.out.println("Forced Shutdown");
         System.exit(0);
      }

      private void startAllBundles() {
         Bundle[] bundles =
            FrameworkUtil.getBundle(CiTestRunner.class).getBundleContext().getBundles();
         for (Bundle bundle : bundles) {
            if (bundle.getState() != Bundle.ACTIVE && !BundleUtility.isBundleFragment(bundle)) {
               try {
                  bundle.start();
               } catch (Throwable th) {
                  th.printStackTrace();
               }
            }
         }
      }
   }

   private ServiceRegistration<EventHandler> registerForStatusBoardData(MyServiceStatusDataVisitor visitor) {
      MyStatusListener statusListener = new MyStatusListener(visitor);
      Hashtable<String, Object> properties = new Hashtable<String, Object>();
      properties.put("event.topics", "ote/status/*");
      return ServiceUtility.getContext().registerService(EventHandler.class, statusListener, properties);
   }

   private void acquireServices() {
      eventAdmin = ServiceUtility.getService(EventAdmin.class, 10000);
      endpointService = ServiceUtility.getService(OteUdpEndpoint.class, 10000);
   }

   private boolean checkInitialConditions(RunTestArguments args) throws Exception {
      StringBuilder sb = new StringBuilder();
      if (!args.isArgsValid()) {
         sb.append("Invalid Program Args\n");
      }
      if (eventAdmin == null) {
         sb.append("Unable to find EventAdmin service");
      }
      if (sb.length() > 0) {
         System.out.println(sb.toString());
         return false;
      }
      return true;
   }

   private void loadPropertyStores(IPropertyStore globalProperties, List<IPropertyStore> scriptData, RunTestArguments args, UUID cmdId, UUID sessionId, String classpath, List<String> tests, Boolean isSim) throws IOException {
      File propertiesFile = new File(args.getPropertiesFile());
      if (propertiesFile.exists()) {
         System.out.println("Loading properties from: " + propertiesFile.getAbsolutePath());
         FileInputStream is = new FileInputStream(propertiesFile);
         try {
            Properties p = new Properties();
            p.load(is);
            for (Entry<Object, Object> entry : p.entrySet()) {
               String key = entry.getKey().toString();
               String value = entry.getValue().toString();
               if (key.equals("DemoWitnesses") || key.equals("DemoExecutedBy")) {
                  globalProperties.put(key, value.split(","));
               } else {
                  globalProperties.put(key, value);
               }
            }
            System.out.println("properties: " + globalProperties);
         } catch (Exception ex) {
            ex.printStackTrace();
         } finally {
            is.close();
         }
      }

      globalProperties.put(RunTestsKeys.classpath.name(), new String[] {classpath});
      globalProperties.put(RunTestsKeys.batchmode.name(), true);
      if (args.isAbortOnFirstFail()) {
         globalProperties.put(RunTestsKeys.batchFailAbortMode.name(), true);
      }

      for (String test : tests) {
         boolean validTest = false;
         try {
            Class<?> clazz = ExportClassLoader.getInstance().loadClass(test);
            validTest = TestScript.class.isAssignableFrom(clazz);
            if (!validTest) {
               OseeLog.logf(getClass(), Level.WARNING, "%s is not an instance of TestScript so will not be run", test);
            }
         } catch (Throwable th) {
            OseeLog.logf(getClass(), Level.SEVERE, th,
               "%s was not found so will not be run.  If the class looks valid make sure the parent bundle is exporting the package in its manifest.",
               test);
         }
         if (validTest) {
            IPropertyStore store = new PropertyStore("batch run thing");
            scriptData.add(store);
            store.put(RunTestsKeys.testClass.name(), test);

            File clientOutfile = getClientOutfile(args.getOutfileDirectory().getAbsolutePath(), test);
            store.put(RunTestsKeys.clientOutfilePath.name(), clientOutfile.getAbsolutePath());
            store.put(RunTestsKeys.serverOutfilePath.name(), clientOutfile.getAbsolutePath());
            store.put(RunTestsKeys.executablePath.name(), "$$$ DEFAULT");
         }
      }
   }

   private File getClientOutfile(String outfileFolder, String test) {
      File outfile;
      int count = 0;
      do {
         outfile = new File(String.format("%s%s%s.%02d%s", outfileFolder, File.separator, test, count++, ".tmo"));
      } while (outfile.exists());
      return outfile;
   }
}
