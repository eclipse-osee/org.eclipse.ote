/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.ote.osgi.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.eclipse.ote.osgi.configuration.internal.SampleConfiguredComponent;
import org.eclipse.ote.services.core.BundleUtility;
import org.eclipse.ote.services.core.ServiceUtility;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Michael P. Masterson
 */
public class OTEConfigurationTest {

   @Before
   public void setUp() {
      BundleUtility.startBundle("org.eclipse.equinox.cm");
   }
   
   @Test
   public void testPropertyLoading() {
      SampleConfiguredComponent test = ServiceUtility.getService(SampleConfiguredComponent.class, 30000);
      Assert.assertNotNull(test);
      for(String key:test.getProperties().keySet()){
         System.out.println(key);
         Object obj = test.getProperties().get(key);
         if(obj != null){
            System.out.println("   " + obj.getClass().getName() + " - " + obj.toString());
         }
      }
      Assert.assertEquals("thereyougo", test.getProperties().get("org.eclipse.ote.osgi.configuration.test.id"));
      Assert.assertEquals("192.168.1.55", test.getProperties().get("org.eclipse.ote.osgi.configuration.ip"));
      Assert.assertEquals("3454", test.getProperties().get("org.eclipse.ote.osgi.configuration.port"));
   }
   
   @Test
   public void testMultiFileSearch() {
      Set<String> entries = BundleUtility.findSubEntries("org.eclipse.ote.osgi.configuration.test", "data2/");
      Assert.assertEquals(6, entries.size());
      List<String> filtered = new ArrayList<String>();
      for(String entry:entries){
         if(entry.endsWith(".properties")){
            filtered.add(entry);
         }
      }
      Assert.assertEquals(4, filtered.size());
      List<URL> urls = BundleUtility.entriesToURLs("org.eclipse.ote.osgi.configuration.test", filtered);
      for(int i = 0; i < urls.size(); i++){
         InputStream stream = null;
         try{
            stream = urls.get(i).openStream();
            Assert.assertNotNull(stream);
         } catch (IOException ex){
            Assert.fail(ex.getMessage());
            ex.printStackTrace();
         }
      }
   }
   
}
