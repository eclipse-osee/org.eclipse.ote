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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.eclipse.ote.message.lookup.CsvMessageLookupParser;
import org.eclipse.ote.message.lookup.MessageLookupOperator;
import org.eclipse.ote.services.core.BundleUtility;
import org.junit.Assert;

/**
 * @author Michael P. Masterson
 */
public class MessageLookupTestUtil {

   public static void loadSampleData(MessageLookupOperator impl, int uniqueProviderId) throws IOException{
      URL url = findEntry("data/DBMessageElementList.txt");

      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
      String line = null;
      while((line = reader.readLine()) != null){
         CsvMessageLookupParser.addDbEntry(impl, uniqueProviderId, line);
      }
   }

   private static URL findEntry(String path){
      URL url = BundleUtility.findEntry("org.eclipse.ote.message.lookup.test", path);
      Assert.assertNotNull(url);
      return url;
   }
}
