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
package org.eclipse.osee.ote.ui.test.manager.util;

import java.io.File;
import java.net.BindException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.CorePreferences;
import org.eclipse.osee.framework.ui.ws.AWorkspace;
import org.eclipse.osee.ote.classserver.ClassServer;
import org.eclipse.osee.ote.classserver.PathResourceFinder;
import org.eclipse.osee.ote.runtimemanager.UserLibResourceFinder;

public class ClassServerInst {
   private ClassServer classServer;
   private String classServerPath;
   private PathResourceFinder pathResourceFinder;

   private static ClassServerInst instance = null;

   public static ClassServerInst getInstance() {
      if (instance == null) {
         instance = new ClassServerInst();
      }
      return instance;
   }

   /**
    * Creates a new ClassServer which will serve all projects currently in the workspace
    */
   private ClassServerInst() {
      try {
         InetAddress useHostAddress = CorePreferences.getDefaultInetAddress();
         classServer = new ClassServer(0, useHostAddress)//;
            {
               @Override
               protected void fileDownloaded(String fp, InetAddress addr) {
                  System.out.println("ClassServerInst: File " + fp + " downloaded to " + addr);
               }
            };
         pathResourceFinder = new PathResourceFinder(new String[] {}, false);
         classServer.addResourceFinder(new UserLibResourceFinder());
         classServer.addResourceFinder(new OTEBuilderResourceFinder());
         classServer.addResourceFinder(pathResourceFinder);
         classServer.start();

         classServerPath = classServer.getHostName().toString();// "http://" + useHostAddress.getHostAddress() + ":" + classServer.getPort() + "/";

         Job job = new Job("Populating TM classserver with projects.") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               try {
                  addAnyNewProjects();
               } catch (Throwable th) {
                  OseeLog.log(ClassServerInst.class, Level.SEVERE, th.getMessage(), th);
               }
               return Status.OK_STATUS;
            }

         };
         job.schedule();
      } catch (BindException ex) {
         OseeLog.log(
            ClassServerInst.class,
            Level.SEVERE,
            "Class Server not started.  Likely the IP address used is not local.  Set your IP address in the advanced page.",
            ex);
      } catch (Exception ex) {
         OseeLog.log(ClassServerInst.class, Level.SEVERE, "Class Server not started.", ex);
      }
   }

   /**
    * Adds any newly created or checked out projects in the workspace to the ClassServer.
    */
   public void addAnyNewProjects() {
      // the ClassServer maintains a list and checks that any passed in projects are not already in
      // its list before adding new ones, so it is safe to simply pass the entire list of projects
      pathResourceFinder.addPaths(getAllProjects());
   }

   /**
    * @return the path to the class server, to be passed to the environment upon connection
    */
   public String getClassServerPath() {
      return classServerPath;
   }

   /**
    * Stops the class server. This should be called upon termination of the testManager
    */
   public void stopServer() {
      classServer.terminate();
   }

   private String[] getAllProjects() {
      ArrayList<String> list = new ArrayList<>();

      IProject[] projects = AWorkspace.getProjects();
      for (IProject project : projects) {
         // If the project start with a '.', (i.e. a hidden project) do not include it in the class
         // server
         // This will keep .osee.data and others from being served
         if (!project.isOpen()) {
            continue;
         }

         IProjectDescription description;
         try {
            description = project.getDescription();
            if (!project.getName().startsWith(".") && description.hasNature("org.eclipse.jdt.core.javanature")) {
               List<File> fileList = getJavaProjectProjectDependancies(JavaCore.create(project));
               for (File file : fileList) {
                  list.add(file.getAbsolutePath());
               }
            }
         } catch (CoreException ex) {
            ex.printStackTrace();
         }
      }

      return list.toArray(new String[list.size()]);
   }
   
   /* 
    * START Code Duplicated from AJavaProject because of release dependencies
    */   
   private final Map<IJavaProject, IClasspathEntry[]> cachedPath =
         new HashMap<IJavaProject, IClasspathEntry[]>();
   
   private IClasspathEntry[] localGetResolvedClasspath(IJavaProject javaProject) throws JavaModelException {
      IClasspathEntry[] paths = cachedPath.get(javaProject);
      if (paths == null) {
         paths = javaProject.getResolvedClasspath(true);
         cachedPath.put(javaProject, paths);
      }
      return paths;
   }
   
   private ArrayList<File> getJavaProjectProjectDependancies(IJavaProject javaProject) {
      ArrayList<File> urls = new ArrayList<File>();
      try {
         IClasspathEntry[] paths = localGetResolvedClasspath(javaProject);
         for (int i = 0; i < paths.length; i++) {
            if (paths[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
               if (paths[i].getPath().toFile().exists()) {
                  //          urls.add(paths[i].getPath().toFile());
               } else {
                  File file = null;
                  file = new File(AWorkspace.getWorkspacePath().concat(paths[i].getPath().toOSString()));
                  if (file.exists()) {
                     urls.add(file);
                  }
               }
            } else if (paths[i].getEntryKind() == IClasspathEntry.CPE_PROJECT) {
               urls.add(new File(AWorkspace.getWorkspacePath().concat(
                  paths[i].getPath().toFile().getPath().concat(File.separator + "bin" + File.separator))));
            } else if (paths[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
               File projectlocation = javaProject.getProject().getLocation().toFile();
               File projecttricky = javaProject.getProject().getFullPath().toFile();
               IPath output = paths[i].getOutputLocation();
               File fileLocation;
               if (output == null) {
                  fileLocation = javaProject.getOutputLocation().toFile();
               } else {
                  fileLocation = paths[i].getOutputLocation().toFile();
               }
               String realLocation =
                  fileLocation.toString().replace(projecttricky.toString(), projectlocation.toString());
               urls.add(new File(realLocation));
            }
         }

      } catch (JavaModelException ex) {
         ex.printStackTrace();
      }
      return urls;
   }
   /* 
    * STOP Code Duplicated from AJavaProject because of release dependencies
    */
}
