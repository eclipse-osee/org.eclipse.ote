/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ote.message.internal;

import org.eclipse.osee.ote.core.environment.TestEnvironmentInterface;
import org.eclipse.osee.ote.message.io.IMessageIoManagementService;
import org.eclipse.osee.ote.message.io.MessageIoManagementService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Ken J. Aguilar
 */
public class MessageIoManagementStarter extends ServiceTracker {

   private ServiceRegistration registration;

   public MessageIoManagementStarter(BundleContext context) {
      super(context, TestEnvironmentInterface.class.getName(), null);
   }

   @Override
   public synchronized TestEnvironmentInterface addingService(ServiceReference reference) {
      TestEnvironmentInterface manager = (TestEnvironmentInterface) super.addingService(reference);
      registration =
         context.registerService(IMessageIoManagementService.class.getName(), new MessageIoManagementService(), null);
      return manager;
   }

   @Override
   public synchronized void removedService(ServiceReference reference, Object service) {
	  unregister();
      super.removedService(reference, service);
   }

   @Override
   public synchronized void close() {
	  unregister();
      super.close();
   }
   
   private void unregister(){
	   if (registration != null) {
		   try{
			   registration.unregister();
		   } catch (IllegalStateException ex){
			   //do nothing, we're just making sure it got cleaned up
		   } finally {
			   registration = null;
		   }
	   }
   }

}
