/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.ote.ui.message.util.internal;

import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class MessageUtilServiceUtility {
   public static Class<MessageUtilServiceUtility> getClazz(){
      return MessageUtilServiceUtility.class;
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public static <T> T getService(Class<T> clazz){
      BundleContext context = getContext();
      if(context == null){
    	 OseeLog.log(MessageUtilServiceUtility.class, Level.SEVERE, "Unable to get service " + clazz.getName());
         return null;
      }
      ServiceReference serviceReference = context.getServiceReference(clazz.getName());
      if(serviceReference == null){
    	 OseeLog.log(MessageUtilServiceUtility.class, Level.SEVERE, "Unable to get service " + clazz.getName());
         return null;
      }
      T obj = (T)getContext().getService(serviceReference);
      if(obj == null){
    	  OseeLog.log(MessageUtilServiceUtility.class, Level.SEVERE, "Unable to get service " + clazz.getName());
      }
      return obj; 
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public static <T> T[] getServices(Class<T> clazz) throws InvalidSyntaxException{
      ServiceReference[] serviceReferences = getContext().getServiceReferences(clazz.getName(), null);
      T[] data = (T[])new Object[serviceReferences.length];
      for(int i = 0; i < serviceReferences.length; i ++){
         data[i] = (T)getContext().getService(serviceReferences[i]);
      }
      return data;
   }

   public static BundleContext getContext(){
	  Bundle bundle = FrameworkUtil.getBundle(getClazz());
	  if(bundle == null){
		  return null;
	  }
      return bundle.getBundleContext();
   }
}

