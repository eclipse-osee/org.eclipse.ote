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
package org.eclipse.osee.ote.ui.markers;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class MarkerPlugin implements BundleActivator {

   private static FileWatchList filesToWatch;
   public static final String PLUGIN_ID = "org.eclipse.osee.ote.ui.markers";

   @Override
   public void start(BundleContext context) throws Exception {
      filesToWatch = new FileWatchList();
      ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener() {

         @Override
         public void resourceChanged(final IResourceChangeEvent event) {
            IResourceDelta delta = event.getDelta();
            try {
               delta.accept(new IResourceDeltaVisitor() {
                  @Override
                  public boolean visit(IResourceDelta delta) throws CoreException {
                     IPath path = delta.getFullPath();
                     String extension = path.getFileExtension();
                     if (extension != null) {
                        if ("tmo".equals(extension)) {
                           IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
                           // file.refreshLocal(depth, monitor);
                           if (file != null) {
                              switch (delta.getKind()) {
                                 case IResourceDelta.REMOVED:
                                    removeMarkers(file);
                                    break;
                                 default:
                                    // do nothing
                              }
                           }
                        }
                     }
                     if (delta.getAffectedChildren().length > 0) {
                        return true;
                     }
                     return false;
                  }
               });
            } catch (CoreException ex) {
               OseeLog.log(MarkerPlugin.class, Level.SEVERE, ex);
            }
         }

      }, IResourceChangeEvent.POST_CHANGE);
   }

   @Override
   public void stop(BundleContext context) throws Exception {
   }

   public static void addMarkers(IFile file) {
      removeMarkers(file);
      Jobs.runInJob("OTE Marker Processing", new ProcessOutfileSax(file), MarkerPlugin.class, MarkerPlugin.PLUGIN_ID,
         false);
   }

   public static void removeMarkers(IFile file) {
      List<IMarker> markers = filesToWatch.get(file);
      if (markers != null) {
         for (IMarker marker : markers) {
            try {
               marker.delete();
            } catch (CoreException ex) {
            }
         }
      }
      findAndRemoveOteMarkers(file);
   }

   static synchronized void updateMarkerInfo(IFile file, List<IMarker> markers) {
      filesToWatch.put(file, markers);
   }

   public static void findAndRemoveOteMarkers(IResource resource) {
      try {
         if (resource == null || !resource.exists()) {
            return;
         }
         IMarker[] markersToRemove =
            resource.findMarkers("org.eclipse.osee.ote.ui.output.errorMarker", false, IResource.DEPTH_INFINITE);
         for (IMarker localMarker : markersToRemove) {
            localMarker.delete();
         }
      } catch (CoreException e) {
         OseeLog.log(MarkerPlugin.class, Level.SEVERE, e);
      }
   }
}
