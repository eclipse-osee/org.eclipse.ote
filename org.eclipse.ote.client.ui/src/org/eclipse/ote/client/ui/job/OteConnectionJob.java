/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.ote.client.ui.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.eclipse.osee.ote.core.BundleInfo;
import org.eclipse.osee.ote.core.OteBundleLocator;
import org.eclipse.osee.ote.core.environment.TestEnvironmentConfig;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.OteUtil;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.service.IOteClientService;
import org.eclipse.ote.client.ui.OteClientServiceTracker;
import org.osgi.framework.Bundle;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Ken J. Aguilar
 * @author Andy Jury
 */
public class OteConnectionJob extends Job {

   private IHostTestEnvironment testHost;
   private IServiceConnector serviceConnector;

   public OteConnectionJob(String jobName, IHostTestEnvironment testHost, long timeout) {
      super(jobName);
      if (testHost == null) {
         throw new IllegalArgumentException("test host cannot be null");
      }
      this.testHost = testHost;
      setUser(true);
   }
   
   public OteConnectionJob(IHostTestEnvironment testHost, long timeout) {
      this("Connecting to OTE test environment", testHost, timeout);
   }
   
   public OteConnectionJob(String jobName, IServiceConnector serviceConnector, long timeout) {
      super(jobName);
      this.serviceConnector = serviceConnector;
      setUser(true);
   }
   
   @Override
   protected IStatus run(IProgressMonitor monitor) {
      Thread.currentThread().setContextClassLoader(new ExportClassLoader());

      monitor.beginTask("Test Environment Connection", 100);
      
      IOteClientService oteClientService;
      try {
         oteClientService = getClientService();
      } catch( InterruptedException ex){
         return Status.CANCEL_STATUS;
      }
     

      Collection<BundleInfo> runtimeLibUrls = new ArrayList<>();
      try {
        getBundles(runtimeLibUrls);
      } catch (Exception ex) {
         return new Status(IStatus.ERROR, "org.eclipse.ote.client", -1, "Exception occurred while trying to locate the runtime libraries", ex);
      }
      try {
         if (oteClientService.isConnected()) {
            /*
             * NOTE **************************** we should make disconnect a blocking method also so that we don't
             * have to do a wait/sleep because that is potentially a problem
             */
            oteClientService.disconnect();
            Thread.sleep(5000);//We need to let things disconnect because this happens in a different thread
         }
         if(testHost == null){
            testHost = (IHostTestEnvironment)serviceConnector.getService();
         }

         ConnectionEvent event = oteClientService.connect(testHost, new OteConfigurer(runtimeLibUrls, testHost.getHttpURL(), serviceConnector), getTestEnvironmentConfig(runtimeLibUrls), monitor);
         if(event == null){
            return new Status(IStatus.ERROR, "org.eclipse.ote.client", "could not establish connection with the test environment");
         }
         return Status.OK_STATUS;
      } catch (Throwable ex) {
         StringBuilder sb = new StringBuilder();
         sb.append("Could not establish connection with the test environment.  You are likely using incompatible versions of an OTE Client and Server.\n\n");
         sb.append(ex.getMessage());
         sb.append("\n");
         Throwable th = ex;
         int count = 0;
         while((th = th.getCause()) != null && count < 5){
            count++;
            sb.append(th.getMessage());
            sb.append("\n");
         }

         return new Status(IStatus.ERROR, "org.eclipse.ote.client", -1, sb.toString(), ex);
      } finally {
         monitor.done();
      }
   }
   
   private IOteClientService getClientService() throws InterruptedException {
      IOteClientService oteClientService;
      OteClientServiceTracker oteClientServiceTracker = null;
      try {
         oteClientServiceTracker = new OteClientServiceTracker();
         oteClientServiceTracker.open();
         oteClientService = (IOteClientService) oteClientServiceTracker.waitForService(5000);
      } finally {
         if(oteClientServiceTracker != null){
            oteClientServiceTracker.close();
         }
      }
      return oteClientService;
   }
   
   @SuppressWarnings({ "rawtypes", "unchecked" })
   private void getBundles(Collection<BundleInfo> runtimeLibUrls) throws IOException, CoreException{
      Bundle bundle = Platform.getBundle("org.eclipse.ote.client.ui");
      ServiceTracker tracker = new ServiceTracker(bundle.getBundleContext(), OteBundleLocator.class.getName(), null);
      tracker.open(true);
      Object[] objs = tracker.getServices();
      if (objs != null && objs.length > 0) {
         for (Object obj : objs) {
            OteBundleLocator locator = (OteBundleLocator) obj;
            runtimeLibUrls.addAll(locator.getRuntimeLibs());
            // Consume any modified libs since the server is about to be sent all libs
            locator.consumeModifiedLibs();
         }
      }
      tracker.close();
   }

   private TestEnvironmentConfig getTestEnvironmentConfig(Collection<BundleInfo> runtimeLibUrls) {
      TestEnvironmentConfig config = null;

      List<String> versions = new ArrayList<>();
      for (BundleInfo libInfo : runtimeLibUrls) {
         String versionStr =
               OteUtil.generateBundleVersionString(
                     libInfo.getManifest().getMainAttributes().getValue("Implementation-Version"), libInfo.getSymbolicName(),
                     libInfo.getVersion(), libInfo.getMd5Digest());
         versions.add(versionStr);
      }
      String[] versionsStrArray = versions.toArray(new String[versions.size()]);
      Arrays.sort(versionsStrArray);
      config = new TestEnvironmentConfig(versionsStrArray);

      return config;
   }


}
