/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.ote.client.ui.job;

import java.io.IOException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.osee.ote.message.event.send.OteEventMessageCallable;
import org.eclipse.osee.ote.message.event.send.OteEventMessageFuture;
import org.eclipse.osee.ote.remote.messages.JobStatus;
import org.eclipse.osee.ote.remote.messages.SerializedConfigurationAndResponse;
import org.eclipse.osee.ote.remote.messages.SerializedOTEJobStatus;

/**
 * @author Andrew M. Finkbeiner
 */
public class WaitForCompletion implements OteEventMessageCallable<SerializedConfigurationAndResponse, SerializedOTEJobStatus> {

   private SubProgressMonitor monitor;
   private int lastUnitsWorked = 0;
   private boolean firstTime = true;
   private JobStatus status;

   public WaitForCompletion(SubProgressMonitor monitor) {
      this.monitor = monitor;
   }

   @Override
   public void call(SerializedConfigurationAndResponse transmitted, SerializedOTEJobStatus recieved, OteEventMessageFuture<?, ?> future) {
      
      try {
         this.status = recieved.getObject();
         reportStatus(status, future);
      } catch (IOException e) {
         e.printStackTrace();
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }
   }

   private synchronized void reportStatus(JobStatus status, OteEventMessageFuture<?, ?> future) {
      if(monitor.isCanceled() || status.isJobComplete()){
         monitor.done();
         future.complete();
         return;
      }
      if(firstTime){
         monitor.beginTask("Configure Test Server", status.getTotalUnitsOfWork());
         firstTime = false;
      } else {
         monitor.worked(status.getUnitsWorked() - lastUnitsWorked);
         lastUnitsWorked = status.getUnitsWorked();
      }
   }

   @Override
   public void timeout(SerializedConfigurationAndResponse transmitted) {
      System.out.println("timed out");
   }
   
   public JobStatus getStatus(){
      return this.status;
   }
   
}
