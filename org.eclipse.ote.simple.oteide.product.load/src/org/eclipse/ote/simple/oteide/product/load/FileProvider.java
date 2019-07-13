/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.simple.oteide.product.load;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.ote.services.core.BundleUtility;
import org.eclipse.ote.services.core.LoadBundleProvider;

/**
 * @author Andrew M. Finkbeiner
 */
public class FileProvider implements LoadBundleProvider {

   @Override
   public List<String> getBundleSymbolicNames() {
      List<String> names = new ArrayList<String>();
      try {
         URL entry = BundleUtility.findEntry("org.eclipse.ote.simple.oteide.product.load", "data/precompiledServerBundleList.txt");
         String fileContent = Lib.inputStreamToString(entry.openStream());
         String[] strNames = fileContent.split("\n");
         for(int i = 0; i < strNames.length; i++){
            if(strNames[i] != null && strNames[i].trim().length() > 0 && !strNames[i].trim().startsWith("#")){
               names.add(strNames[i].trim());
            }
         }
      } catch (IOException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      }
      return names;
   }

}
