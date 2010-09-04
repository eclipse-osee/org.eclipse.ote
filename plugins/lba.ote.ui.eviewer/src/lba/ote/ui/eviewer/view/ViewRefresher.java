/*
 * Created on Apr 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package lba.ote.ui.eviewer.view;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osee.framework.ui.swt.PeriodicDisplayTask;

/**
 * @author Ken J. Aguilar
 */
public class ViewRefresher extends PeriodicDisplayTask {

   private final TableViewer viewer;
   private final LinkedList<ElementUpdate> updates = new LinkedList<ElementUpdate>();
   private final int limit;
   private final ArrayList<ElementUpdate> incomingUpdates = new ArrayList<ElementUpdate>(2048);
   private boolean autoReveal = true;

   public ViewRefresher(TableViewer viewer, int limit) {
      super(viewer.getTable().getDisplay(), 333);
      this.viewer = viewer;
      this.limit = limit;
   }

   @Override
   protected synchronized void update() {
      if (incomingUpdates.isEmpty()) {
         return;
      }
      viewer.getTable().setRedraw(false);
      int newTotal = updates.size() + incomingUpdates.size();
      if (newTotal > limit) {
         int numberToRemove = newTotal - limit;
         Object[] removed = new Object[numberToRemove];
         for (int i = 0; i < numberToRemove; i++) {
            removed[i] = updates.remove();
         }
         viewer.remove(removed);
      }
      updates.addAll(incomingUpdates);
      viewer.add(incomingUpdates.toArray());
      incomingUpdates.clear();
      if (autoReveal) {
         viewer.reveal(updates.getLast());
      }
      viewer.getTable().setRedraw(true);
   }

   public synchronized void addUpdate(ElementUpdate update) {
      incomingUpdates.add(update);
   }

   public synchronized void clearUpdates() {
      updates.clear();
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

   /**
    * @return
    */
   public synchronized List<ElementUpdate> getUpdates() {
      return new ArrayList<ElementUpdate>(updates);
   }
}
