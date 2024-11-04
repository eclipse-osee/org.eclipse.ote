/*********************************************************************
 * Copyright (c) 2024 Boeing
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.ote.cat.plugin.composites;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * A {@link Composite} extension containing a {@link TableViewer}.
 *
 * @param <T> the type of object displayed in the selection table.
 */

public class AddRemoveBox<T> extends Composite {

   /**
    * A functional interface for the callback that is invoked when the table's selection is changed.
    */

   @FunctionalInterface
   public interface SelectionChangedAction {

      /**
       * Callback method for a new table selection.
       */

      public void selectionChanged();
   }

   /**
    * Saves the callback for a table selection change.
    */

   private SelectionChangedAction selectionChangedAction;

   /**
    * Saves the {@link TableViewer} displayed in the {@link Composite}.
    */

   private TableViewer tableViewer;

   /**
    * Creates a new {@link Composite} containing a {@link TableViewer}.
    * 
    * @param parent the {@link Composite} the selection box is to be attached to.
    * @param selectionChangedAction this callback is invoked when the table selection changes.
    */

   public AddRemoveBox(Composite parent, SelectionChangedAction selectionChangedAction) {

      super(parent, SWT.NULL);

      GridLayout gridLayout = new GridLayout();
      gridLayout.marginWidth = 0;

      this.setLayout(gridLayout);

      GridData childGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
      childGridData.heightHint = 300;
      childGridData.widthHint = 300;

      this.tableViewer = new TableViewer(this);
      Composite childComposite = this.tableViewer.getTable();
      childComposite.setLayoutData(childGridData);

      this.selectionChangedAction = selectionChangedAction;
      //@formatter:off
      ISelectionChangedListener selectionChangedListener =
         new ISelectionChangedListener() {

         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            AddRemoveBox.this.selectionChangedAction.selectionChanged();
         }
      
      };
      //@formatter:on
      this.tableViewer.addSelectionChangedListener(selectionChangedListener);
      this.addDisposeListener(this::dispose);
   }

   /**
    * Releases operating system resources for the table.
    * 
    * @param disposeEvent unused
    */

   public void dispose(DisposeEvent disposeEvent) {
      this.tableViewer.getTable().dispose();
      this.tableViewer = null;
   }

   /**
    * Adds the elements of the <code>items</code> {@link List} to the table.
    * 
    * @param items a {@link List} of the Class&lt;T&gt; items to be added.
    */

   public void add(List<T> items) {
      Set<T> currentContents = this.getContents(HashSet::new);
      items.stream().filter((project) -> !currentContents.contains(project)).forEach(this.tableViewer::add);
   }

   /**
    * Gets a {@link Collection} of the Class&lt;T&gt; items held in the selection table.
    * 
    * @param <C> the type of collection created and returned by the method.
    * @param collectionFactory a {@link Supplier} for the {@link Collection} to be filled by the method.
    * @return the {@link Collection} provided by the <code>collectionFactory</code> {@link Supplier}.
    */

   public <C extends Collection<T>> C getContents(Supplier<C> collectionFactory) {
      int i;
      C selections = collectionFactory.get();
      //@formatter:off
      for( Object element = this.tableViewer.getElementAt(i=0);
           Objects.nonNull( element );
           element = this.tableViewer.getElementAt(++i)) {
         @SuppressWarnings("unchecked")
         T t = (T) element;
         selections.add( t );
      }
      return selections;
      
   }

   /**
    * Gets a {@link List} of the currently selected items in the table viewer.
    * 
    * @return a {@link List} of the selected items.
    */
   
   public List<T> getSelected() {
      IStructuredSelection structuredSelection = this.tableViewer.getStructuredSelection();
      @SuppressWarnings("unchecked")
      List<T> selected = structuredSelection.toList();
      return selected;
   }

   /**
    * Predicate to determine if at least one item is selected in the table viewer.
    * 
    * @return <code>true</code> when an item is selected; otherwise, <code>false</code>.
    */

   public boolean hasSelection() {
      ISelection selection = this.tableViewer.getSelection();
      boolean result = !selection.isEmpty();
      return result;
   }

   /**
    * Removes the elements of the <code>items</code> {@link List} from the table viewer.
    * 
    * @param items a {@link List} of the Class&lt;T&gt; items to be removed.
    */
   
   public void remove(List<T> items) {
      this.tableViewer.remove(items.toArray());
   }

}
