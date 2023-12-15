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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.ote.core.environment.interfaces.BasicTimeout;
import org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer;
import org.eclipse.osee.ote.core.environment.interfaces.ITimerControl;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * @author Nydia Delgado, Dominic Leiner
 */
public class BasicRemoteTerminal implements OteRemoteTerminal {
   private static final int TEN_MINUTE_DEFAULT_TIMEOUT = 600000;
   //wait this long in milliseconds between output updates
   static final int SLEEP_TIME = 250; 
   private final BasicTimeout timeoutWaiter = new BasicTimeout();
   private final ITimerControl timerControl;
   
   protected String hostname;
   protected int port;
   protected String username;
   protected String password;

   private Session session = null;

   public BasicRemoteTerminal(ITimerControl timerControl) {
      hostname = System.getProperty("remote.terminal.host", "");
      port = Integer.parseInt(System.getProperty("remote.terminal.port", "22"));
      username = System.getProperty("remote.terminal.username", "");
      password = System.getProperty("remote.terminal.password", "");
      this.timerControl = timerControl;
   }

   public BasicRemoteTerminal(String hostname, int port, String username, String password, ITimerControl timerControl) {
      this.hostname = hostname;
      this.port = port;
      this.username = username;
      this.password = password;
      this.timerControl = timerControl;
   }

