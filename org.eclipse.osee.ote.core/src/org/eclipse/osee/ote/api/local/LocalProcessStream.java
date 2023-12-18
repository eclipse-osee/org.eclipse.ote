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
package org.eclipse.osee.ote.api.local;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Dominic Leiner
 */
public class LocalProcessStream {

   public final static int OK_CODE = 0;
   public final static int EXCEPTION = Integer.MIN_VALUE;
   
   private final String[] command;

   private LocalProcessResponse response;
   private Process process;
   private boolean isDone;

   public LocalProcessStream(String[] command) {
      this(command, Strings.EMPTY_STRING, Strings.EMPTY_STRING, 0);
   }
   
   public LocalProcessStream(String[] command, String output, String error, int exitCode) {
      this.response = new LocalProcessResponse(command, output, error, exitCode);
      this.command = command;
      this.isDone = false;
   }
   
   /**
    * Returns the current response object from the process.
    * <b> The returned object will not update as the process continues. </b>
    * @return {@link LocalProcessResponse}}
    */
   public LocalProcessResponse getResponse() {
      return this.response;
   }
   
   /**
    * Return if the process is done.
    */
   public boolean isDone() {
      return isDone;
   }
   
   /**
    * Signals the process to destroy & closes the response object.
    * Sets response to Exception: Process killed.
    */
   public void kill() {
      if(process!=null)
         process.destroy();
      
      Exception ex = new OseeCoreException("Process kill called.");
      exception(ex);
   }
   
   protected void addProcess(Process process) {
      this.process = process;
   }
   
   protected void close(int exitCode) {
      this.isDone = true;
      response = new LocalProcessResponse(command, response.getOutput(), response.getError(), exitCode);         
   }

   protected void update(String output, String error) {
      response = new LocalProcessResponse(command, output, error, response.getExitCode());
   }

   protected void exception(Exception ex) {
      if(response!=null) {
         response = new LocalProcessExceptionResponse(ex, command, response.getOutput(),
            response.getError(),
            EXCEPTION);         
      } else {
         response = new LocalProcessExceptionResponse(ex, command, Strings.EMPTY_STRING,
            Strings.EMPTY_STRING,
            EXCEPTION);
      }
      
      this.isDone = true;
   }
   
   /**
    * Verifies the exit code from the local process is exactly equal to the code parameter
    * 
    * @param accessor For logging
    * @param testPointName
    * @param expectedCode Expected status code
    */
   public void verifyExitCode(ITestAccessor accessor, String testPointName, int expectedCode) {
       this.getResponse().verifyExitCode(accessor, testPointName, expectedCode);
   }

   /**
    * Verifies the output stream from the local process contains the provided string
    * 
    * @param accessor For logging
    * @param testPointName
    * @param str Substring to match in the output stream
    */
   public void verifyOutputStreamContains(ITestAccessor accessor, String testPointName, String str) {
       this.getResponse().verifyOutputStreamContains(accessor, testPointName, str);
   }

   /**
    * Verifies the error stream from the local process contains the provided string
    * 
    * @param accessor For logging
    * @param testPointName
    * @param str Substring to match in the error stream
    */
   public void verifyErrorStreamContains(ITestAccessor accessor, String testPointName, String str) {
       this.getResponse().verifyErrorStreamContains(accessor, testPointName, str);
   }
   
   
   public String toString() {
         return getClass().getSimpleName()  + " {\n\tisDone: " + isDone + 
            "\n\tResponse: " + response.toString().replace("\n", "\n\t") + "\n}";
   }
}
