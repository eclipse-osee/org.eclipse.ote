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
package org.eclipse.osee.ote.api.local;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Michael P. Masterson, Dominic Leiner
 */
public class LocalProcessApi {

   private static final long DEFAULT_TIMEOUT_SECONDS = 10000;

   /**
    * Calls {@link #executeProcess(long, String...)} using default timeout of
    * {@value #DEFAULT_TIMEOUT_SECONDS} milliseconds.
    * 
    * @param command Variable list of strings building the entire command. The first entry is
    *           assumed to be the externally executable program file and the rest of the entries the
    *           arguments to that application.
    * @return A response object capable of verifying the various outputs of the execution.
    */
   public LocalProcessResponse executeProcess(String... command) {
      return this.executeProcess(DEFAULT_TIMEOUT_SECONDS, command);
   }

   /**
    * Executes the provided command and encapsulates the outputs into a {@link LocalProcessResponse}
    * for future verification. The execution must complete within the timeout alotted (in milliseconds)
    * or an exception response will be logged<br>
    * <br>
    * <b>Please note that this process should NOT require any inputs from the user during execution
    * and any prompts awaiting keyboard response will cause a timeout.</b>
    * 
    * @param timoutInMs Time, in milliseconds, to allow for the process to complete
    * @param command Variable list of strings building the entire command. The first entry is
    *           assumed to be the externally executable program file and the rest of the entries the
    *           arguments to that application. It is good practice to separate each argument into a
    *           separate entry but different Operating Systems may have specific requirements for
    *           this.<br>
    *           <br>
    *           Example call: executeProcess(2000, "java", "-version")
    * @return A response object capable of verifying the various outputs of the execution.
    */
   public LocalProcessResponse executeProcess(long timoutInMs, String... command) {
      final ExecutorService processThread = Executors.newSingleThreadExecutor();
      LocalProcessResponse response;
      LocalProcRunnable myRunnable = new LocalProcRunnable(command);
      processThread.execute(myRunnable);
      processThread.shutdown();
      
      try {
         boolean completed = processThread.awaitTermination(timoutInMs, TimeUnit.MILLISECONDS);
         if(completed) {
            response = myRunnable.getResponse();
         } else {
            myRunnable.stop();
            Exception ex = new OseeCoreException("Timed out executing local process after %d %s",
                                                 timoutInMs, TimeUnit.MILLISECONDS);
            response = new LocalProcessExceptionResponse(ex, command, Strings.EMPTY_STRING,
                                                        Strings.EMPTY_STRING,
                                                        LocalProcessResponse.EXCEPTION);
         }
      }
      catch (InterruptedException ex) {
         response = new LocalProcessExceptionResponse(ex, command, Strings.EMPTY_STRING,
                                                      Strings.EMPTY_STRING,
                                                      LocalProcessResponse.EXCEPTION);
      }
      return response;
   }
   
   private class LocalProcRunnable implements Runnable {

      private final String[] command;
      private LocalProcessResponse response;
      private Process process;

      /**
       * @param command
       */
      public LocalProcRunnable(String[] command) {
         this.command = command;
      }

      /**
       * @return the response
       */
      public LocalProcessResponse getResponse() {
         return response;
      }
      
      /**
       * Stops the process.
       */
      public void stop() {
         if(process!=null)
            process.destroy();
      }

      @Override
      public void run() {
         ProcessBuilder builder = new ProcessBuilder(command);
         try {
            process = builder.start();
            String output = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
            String error = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
            int exitCode = process.exitValue();
            response = new LocalProcessResponse(command, output, error, exitCode);
         }
         catch (Exception ex) {
            response = new LocalProcessExceptionResponse(ex, command, Strings.EMPTY_STRING,
                                                         Strings.EMPTY_STRING, LocalProcessResponse.EXCEPTION);
         }
      }

   }
   
   /**
    * Calls {@link #executeProcessAndContinue(long, String...)} using default timeout of
    * {@value #DEFAULT_TIMEOUT_SECONDS} milliseconds.
    * 
    * @param command Variable list of strings building the entire command. The first entry is
    *           assumed to be the externally executable program file and the rest of the entries the
    *           arguments to that application.
    * @return A response object capable of verifying the various outputs of the execution.
    */
   public LocalProcessStream executeProcessAndContinue(String... command) {
      return this.executeProcessAndContinue(DEFAULT_TIMEOUT_SECONDS, command);
   }
   
   /**
    * Executes the provided command and encapsulates the outputs into a {@link LocalProcessStream}
    * for future verification. The execution must complete within the timeout alotted (in milliseconds)
    * or an exception response will be logged<br>
    * <br>
    * Opens up the process and updates the stream on a separate thread, allowing tests to continue.
    * 
    * @param timoutInMs Time, in milliseconds, to allow for the process to complete
    * @param command Variable list of strings building the entire command. The first entry is
    *           assumed to be the externally executable program file and the rest of the entries the
    *           arguments to that application. It is good practice to separate each argument into a
    *           separate entry but different Operating Systems may have specific requirements for
    *           this.<br>
    *           <br>
    *           Example call: executeProcess(2000, "java", "-version")
    * @return A response object capable of verifying the various outputs of the execution.
    */
   public LocalProcessStream executeProcessAndContinue(long timoutInMs, String... command) {
      final ExecutorService processThread = Executors.newSingleThreadExecutor();
      final ExecutorService timeoutExecutor = Executors.newSingleThreadExecutor();
      LocalProcContinueRunnable myRunnable = new LocalProcContinueRunnable(command);
      LocalProcessStream response = myRunnable.getResponse();
      processThread.execute(myRunnable);
          
      timeoutExecutor.execute(new Runnable() {
         @Override
         public void run() {
            try {
               boolean completed = processThread.awaitTermination(timoutInMs, TimeUnit.MILLISECONDS);
               if(completed) {
                  if(!response.isDone())
                     myRunnable.close();
                       
               } else {
                  myRunnable.stop();
                  Exception ex = new OseeCoreException("Timed out executing local process after %d %s",
                                                       timoutInMs, TimeUnit.MILLISECONDS);
                  response.exception(ex);
               }
            }
            catch (InterruptedException ex) {
               response.exception(ex);
            } 
         }
      });
      
      processThread.shutdown();
      timeoutExecutor.shutdown();
      
      return response;
   }
   
   private class LocalProcContinueRunnable implements Runnable {

      private final String[] command;
      private volatile LocalProcessStream response;
      private volatile Process process;

      /**
       * @param command
       */
      public LocalProcContinueRunnable(String[] command) {
         this.command = command;
         this.response = new LocalProcessStream(command);
      }

      /**
       * @return the response
       */
      public LocalProcessStream getResponse() {
         return response;
      }
      
      /**
       * forcefully stop the process
       */
      public void stop() {
         process.destroy();
      }

      /**
       * close the response stream with the current process exit Value
       */
      public void close() {
         response.close(process.exitValue());
      }

      @Override
      public void run() {
         ProcessBuilder builder = new ProcessBuilder(command);
         try {
            process = builder.start();
            
            response.addProcess(process);
            
            InputStream in = process.getInputStream();
            InputStream err = process.getErrorStream();
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            
            //TODO: Replace when updated to java 9+
            
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
               response.update(outputStream.toString(), errorStream.toString());
               
               if (! process.isAlive()) {
                  if ((in.available() > 0) || (err.available() > 0))
                     continue;
                  else
                     break;
               }
               try {
                  Thread.sleep(1000);
               } catch (InterruptedException e) {
                  response.exception(e);
               }
            }
         }
         catch (Exception ex) {
            response.exception(ex);
         }
      }

   }
}
