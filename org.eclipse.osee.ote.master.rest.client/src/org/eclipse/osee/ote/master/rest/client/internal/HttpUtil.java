/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ote.master.rest.client.internal;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;

public class HttpUtil {

   public static boolean canConnect(URI targetUri) {
      try{
         HttpURLConnection connection = (HttpURLConnection)targetUri.toURL().openConnection();
         connection.setRequestMethod("HEAD");
         int responseCode = connection.getResponseCode();
         if(responseCode == 200){
            return true;
         }
      } catch (Throwable th){
         OseeLog.log(HttpUtil.class, Level.INFO, th);
      }
      return false;
   }
}
