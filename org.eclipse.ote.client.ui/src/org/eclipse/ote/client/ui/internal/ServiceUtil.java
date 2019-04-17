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
package org.eclipse.ote.client.ui.internal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Andrew M. Finkbeiner
 */
public class ServiceUtil {
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public static <T> T getService(Class<T> clazz){
      BundleContext context = getContext();
      if(context == null){
         return null;
      }
      ServiceReference serviceReference = context.getServiceReference(clazz.getName());
      if(serviceReference == null){
         return null;
      }
      return (T)getContext().getService(serviceReference);
   }

   public static BundleContext getContext(){
      return FrameworkUtil.getBundle(ServiceUtil.class).getBundleContext();
   }
}

