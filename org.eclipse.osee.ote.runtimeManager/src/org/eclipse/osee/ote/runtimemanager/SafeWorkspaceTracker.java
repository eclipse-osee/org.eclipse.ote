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
package org.eclipse.osee.ote.runtimemanager;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.workspace.SafeWorkspaceAccess;
import org.eclipse.osee.framework.ui.workspacebundleloader.JarChangeResourceListener;
import org.eclipse.osee.ote.core.BundleInfo;
import org.eclipse.osee.ote.core.OteBundleLocator;
import org.eclipse.osee.ote.runtimemanager.container.OteClasspathContainer;
import org.eclipse.osee.ote.runtimemanager.internal.ProjectChangeResourceListener;
import org.eclipse.osee.ote.runtimemanager.internal.RuntimeBundleServer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Robert A. Fisher
 * @author Andrew M. Finkbeiner
 */
public class SafeWorkspaceTracker extends ServiceTracker implements OteBundleLocator {

   private JarChangeResourceListener<OteSystemLibsNature> systemLibResourceListener;
   private JarChangeResourceListener<OteSystemLibsNature> precompiledResourceListener;
   private JarChangeResourceListener<OteUserLibsNature> userLibResourceListener;
   private LibJarListener<OteSystemLibsNature> systemLibListener;
   private PrecompiledListener precompiledListener;
   private LibJarListener<OteUserLibsNature> userLibListener;
   private ProjectChangeResourceListener projectChangeResourceListener;
   private RuntimeBundleServer bundleServer;
   private SafeWorkspaceAccess service;
   private final BundleContext context;

   public SafeWorkspaceTracker(BundleContext context) {
      super(context, SafeWorkspaceAccess.class.getName(), null);
      this.context = context;
   }

   @Override
   public Object addingService(ServiceReference reference) {
      this.systemLibListener = new LibJarListener<>();
      this.userLibListener = new LibJarListener<>();
      this.precompiledListener = new PrecompiledListener();
      this.systemLibResourceListener =
         new JarChangeResourceListener<OteSystemLibsNature>(OteSystemLibsNature.NATURE_ID, systemLibListener);
      this.userLibResourceListener =
         new JarChangeResourceListener<OteUserLibsNature>(OteUserLibsNature.NATURE_ID, userLibListener);
      this.precompiledResourceListener = new JarChangeResourceListener<>(OteSystemLibsNature.NATURE_ID, precompiledListener);
      this.projectChangeResourceListener = new ProjectChangeResourceListener();
      service = (SafeWorkspaceAccess) context.getService(reference);
      slowLoadingJars();
      precompiledListener.runCheckInThread();

      return super.addingService(reference);
   }

   private void slowLoadingJars() {
      Jobs.runInJob(new LocateWorkspaceBundles("Locating Workspace Bundles", RuntimeManager.BUNDLE_ID), false);

   }

   private class LocateWorkspaceBundles extends AbstractOperation {
      public LocateWorkspaceBundles(String operationName, String pluginId) {
         super(operationName, pluginId);
      }

      @SuppressWarnings("rawtypes")
      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         IWorkspace workspace = service.getWorkspace();
         try {
            scrapeAllLibs();
         } catch (CoreException ex) {
            OseeLog.log(RuntimeManager.class, Level.SEVERE, ex);
         }
         workspace.addResourceChangeListener(systemLibResourceListener);
         workspace.addResourceChangeListener(userLibResourceListener);
         workspace.addResourceChangeListener(precompiledResourceListener);

         SafeWorkspaceTracker.this.bundleServer = new RuntimeBundleServer(SafeWorkspaceTracker.this);

