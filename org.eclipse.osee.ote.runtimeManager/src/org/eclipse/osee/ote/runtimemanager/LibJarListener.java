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
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.eclipse.core.resources.IProject;
import org.eclipse.osee.framework.ui.workspacebundleloader.FileChangeDetector;
import org.eclipse.osee.framework.ui.workspacebundleloader.IJarChangeListener;
import org.eclipse.osee.framework.ui.workspacebundleloader.JarCollectionNature;
import org.eclipse.osee.ote.core.BundleInfo;
import org.eclipse.osee.ote.runtimemanager.container.OteClasspathContainer;

/**
 * @author Robert A. Fisher
 */
public class LibJarListener<T extends JarCollectionNature> implements IJarChangeListener<T> {

   private static final boolean VERBOSE_DEBUG = true;

   private final FileChangeDetector detector = new FileChangeDetector();

   private final Object bundleSynchronizer;
   private final Set<URL> newBundles;
   private final Set<URL> changedBundles;
   private final Set<URL> removedBundles;

   public LibJarListener() {
      this.bundleSynchronizer = new Object();
      this.newBundles = new HashSet<>();
      this.changedBundles = new HashSet<>();
      this.removedBundles = new HashSet<>();
   }

   @Override
   public void handleBundleAdded(URL url) {
      synchronized (bundleSynchronizer) {
         if (detector.isChanged(url)) {
            newBundles.add(url);
            changedBundles.remove(url);
            removedBundles.remove(url);
            debugEcho(url);
         }
      }
   }

   @Override
   public void handleBundleChanged(URL url) {
      synchronized (bundleSynchronizer) {
         if (detector.isChanged(url)) {
            changedBundles.add(url);
            newBundles.remove(url);
            removedBundles.remove(url);
            debugEcho(url);
         }
      }
   }

   @Override
   public void handleBundleRemoved(URL url) {
      synchronized (bundleSynchronizer) {
         detector.remove(url);
         removedBundles.add(url);
         newBundles.remove(url);
         changedBundles.remove(url);
      }
      debugEcho(url);
   }

   private void debugEcho(URL url) {
      if (VERBOSE_DEBUG) {
         try {
            String bundleName = getBundleNameFromJar(url);
            System.out.println("Bundle changed:" + bundleName);
         } catch (IOException ex) {
         }
      }
   }

   @Override
   public void handleNatureClosed(T nature) {
      IProject project = nature.getProject();
      System.out.println("Project closed: " + project.getName());
      for (URL url : nature.getBundles()) {
         handleBundleRemoved(url);
      }

      nature.setClosing(true);
      updateContainers();
      nature.setClosing(false);
   }

   private void updateContainers() {
      OteClasspathContainer.refreshAll();
   }

   @Override
   public void handlePostChange() {
      updateContainers();
      System.out.println("Bunch of changes just finished");
   }

   private <S extends Object> Set<S> duplicateAndClear(Set<S> set) {
      synchronized (bundleSynchronizer) {
         Set<S> returnBundles = new HashSet<>(set);
         set.clear();
         return returnBundles;
      }
   }

   /**
    * @return the newBundles
    */
   public Set<URL> consumeNewBundles() {
      return duplicateAndClear(newBundles);
   }

   /**
    * @return the changedBundles
    */
   public Set<URL> consumeChangedBundles() {
      return duplicateAndClear(changedBundles);
   }

   /**
    * @return the removedBundles
    */
   public Set<URL> consumeRemovedBundles() {
      return duplicateAndClear(removedBundles);
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

}