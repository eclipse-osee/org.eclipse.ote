/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.output.tree;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewerTextFilter;
import org.eclipse.osee.ote.ui.output.tree.items.IOutfileTreeItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IManagedForm;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class OutfileTreeXViewer extends XViewer {
   private boolean formNeedsReflow;

   public static enum OutfileType {
      Summary,
      Content
   };

   public OutfileTreeXViewer(Composite parent, XViewerFactory factory, OutfileType outfileType) {
      super(parent, SWT.VIRTUAL | SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, factory);
      setSorter(null);
      setContentProvider(new OutfileContentProviderXViewer());
      if (outfileType == OutfileType.Summary) {
         setLabelProvider(new OutfileSummaryLabelProvider(this));
      } else if (outfileType == OutfileType.Content) {
         setLabelProvider(new OutfileXViewerStyledLabelProvider(this));
      } else {
         throw new IllegalArgumentException("Unhandled OutfileType");
      }
      setUseHashlookup(true);
   }

   public Object[] locateItem(IOutfileTreeItem item) {
      List<Object> currentPath = new ArrayList<>();
      Object obj = getInput();
      if (obj instanceof IOutfileTreeItem) {
         if (locateItem(item, (IOutfileTreeItem) obj, currentPath)) {

            Object[] path = new Object[currentPath.size()];
            for (int j = 0, i = currentPath.size() - 1; i >= 0; i--, j++) {
               path[j] = currentPath.get(i);
            }

            return path;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private boolean locateItem(IOutfileTreeItem matchme, IOutfileTreeItem item, List<Object> currentPath) {
      if (!item.equals(matchme)) {
         for (IOutfileTreeItem child : item.getChildren()) {
            if (locateItem(matchme, child, currentPath)) {
               currentPath.add(item);
               return true;
            }
         }
      } else {
         currentPath.add(item);
         return true;
      }
      return false;
   }

   public IOutfileTreeItem getRootItem() {
      return (IOutfileTreeItem) getInput();
   }

   public void expandChildren() {
      ISelection selection = getSelection();
      Object element = ((TreeSelection) selection).getFirstElement();
      expandToLevel(element, -1);
   }

   public void propagateScrollWheelEvent(final ScrolledComposite scrolledComposite) {
      getTree().addListener(SWT.MouseWheel, new Listener() {
         @Override
         public void handleEvent(Event event) {
            Point point = scrolledComposite.getOrigin();
            scrolledComposite.setOrigin(point.x, point.y - event.count * 10);
         }
      });
   }

   public void updateFormOnTreeExpansion(final IManagedForm form) {
      addTreeListener(new ITreeViewerListener() {

         @Override
         public void treeCollapsed(TreeExpansionEvent event) {
            resize();
         }

         @Override
         public void treeExpanded(TreeExpansionEvent event) {
            resize();
         }

         private void resize() {
            formNeedsReflow = true;
         }

      });

      getTree().addPaintListener(new PaintListener() {

         @Override
         public void paintControl(PaintEvent e) {
            if (formNeedsReflow) {
               form.reflow(true);
               formNeedsReflow = false;
            }
         }

      });
   }
   
   @Override
   public void dispose(){
      super.dispose();
   }
   
   @Override
   public XViewerTextFilter getXViewerTextFilter() {
      return new OutfileXViewerTextFilter(this);
   }

}
