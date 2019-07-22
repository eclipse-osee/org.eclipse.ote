/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.ote.simple.test.environment;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.osee.ote.core.environment.jini.ITestEnvironmentCommandCallback;
import org.eclipse.osee.ote.core.framework.command.RunTestsKeys;
import org.eclipse.osee.ote.core.framework.testrun.ITestFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Andy Jury
 */
public class SimpleTestFactory implements ITestFactory {

   private final IRuntimeLibraryManager rtLibManager;

   public SimpleTestFactory(IRuntimeLibraryManager rtLibManager) {
      this.rtLibManager = rtLibManager;
   }

   @Override
   public TestScript createInstance(TestEnvironment env, IPropertyStore properties) throws Exception {
      String scriptClassStr = properties.get(RunTestsKeys.testClass.name());
      Class<?> scriptClass = null;
      Throwable scriptClassLoaderTh = null;
      Throwable bundleClassLoaderTh = null;
      try {
         scriptClass = rtLibManager.loadFromScriptClassLoader(scriptClassStr);
      } catch (Throwable th) {
         scriptClassLoaderTh = th;
      }
      if (scriptClass == null) {
         try {
            scriptClass = loadClass(scriptClassStr);
         } catch (Throwable th) {
            bundleClassLoaderTh = th;
         }
      }
      if (scriptClass == null) {
         if (scriptClassLoaderTh != null) {
            OseeLog.log(getClass(), Level.SEVERE, scriptClassLoaderTh);
         }
         if (bundleClassLoaderTh != null) {
            OseeLog.log(getClass(), Level.SEVERE, bundleClassLoaderTh);
         }
      }

      if (scriptClass == null) {
         if (bundleClassLoaderTh != null) {
            throw new Exception(bundleClassLoaderTh);
         }
         if (scriptClassLoaderTh != null) {
            throw new Exception(scriptClassLoaderTh);
         }
      }
      Constructor<?> constructor = scriptClass.getConstructor(new Class[] { SimpleTestEnvironment.class, ITestEnvironmentCommandCallback.class });
      return (TestScript) constructor.newInstance(env, null);
   }

   List<BundleWiring> fastCache = new ArrayList<BundleWiring>();

   @SuppressWarnings("rawtypes")
   public Class loadClass(String classToLoad) throws ClassNotFoundException {
      Bundle[] allBundles = FrameworkUtil.getBundle(getClass()).getBundleContext().getBundles();

      for (BundleWiring wiring : fastCache) {
         try {
            Class clazz = wiring.getClassLoader().loadClass(classToLoad);
            // System.out.println("loaded " + classToLoad + " from " +
            // wiring.getBundle().getSymbolicName());
            return clazz;
         } catch (Throwable ex) {
         }
      }

      for (Bundle bundle : allBundles) {
         BundleWiring wiring = bundle.adapt(BundleWiring.class);
         if (wiring != null) {
            try {
               Class clazz = wiring.getClassLoader().loadClass(classToLoad);
               // System.out.println("loaded " + classToLoad + " from " +
               // wiring.getBundle().getSymbolicName());
               fastCache.add(wiring);
               return clazz;
            } catch (Throwable ex) {
            }
         }
      }
      throw new ClassNotFoundException(classToLoad);
   }
}