         context.registerService(OteBundleLocator.class.getName(), SafeWorkspaceTracker.this, new Hashtable());
         OteClasspathContainer.refreshAll();
      }
   }

   private void scrapeAllLibs() throws CoreException {
      for (OteSystemLibsNature nature : OteSystemLibsNature.getWorkspaceProjects()) {
         for (URL url : nature.getBundles()) {
            systemLibListener.handleBundleAdded(url);
         }

         projectChangeResourceListener.addProject(nature.getProject());
      }
      for (OteUserLibsNature nature : OteUserLibsNature.getWorkspaceProjects()) {
         for (URL url : nature.getBundles()) {
            userLibListener.handleBundleAdded(url);
         }

         projectChangeResourceListener.addProject(nature.getProject());
      }
   }

   @Override
   public synchronized void close() {
      IWorkspace workspace = service.getWorkspace();
      if (workspace != null) {
         workspace.removeResourceChangeListener(systemLibResourceListener);
         workspace.removeResourceChangeListener(userLibResourceListener);
         workspace.removeResourceChangeListener(precompiledResourceListener);
      }
      super.close();
   }

   /**
    * Returns a list of URL's to workspace jars to be used for the test server. The collection returned is a combination
    * of all the user libraries and any system libraries that weren't already supplied in the user libraries. The
    * workspace is considered to have runtime libraries only if there are system libraries present. Subsequently, if no
    * system libraries are in the workspace then this method will return an empty collection.
    * 
    * @return runtime library bundle infos
    */
   @Override
   public Collection<BundleInfo> getRuntimeLibs() throws IOException, CoreException {
      Collection<URL> userLibUrls = getUserLibUrls();
      Collection<URL> systemLibUrls = getSystemLibUrls();
      // If there are no system libs, then claim no runtime libs
      if (!systemLibUrls.isEmpty()) {
         return getRuntimeLibs(systemLibUrls, userLibUrls);
      } else {
         return Collections.emptyList();
      }
   }

   private Collection<BundleInfo> getRuntimeLibs(Collection<URL> systemLibUrls, Collection<URL> userLibUrls) throws IOException {
      Map<String, BundleInfo> runtimeMap = new HashMap<>();
      Collection<BundleInfo> runtimeInfos = new LinkedList<>();

      // First add all of the system libraries to the map
      for (URL url : systemLibUrls) {
         String symbolicName = getBundleNameFromJar(url);

         runtimeMap.put(symbolicName, new BundleInfo(url, bundleServer.getClassServerPath(), true));
      }

      // Now add the user libraries so any system library with the same name
      // gets replaced
      for (URL url : userLibUrls) {
         String symbolicName = getBundleNameFromJar(url);

         runtimeMap.put(symbolicName, new BundleInfo(url, bundleServer.getClassServerPath(), false));
      }

      runtimeInfos.addAll(runtimeMap.values());

      return runtimeInfos;
   }

   /**
    * Returns a list of URL's to all system libraries in the workspace regardless of ones that are supplied in user
    * libraries.
    * 
    * @return system library URL's
    */
   public Collection<URL> getSystemLibUrls() throws CoreException {
      Collection<URL> libs = new LinkedList<>();
      for (OteSystemLibsNature systemNature : OteSystemLibsNature.getWorkspaceProjects()) {
         libs.addAll(systemNature.getBundles());
      }

      return libs;
   }

   /**
    * Returns a list of URL's to all user libraries in the workspace.
    * 
    * @return user library URL's
    */
   public Collection<URL> getUserLibUrls() throws CoreException {
      Collection<URL> libs = new LinkedList<>();
      for (OteUserLibsNature userNature : OteUserLibsNature.getWorkspaceProjects()) {
         libs.addAll(userNature.getBundles());
      }

      return libs;
   }

   private String getBundleNameFromJar(URL url) throws IOException {
      File file;
      try {
         file = new File(url.toURI());
      } catch (URISyntaxException ex) {
         file = new File(url.getPath());
      }

      JarFile jarFile = new JarFile(file);
      Manifest jarManifest = jarFile.getManifest();
      return BundleInfo.generateBundleName(jarManifest);
   }

   /**
    * Returns a list of all bundles that have been modified since the last time this was called.
    */
   @Override
   public Collection<BundleInfo> consumeModifiedLibs() throws IOException {
      //		Collection<BundleInfo> modifiedLibs = new LinkedList<>();

      Set<URL> sysNewBundles = systemLibListener.consumeNewBundles();
      Set<URL> sysChangedBundles = systemLibListener.consumeChangedBundles();
      //		Set<URL> sysRemovedBundles =
      systemLibListener.consumeRemovedBundles();

      Set<URL> userNewBundles = userLibListener.consumeNewBundles();
      Set<URL> userChangedBundles = userLibListener.consumeChangedBundles();
      //		Set<URL> userRemovedBundles =
      userLibListener.consumeRemovedBundles();

      Collection<URL> sysNewModLibs = new ArrayList<>(sysNewBundles.size() + sysChangedBundles.size());
      sysNewModLibs.addAll(sysNewBundles);
      sysNewModLibs.addAll(sysChangedBundles);

      Collection<URL> userNewModLibs = new ArrayList<>(userNewBundles.size() + userChangedBundles.size());
      userNewModLibs.addAll(userNewBundles);
      userNewModLibs.addAll(userChangedBundles);

      // TODO what about removed libs?
      return getRuntimeLibs(sysNewModLibs, userNewModLibs);

      // // For now, return all user libs
      // for (BundleInfo info : getRuntimeLibs()) {
      // if (!info.isSystemLibrary()) {
      // modifiedLibs.add(info);
      // }
      // }
      //
      // return modifiedLibs;
   }
}
