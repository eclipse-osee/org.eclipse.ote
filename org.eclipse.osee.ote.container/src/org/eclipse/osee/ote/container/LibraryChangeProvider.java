/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ote.container;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.ui.workspacebundleloader.JarCollectionNature;

public class LibraryChangeProvider<T extends JarCollectionNature> {

   private final List<IUserLibListener> listeners;

   public LibraryChangeProvider() {
      listeners = new ArrayList<>();
   }

   public void addListener(IUserLibListener listener) {
      if (!listeners.contains(listener)) {
         listeners.add(listener);
      }
   }

   public void handleBundleAdded(URL url) {
      resetClasspath();
   }

   private void resetClasspath() {
      for (IUserLibListener listener : listeners) {
         listener.libraryChanged();
         //      try {
         //         this.oteClasspathContainer.javaProject.getPerProjectInfo().resetResolvedClasspath();
         //      }
         //      catch (JavaModelException ex) {
         //         ex.printStackTrace();
         //      }
      }
   }

   public void handleBundleChanged(URL url) {
   }

   public void handleBundleRemoved(URL url) {
      resetClasspath();
   }

   public void handleNatureClosed(T nature) {
      resetClasspath();
   }

   public void handlePostChange() {
   }

}