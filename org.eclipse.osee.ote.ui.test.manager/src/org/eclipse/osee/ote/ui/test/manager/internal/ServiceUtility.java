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

package org.eclipse.osee.ote.ui.test.manager.internal;

import org.eclipse.osee.ote.version.FileVersionInformation;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class ServiceUtility {

	public static FileVersionInformation getFileVersionInformation(){
		return getService(FileVersionInformation.class);
	}
	
	public static Class<ServiceUtility> getClazz(){
		return ServiceUtility.class;
	}

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

	public static <T> T[] getServices(Class<T> clazz) throws InvalidSyntaxException{
		ServiceReference[] serviceReferences = getContext().getServiceReferences(clazz.getName(), null);
		T[] data = (T[])new Object[serviceReferences.length];
		for(int i = 0; i < serviceReferences.length; i ++){
			data[i] = (T)getContext().getService(serviceReferences[i]);
		}
		return data;
	}

	public static BundleContext getContext(){
		return FrameworkUtil.getBundle(getClazz()).getBundleContext();
	}
}

