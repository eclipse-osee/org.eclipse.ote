/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.ote.client.ui.core.widgets;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ote.client.ui.core.TestHostItem;
import org.osgi.framework.Bundle;


/**
 * @author Michael P. Masterson
 */
public class ClientServerBundleVersionChecker {
   
   private static String clientVersion;

   
   public static boolean clientAndServerVersionsMatch(TestHostItem serverItem) {
      boolean theyMatch = false;
      String clientVersion = getClientVersion();
      String serverVersion = getServerVersion(serverItem);
      Pattern pattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+).*");
      Matcher clientMatcher = pattern.matcher(clientVersion);
      Matcher serverMatcher = pattern.matcher(serverVersion);
      if(clientMatcher.matches() && serverMatcher.matches()){
         String clientStrippedVersion = clientMatcher.group(1);
         String serverStrippedVersion = serverMatcher.group(1);
         if(clientStrippedVersion.equals(serverStrippedVersion)){
            theyMatch = true; 
         }
      } else if(clientVersion.equals(serverVersion)){
         theyMatch = true; 
      }
      
      return theyMatch;
   }

   public static String getServerVersion(TestHostItem item) {
      return item.getProperties().getVersion();
   }
   
   public static String getClientVersion() {
      if( clientVersion == null ) {
         Bundle bundle = Platform.getBundle("org.eclipse.osee.ote.core");
         if(bundle != null){
            clientVersion = bundle.getHeaders().get("Bundle-Version");
         }
      }
      
      return clientVersion;
   }
}
