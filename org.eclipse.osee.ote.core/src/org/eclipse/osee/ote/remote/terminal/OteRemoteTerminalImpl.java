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

import java.io.InputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
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
    * @return {@link OteRemoteTerminalResponse} if no exceptions while starting
    *         remote terminal session, otherwise an
    *         {@link OteRemoteTerminalResponseException} that fails all
    *         verifications
    * @throws Exception
    */
   @Override
   public OteRemoteTerminalResponse open() throws Exception {
      OteRemoteTerminalResponse retVal;
      try {
         java.util.Properties config = new java.util.Properties();
         config.put("StrictHostKeyChecking", "no");

         JSch jsch = new JSch();
         session = jsch.getSession(user, host, port);
         session.setPassword(password);
         session.setConfig(config);
         session.connect();

         retVal = new OteRemoteTerminalResponse("");
      } catch (Exception e) {
         e.printStackTrace();
         retVal = new OteRemoteTerminalResponseException(e);
      }
      return retVal;
   }

   /**
    * Closes remote terminal session
    * 
    * @return {@link OteRemoteTerminalResponse} if no exceptions while closing
    *         remote terminal connection, otherwise an
    *         {@link OteRemoteTerminalResponseException} that fails all
    *         verifications
    * @throws Exception
    */
   @Override
   public OteRemoteTerminalResponse close() throws Exception {
      OteRemoteTerminalResponse retVal;
      try {
         if (session != null && session.isConnected()) {
            session.disconnect();
         }
         retVal = new OteRemoteTerminalResponse("");
      } catch (Exception e) {
         e.printStackTrace();
         retVal = new OteRemoteTerminalResponseException(e);
      }
      return retVal;
   }

   /**
    * Issues command to open remote terminal session
    * 
    * @param command
    * @return {@link OteRemoteTerminalResponse} if no exceptions while sending
    *         command to remote terminal session, otherwise an
    *         {@link OteRemoteTerminalResponseException} that fails all
    *         verifications
    */
   @Override
   public OteRemoteTerminalResponse command(String command) {
      OteRemoteTerminalResponse retVal;
      String responseString = "";
      try {
         Channel channel = session.openChannel("exec");
         ((ChannelExec) channel).setCommand(command);
         channel.setInputStream(null);
         ((ChannelExec) channel).setErrStream(System.err);

         InputStream in = channel.getInputStream();
         channel.connect();
         byte[] tmp = new byte[1024];
         while (true) {
            while (in.available() > 0) {
               int i = in.read(tmp, 0, 1024);
               if (i < 0)
                  break;
               responseString = new String(tmp, 0, i);
            }
            if (channel.isClosed()) {
               break;
            }
            Thread.sleep(1000);
         }
         retVal = new OteRemoteTerminalResponse(responseString);
      } catch (Exception e) {
         e.printStackTrace();
         retVal = new OteRemoteTerminalResponseException(e);
      }
      return retVal;
   }

   /**
    * Returns host name
    * 
    * @return
    */
   @Override
   public String getHostName() {
      return host;
   }
}
