/*
 * Created on Dec 7, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.ote.ui.eviewer.view;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
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

   private List<ColumnDetails> getSelection() {
	      IStructuredSelection selection = (IStructuredSelection) getViewer().getSelection();
	      LinkedList<ColumnDetails> list = new LinkedList<ColumnDetails>();
	      for (Object item : selection.toList()) {
	         list.add((ColumnDetails) item);
	      }
	      return list;
	   }
   
   @Override
   public boolean performDrop(Object data) {
      if (target == null) {
         return false;
      }
      
      configuration.moveTo(getSelection(), target);
      return true;
   }

   @Override
   public boolean validateDrop(Object target, int operation, TransferData transferType) {

      return true;
   }

}