   /**
    * Opens a remote terminal session
    * 
    * @return {@link OteRemoteTerminalResponse} if no exceptions while starting
    *         remote terminal session, otherwise an
    *         {@link OteRemoteTerminalResponseException} that fails all
    *         verifications
    * @throws Exception
    */
   public OteRemoteTerminalResponse open() throws Exception {
      OteRemoteTerminalResponse retVal;
      try {
         java.util.Properties config = new java.util.Properties();
         config.put("StrictHostKeyChecking", "no");

         JSch jsch = new JSch();
         session = jsch.getSession(username, hostname, port);
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
    * Uses default timeout
    * 
    * @param command
    * @return {@link OteRemoteTerminalResponse} if no exceptions while sending
    *         command to remote terminal session, otherwise an
    *         {@link OteRemoteTerminalResponseException} that fails all
    *         verifications
    */
   public OteRemoteTerminalResponse command(String command) {
      return command(command, TEN_MINUTE_DEFAULT_TIMEOUT);
   }
   
   /**
    * Issues command to open remote terminal session
    * 
    * @param command
    * @param timeoutInMs
    * @return {@link OteRemoteTerminalResponse} if no exceptions while sending
    *         command to remote terminal session, otherwise an
    *         {@link OteRemoteTerminalResponseException} that fails all
    *         verifications
    */
   public OteRemoteTerminalResponse command(String command, int timeoutInMs) {
       final ExecutorService processThread = Executors.newSingleThreadExecutor();
       OteRemoteTerminalResponse response;
       RemoteProcRunnable myRunnable = new RemoteProcRunnable(command);
       processThread.execute(myRunnable);
       processThread.shutdown();
       
       try {
          boolean completed = processThread.awaitTermination(timeoutInMs, TimeUnit.MILLISECONDS);
          if(completed) {
             response = myRunnable.getResponse();
          } else {
             myRunnable.stop();
             Exception ex = new OseeCoreException("Timed out executing remote process after %d %s",
                                                  timeoutInMs, TimeUnit.MILLISECONDS);
             response = new OteRemoteTerminalResponseException(ex);
          }
       }
       catch (InterruptedException ex) {
           response = new OteRemoteTerminalResponseException(ex);
       }
       return response;
    }
   
   private class RemoteProcRunnable implements Runnable {

       private final String command;
       private OteRemoteTerminalResponse response;
       private ChannelExec channel;

       /**
        * @param command
        */
       public RemoteProcRunnable(String command) {
          this.command = command;
       }

       /**
        * @return the response
        */
       public OteRemoteTerminalResponse getResponse() {
          return response;
       }
       
       /**
        * Stops the process.
        */
       public void stop() {
          if(channel != null)
              channel.disconnect();
       }

       @Override
       public void run() {
          try {
              channel = (ChannelExec) session.openChannel("exec");
              channel.setCommand(command);
              channel.connect();

              response = getOutput(channel);

              channel.disconnect();
          }
          catch (Exception ex) {
              response = new OteRemoteTerminalResponseException(ex);
          }
       }

    }
   
   /**
    * Issues command to open remote terminal session & not wait for a response.
    * Default 10 minute timeout.
    * 
    * @param command
    * @return {@link OteRemoteTerminalResponseStream} 
    *          this object is shared between the main thread & the thread updating the output,
    *          Encapsulates an OteRemoteTerminalResponse.
    */
   public OteRemoteTerminalResponseStream commandAndContinue(String command) {
      return commandAndContinue(command, TEN_MINUTE_DEFAULT_TIMEOUT);
   }
   
   /**
    * Issues command to open remote terminal session & not wait for a response.
    * 
    * @param command
    * @param timeout - in seconds. ( noTimeout is <= 0 )
    * @return {@link OteRemoteTerminalResponseStream} 
    *          this object is shared between the main thread & the thread updating the output,
    *          Encapsulates an OteRemoteTerminalResponse.
    */
   public OteRemoteTerminalResponseStream commandAndContinue(String command, int timeout) {
      class CommandRunner implements Runnable {
         volatile OteRemoteTerminalResponseStream responseStream;
         
         public CommandRunner(OteRemoteTerminalResponseStream responseStream) {
            this.responseStream = responseStream;
         }
         
         public void stop() {
            if(!responseStream.isClosed()) {
               this.responseStream.kill();
            }
         }
         
         public void run() {
            ChannelExec channel = responseStream.getChannel();
            try {
               try {
                  //Give the thread a brief time to start & get some output.
                  Thread.sleep(100);
               } catch (InterruptedException e) {
                  //Do nothing
               }
               getOutputStream(responseStream);
               responseStream.closed(channel.getExitStatus());
            }
            catch (IOException e) {
               responseStream.exception(new OteRemoteTerminalResponseException(e));
            } finally {
               if(channel != null) {
                  channel.disconnect();
               }
            }
            
         }
      }
      
      OteRemoteTerminalResponseStream retVal = new OteRemoteTerminalResponseStream();
      ChannelExec channel;
      
      try {
         channel = (ChannelExec) session.openChannel("exec");
         channel.setCommand(command);
         channel.connect();
         retVal.addChannel(channel);
      }
      catch (JSchException e) {
         retVal.exception(new OteRemoteTerminalResponseException(e));
         return retVal;
      }
      
      CommandRunner commandRunner = new CommandRunner(retVal);
      Thread commandThread = new Thread(commandRunner);
      commandThread.start();
         
      if(timeout > 0) {
         Thread threadStopper = new Thread(() -> {       
            synchronized (timeoutWaiter) {
               ICancelTimer timer = timerControl.setTimerFor(timeoutWaiter, timeout*1000);

               boolean isDone = false;
               try {
                  while (!isDone) {
                     timeoutWaiter.wait(SLEEP_TIME);
                     isDone = (!commandThread.isAlive()) || timeoutWaiter.isTimedOut();
                  }
                  
                  synchronized (commandThread) {
                     if(commandThread.isAlive())
                        commandRunner.stop();
                  }
               } catch (InterruptedException e) {
                  e.printStackTrace();
               } finally {
                  timer.cancelTimer();
               }
            }
         });
         
         threadStopper.setDaemon(true);
         threadStopper.start(); 
      }      
      
      try {
         //Give the thread a brief period to get output
         Thread.sleep(100);
      } catch (InterruptedException e) {
         //Do nothing
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
            Thread.sleep(SLEEP_TIME);
         } catch (InterruptedException e) {
            retVal = new OteRemoteTerminalResponseException(e);
         }
      }
      retVal = new OteRemoteTerminalResponse(outputBuffer.toString(), errorBuffer.toString(), channel.getExitStatus());
      return retVal;
   }
   
   /**
    * Updates the ouputStream & errorStream byteArrays in responseStream
    * with the input from channel
    * Ends when the channel is closed.
    * 
    * @param responseStream
    * @throws IOException
    */
   private void getOutputStream(OteRemoteTerminalResponseStream responseStream) throws IOException {
      ChannelExec channel = responseStream.getChannel();
      InputStream in = channel.getInputStream();
      InputStream err = channel.getErrStream();
      
      ByteArrayOutputStream outputStream = responseStream.getStdOutStream();
      ByteArrayOutputStream errorStream = responseStream.getStdErrStream();
      
      //TODO: Replace with in.transferTo(outputStream); when updated to java 9+
      
      byte[] tmpOutBuf = new byte[in.available() + 1];
      byte[] tmpErrBuf = new byte[err.available() + 1];
      while (true) {         
         while (in.available() > 0) {
            int i = in.read(tmpOutBuf);
            if (i < 0)
               break;
            outputStream.write(tmpOutBuf);
         }
         while (err.available() > 0) {
            int i = err.read(tmpErrBuf);
            if (i < 0)
               break;
            errorStream.write(tmpErrBuf);
         }

         if (channel.isClosed()) {
            if ((in.available() > 0) || (err.available() > 0))
               continue;
            else
               break;
         }
         try {
            Thread.sleep(SLEEP_TIME);
         } catch (InterruptedException e) {
            responseStream.exception(new OteRemoteTerminalResponseException(e));
         }
      }
   }
   
   /**
    * Returns host name of remote terminal session
    * 
    * @return
    */
   public String getHostName() {
      return hostname;
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
