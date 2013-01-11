/*
 * Created on Apr 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.ote.ui.eviewer.view;

import java.util.Arrays;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osee.framework.ui.swt.PeriodicDisplayTask;

/**
 * @author Ken J. Aguilar
 */
public class ViewRefresher extends PeriodicDisplayTask {

   private final TableViewer viewer;
   private final ElementUpdate[] updates;
   private int updateAppendIndex = 0;
   private ElementUpdate[] incomingUpdates = new ElementUpdate[2048];
   private int incomingCount = 0;
   private boolean autoReveal = true;
   private boolean updatesWrappedAround = false;
   public ViewRefresher(TableViewer viewer, int limit) {
      super(viewer.getTable().getDisplay(), 333);
      this.viewer = viewer;
      this.updates = new ElementUpdate[limit];
   }

   @Override
   protected synchronized void update() {
      if (incomingCount == 0) {
         return;
      }
      viewer.getTable().setRedraw(false);
      int newTotal = updateAppendIndex + incomingCount;
      if (newTotal > updates.length) {
         // remove the oldest updates from the viewer
         int numberToRemove = newTotal - updates.length;
         viewer.remove(Arrays.copyOfRange(updates, 0, numberToRemove));
         
         // the updates array is now wrapping around since we surpassed the arrays capacity
         updatesWrappedAround = true;
         
         // find how much capacity with have remaining before we wrap
         int remaining = updates.length - updateAppendIndex;
         // fill the remaining portion of the updates array with the incoming 
         System.arraycopy(incomingUpdates, 0, updates, updateAppendIndex, remaining);
         // wrap around and fill the part of the updates array that got removed
         System.arraycopy(incomingUpdates, remaining, updates, 0, numberToRemove);
         updateAppendIndex = numberToRemove;
      } else {
         if (updatesWrappedAround) {
            // if we are wrapping around then for every incoming update we must remove the oldest update from the viewwer
            viewer.remove(getUpdatesWithWrapAround(incomingCount));
         }
         System.arraycopy(incomingUpdates, 0, updates, updateAppendIndex, incomingCount);
         updateAppendIndex += incomingCount;
      }
      viewer.add(Arrays.copyOf(incomingUpdates, incomingCount));
      incomingCount = 0;
      if (autoReveal) {
         viewer.reveal(updates[updateAppendIndex-1]);
      }
      viewer.getTable().setRedraw(true);
   }

   public synchronized void addUpdate(ElementUpdate update) {
      if (incomingCount >= incomingUpdates.length) {
         // force an update to free up space on our incoming array
         getDisplay().syncExec(new Runnable() {
            
            @Override
            public void run() {
               update();
            }
         });
      }
      incomingUpdates[incomingCount++] = update;
   }

   public synchronized void clearUpdates() {
      updateAppendIndex = 0;
      updatesWrappedAround = false;
      viewer.refresh();
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
   public synchronized void setAutoReveal(boolean autoReveal) {
      this.autoReveal = autoReveal;
   }

   public synchronized ElementUpdate getUpdate(int index) {
      if (updatesWrappedAround) {
         int calcIndex = updateAppendIndex + index ;
         if (calcIndex >= updates.length) {
            calcIndex -= updates.length;
         }
         return updates[calcIndex];
      } else {
         return updates[index];
      }
   }
   
   public synchronized ElementUpdate[] getUpdates() {
      if (updatesWrappedAround) {
         ElementUpdate[] copy = new ElementUpdate[updates.length];
         int remaining = updates.length - updateAppendIndex;
         System.arraycopy(updates, updateAppendIndex, copy, 0, remaining);
         System.arraycopy(updates, 0, copy, remaining, updateAppendIndex);
         return copy;
      } else {
         return Arrays.copyOf(updates, updateAppendIndex);
      }
   }
   
   private ElementUpdate[] getUpdatesWithWrapAround(int count) {
         ElementUpdate[] copy = new ElementUpdate[count];
         if ((count + updateAppendIndex) > updates.length) {
            int remaining = updates.length - updateAppendIndex;
            System.arraycopy(updates, updateAppendIndex, copy, 0, remaining);
            System.arraycopy(updates, 0, copy, remaining, count - remaining);  
         } else {
            System.arraycopy(updates, updateAppendIndex, copy, 0, count);
         }
         return copy;
   }
   
   
}
