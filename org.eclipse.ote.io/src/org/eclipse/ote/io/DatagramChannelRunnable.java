package org.eclipse.ote.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;

public abstract class DatagramChannelRunnable implements Runnable {

   private LinkedBlockingQueue<DatagramChannelData> data;
   private DatagramChannel channel;
   private InetSocketAddress address;   
   
   DatagramChannelRunnable(InetSocketAddress address){
      this.address = address;
   }
   
   void setQueue(LinkedBlockingQueue<DatagramChannelData> data) {
      this.data = data;
   }
   
   @Override
   public void run() {
      try {
         channel = openAndInitializeDatagramChannel(address); 
         boolean keepRunning = true;
         List<DatagramChannelData> dataToSend = new ArrayList<DatagramChannelData>();
         while(keepRunning){
            try{
               dataToSend.clear();
               data.drainTo(dataToSend);
               for(DatagramChannelData datagramChannelData: dataToSend){
                  if(datagramChannelData == DatagramChannelWorker.POISON_PILL){
                     keepRunning = false;
                  }
               }
               if(keepRunning){
                  doSend(channel, dataToSend);
               }
            } catch (ClosedByInterruptException ex){
               OseeLog.log(getClass(), Level.SEVERE, "Error trying to send data", ex);
               channel = openAndInitializeDatagramChannel(address);
            } catch (AsynchronousCloseException ex){
               OseeLog.log(getClass(), Level.SEVERE, "Error trying to send data", ex);
               channel = openAndInitializeDatagramChannel(address);
            } catch (ClosedChannelException ex){
               OseeLog.log(getClass(), Level.SEVERE, "Error trying to send data", ex);
               channel = openAndInitializeDatagramChannel(address);
            } catch (IOException ex){
               OseeLog.log(getClass(), Level.SEVERE, "Error trying to send data", ex);
            } finally {
               for(DatagramChannelData datagramChannelData: dataToSend){
                  datagramChannelData.postProcess();
               }
            }
         } 
      } catch (IOException ex){
         OseeLog.log(getClass(), Level.SEVERE, "Error opening DatagramChannel.  Ending DatagramChannelRunnable unexpectedly.", ex);
      } finally{
         try {
            channel.close();
         } catch (IOException e) {
            OseeLog.log(getClass(), Level.SEVERE, "Error trying to send data", e);
         }
      }
   }

   public abstract void doSend(DatagramChannel channel2, List<DatagramChannelData> dataToSend) throws ClosedChannelException, AsynchronousCloseException, ClosedByInterruptException, IOException;

   public abstract DatagramChannel openAndInitializeDatagramChannel(InetSocketAddress address) throws IOException;
   
}
