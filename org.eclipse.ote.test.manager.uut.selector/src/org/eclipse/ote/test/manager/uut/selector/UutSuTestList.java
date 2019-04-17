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
package org.eclipse.ote.test.manager.uut.selector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author David N. Phillips
 * @author Andy Jury
 */
public class UutSuTestList {
   private static List<BundleWiring> wiringCache = new ArrayList<>();
   @SuppressWarnings("rawtypes")
   private static Map<String, Class> classCache = new HashMap<>();
   private static final String[] defaultPartitions = {  
      "SoftwareUnit1",
      "SoftwareUnit2"
      };
   
   public static List<String> getTestSuList() {
      List<String> original = getUniqueSuNames();
      List<String> list = new ArrayList<>();
      for (String su : original) {
            list.add(su);
      }
      if (list.isEmpty()) {
         list.addAll(Arrays.asList(defaultPartitions));
      }
      
      return list;
   }
   
   @SuppressWarnings("rawtypes")
   static private List<String> getUniqueSuNames() {
      List<String> list = new ArrayList<>();
      try {
         Class uutParticipants = loadClass("ote.databaseName.participants.UUTParticipants");
         Object[] uutEnumConstants = uutParticipants.getEnumConstants();
         for (Object e : uutEnumConstants) {
            String su = e.getClass().getMethod("getSu").invoke(e).toString();
            if (!list.contains(su)) {
               list.add(su);
            }
         }
      } catch (Throwable e) {
//         OseeLog.log(UutSuTestList.class, Level.SEVERE, e);
      }
      return list;
   }

   @SuppressWarnings("rawtypes")
   static private Class loadClass(String classToLoad) throws ClassNotFoundException{
      Class clazz = null;
      if (null != (clazz = classCache.get(classToLoad))) {
         return clazz;
      }

      for(BundleWiring wiring: wiringCache){
         try{
            clazz = wiring.getClassLoader().loadClass(classToLoad);
            classCache.put(classToLoad, clazz);
            return clazz;
         } catch (Throwable ex){
            // INTENTIONALLY EMPTY BLOCK
            }
      }
      
      Bundle frameworkBundle = FrameworkUtil.getBundle(UutSuTestList.class);
      if (frameworkBundle != null) {
         Bundle[] allBundles = frameworkBundle.getBundleContext().getBundles();

         for(Bundle bundle: allBundles) {
            BundleWiring wiring = null;
            try {
               wiring = bundle.adapt(BundleWiring.class);
            } catch (Throwable th) {
               // INTENTIONALLY EMPTY BLOCK
               }
            if(wiring != null){
               try{
                  wiringCache.add(wiring);
                  clazz = wiring.getClassLoader().loadClass(classToLoad);
                  classCache.put(classToLoad, clazz);
                  return clazz;
               } catch (Throwable ex){
                  // INTENTIONALLY EMPTY BLOCK
                  }
            }
            else {
               try {
                  clazz = bundle.loadClass(classToLoad);
                  classCache.put(classToLoad, clazz);
                  return clazz;
               } catch (Throwable th) {
                  // INTENTIONALLY EMPTY BLOCK
                  }
            }
         }
      }
      
      if (clazz == null) {
         try {
            clazz = UutSuTestList.class.getClassLoader().loadClass(classToLoad);
            classCache.put(classToLoad, clazz);
            return clazz;
         } catch (Throwable th) {
            // INTENTIONALLY EMPTY BLOCK
            }
      }
      
      throw new ClassNotFoundException(classToLoad); 
   }
}
