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
package org.eclipse.ote.client.ui.job;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.ConfigurationStatusCallback;
import org.eclipse.osee.ote.OTETestEnvironmentClient;
import org.eclipse.osee.ote.core.BundleInfo;
import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.endpoint.OteEndpointUtil;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.service.IEnvironmentConfigurer;
import org.eclipse.swt.widgets.Display;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class OteConfigurer implements IEnvironmentConfigurer {

   private final Collection<BundleInfo> runtimeLibUrls;

   public OteConfigurer(Collection<BundleInfo> runtimeLibUrls, String oteHttpServer, IServiceConnector serviceConnector) {
      super();
      this.runtimeLibUrls = runtimeLibUrls;
   }

   @Override
   public boolean configure(IHostTestEnvironment event, SubProgressMonitor monitor) throws Exception {
      return newBundleLoadingMechanism(event, monitor);
   }

   private boolean newBundleLoadingMechanism(IHostTestEnvironment env, SubProgressMonitor monitor) throws RemoteException {
      return handleFreshConnection(env, monitor);
   }

   private boolean handleFreshConnection(IHostTestEnvironment env, final SubProgressMonitor monitor) throws RemoteException {
      List<File> bundlesToSend = new ArrayList<>();
      for (BundleInfo libInfo : runtimeLibUrls) {
         bundlesToSend.add(libInfo.getFile());
      }
      EnhancedProperties properties = env.getProperties();
      String endpoint = (String) properties.getProperty("oteUdpEndpoint");
      if (endpoint == null) {
         OseeLog.log(getClass(), Level.SEVERE, "Failed to configure server");
      } else {
         OTETestEnvironmentClient client = new OTETestEnvironmentClient(ServiceUtility.getService(OteUdpEndpoint.class),
            OteEndpointUtil.getAddress(endpoint));
         ConnectionCallback callback = new ConnectionCallback();

         client.configureEnvironment((String) properties.getProperty("id", "unknown"), bundlesToSend, true, monitor,
            callback);
         return callback.shouldContinue();
      }
      return false;
   }

   private static class ConnectionCallback implements ConfigurationStatusCallback {
      private boolean shouldContinue;

      @Override
      public void success() {
         shouldContinue = true;
      }

      @Override
      public void failure(String errorLog) {
         ContinueConnection continueConnect = new ContinueConnection(errorLog);
         Display.getDefault().syncExec(continueConnect);
         shouldContinue = continueConnect.proceed();
      }

      public boolean shouldContinue() {
         return shouldContinue;
      }

   }

   private static class ContinueConnection implements Runnable {
      private boolean proceed = false;
      private String errorLog;

      public ContinueConnection(String errorLog) {
         this.errorLog = errorLog;
      }

      @Override
      public void run() {
         proceed = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), "Connection Warning",
            String.format("OTE Server already configured, connect anyways?\n\n%s", errorLog));
      }

      public boolean proceed() {
         return proceed;
      }
   }
}
