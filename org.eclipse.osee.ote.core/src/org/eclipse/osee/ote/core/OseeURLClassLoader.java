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

package org.eclipse.osee.ote.core;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;

/**
 * @author Andrew M. Finkbeiner
 */
public class OseeURLClassLoader extends URLClassLoader {

   private static final int RETRY_MAX = 10;
   private final String name;
   private final ExportClassLoader exportClassLoader;

   public OseeURLClassLoader(String name, URL[] urls, ClassLoader parent) {
      super(urls, parent);
      this.name = name;
      GCHelper.getGCHelper().addRefWatch(this);
      exportClassLoader = ExportClassLoader.getInstance();

   }

   public OseeURLClassLoader(String name, URL[] urls) {
      super(urls);
      GCHelper.getGCHelper().addRefWatch(this);
      this.name = name;
      exportClassLoader = ExportClassLoader.getInstance();
   }

   public OseeURLClassLoader(String name, URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
      super(urls, parent, factory);
      GCHelper.getGCHelper().addRefWatch(this);
      this.name = name;
      exportClassLoader = ExportClassLoader.getInstance();
   }

   @Override
   public Class<?> loadClass(String clazz) throws ClassNotFoundException {
      try {
         return exportClassLoader.loadClass(clazz);
      } catch (Exception ex2) {
         int timesTriedToLoad = 0;
         while (timesTriedToLoad < RETRY_MAX) {
            try {
               Class<?> loadClass = super.loadClass(clazz);
               return loadClass;
            } catch (ClassNotFoundException ex) {
               System.out.println("Retrying to load from OseeURLClassLoader for class = " + clazz);
               timesTriedToLoad++; //Try to load again
               try {
                  Thread.sleep(1);
               } catch (InterruptedException ex1) {
                  OseeLog.log(OseeURLClassLoader.class, Level.SEVERE, ex1.toString(), ex1);
               }
            }
         }
         throw new ClassNotFoundException("Class = " + clazz);
      }
   }

   /**
    * Tries to use the export class loader first then when that fails will use the URL listing to find and download the
    * resource stream.
    */
   @Override
   public InputStream getResourceAsStream(String name) {
      InputStream resourceAsStream;
      try {
         resourceAsStream = exportClassLoader.getResourceAsStream(name);
      } catch (Exception ex2) {
         resourceAsStream = null;
      }
      if (resourceAsStream == null) {
         URL findResource = super.findResource(name);
         System.out.println(findResource);
         int timesTriedToLoad = 0;
         while (timesTriedToLoad < RETRY_MAX) {
            InputStream stream = super.getResourceAsStream(name);
            if (stream != null) {
               return stream;
            }
            timesTriedToLoad++; //Try to load again
            System.out.printf("Retry %d of %d to load from OseeURLClassLoader for resource = %s\n", timesTriedToLoad,
               RETRY_MAX, name);
            try {
               Thread.sleep(1);
            } catch (InterruptedException ex1) {
               OseeLog.log(OseeURLClassLoader.class, Level.SEVERE, ex1.toString(), ex1);
            }
         }
      }
      return null;
   }

   @Override
   public String toString() {
      return this.getClass().getName() + " [ " + name + " ] ";
   }
}
