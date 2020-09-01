/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.ote.simple.test.script;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.TestCase;
import org.eclipse.osee.ote.core.TestException;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.EnvironmentTask;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.environment.interfaces.ITestLogger;
import org.eclipse.osee.ote.core.environment.jini.ITestEnvironmentCommandCallback;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.ote.io.mux.MuxHeader;
import org.eclipse.ote.simple.io.SimpleDataType;
import org.eclipse.ote.simple.io.SimpleMuxReceiver;
import org.eclipse.ote.simple.io.message.HELLO_WORLD;
import org.eclipse.ote.simple.io.message.SIMPLE_MUX_R_MSG;
import org.eclipse.ote.simple.io.message.lookup.SimpleMuxReceiverHeader;

/**
 * @author Andy Jury
 */
public class SimpleTestScript extends SimpleTestScriptType {
   
   HELLO_WORLD writer;
   
   public SimpleTestScript(MessageSystemTestEnvironment testEnvironment, ITestEnvironmentCommandCallback callback) {
      super(testEnvironment, callback);
      
      this.writer = getMessageWriter(HELLO_WORLD.class);

      new TestCase1(this);
      new TestCase2(this);
      new TestCaseSend(this);
   }

   private class LocalSetupTestCase extends TestCase {

      protected LocalSetupTestCase(TestScript parent) {

         super(parent, false, false);
      }

      public void doTestCase(ITestEnvironmentAccessor environment, ITestLogger logger) {}
   }

   protected TestCase getSetupTestCase() {

      return new LocalSetupTestCase(this);
   }

   public class TestCase1 extends TestCase {

      public TestCase1(TestScript parent) {

         super(parent);
      }

      public void doTestCase(ITestEnvironmentAccessor environment, ITestLogger logger) {
         prompt("In TestCase1");
         promptPause("In TestCase1");
         promptPassFail("Pass/Fail?");
      }
   }

   public class TestCase2 extends TestCase {

      public TestCase2(TestScript parent) {

         super(parent);
      }

      public void doTestCase(ITestEnvironmentAccessor environment, ITestLogger logger) {
         // This test case will fail when running in an environment with Mux
         // unless you uncomment the following line to force the message mem type
         writer.setMemSource(SimpleDataType.SIMPLE);
         prompt("In the LocalSetupTestCase");
         writer.PRINT_ME.set(this, "TEST1");
         testWait(1000);
         writer.PRINT_ME.setNoLog("TEST2");
         testWait(1000);
         writer.PRINT_ME.setNoLog("TEST3");
         testWait(1000);
         writer.PRINT_ME.setNoLog("TEST4");
         writer.ONLY_IN_SIMPLE.set(this, 64);
         writer.send();
         testWait(1000);
         writer.unschedule();
      }
   }
   
   public class TestCaseSend extends TestCase {

      public TestCaseSend(TestScript parent) {

         super(parent);
      }

      public void doTestCase(ITestEnvironmentAccessor environment, ITestLogger logger) {
         try {
            MuxChannelSender sender = new MuxChannelSender();
            environment.addTask(sender);
            
            testWait(10000);
            sender.disable();
         }
         catch (IOException ex) {
            logTestPoint(false, "Error starting packet sender", "N/A", ex.getMessage());
         }
      }
   }
   
   private class MuxChannelSender extends EnvironmentTask {

      private SIMPLE_MUX_R_MSG sendMsg = new SIMPLE_MUX_R_MSG();
      private SimpleMuxReceiverHeader header = new SimpleMuxReceiverHeader();
      private int counter = 1;
      private DatagramChannel datagramChannel;
      private ByteBuffer buffer;
      private InetSocketAddress socket;
      
      
      public MuxChannelSender() throws IOException {
         super(1.0);
         datagramChannel = DatagramChannel.open();
         int payloadSize = sendMsg.getDefaultByteSize() + MuxHeader.MUX_HEADER_BYTE_SIZE;
         buffer = ByteBuffer.allocate(payloadSize + header.getDefaultByteSize());
         InetAddress receiveAddress = InetAddress.getLocalHost();
         socket = new InetSocketAddress(receiveAddress, SimpleMuxReceiver.SIMPLE_MUX_RECEIVE_PORT);
         
      }

      @Override
      public void runOneCycle() throws InterruptedException, TestException { 
         sendMsg.MUX_SPECIFIC_ELEMENT.setNoLog((double) counter++);
         header.fillInBytes(sendMsg);
         
         byte[] headerData = header.getData();
         byte[] muxData = sendMsg.getData();
         
         buffer.clear();
         buffer.put(headerData);
         buffer.put(muxData);
         buffer.flip();
         try {
            System.out.printf("%d: Sending to %s\n", System.currentTimeMillis(), socket.toString());
            datagramChannel.send(buffer, socket);
         }
         catch (IOException ex) {
            OseeLog.log(MuxChannelSender.class, Level.WARNING, "Error sending test packet to " + socket.toString(), ex);
         }
      }
      
   }
}
