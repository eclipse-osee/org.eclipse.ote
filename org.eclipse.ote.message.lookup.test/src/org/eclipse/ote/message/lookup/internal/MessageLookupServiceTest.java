/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.ote.message.lookup.internal;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.ote.message.lookup.MessageLookup;
import org.eclipse.ote.message.lookup.MessageLookupProvider;
import org.eclipse.ote.message.lookup.MessageLookupResult;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;


/**
 * @author Michael P. Masterson
 */
public class MessageLookupServiceTest {

   @Test
   public void testService() throws IOException, ClassNotFoundException, SQLException {
      BundleContext context = FrameworkUtil.getBundle(MessageLookup.class).getBundleContext();
      ServiceRegistration<MessageLookupProvider> registration = context.registerService(MessageLookupProvider.class, new TestProvider(), null);
      ServiceReference<MessageLookup> ref = context.getServiceReference(MessageLookup.class);
      MessageLookup lookup = context.getService(ref);
     
      try {
         Thread.sleep(5000);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      List<MessageLookupResult> results = lookup.lookup("*_CMD", "PUB_SUB");
      
      //do some retrys in case init took a while on a busy system
      int size = results.size();
      for(int i = 0; i < 10 && size == 0; i++){
         try {
            Thread.sleep(5000);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
         results = lookup.lookup("*_CMD", "PUB_SUB");
         size = results.size();
      }
      
      Assert.assertNotSame(0, results.size());

      registration.unregister();
      
      try {
         Thread.sleep(5000);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      results = lookup.lookup("*_CMD", "PUB_SUB");

      //do some retrys in case init took a while on a busy system
      size = results.size();
      for(int i = 0; i < 10 && size != 0; i++){
         try {
            Thread.sleep(5000);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
         results = lookup.lookup("*_CMD", "PUB_SUB");
         size = results.size();
      }
      
      Assert.assertEquals(0, results.size());
   }

}
