package org.eclipse.ote.io;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;

public class DatagramChannelDataPool {

   private final LinkedBlockingQueue<DatagramChannelData> data;
   private final DatagramChannelDataFactory factory;
   
   public DatagramChannelDataPool(DatagramChannelDataFactory factory, int max){
      this.factory = factory;
      data = new LinkedBlockingQueue<DatagramChannelData>(max);
      for(int i = 0; i < max; i++){
         data.offer(factory.create(this));
      }
   }
   
   public DatagramChannelData get() throws InterruptedException{
      DatagramChannelData datagramChannelData = data.poll(500, TimeUnit.MILLISECONDS);
      if(datagramChannelData == null){
         OseeLog.log(getClass(), Level.WARNING, "Timed out waiting for datagram send object");
         datagramChannelData = factory.create(this);
      }
      return datagramChannelData;
   }
   
   public void offer(DatagramChannelData datagramChannelData){
      data.offer(datagramChannelData);
   }
}
