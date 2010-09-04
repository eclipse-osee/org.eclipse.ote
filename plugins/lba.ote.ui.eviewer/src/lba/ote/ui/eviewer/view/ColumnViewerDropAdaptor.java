/*
 * Created on Dec 7, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package lba.ote.ui.eviewer.view;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;

/**
 * @author Ken J. Aguilar
 */
public class ColumnViewerDropAdaptor extends ViewerDropAdapter {

   private final ColumnConfiguration configuration;
   private ColumnDetails target;

   public ColumnViewerDropAdaptor(Viewer viewer, ColumnConfiguration configuration) {
      super(viewer);
      this.configuration = configuration;
      setFeedbackEnabled(true);
   }

   @Override
   public void drop(DropTargetEvent event) {
      //      location = determineLocation(event);
      target = (ColumnDetails) determineTarget(event);
      super.drop(event);
   }

   @Override
   public boolean performDrop(Object data) {
      if (target == null) {
         return false;
      }
      int sourceIndex = Integer.parseInt((String) data);
      int targetIndex = configuration.indexOf(target);
      configuration.moveTo(sourceIndex, targetIndex);
      return true;
   }

   @Override
   public boolean validateDrop(Object target, int operation, TransferData transferType) {

      return true;
   }

}
