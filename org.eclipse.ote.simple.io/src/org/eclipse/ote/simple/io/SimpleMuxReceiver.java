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
package org.eclipse.ote.simple.io;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.OseeTestThread;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.message.MessageSystemException;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.ote.io.mux.MuxReceiveTransmit;
import org.eclipse.ote.io.mux.lookup.MuxDataLookup;
import org.eclipse.ote.io.mux.lookup.MuxLookupKey;
import org.eclipse.ote.simple.io.message.lookup.SimpleMuxReceiverHeader;

/**
 * @author Michael P. Masterson
 */
public class SimpleMuxReceiver {

   public static final int SIMPLE_MUX_RECEIVE_PORT = 22222;
   private static final int TIMEOUT_IN_MS = 10000;
   private static final int DATA_SIZE = 65536;
   private static final int ONE_MEG = 1000000;
   private InetAddress receiveAddress;
   private DatagramChannel channel;
   private OseeTestThread myThread;
   private boolean isRunning;
   private final ByteBuffer buffer = ByteBuffer.allocateDirect(DATA_SIZE);
   private TestEnvironment env;
   private MuxDataLookup lookup;
   private SimpleMuxReceiverHeader header = new SimpleMuxReceiverHeader();
   private byte[] headerArray = new byte[2];
   private MuxLookupKey lookupKey = new MuxLookupKey();


   /**
    * @param env
    * @param muxDataLookup
    */
   public SimpleMuxReceiver(TestEnvironment env, MuxDataLookup muxDataLookup) {
      this.env = env; 
      this.lookup = muxDataLookup;
   }

   public void startThread() {
      myThread = new OseeTestThread("Simple Mux Receiver Test Thread", env) {

         @Override
         protected void run() throws Exception {
            setRunning(true);
            SimpleMuxReceiver.this.run();
         }
      };
      myThread.start();
   }

   public void destroy() {
      System.out.println("Killing Simple Mux Receiver");
      try{
         if(myThread != null){
            setRunning(false);
            myThread.interrupt();
         }
      } catch (Throwable th){
         th.printStackTrace();
      }
   }

   protected void setRunning(boolean isRunning) {
      this.isRunning = isRunning;
   }

   public void run() {
      try{
         this.receiveAddress = InetAddress.getLocalHost();
         final InetSocketAddress socket = new InetSocketAddress(receiveAddress, SIMPLE_MUX_RECEIVE_PORT);
         OseeLog.log(SimpleMuxReceiver.class, Level.INFO,
                     "Simple Mux Receive address = " + socket.toString());
         Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
         while(isRunning()){
            try {
               channel = DatagramChannel.open();
               channel.socket().setReuseAddress(true);
               channel.socket().bind(socket);
               myThread.setName("Simple Mux Receiver Test Thread (bound to " + socket.toString() + ")");
               channel.socket().setSoTimeout(TIMEOUT_IN_MS);
               channel.socket().setReceiveBufferSize(ONE_MEG);
               channel.configureBlocking(true);
               OseeLog.log(SimpleMuxReceiver.class, Level.INFO, String.format(
                                                                              "Simple Mux Receiver started. port=%d", SIMPLE_MUX_RECEIVE_PORT));

               while (isRunning()) {

                  buffer.clear();
                  channel.receive(buffer);
                  buffer.flip();
                  processBuffer(buffer);
               }
            } catch (BindException ex) {
               OseeLog.log(SimpleMuxReceiver.class, Level.FINEST, ex);
               channel.close();
               Thread.sleep(1000);
            }
         }
      }catch (InterruptedIOException ex) {
         // thread has been interrupted
         Thread.interrupted();
         if (isRunning()) {
            /*
             * we are still supposed to be running, something other than the
             * kill method interrupted us
             */
            OseeLog.log(SimpleMuxReceiver.class, Level.WARNING,
                        myThread.getName() + ": Unexpected interruption", ex);
         }
      } catch (ClosedByInterruptException ie) {
         // thread has been interrupted
         Thread.interrupted();
         if (isRunning()) {
            /*
             * we are still supposed to be running, something other than the
             * kill method interrupted us
             */
            OseeLog.log(SimpleMuxReceiver.class, Level.WARNING,
                        myThread.getName() + ": Unexpected interruption", ie);
         }
      } catch (Throwable t) {
         handleRunException(t);
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

   private void processBuffer(ByteBuffer buffer) {
      buffer.get(headerArray);
      header.setData(headerArray);
      System.out.println("Received mux transmission = " + header);
      int size = buffer.remaining();
      
      lookupKey.channel = header.channel.getNoLog();
      lookupKey.rt = header.remoteTerminal.getNoLog();
      lookupKey.receiveTransmit = header.isReceive.getNoLog()?MuxReceiveTransmit.RECEIVE:MuxReceiveTransmit.TRANSMIT;
      lookupKey.subaddress = header.subaddress.getNoLog();
      
      MessageData data = lookup.get(lookupKey);
      if (data != null) {
         data.copyData(0, buffer, size);
         data.incrementActivityCount();
         data.notifyListeners();
      } 
   }

   private void handleRunException(Throwable t) {
      if(isRunning()){//suppress the log when we kill the reciever
         throw new MessageSystemException("Simple Mux Receiver Test Thread has stopped receiving messages and has been terminated", Level.SEVERE, t);
     }
  }

   /**
    * @return
    */
   private boolean isRunning() {
      return isRunning;
   }

}
