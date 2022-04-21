/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ote.core.framework.testrun;

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
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Andy Jury
 */
public abstract class OteTestFactory implements ITestFactory {

   private final IRuntimeLibraryManager rtLibManager;

   public OteTestFactory(IRuntimeLibraryManager rtLibManager) {
      this.rtLibManager = rtLibManager;
   }

   protected abstract Class<? extends TestEnvironment> getTestEnvironmentClass();

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
      } else {
         Constructor<?> constructor =
            scriptClass.getConstructor(new Class[] {getTestEnvironmentClass(), ITestEnvironmentCommandCallback.class});
         return (TestScript) constructor.newInstance(env, null);
      }
      return null;
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
