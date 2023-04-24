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
package org.eclipse.osee.ote.remote.terminal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;
import com.jcraft.jsch.ChannelExec;

/**
 * @author Dominic Leiner
 */
public class OteRemoteTerminalResponseStream {
   
   private OteRemoteTerminalResponse response;
   
   private ByteArrayOutputStream stdOutStream;
   private ByteArrayOutputStream stdErrStream;
   private ChannelExec channel;
   private boolean closed;
   private boolean kill;

   public OteRemoteTerminalResponseStream(ByteArrayOutputStream stdOutStream, ByteArrayOutputStream stdErrStream) {
      this.stdOutStream = stdOutStream;
      this.stdErrStream = stdErrStream;
      initialize();
   }

   public OteRemoteTerminalResponseStream() {
      this.stdOutStream = new ByteArrayOutputStream();
      this.stdErrStream = new ByteArrayOutputStream();
      initialize();
   }
   
   private void initialize() {
      this.closed = false;
      this.kill = false;
   }

   /**
    * Verifies that the standard output of the remote terminal response is exactly
    * equal to the expected parameter
    * 
    * @param accessor For logging
    * @param expected Expected output
    */
   public void verifyStandardOut(ITestAccessor accessor, String expected) {
      checkClosed();
      response.verifyStandardOut(accessor, expected);
   }

   /**
    * Verifies that the standard output of the remote terminal response contains
    * the expected substring parameter
    * 
    * @param accessor  For logging
    * @param subString Expected substring
    */
   public void verifyStandardOutContains(ITestAccessor accessor, String subString) {
      checkClosed();
      response.verifyStandardOutContains(accessor, subString);
   }

   /**
    * Verifies that the standard error of the remote terminal response is exactly
    * equal to the expected parameter
    * 
    * @param accessor
    * @param expected
    */
   public void verifyStandardError(ITestAccessor accessor, String expected) {
      checkClosed();
      response.verifyStandardError(accessor, expected);
   }

   /**
    * Verifies that the standard error of the remote terminal response contains the
    * expected substring parameter
    * 
    * @param accessor
    * @param subString
    */
   public void verifyStandardErrorContains(ITestAccessor accessor, String subString) {
      checkClosed();
      response.verifyStandardErrorContains(accessor, subString);
   }

   /**
    * Verifies that the exit code of the remote terminal response is exactly equal
    * to the expected parameter
    * 
    * @param accessor
    * @param expected
    */
   public void verifyExitCode(ITestAccessor accessor, int expected) {
      checkClosed();
      response.verifyExitCode(accessor, expected);
   }

   /**
    * Returns the standard output of the remote terminal response
    * 
    * @return Standard output string
    */
   public String getStdOut() {
      checkClosed();
      return response.getStdOut();
   }

   /**
    * Returns the standard error of the remote terminal response
    * 
    * @return Standard error string
    */
   public String getStdErr() {
      checkClosed();
      return response.getStdErr();
   }

   /**
    * Returns the exit code of the remote terminal response
    * 
    * 
    * @return
    */
   public int getExitCode() {
      checkClosed();
      return response.getExitCode();
   }
   
   /**
    * Return if the streams from the remote terminal has been closed.
    * 
    * @return
    */
   public boolean isClosed() {
      return closed;
   }
   
   //RemoteTerminal to signal that the stream has been closed.
   protected void closed(int exitCode) {
      this.response = new OteRemoteTerminalResponse(stdOutStream.toString(), stdErrStream.toString(), exitCode);
      this.closed = true;
      this.stdOutStream = null;
      this.stdErrStream = null;
   }
   
   protected void exception(OteRemoteTerminalResponse response) {
      this.closed = true;
      this.response = response;
   }
   
   private void checkClosed() {
      if(!closed) {
         //if not closed, update the response
         this.response = new OteRemoteTerminalResponse(stdOutStream.toString(), stdErrStream.toString(), 0);
      }
   }
   
   protected ByteArrayOutputStream getStdOutStream() {
      return stdOutStream;
   }
   
   protected ByteArrayOutputStream getStdErrStream() {
      return stdErrStream;
   }
   
   /**
    * Signal to kill the command
    * This will immediately disconnect, causing the streams to close.
    * Exit code will be -1.
    */
   public void kill() {
      if(channel != null)
         channel.disconnect();
         
      this.kill = true;
   }

   protected boolean getKill() {
      return kill;
   }
   
   protected void addChannel(ChannelExec channel) {
      this.channel = channel;
   }
   
   protected ChannelExec getChannel() {
      return this.channel;
   }
   
}
