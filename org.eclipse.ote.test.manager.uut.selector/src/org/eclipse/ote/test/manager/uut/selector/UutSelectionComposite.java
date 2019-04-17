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
package org.eclipse.ote.test.manager.uut.selector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.nebula.widgets.xviewer.edit.DefaultXViewerControlFactory;
import org.eclipse.nebula.widgets.xviewer.edit.XViewerEditAdapter;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.ote.test.manager.uut.selector.internal.OteTestManagerUutImage;
import org.eclipse.ote.test.manager.uut.selector.internal.UutAvailableChangeListener;
import org.eclipse.ote.test.manager.uut.selector.internal.UutAvailableEventHandler;
import org.eclipse.ote.test.manager.uut.selector.xml.TestManagerXmlInterface;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author David N. Phillips
 * @author Andy Jury
 */
public class UutSelectionComposite extends Composite implements UutAvailableChangeListener {
   private UutSelectionTable table;
   private TestManagerXmlInterface xmlInterface;
   UutSelectionContentProvider contentProvider;
   private boolean noDefaults;

   public UutSelectionComposite(Composite parent, int style) {
      super(parent, style);
      noDefaults = false;
      setLayout(GridLayoutFactory.swtDefaults().numColumns(1).spacing(0, 0).create());
      setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      Composite controlComposite = new Composite(this, SWT.NONE);
      controlComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(0).create());
      controlComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());

      fillToolbar(controlComposite);
      table = new UutSelectionTable(this, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
      table.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
      contentProvider = new UutSelectionContentProvider();
      table.setContentProvider(contentProvider);
      table.setLabelProvider(new UutSelectionLabelProvider(table));
      table.setXViewerEditAdapter(new XViewerEditAdapter(new DefaultXViewerControlFactory(), new UutSelectionViewerConverter(table)));
      table.setInput(new UutItemCollection());
      table.refresh();
      xmlInterface = new TestManagerXmlInterface();

      UutAvailableEventHandler handler = UutAvailableEventHandler.getHandler();
      if (handler != null) {
         handler.addListener(this);
      }
   }

   private String getPathForFile(String dialogTitle, int dialogStyle) {
      FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), dialogStyle);
      dialog.setText(dialogTitle);
      dialog.setFilterExtensions(new String[]{"*.ote"});
      String filePath = dialog.open();
      return filePath;
   }

   private void loadUutFile(String fileLocAndName) {
      File userFile = new File(fileLocAndName);
      
      try {
         BufferedReader br = new BufferedReader(new FileReader(userFile));
         try {
            StringBuilder sb = new StringBuilder();
            String line;
            while (null != (line = br.readLine())) {
               sb.append(line);
               sb.append("\n");
            }
            if (!xmlInterface.setXml(sb.toString())) {
               OseeLog.log(getClass(), Level.WARNING, xmlInterface.getErrorMessage());
            }
         } finally {
            br.close();
            setCollection(xmlInterface.getUutItemCollection());
         }
      } catch (Throwable th) {
         OseeLog.log(getClass(), Level.SEVERE, th);
      }
   }

   private void writeUutFile(String fileLocAndName) {
      File userFile = new File(fileLocAndName);
      
      xmlInterface.setUutItemCollection(getCollection());
      try {
         Writer out = new OutputStreamWriter(new FileOutputStream(userFile), "UTF-8");
         try {
            out.write(xmlInterface.getXml());
         }
         finally {
            out.close();
         }
      } catch (Throwable th) {
         OseeLog.log(getClass(), Level.SEVERE, th);
      }
   }

   private void fillToolbar(Composite parent) {
      Button button;
      button = createToolbarButton(parent);
      button.setImage(OteTestManagerUutImage.loadImage(OteTestManagerUutImage.COLLAPSE_ALL));
      button.addSelectionListener(new SelectionListener() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            table.collapseAll();
         }
         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            // INTENTIONALLY EMPTY BLOCK
         }
      });

      button = createToolbarButton(parent);
      button.setImage(OteTestManagerUutImage.loadImage(OteTestManagerUutImage.EXPAND_ALL));
      button.addSelectionListener(new SelectionListener() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            table.expandAll();
         }
         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            // INTENTIONALLY EMPTY BLOCK
         }
      });

      button = createToolbarButton(parent);
      button.setText("Unpin All");
      button.addSelectionListener(new SelectionListener() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            for (IUutItem item : contentProvider.getCollection().getPartitions()) {
               item.setSelected(false);
            }
            table.refresh();
         }
         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            // INTENTIONALLY EMPTY BLOCK
         }
      });

      button = createToolbarButton(parent);
      button.setText("Add Default UUTs");
      button.addSelectionListener(new SelectionListener() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            for (String partition : UutSuTestList.getTestSuList()) {
               contentProvider.getCollection().getPartitionItem(partition);
            }
            table.refresh();
         }
         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            // INTENTIONALLY EMPTY BLOCK
         }
      });

      button = createToolbarButton(parent);
      button.setText("Load UUTs");
      button.addSelectionListener(new SelectionListener() {        
         @Override
         public void widgetSelected(SelectionEvent e) {
            String filePath = getPathForFile("Load uuts from File", SWT.OPEN);
            
            if (filePath != null) {
               loadUutFile(filePath);
            }
         }
         
         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            // INTENTIONALLY EMPTY BLOCK
         }
      });
      
      button = createToolbarButton(parent);
      button.setText("Save UUTs");
      button.addSelectionListener(new SelectionListener() {        
         @Override
         public void widgetSelected(SelectionEvent e) {
            String filePath = getPathForFile("Save UUTs to File", SWT.SAVE);
            
            if (filePath != null) {
               writeUutFile(filePath);
            }            
         }
         
         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            // INTENTIONALLY EMPTY BLOCK
         }
      });
       
      button = createToolbarButton(parent);
      button.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).grab(true, false).create());
      button.setText("View Help");
      button.addSelectionListener(new SelectionListener() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               Dialog dialog = new Dialog(getShell()) {
                  final Image image = OteTestManagerUutImage.loadImage(OteTestManagerUutImage.UUT_HELP);
                  @Override
                  protected Control createDialogArea(Composite parent){
                     Label label = new Label(parent, SWT.NONE);
                     label.setImage(image);
                     return label;
                  }
                  @Override
                  protected void createButtonsForButtonBar(Composite parent) {
                     createButton(parent, 0, "Close", true);
                  }
               };
               dialog.open();
            } catch (Throwable th) {
               th.printStackTrace();
            }
         }
         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            // INTENTIONALLY EMPTY BLOCK
         }
      });
   }

   private Button createToolbarButton(Composite parent) {
      Button button = new Button(parent, SWT.PUSH);
      ((GridLayout) parent.getLayout()).numColumns++;
      return button;
   }

   public void setCollection(UutItemCollection collection) {
      contentProvider.setCollection(collection);
   }

   public UutItemCollection getCollection() {
      return contentProvider.getCollection();
   }

   public String checkErrorConditions() {
      boolean onlyDefaultsSelected = true;
      for (UutItemPartition partition : contentProvider.getCollection().getPartitions()) {
         if (partition.isSelected()) {
            onlyDefaultsSelected = false;
         }
      }

      if (noDefaults && onlyDefaultsSelected) {
         return "  Specific UUT(s) must be selected[check marked] for running.";
      }


      return "";
   }

   public void setNoDefaults(boolean noDefaults) {
      this.noDefaults = noDefaults;
   }

   @Override
   public void uutAvailableChange() {
      if (isDisposed()) {
         UutAvailableEventHandler.getHandler().removeListener(this);
         return;
      }
      getDisplay().asyncExec(new Runnable() {
         @Override
         public void run() {
            table.refresh(true);
            if (table.getTree() != null) {
               table.getTree().redraw();
            }
         }
      });
   }

   public static UutItemCollection getTestData() {
      UutItemCollection collection = new UutItemCollection();
      collection.createItem("SoftwareUnit1", "\\somepath1").setSelected(true);
      collection.createItem("SoftwareUnit1", "\\somepath2").setSelected(false);
      collection.createItem("SoftwareUnit1", "\\somepath3").setSelected(true);
      collection.createItem("SoftwareUnit2", "\\somepath1").setSelected(false);
      collection.createItem("SoftwareUnit2", "\\somepath2").setSelected(false);
      collection.createItem("SoftwareUnit2", "\\somepath3").setSelected(true);
      collection.getPartitionItem("SoftwareUnit2").setSelected(true);
      return collection;
   }

   public static void main(String[] args) {
      Display display = Display.getDefault();
      final Shell shell = new Shell(display);
      shell.setLayout(new FillLayout());
      shell.setText ("Shell");
      UutSelectionComposite uutSelectionComposite = new UutSelectionComposite(shell, SWT.NONE);
      uutSelectionComposite.setCollection(getTestData());
      shell.setSize(600, 400);
      shell.layout(true, true);
      shell.open ();
      while (!shell.isDisposed ()) {
         if (!display.readAndDispatch ()) display.sleep ();
      }
      display.dispose ();
   }

}
