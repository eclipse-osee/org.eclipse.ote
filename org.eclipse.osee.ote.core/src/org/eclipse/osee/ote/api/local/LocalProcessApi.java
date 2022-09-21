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

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Michael P. Masterson
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
    * for future verification. The execution must complete within the timeout alotted (in seconds)
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
      final ExecutorService threadPool = Executors.newSingleThreadExecutor();
      LocalProcessResponse response;
      LocalProcRunnable myRunnable = new LocalProcRunnable(command);
      threadPool.execute(myRunnable);
      threadPool.shutdown();

      try {
         boolean completed = threadPool.awaitTermination(timoutInMs, TimeUnit.MILLISECONDS);
         if(completed) {
            response = myRunnable.getResponse();
         } else {
            Exception ex = new OseeCoreException("Timed out executing local process after %d %s",
                                                 timoutInMs, TimeUnit.MILLISECONDS);
            response = new LocalProcessExceptionResponse(ex, command, Strings.EMPTY_STRING,
                                                        Strings.EMPTY_STRING,
                                                        Integer.MIN_VALUE);
         }
      }
      catch (InterruptedException ex) {
         response = new LocalProcessExceptionResponse(ex, command, Strings.EMPTY_STRING,
                                                      Strings.EMPTY_STRING,
                                                      Integer.MIN_VALUE);
      }
      return response;
   }
   
   private class LocalProcRunnable implements Runnable {

      private final String[] command;
      private LocalProcessResponse response;

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

      @Override
      public void run() {
         ProcessBuilder builder = new ProcessBuilder(command);
         Process process;
         try {
            process = builder.start();
            String output = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
            String error = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
            int exitCode = process.exitValue();
            response = new LocalProcessResponse(command, output, error, exitCode);
         }
         catch (Exception ex) {
            response = new LocalProcessExceptionResponse(ex, command, Strings.EMPTY_STRING,
                                                         Strings.EMPTY_STRING, Integer.MIN_VALUE);
         }
      }

   }
}
