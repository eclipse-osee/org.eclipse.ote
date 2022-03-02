/*******************************************************************************
 * Copyright (c) 2022 Boeing.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.io.pubsub.service;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.ref.WeakReference;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.util.benchmark.Benchmark;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.GCHelper;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.message.MessageSystemException;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.interfaces.MessageDataLookup;
import org.eclipse.osee.ote.message.interfaces.Namespace;
import org.eclipse.osee.ote.messaging.dds.entity.Publisher;
import org.eclipse.ote.io.pubsub.BasicLogicalParticipant;
import org.eclipse.ote.io.pubsub.OtePubSubParticipants;
import org.eclipse.ote.io.pubsub.config.PubSubEthernetConfigurationProvider;

/**
 * @author Michael P. Masterson
 */
public class PubSubReceive {

   private static final int MESSAGE_PREAMBLE_SIZE = 64;

   private static final boolean USE_DEFAULT_SU_PORTS = Boolean.parseBoolean(System.getProperty("ote.ofp.su.defaultport", "false"));

   private static final int DATA_SIZE = 65536;

   private static final int PUB_SUB_TIMEOUT = 240000; // 4 MINUTES

   private static final int ONE_MEG = 1000000;

   private static final String OTE_PUBSUB_RECEIVE = "ote.pubsub.receive.debug";

   private DatagramChannel channel;
   private final ByteBuffer buffer = ByteBuffer.allocateDirect(DATA_SIZE);
   private ReceiverThread myThread;
   private final BasicLogicalParticipant ote;
   private final WeakReference<TestEnvironment> env;
   private final MessageDataLookup dataLookup;

   private final PubSubEthernetConfigurationProvider config;

   public PubSubReceive(Namespace namespace, Publisher publisher, MessageDataLookup pubsubLookup, PubSubEthernetConfigurationProvider config, TestEnvironment env) throws IOException {
      GCHelper.getGCHelper().addRefWatch(this);
      this.ote = OtePubSubParticipants.OTE;
      this.config = config;

      this.env = new WeakReference<TestEnvironment>(env);
      this.dataLookup = pubsubLookup;

   }

   public int getPort() {
      if (channel != null) {
         return channel.socket().getLocalPort();
      } else {
         return 0;
      }
   }

   private void processBuffer(ByteBuffer buffer) {
      int initialPosition = buffer.position();
      int offset = 0;
      int byteBoundary = 0;
      int length = buffer.remaining();

      while (length - offset >= MESSAGE_PREAMBLE_SIZE && offset < length) {
         // populate the header
         buffer.position(offset);
         int msgId = buffer.getInt();
         int headerSize = buffer.getShort();
         int bodySize = buffer.getInt();

         int size = headerSize + bodySize;

         buffer.position(offset);
         MessageData data = dataLookup.getById(msgId);
         if (data != null) {
            if(buffer.remaining() >= size ) {
               data.copyData(0, buffer, size);
               data.incrementActivityCount();
               data.notifyListeners();
            }
         }
         offset = offset + size;

         if (length - offset < MESSAGE_PREAMBLE_SIZE && offset >= length
            && length - offset != 0) {
            OseeLog.log(
               PubSubReceive.class,
               Level.WARNING,
               String
               .format(
                  "No more buffer processing but we still have [ %d ] bytes from the socket.",
                  buffer.remaining()));
         }

         // make sure to go to the next 8 byte boundary
         if ((byteBoundary = offset % 8) > 0) {
            offset += (8 - byteBoundary);
         }
      }
   }

   public void run() throws MessageSystemException {
      final Benchmark bm = new Benchmark("Pub Sub Receiver Process Time", 5000);
      InetSocketAddress oteAddress = this.config.getAddress(OtePubSubParticipants.OTE.getName());
      OseeLog.log(PubSubReceive.class, Level.INFO,"OTE participant address = " + oteAddress.toString());
      Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
      try{
         while(myThread.isRunning()){
            try {
               channel = DatagramChannel.open();
               channel.socket().setReuseAddress(true);
               channel.socket().bind(oteAddress);
               myThread.setName("PubSub Message Receiver Thread (bound to " + oteAddress.toString() + ")");
               channel.socket().setSoTimeout(PUB_SUB_TIMEOUT);
               channel.socket().setReceiveBufferSize(ONE_MEG);
               channel.configureBlocking(true);
               OseeLog.log(PubSubReceive.class, Level.INFO, String.format("Pub sub receiver started. OTE port=%d", oteAddress.getPort()));

               while (myThread.isRunning()) {

                  buffer.clear();
                  channel.receive(buffer);

                  bm.startSample();
                  buffer.flip();
                  processBuffer(buffer);
                  bm.endSample();
               }
            } catch (BindException ex) {
               OseeLog.log(PubSubReceive.class, Level.FINEST, ex);
               channel.close();
               Thread.sleep(1000);
            }
         }
      }catch (InterruptedIOException ex) {
         // thread has been interrupted
         Thread.interrupted();
         if (myThread.isRunning()) {
            /*
             * we are still supposed to be running, something other than the
             * kill method interrupted us
             */
            OseeLog.log(PubSubReceive.class, Level.WARNING, myThread
               .getName()
               + ": Unexpected interruption", ex);
         }
      } catch (ClosedByInterruptException ie) {
         // thread has been interrupted
         Thread.interrupted();
         if (myThread.isRunning()) {
            /*
             * we are still supposed to be running, something other than the
             * kill method interrupted us
             */
            OseeLog.log(PubSubReceive.class, Level.WARNING, myThread
               .getName()
               + ": Unexpected interruption", ie);
         }
      } catch (Throwable t) {
         myThread.handleRunException(t);
      } finally {
         try {
            if (channel != null) {
               channel.close();
               assert (channel.socket().isClosed());
            }
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      }
   }

   public void startThread() {
      myThread = new ReceiverThread("PubSub Message Receiver Thread", env
         .get()) {
         @Override
         protected void run() throws MessageSystemException {
            setRunning(true);
            PubSubReceive.this.run();
         }

      };
      myThread.start();
   }

   public void destroy() {
      try{
         if(myThread != null){
            myThread.setRunning(false);
            myThread.interrupt();
         }
      } catch (Throwable th){
         th.printStackTrace();
      }
   }

}
