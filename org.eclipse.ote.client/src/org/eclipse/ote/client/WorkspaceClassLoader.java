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

package org.eclipse.ote.client;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author Andrew M. Finkbeiner
 */
public class WorkspaceClassLoader extends ClassLoader {
   private final String[] jarFiles;
   private byte[] store = new byte[64000];
   private final HashMap<String, File> resources = new HashMap<>(128);

   public WorkspaceClassLoader(String... jarFiles) {
      this(WorkspaceClassLoader.class.getClassLoader(), jarFiles);
   }

   public WorkspaceClassLoader(ClassLoader parent, String... jarFiles) {
      super(parent);
      this.jarFiles = jarFiles;
   }

   public Map<String, String> getImplementationVersions() throws IOException {
      HashMap<String, String> map = new HashMap<>();
      for (String jfile : jarFiles) {
         JarFile jarFile = new JarFile(jfile);
         Attributes attribs = jarFile.getManifest().getMainAttributes();
         map.put(jfile, attribs.getValue("Implementation-Version"));
      }
      return map;
   }

   @Override
   protected Class<?> findClass(String name) throws ClassNotFoundException {
      try {
         for (String jfile : jarFiles) {
            JarFile jarFile = new JarFile(jfile);
            try {
               ZipEntry entry = jarFile.getEntry(classNameToJarEntry(name));
               if (entry != null) {
                  InputStream is = jarFile.getInputStream(entry);
                  long size = entry.getSize();
                  if (size == -1) {
                     throw new IOException("unknown entry size");
                  }
                  if (store.length < size) {
                     store = new byte[(int) size];
                  }
                  BufferedInputStream zis = new BufferedInputStream(is);
                  int rb = 0;
                  int chunk = 0;
                  while ((int) size - rb > 0) {
                     chunk = zis.read(store, rb, (int) size - rb);
                     if (chunk == -1) {
                        break;
                     }
                     rb += chunk;
                  }
                  return defineClass(name, store, 0, (int) size);
               }
            } finally {
               jarFile.close();
            }
         }
         throw new ClassNotFoundException(name);
      } catch (IOException ex) {
         throw new ClassNotFoundException(name, ex);
      }
   }

   @Override
   protected URL findResource(String name) {
      try {
         File file = resources.get(name);
         if (file != null) {
            return file.toURI().toURL();
         }
         for (String jarFileName : jarFiles) {
            JarFile jarFile = new JarFile(jarFileName);
            try {
               ZipEntry entry = jarFile.getEntry(name);
               if (entry != null) {
                  InputStream is = jarFile.getInputStream(entry);
                  long size = entry.getSize();
                  if (size == -1) {
                     throw new IOException("unknown entry size");
                  }
                  file = File.createTempFile(name, "tmp");
                  FileChannel channel = new FileOutputStream(file).getChannel();
                  try {
                     channel.transferFrom(Channels.newChannel(is), 0, size);
                  } finally {
                     channel.close();
                  }
                  resources.put(name, file);
                  return file.toURI().toURL();
               }
            } finally {
               jarFile.close();
            }
         }
         return null;
      } catch (IOException ex) {
         throw new RuntimeException("exception getting resource " + name, ex);
      }
   }

   @Override
   public InputStream getResourceAsStream(String name) {
      try {
         for (String file : jarFiles) {
            JarFile jarFile = new JarFile(file);
            ZipEntry entry = jarFile.getEntry(name);
            if (entry != null) {
               return jarFile.getInputStream(entry);
            }
         }
         return getParent().getResourceAsStream(name);
      } catch (IOException ex) {
         throw new RuntimeException("exception getting resource " + name, ex);
      }
   }

   private static String classNameToJarEntry(String className) {
      String result = className.replace('.', '/');
      return result + ".class";
   }

   public static void main(String[] args) {
      String[] files =
         new String[] {"C:\\DefineYourPath\\lib\\runtime.jar"};

      WorkspaceClassLoader cl = new WorkspaceClassLoader(files);
      try {
         Class<?> c = cl.loadClass("packageToYourBundle.bundleName");
         System.out.printf("found class %s", c.getName());
      } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
      }
   }

   public void dispose() {
      for (File file : resources.values()) {
         file.delete();
      }
      resources.clear();
   }
}
