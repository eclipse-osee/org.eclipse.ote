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

package org.eclipse.ote.simple.io.message.lookup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.ote.message.lookup.AbstractMessageLookupProvider;
import org.eclipse.ote.message.lookup.CsvMessageLookupParser;
import org.eclipse.ote.message.lookup.MessageAssociationLookup;
import org.eclipse.ote.message.lookup.MessageLookupOperator;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Michael P. Masterson
 */
public class SimpleMessageLookupProvider extends AbstractMessageLookupProvider implements MessageAssociationLookup {

   @Override
   public void addToDb(MessageLookupOperator lookupOperator) {
      try{
         Bundle bundle = FrameworkUtil.getBundle(this.getClass());
         String id = "data/messageLookupDb.txt";
         URL url = bundle.getEntry(id);
         if(url == null){
            url = bundle.getEntry("src/" + id);
         }
         if(url == null){
            url = bundle.getEntry("bin/" + id);
         }
         if(url != null){
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = null;
            while((line = reader.readLine()) != null){
               CsvMessageLookupParser.addDbEntry(lookupOperator, getUniqueProviderId(), line);
            }
         }
      } catch (IOException e){
         OseeLog.log(getClass(), Level.SEVERE, "Failed to import File Entries into message lookup.", e);
      }
   }

   @Override
   public List<String> lookupAssociatedMessages(String classname) {
      List<String> retVal = new ArrayList<>();
      switch (classname) {
         case "org.eclipse.ote.simple.io.message.HELLO_WORLD":
            retVal.add("org.eclipse.ote.simple.io.message.SIMPLE_MUX_MSG");
            break;
         case "org.eclipse.ote.simple.io.message.SIMPLE_MUX_MSG":
            retVal.add("org.eclipse.ote.simple.io.message.HELLO_WORLD");
            break;
         default:
            break;
      }
      return retVal;
   }

}
