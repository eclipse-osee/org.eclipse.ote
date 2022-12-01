/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.ote.remote.terminal;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * @author Nydia Delgado
 */
public class OteRemoteTerminalImpl implements OteRemoteTerminal {

   private final String user = System.getProperty("remote.terminal.username");
   private final String password = System.getProperty("remote.terminal.password");
   private final String host = System.getProperty("remote.terminal.host");
   private final int port = Integer.parseInt(System.getProperty("remote.terminal.port"));

   private static Session session = null;

   /**
    * Opens a remote terminal session
    * 
    * @throws Exception
    */
   @Override
   public boolean open() throws Exception {
      try {
         java.util.Properties config = new java.util.Properties();
         config.put("StrictHostKeyChecking", "no");

         JSch jsch = new JSch();
         session = jsch.getSession(user, host, port);
         session.setPassword(password);
         session.setConfig(config);
         session.connect();

         if (session.isConnected()) {
            return true;
         } else {
            return false;
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return false;
   }

   /**
    * Closes remote terminal session
    * 
    * @throws Exception
    */
   @Override
   public boolean close() throws Exception {
      try {
         session.disconnect();

         if (!session.isConnected()) {
            return true;
         } else {
            return false;
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return false;
   }

}
