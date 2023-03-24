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
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.logging.Level;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.api.local.LocalProcessResponse;
import org.eclipse.osee.ote.core.TestException;
import org.eclipse.osee.ote.core.annotations.Order;
import org.eclipse.osee.ote.core.environment.EnvironmentTask;
import org.eclipse.osee.ote.core.environment.jini.ITestEnvironmentCommandCallback;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.osee.ote.rest.OteRestResponse;
import org.eclipse.ote.io.mux.MuxHeader;
import org.eclipse.ote.simple.io.SimpleDataType;
import org.eclipse.ote.simple.io.SimpleMuxReceiver;
import org.eclipse.ote.simple.io.message.HELLO_WORLD;
import org.eclipse.ote.simple.io.message.SIMPLE_MUX_R_MSG;
import org.eclipse.ote.simple.io.message.lookup.SimpleMuxReceiverHeader;
import org.eclipse.ote.simple.test.environment.SimpleOteApi;
import org.eclipse.ote.simple.test.script.endpoints.CustomSimpleEndpoint;
import org.junit.Test;

/**
 * @author Andy Jury
 */
public class SimpleTestScript extends SimpleMessageSystemTestScript {

   HELLO_WORLD writer;

   public SimpleTestScript(MessageSystemTestEnvironment testEnvironment, ITestEnvironmentCommandCallback callback) {
      super(testEnvironment, callback);

      this.writer = getMessageWriter(HELLO_WORLD.class);
   }

   @Test
   @Order(1)
   public void testCase1(SimpleOteApi oteApi) {
      oteApi.prompt("In TestCase1");
      oteApi.promptPause("In TestCase1");
      oteApi.promptPassFail("Pass/Fail?");
   }

   @Test
   @Order(2)
   public void testCase2(SimpleOteApi oteApi) {
      // This test case will fail when running in an environment with Mux
      // unless you uncomment the following line to force the message mem type
      writer.setMemSource(SimpleDataType.SIMPLE);
      oteApi.prompt("In the LocalSetupTestCase");
      writer.PRINT_ME.setNoLog("TEST1");
      oteApi.testWait(1000);
      writer.PRINT_ME.setNoLog("TEST2");
      oteApi.testWait(1000);
      writer.PRINT_ME.setNoLog("TEST3");
      oteApi.testWait(1000);
      writer.PRINT_ME.setNoLog("TEST4");
      writer.ONLY_IN_SIMPLE.setNoLog(64);
      writer.send();
      oteApi.testWait(1000);
      writer.unschedule();
   }

   @Test
   @Order(3)
   public void testCaseSend(SimpleOteApi oteApi) {
      try {
         MuxChannelSender sender = new MuxChannelSender();
         environment.addTask(sender);

         oteApi.testWait(1000);
         sender.disable();
      } catch (IOException ex) {
         logTestPoint(false, "Error starting packet sender", "N/A", ex.getMessage());
      }
   }

   @Test
   @Order(4)
   public void restTestCase(SimpleOteApi oteApi) {
      prompt("Running REST Test Case");

      OteRestResponse dataOne = oteApi.rest().endpoint1().getDataOne(this);
      String content = dataOne.getContents(String.class);
      prompt(content);

      dataOne.verifyResponseCode(this, Status.OK);
      dataOne.verifyResponseFamily(this, Response.Status.Family.SUCCESSFUL);
      dataOne.verifyContentsContains(this, "linux");

      // Should Fail
      dataOne.verifyContentsEquals(this, "linux");

      CustomSimpleEndpoint mySpecialEndpoint = new CustomSimpleEndpoint(oteApi);
      OteRestResponse customData = mySpecialEndpoint.getCustomData();
      customData.verifyResponseCode(this, Status.NOT_FOUND);

      InputStream inputStream = OseeInf.getResourceAsStream("RestPostFile.txt", getClass());
      OteRestResponse postResponse = oteApi.rest().endpoint1().postFile(this, inputStream);
      postResponse.verifyResponseCode(this, Status.OK);
   }

   @Test
   @Order(5)
   public void localProcessTestCase(SimpleOteApi oteApi) {
      LocalProcessResponse executeProcess = oteApi.localProcess().executeProcess("java", "-version");
      executeProcess.verifyExitCode(this, "", LocalProcessResponse.OK_CODE);
      executeProcess.verifyErrorStreamContains(this, "", "SE Runtime Environment");
      // This will fail
      executeProcess.verifyOutputStreamContains(this, "", "SE Runtime Environment");

      // Extra dash will cause jvm to throw exception
      executeProcess = oteApi.localProcess().executeProcess("java", "--version");
      executeProcess.verifyExitCode(this, "", 1);
      // These should all fail
      executeProcess.verifyErrorStreamContains(this, "", "SE Runtime Environment");
      executeProcess.verifyOutputStreamContains(this, "", "SE Runtime Environment");

      // This will test the timeout as this ssh will take a long time to resolve
      executeProcess = oteApi.localProcess().executeProcess("ssh", "www.github.com");
      // These should all fail
      executeProcess.verifyExitCode(this, "", 1);
      executeProcess.verifyErrorStreamContains(this, "", "SE Runtime Environment");
      executeProcess.verifyOutputStreamContains(this, "", "SE Runtime Environment");

   }

   private class MuxChannelSender extends EnvironmentTask {

      private final SIMPLE_MUX_R_MSG sendMsg = new SIMPLE_MUX_R_MSG();
      private final SimpleMuxReceiverHeader header = new SimpleMuxReceiverHeader();
      private int counter = 1;
      private final DatagramChannel datagramChannel;
      private final ByteBuffer buffer;
      private final InetSocketAddress socket;

      public MuxChannelSender() throws IOException {
         super(1.0);
         datagramChannel = DatagramChannel.open();
         int payloadSize = sendMsg.getDefaultByteSize() + MuxHeader.MUX_HEADER_BYTE_SIZE;
         buffer = ByteBuffer.allocate(payloadSize + header.getDefaultByteSize());
         InetAddress receiveAddress = InetAddress.getLocalHost();
         socket = new InetSocketAddress(receiveAddress, SimpleMuxReceiver.SIMPLE_MUX_RECEIVE_PORT);

      }

      @Override
      public void runOneCycle() throws TestException {
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
         } catch (IOException ex) {
            OseeLog.log(MuxChannelSender.class, Level.WARNING, "Error sending test packet to " + socket.toString(), ex);
         }
      }

   }
}
