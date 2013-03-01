/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.ui.eviewer.view;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.ote.ui.eviewer.Activator;
import org.eclipse.ote.ui.eviewer.action.ColumnContentProvider;
import org.eclipse.ote.ui.eviewer.action.ElementTableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author Ken J. Aguilar
 */
public class ColumnConfigurationDialog extends Dialog {

   private final ColumnConfiguration configuration;
   private TableViewer columnViewer;
   private final Image shellIcon;

   public ColumnConfigurationDialog(Shell parentShell, ColumnConfiguration configuration) {
      super(parentShell);
      this.configuration = configuration;
      shellIcon = Activator.getImageDescriptor("icons/table_config.gif").createImage();
   }

   @Override
   protected Control createDialogArea(Composite parent) {
      Composite control = (Composite) super.createDialogArea(parent);
      Composite main = new Composite(control, SWT.NONE);
      getShell().setText("Element Column Configuration");
      getShell().setImage(shellIcon);
      Widgets.setFormLayout(main);
      columnViewer = new TableViewer(main, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
      columnViewer.setUseHashlookup(true);
      columnViewer.getTable().setHeaderVisible(true);
      TableColumn nameCol = new TableColumn(columnViewer.getTable(), SWT.CENTER);
      nameCol.setText("Column Name");
      nameCol.setWidth(400);
      TableColumn activeCol = new TableColumn(columnViewer.getTable(), SWT.CENTER);
      activeCol.setText("Active");
      activeCol.setWidth(90);
      columnViewer.setContentProvider(new ColumnContentProvider());
      columnViewer.setLabelProvider(new ElementTableLabelProvider());

      int operations = DND.DROP_MOVE | DND.DROP_COPY;
      Transfer[] types = new Transfer[] {TextTransfer.getInstance()};

      columnViewer.addDragSupport(operations, types, new DragSourceListener() {

         @Override
         public void dragStart(DragSourceEvent event) {
         }

         @Override
         public void dragSetData(DragSourceEvent event) {
            event.data = "selection";
         }

         @Override
         public void dragFinished(DragSourceEvent event) {
         }
      });

      columnViewer.addDropSupport(operations, types, new ColumnViewerDropAdaptor(columnViewer, configuration));

      Button upBtn = new Button(main, SWT.PUSH);
      upBtn.setText("Move Up");
      upBtn.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
        	 List<ColumnDetails> list = getSelection();
               configuration.moveUp(list);

         }
      });

      Button downBtn = new Button(main, SWT.PUSH);
      downBtn.setText("Move Down");
      downBtn.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
        	List<ColumnDetails> list = getSelection();
        	configuration.moveDown(list);

         }
      });

      Button activateBtn = new Button(main, SWT.PUSH);
      activateBtn.setText("Activate");
      activateBtn.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            configuration.activate(getSelection());
         }
      });

      Button deactivateBtn = new Button(main, SWT.PUSH);
      deactivateBtn.setText("Deactivate");
      deactivateBtn.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            configuration.deactivate(getSelection());
         }
      });

      Widgets.attachToParent(columnViewer.getControl(), SWT.TOP, 0, 5);
      Widgets.attachToParent(columnViewer.getControl(), SWT.LEFT, 0, 5);
      Widgets.attachToParent(columnViewer.getControl(), SWT.BOTTOM, 100, -5);
      Widgets.attachToParent(columnViewer.getControl(), SWT.RIGHT, 100, -85, SWT.DEFAULT, 600);

      Widgets.attachToControl(upBtn, columnViewer.getControl(), SWT.LEFT, SWT.RIGHT, 5);
      Widgets.attachToControl(upBtn, columnViewer.getControl(), SWT.TOP, SWT.TOP, 0);
      Widgets.attachToParent(upBtn, SWT.RIGHT, 100, -5);

      Widgets.attachToControl(downBtn, upBtn, SWT.LEFT, SWT.LEFT, 0);
      Widgets.attachToControl(downBtn, upBtn, SWT.RIGHT, SWT.RIGHT, 0);
      Widgets.attachToControl(downBtn, upBtn, SWT.TOP, SWT.BOTTOM, 0);

      Widgets.attachToControl(activateBtn, upBtn, SWT.LEFT, SWT.LEFT, 0);
      Widgets.attachToControl(activateBtn, upBtn, SWT.RIGHT, SWT.RIGHT, 0);
      Widgets.attachToControl(activateBtn, downBtn, SWT.TOP, SWT.BOTTOM, 0);

      Widgets.attachToControl(deactivateBtn, upBtn, SWT.LEFT, SWT.LEFT, 0);
      Widgets.attachToControl(deactivateBtn, upBtn, SWT.RIGHT, SWT.RIGHT, 0);
      Widgets.attachToControl(deactivateBtn, activateBtn, SWT.TOP, SWT.BOTTOM, 0);
      columnViewer.setInput(configuration);
      return control;
   }

   private List<ColumnDetails> getSelection() {
      IStructuredSelection selection = (IStructuredSelection) columnViewer.getSelection();
      LinkedList<ColumnDetails> list = new LinkedList<ColumnDetails>();
      for (Object item : selection.toList()) {
         list.add((ColumnDetails) item);
      }
      return list;
   }

   @SuppressWarnings("unused")
   private ColumnDetails getFirstSelected() {
      if (columnViewer.getSelection().isEmpty()) {
         return null;
      }
      IStructuredSelection selection = (IStructuredSelection) columnViewer.getSelection();
      return (ColumnDetails) selection.getFirstElement();
   }

   @Override
   public boolean close() {
      shellIcon.dispose();
      return super.close();
   }

}
