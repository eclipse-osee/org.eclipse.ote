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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
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

         retVal = new OteRemoteTerminalResponse();
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
         retVal = new OteRemoteTerminalResponse();
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
   public OteRemoteTerminalResponse command(String command) {
      OteRemoteTerminalResponse retVal;

      try {
         ChannelExec channel = (ChannelExec) session.openChannel("exec");
         channel.setCommand(command);
         channel.connect();

         retVal = getOutput(channel);

         channel.disconnect();
      } catch (IOException e) {
         retVal = new OteRemoteTerminalResponseException(e);
      } catch (JSchException e) {
         retVal = new OteRemoteTerminalResponseException(e);
      }
      return retVal;
   }

   /**
    * Returns OteRemoteTerminalResponse containing remote terminal session channel
    * output after a command is issued.
    * 
    * @param channel
    * @return
    * @throws IOException
    */
   private OteRemoteTerminalResponse getOutput(ChannelExec channel) throws IOException {
      OteRemoteTerminalResponse retVal = null;

      InputStream in = channel.getInputStream();
      InputStream err = channel.getErrStream();

      ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
      ByteArrayOutputStream errorBuffer = new ByteArrayOutputStream();

      byte[] tmpOutBuf = new byte[in.available() + 1];
      byte[] tmpErrBuf = new byte[err.available() + 1];
      while (true) {
         while (in.available() > 0) {
            int i = in.read(tmpOutBuf);
            if (i < 0)
               break;
            outputBuffer.write(tmpOutBuf);
         }
         while (err.available() > 0) {
            int i = err.read(tmpErrBuf);
            if (i < 0)
               break;
            errorBuffer.write(tmpErrBuf);
         }

         if (channel.isClosed()) {
            if ((in.available() > 0) || (err.available() > 0))
               continue;
            else
               break;
         }
         try {
            Thread.sleep(1000);
         } catch (InterruptedException e) {
            retVal = new OteRemoteTerminalResponseException(e);
         }
      }
      retVal = new OteRemoteTerminalResponse(outputBuffer.toString(), errorBuffer.toString(), channel.getExitStatus());
      return retVal;
   }

   /**
    * Returns host name of remote terminal session
    * 
    * @return
    */
   @Override
   public String getHostName() {
      return host;
   }

   /**
    * Returns true if remote terminal session is connected
    * 
    * @return
    */
   public boolean isConnected() {
      return session.isConnected();
   }
}
