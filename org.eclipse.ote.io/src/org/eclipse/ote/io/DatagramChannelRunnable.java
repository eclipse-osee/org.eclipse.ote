package org.eclipse.ote.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;

public abstract class DatagramChannelRunnable implements Runnable {

	private ArrayBlockingQueue<DatagramChannelData> data;
	private ThreadLocal<DatagramChannel> classChannel;   

	private InetSocketAddress address;

	public DatagramChannelRunnable(InetSocketAddress address){
		this.address = address;
	}
	
	public DatagramChannelRunnable(){
   }

	void setQueue(ArrayBlockingQueue<DatagramChannelData> data) {
		this.data = data;
	}

	@Override
	public void run() {
	   DatagramChannel threadChannel = null; 
		try {
			threadChannel = openAndInitializeDatagramChannel(address); 
			boolean keepRunning = true;
			final List<DatagramChannelData> dataToSend = new ArrayList<DatagramChannelData>(32);
			while(keepRunning){
				try{
					dataToSend.clear();
					if (data.drainTo(dataToSend) < 1) {
						try {
							// block until something is available
							dataToSend.add(data.take());							
						} catch (InterruptedException e) {
							keepRunning = false;
							continue;
						}
					}
					int size = dataToSend.size();
					for (int i = 0; i < size; i++) {
						DatagramChannelData data = dataToSend.get(i);
						if (data == DatagramChannelWorker.POISON_PILL) {
							keepRunning = false;
							break;
						}
					}
					if(keepRunning){
						doSend(threadChannel, dataToSend);
					}
				} catch (ClosedByInterruptException ex){
					OseeLog.log(getClass(), Level.SEVERE, "Error trying to send data", ex);
					threadChannel = openAndInitializeDatagramChannel(address);
				} catch (AsynchronousCloseException ex){
					OseeLog.log(getClass(), Level.SEVERE, "Error trying to send data", ex);
					threadChannel = openAndInitializeDatagramChannel(address);
				} catch (ClosedChannelException ex){
					OseeLog.log(getClass(), Level.SEVERE, "Error trying to send data", ex);
					threadChannel = openAndInitializeDatagramChannel(address);
				} catch (IOException ex){
					OseeLog.log(getClass(), Level.SEVERE, "Error trying to send data", ex);
				} finally {
					int size = dataToSend.size();
					for (int i = 0; i < size; i++) {
						DatagramChannelData data = dataToSend.get(i);
						data.postProcess();
					}
				}
			} 
		} catch (IOException ex){
			OseeLog.log(getClass(), Level.SEVERE, "Error opening DatagramChannel.  Ending DatagramChannelRunnable unexpectedly.", ex);
		} finally{
			try {
				if (threadChannel != null) {
					threadChannel.close();
				}
			} catch (IOException e) {
				OseeLog.log(getClass(), Level.SEVERE, "Error trying to send data", e);
			}
		}
	}
	
	public final void send(DatagramChannelData datagramChannelData){
	   if(classChannel == null){
	      classChannel = new ThreadLocal<DatagramChannel>(){
	         @Override
	         protected DatagramChannel initialValue(){
	            try {
	               return openAndInitializeDatagramChannel(address);
	            } catch (IOException ex) {
	               OseeLog.log(getClass(), Level.SEVERE, "Error opening DatagramChannel.  Unable to send.", ex);
	            }
	            return null;
	         }
	      };
	   }
	   if(classChannel != null){
	      try {
            doSend(classChannel.get(), datagramChannelData);
         } catch (IOException ex) {
            OseeLog.log(getClass(), Level.SEVERE, "Failed to send UDP packet.", ex);
         }
	   }
	}
	
	public abstract void doSend(DatagramChannel channel2, List<DatagramChannelData> dataToSend) throws ClosedChannelException, AsynchronousCloseException, ClosedByInterruptException, IOException;

	public abstract void doSend(DatagramChannel channel2, DatagramChannelData datagramChannelData) throws IOException;

	public abstract DatagramChannel openAndInitializeDatagramChannel(InetSocketAddress address) throws IOException;

}
