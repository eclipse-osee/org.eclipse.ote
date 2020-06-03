/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.ote.ui.eviewer.view;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.PeriodicDisplayTask;
import org.eclipse.ote.io.CircularBuffer;

/**
 * @author Ken J. Aguilar
 */
public class ViewRefresher extends PeriodicDisplayTask {

   private final TableViewer viewer;
   private RowUpdate[] incomingUpdates = new RowUpdate[2048];
   
   private int incomingCount = 0;
   private boolean autoReveal = true;
   
   private ReentrantLock lock;
   private Condition clearIncomingUpdates;
   private CircularBuffer<RowUpdate> updatesNew;
   private volatile boolean updateView = true;
   
   public ViewRefresher(TableViewer viewer, int limit) {
      super(viewer.getTable().getDisplay(), 333);
      lock = new ReentrantLock();
      clearIncomingUpdates = lock.newCondition();
      
      updatesNew = new CircularBuffer<RowUpdate>(limit);
      
      this.viewer = viewer;
   }

   @Override
   protected void update() {
      try{
         lock.lock();

         if (incomingCount == 0) {
            return;
         }
         RowUpdate[] overWritten = updatesNew.add(incomingUpdates, 0, incomingCount);
         
         if(updateView){
            viewer.getTable().setRedraw(false);
            viewer.remove(overWritten);
            viewer.add(Arrays.copyOf(incomingUpdates, incomingCount > incomingUpdates.length ? incomingUpdates.length : incomingCount));
            if (autoReveal) {
               viewer.reveal(updatesNew.head());
            }
            viewer.getTable().setRedraw(true);
         }
         
         incomingCount = 0;
         clearIncomingUpdates.signalAll();
      } finally {
         lock.unlock();
      }
   }
   
   public void forceUpdate(){
      getDisplay().asyncExec(new Runnable() {
         @Override
         public void run() {
            update();
         }
      });
   }

   public void addUpdate(RowUpdate update) {
      try {
         lock.lock();

         boolean needsClear = incomingCount >= incomingUpdates.length;

         if (needsClear) {
            // force an update to free up space on our incoming array
            forceUpdate();
            try{
               long nanoTime = TimeUnit.SECONDS.toNanos(5);
               while(nanoTime > 0 && needsClear){
                  nanoTime = clearIncomingUpdates.awaitNanos(nanoTime);
                  needsClear = incomingCount >= incomingUpdates.length;
               }
            } catch(InterruptedException ex){
               OseeLog.log(getClass(), Level.SEVERE, ex);
            }
         }
         
         incomingUpdates[incomingCount++] = update;
         
      } finally {
         lock.unlock();
      }
   }

   public void clearUpdates() {
      try{
         lock.lock();
         updatesNew.clear();
         viewer.refresh();
      } finally {
         lock.unlock();
      }
   }

   /**
    * @return the autoReveal
    */
   public boolean isAutoReveal() {
      return autoReveal;
   }

   /**
    * @param autoReveal the autoReveal to set
    */
   public void setAutoReveal(boolean autoReveal) {
      try{
         lock.lock();
         this.autoReveal = autoReveal;
      } finally {
         lock.unlock();
      }
   }

   public RowUpdate[] getUpdates() {
      try{
         lock.lock();
         return updatesNew.getCopy(RowUpdate.class);
      }finally{
         lock.unlock();
      }
   }

   public void setUpdateView(boolean updateView) {
      this.updateView = updateView;
      if(updateView){
         viewer.getTable().setRedraw(false);
         viewer.refresh();
         viewer.getTable().setRedraw(true);
      }
   }
   
}
