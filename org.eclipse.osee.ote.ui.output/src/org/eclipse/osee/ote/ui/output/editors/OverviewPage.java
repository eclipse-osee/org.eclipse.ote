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
package org.eclipse.osee.ote.ui.output.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.ote.ui.output.Activator;
import org.eclipse.osee.ote.ui.output.OteOutputImage;
import org.eclipse.osee.ote.ui.output.tree.OutfileContentProviderXViewer;
import org.eclipse.osee.ote.ui.output.tree.OutfileSummaryContentProviderFailuresOnly;
import org.eclipse.osee.ote.ui.output.tree.OutfileSummaryXViewerFactory;
import org.eclipse.osee.ote.ui.output.tree.OutfileTreeXViewer;
import org.eclipse.osee.ote.ui.output.tree.OutfileTreeXViewer.OutfileType;
import org.eclipse.osee.ote.ui.output.tree.OutfileXViewerFactory;
import org.eclipse.osee.ote.ui.output.tree.items.IOutfileTreeItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class OverviewPage extends FormPage {
   
   private static final String OTE_TEST_OUTPUT = "OTE Test Output";
   private static final int SUMMARY_ROWS = 9;
   private static final int OTHER_ROWS = 8;
   protected static final int DEFAULT_MAX_HEIGHT = 200;
   protected static final int PAD = 7;
   
   private final StringBuilder overviewStringInfo;
   private final StringBuilder versionStringInfo;
   
   private final Clipboard clipboard = new Clipboard(null);
   private final OteOutput editor;
   private Action refresh;
   
   private Color colorResource;
   
   private ScrolledForm form;
   
   private Composite overviewSectionContent;
   private ScrolledComposite overviewSectionContentScrollable;
   private Composite overviewSectionLink;
   private Composite versionBaseSectionContent;
   private Composite versionSectionLink;
   private Composite notScrolledComposite;
   private Composite sectionContent;
   
   private OutfileTreeXViewer outfileTree;
   private OutfileTreeXViewer uutLogInfo;
   private OutfileTreeXViewer uutVersionInfo;
   private OutfileTreeXViewer oteLogInfo;

   private ExpandableComposite overviewBaseSection;
   private ExpandableComposite summarySection;
   private ExpandableComposite uutVersionSection;
   private ExpandableComposite uutLogSection;
   private ExpandableComposite oteLogSection;
   private ExpandableComposite versionBaseSection;
   private ScrolledComposite versionBaseSectionContentScrollable;
   private boolean isLargeFile;
   private int failCount;
   
   public OverviewPage(OteOutput editor) {
      super(editor, String.format("Overview%d", editor.hashCode()), "Overview");
      this.editor = editor;
      overviewStringInfo = new StringBuilder();
      versionStringInfo = new StringBuilder();
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);
      form = managedForm.getForm();

      form.setText(OTE_TEST_OUTPUT);
      
      final Composite body = managedForm.getForm().getBody();
      body.setLayout(new GridLayout(1, false));

      /**
      * Create the composite which should not have the scrollbar and set its layout data
      * to GridData with width and height hints equal to the size of the form's body
      */
      notScrolledComposite = managedForm.getToolkit().createComposite(body);
      notScrolledComposite.setLayout(new GridLayout(1,false));
      
      final GridData gdata = GridDataFactory.fillDefaults()
              .grab(true, true)
              .hint(body.getClientArea().width, body.getClientArea().height)
              .create();
      notScrolledComposite.setLayoutData(gdata);
      
      form.setText(OTE_TEST_OUTPUT);
      
      createOverviewSection(managedForm, managedForm.getForm(), notScrolledComposite);
      createVersionSection(managedForm, managedForm.getForm(), notScrolledComposite);
      createResultsSummarySection(managedForm, managedForm.getForm(), notScrolledComposite);
      createUutLogSection(managedForm, managedForm.getForm(), notScrolledComposite);
      createUutVersionSection(managedForm, managedForm.getForm(), notScrolledComposite);
      createOteLogSection(managedForm, managedForm.getForm(), notScrolledComposite);

      refresh = new Action("refresh", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            Jobs.runInJob(
                  String.format("Refresh input.", ((IFileEditorInput) editor.getEditorInput()).getFile().getName()),
                  new IExceptionableRunnable() {

                     @Override
                     public IStatus run(IProgressMonitor monitor) throws Exception {
                        editor.refresh();
                        return Status.OK_STATUS;
                     }
                  }, Activator.class, Activator.PLUGIN_ID);

         }
      };
      refresh.setToolTipText("Refresh Output Editor");
      refresh.setImageDescriptor(ImageManager.getImageDescriptor(OteOutputImage.REFRESH));
      managedForm.getForm().getToolBarManager().add(refresh);
      managedForm.getForm().updateToolBar();
      
   // Add resize listener to sash form's parent so that sash form always fills the page
      form.getBody().addControlListener(new ControlAdapter() {
         
          @Override
          public void controlResized(ControlEvent e)
          {
             super.controlResized(e);
             updateGridData(false);
             notScrolledComposite.layout(true);
          }
      });
      form.getBody().pack();
   }


   private void createOverviewSection(IManagedForm managedForm, final ScrolledForm form, Composite notScrolledComposite) {
      overviewBaseSection =
            managedForm.getToolkit().createSection(
                  notScrolledComposite, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.COMPACT);
      overviewBaseSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      overviewBaseSection.addExpansionListener(new ExpansionAdapter() {
         @Override
         public void expansionStateChanged(ExpansionEvent e) {            
            versionBaseSection.setExpanded(false);
            summarySection.setExpanded(false);
            uutLogSection.setExpanded(false);
            uutVersionSection.setExpanded(false);
            oteLogSection.setExpanded(false);
            
            overviewSectionContentScrollable.setFocus();
            updateGridData(true);
         }
      });
      overviewBaseSection.setText("Test Overview");
      Composite parentSection = managedForm.getToolkit().createComposite(overviewBaseSection);
      parentSection.setLayout(new GridLayout(1, false));
      overviewSectionContentScrollable = new ScrolledComposite(parentSection, SWT.H_SCROLL | SWT.V_SCROLL);
      overviewSectionContent = managedForm.getToolkit().createComposite(overviewSectionContentScrollable);
      overviewSectionContent.setLayout(new GridLayout(1, false));
      overviewSectionLink = managedForm.getToolkit().createComposite(parentSection);
      overviewSectionLink.setLayout(new GridLayout(1, false));
      overviewBaseSection.setClient(parentSection);
      overviewSectionContentScrollable.setContent(overviewSectionContent);
      overviewSectionContentScrollable.setExpandHorizontal(true);
      overviewSectionContentScrollable.setExpandVertical(true);

      Hyperlink hyperlink =
            managedForm.getToolkit().createHyperlink(overviewSectionLink,
                  "Copy the overview information to the Clipboard.", SWT.END);
      hyperlink.addHyperlinkListener(new IHyperlinkListener() {

         @Override
         public void linkActivated(HyperlinkEvent e) {
            clipboard.setContents(new Object[] {overviewStringInfo.toString()},
                  new Transfer[] {TextTransfer.getInstance()});
         }

         @Override
         public void linkEntered(HyperlinkEvent e) {
            // Intentionally Empty Block
         }

         @Override
         public void linkExited(HyperlinkEvent e) {
            // Intentionally Empty Block
         }

      });
      form.layout();
   }
   
   private void createVersionSection(IManagedForm managedForm, final ScrolledForm form, Composite notScrolledComposite) {
      versionBaseSection =
            managedForm.getToolkit().createSection(
                  notScrolledComposite, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.COMPACT);
      versionBaseSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      versionBaseSection.addExpansionListener(new ExpansionAdapter() {
         @Override
         public void expansionStateChanged(ExpansionEvent e) {
            overviewBaseSection.setExpanded(false);
            summarySection.setExpanded(false);
            uutLogSection.setExpanded(false);
            uutVersionSection.setExpanded(false);
            oteLogSection.setExpanded(false);
            versionBaseSectionContentScrollable.setFocus();
            updateGridData(true);
         }
      });
      versionBaseSection.setText("Version Info");
      Composite parentSection = managedForm.getToolkit().createComposite(versionBaseSection);
      parentSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      parentSection.setLayout(new GridLayout(1, false));
      versionBaseSectionContentScrollable = new ScrolledComposite(parentSection, SWT.H_SCROLL | SWT.V_SCROLL);
      versionBaseSectionContentScrollable.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      versionBaseSectionContentScrollable.setLayout(new GridLayout());
      versionBaseSectionContent = managedForm.getToolkit().createComposite(versionBaseSectionContentScrollable);
      versionBaseSectionContent.setLayout(new GridLayout(1, false));
      versionBaseSectionContent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      versionSectionLink = managedForm.getToolkit().createComposite(parentSection);
      versionSectionLink.setLayout(new GridLayout(1, false));
      versionBaseSection.setClient(parentSection);
      versionBaseSectionContentScrollable.setContent(versionBaseSectionContent);
      versionBaseSectionContentScrollable.setExpandHorizontal(true);
      versionBaseSectionContentScrollable.setExpandVertical(true);
      
      Hyperlink hyperlink =
            managedForm.getToolkit().createHyperlink(versionSectionLink,
                  "Copy the overview information to the Clipboard.", SWT.END);
      hyperlink.addHyperlinkListener(new IHyperlinkListener() {


         @Override
         public void linkActivated(HyperlinkEvent e) {
            clipboard.setContents(new Object[] {versionStringInfo.toString()},
                  new Transfer[] {TextTransfer.getInstance()});
         }

         @Override
         public void linkEntered(HyperlinkEvent e) {
            // Intentionally Empty Block
         }

         @Override
         public void linkExited(HyperlinkEvent e) {
            // Intentionally Empty Block
         }

      });
      form.layout();
   }

   private void createResultsSummarySection(final IManagedForm managedForm, final ScrolledForm form, final Composite notScrolledComposite) {
      summarySection =
            managedForm.getToolkit().createSection(
                  notScrolledComposite, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.COMPACT);
      summarySection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      summarySection.addExpansionListener(new ExpansionAdapter() {
         @Override
         public void expansionStateChanged(ExpansionEvent e) {
            overviewBaseSection.setExpanded(false);
            versionBaseSection.setExpanded(false);
            uutLogSection.setExpanded(false);
            uutVersionSection.setExpanded(false);
            oteLogSection.setExpanded(false);
            
            outfileTree.getTree().setFocus();
            
            updateGridData(true);
         }
      });

      summarySection.setText("Results Summary");
      sectionContent = managedForm.getToolkit().createComposite(summarySection);
      sectionContent.setLayout(new GridLayout());
      summarySection.setClient(sectionContent);

      Composite failuresOnlyComp = new Composite(sectionContent, SWT.None);
      failuresOnlyComp.setLayout(new GridLayout(2, false));

      final Button showFailuresButton = new Button(failuresOnlyComp, SWT.CHECK);
      showFailuresButton.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            if( showFailuresButton.getSelection())
            {
               outfileTree.setContentProvider(new OutfileSummaryContentProviderFailuresOnly());
            } else {
               outfileTree.setContentProvider(new OutfileContentProviderXViewer());
            }
            updateGridData(true);
            
         }
      });

      Label label = new Label(failuresOnlyComp, SWT.None);
      label.setText("Show failures only");

      outfileTree =
            new OutfileTreeXViewer(sectionContent, new OutfileSummaryXViewerFactory(getManagedForm()), OutfileType.Summary);
      Tree tree = outfileTree.getTree();
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.grabExcessVerticalSpace = true;
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);

      outfileTree.addDoubleClickListener(new IDoubleClickListener() {
         @Override
         public void doubleClick(DoubleClickEvent event) {
            ISelection selection = event.getSelection();

            if (!selection.isEmpty()) {
               if (selection instanceof TreeSelection) {
                  TreeSelection treeSelection = (TreeSelection) selection;
                  Object obj = treeSelection.getFirstElement();
                  if (obj instanceof IOutfileTreeItem) {
                     IOutfileTreeItem outfileTreeItem = (IOutfileTreeItem) obj;
                     ((OteOutput) getEditor()).navigateToItemOnDetailsPage(outfileTreeItem);
                  }
               }
            }
         }
      });

      outfileTree.propagateScrollWheelEvent(getManagedForm().getForm());
   }

   private void createUutLogSection(IManagedForm managedForm, final ScrolledForm form, Composite notScrolledComposite) {
      uutLogSection =
            managedForm.getToolkit().createSection(
                  notScrolledComposite, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.COMPACT);
      uutLogSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      uutLogSection.addExpansionListener(new ExpansionAdapter() {
         @Override
         public void expansionStateChanged(ExpansionEvent e) {
            overviewBaseSection.setExpanded(false);
            versionBaseSection.setExpanded(false);
            summarySection.setExpanded(false);
            uutVersionSection.setExpanded(false);
            oteLogSection.setExpanded(false);
            
            uutLogInfo.getTree().setFocus();
            
            updateGridData(true);
         }
      });

      uutLogSection.setText("Uut Log");
      Composite sectionContent = managedForm.getToolkit().createComposite(uutLogSection);
      sectionContent.setLayout(new GridLayout());
      uutLogSection.setClient(sectionContent);

      this.uutLogInfo =
            new OutfileTreeXViewer(sectionContent, new OutfileXViewerFactory(getManagedForm()), OutfileType.Content);
      Tree tree = uutLogInfo.getTree();
      GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);

      uutLogInfo.addDoubleClickListener(new IDoubleClickListener() {
         @Override
         public void doubleClick(DoubleClickEvent event) {
            ISelection selection = event.getSelection();

            if (!selection.isEmpty()) {
               if (selection instanceof TreeSelection) {
                  TreeSelection treeSelection = (TreeSelection) selection;
                  Object obj = treeSelection.getFirstElement();
                  if (obj instanceof IOutfileTreeItem) {
                     IOutfileTreeItem outfileTreeItem = (IOutfileTreeItem) obj;
                     ((OteOutput) getEditor()).navigateToItemOnDetailsPage(outfileTreeItem);
                  }
               }
            }
         }
      });
      uutLogInfo.propagateScrollWheelEvent(getManagedForm().getForm());
   }

   private void createOteLogSection(IManagedForm managedForm, final ScrolledForm form, Composite notScrolledComposite) {
      oteLogSection =
            managedForm.getToolkit().createSection(
                  notScrolledComposite, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.COMPACT);
      oteLogSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      oteLogSection.addExpansionListener(new ExpansionAdapter() {
         @Override
         public void expansionStateChanged(ExpansionEvent e) {
            overviewBaseSection.setExpanded(false);
            versionBaseSection.setExpanded(false);
            summarySection.setExpanded(false);
            uutLogSection.setExpanded(false);
            uutVersionSection.setExpanded(false);
            
            oteLogInfo.getTree().setFocus();
            updateGridData(true);
         }
      });

      oteLogSection.setText("Ote Log");
      Composite sectionContent = managedForm.getToolkit().createComposite(oteLogSection);
      sectionContent.setLayout(new GridLayout());
      oteLogSection.setClient(sectionContent);

      this.oteLogInfo =
            new OutfileTreeXViewer(sectionContent, new OutfileXViewerFactory(getManagedForm()), OutfileType.Content);
      Tree tree = oteLogInfo.getTree();
      GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);

      oteLogInfo.addDoubleClickListener(new IDoubleClickListener() {
         @Override
         public void doubleClick(DoubleClickEvent event) {
            ISelection selection = event.getSelection();

            if (!selection.isEmpty()) {
               if (selection instanceof TreeSelection) {
                  TreeSelection treeSelection = (TreeSelection) selection;
                  Object obj = treeSelection.getFirstElement();
                  if (obj instanceof IOutfileTreeItem) {
                     IOutfileTreeItem outfileTreeItem = (IOutfileTreeItem) obj;
                     ((OteOutput) getEditor()).navigateToItemOnDetailsPage(outfileTreeItem);
                  }
               }
            }
         }
      });
      oteLogInfo.propagateScrollWheelEvent(getManagedForm().getForm());
   }

   private void createUutVersionSection(IManagedForm managedForm, final ScrolledForm form, Composite notScrolledComposite) {
      uutVersionSection =
            managedForm.getToolkit().createSection(
                  notScrolledComposite, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.COMPACT);
      uutVersionSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      uutVersionSection.addExpansionListener(new ExpansionAdapter() {
         @Override
         public void expansionStateChanged(ExpansionEvent e) {
            overviewBaseSection.setExpanded(false);
            versionBaseSection.setExpanded(false);
            summarySection.setExpanded(false);
            uutLogSection.setExpanded(false);
            oteLogSection.setExpanded(false);
            
            uutVersionInfo.getTree().setFocus();
            updateGridData(true);
         }
      });

      uutVersionSection.setText("Uut Version");
      Composite sectionContent = managedForm.getToolkit().createComposite(uutVersionSection);
      sectionContent.setLayout(new GridLayout());
      uutVersionSection.setClient(sectionContent);

      this.uutVersionInfo =
            new OutfileTreeXViewer(sectionContent, new OutfileXViewerFactory(getManagedForm()), OutfileType.Content);
      Tree tree = uutVersionInfo.getTree();
      GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);

      uutVersionInfo.addDoubleClickListener(new IDoubleClickListener() {
         @Override
         public void doubleClick(DoubleClickEvent event) {
            ISelection selection = event.getSelection();

            if (!selection.isEmpty()) {
               if (selection instanceof TreeSelection) {
                  TreeSelection treeSelection = (TreeSelection) selection;
                  Object obj = treeSelection.getFirstElement();
                  if (obj instanceof IOutfileTreeItem) {
                     IOutfileTreeItem outfileTreeItem = (IOutfileTreeItem) obj;
                     ((OteOutput) getEditor()).navigateToItemOnDetailsPage(outfileTreeItem);
                  }
               }
            }
         }
      });
      uutVersionInfo.propagateScrollWheelEvent(getManagedForm().getForm());
   }

   public void addOverviewData(String label, String value) {
     
      if(label.contains("Version")){
         versionStringInfo.append(label);
         versionStringInfo.append("\t");
         versionStringInfo.append(value);
         versionStringInfo.append("\n");
         Composite labelComposite = this.getManagedForm().getToolkit().createComposite(versionBaseSectionContent);
         labelComposite.setLayout(new RowLayout());
         Label valuelabel = this.getManagedForm().getToolkit().createLabel(labelComposite, label + ": ");
         RowData rd = new RowData();
         valuelabel.setLayoutData(rd);
         colorResource = Displays.getColor(150, 180, 245);
         valuelabel.setForeground(Displays.getColor(150, 180, 245));
         Label otherone = this.getManagedForm().getToolkit().createLabel(labelComposite, value);
         otherone.setLayoutData(rd);
      } else {
         overviewStringInfo.append(label);
         overviewStringInfo.append("\t");
         overviewStringInfo.append(value);
         overviewStringInfo.append("\n");
         Composite labelComposite = this.getManagedForm().getToolkit().createComposite(overviewSectionContent);
         labelComposite.setLayout(new RowLayout());
         labelComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
         Label valuelabel = this.getManagedForm().getToolkit().createLabel(labelComposite, label + ": ");
         RowData rd = new RowData();
         valuelabel.setLayoutData(rd);
         colorResource = Displays.getColor(150, 180, 245);
         valuelabel.setForeground(Displays.getColor(150, 180, 245));
         Label otherone = this.getManagedForm().getToolkit().createLabel(labelComposite, value);
         otherone.setLayoutData(rd);
      }
   }

   @Override
   public void dispose() {
      super.dispose();
      clipboard.dispose();
      if (!overviewSectionContent.isDisposed()) {
         overviewSectionContent.dispose();
      }
      if (!overviewBaseSection.isDisposed()) {
         overviewBaseSection.dispose();
      }
      if (colorResource != null) {
         colorResource.dispose();
      }
   }

   public void addSummaryData(IOutfileTreeItem item) {
      outfileTree.getRootItem().getChildren().add(item);
   }

   public void addUutVersionData(IOutfileTreeItem item) {
      uutVersionInfo.setInput(item);
      refresh();
   }

   public void addUutLogData(IOutfileTreeItem item) {
      uutLogInfo.setInput(item);
      refresh();
   }

   public void addOteLogData(IOutfileTreeItem item) {
      oteLogInfo.setInput(item);
      refresh();
   }

   public void refresh() {
      outfileTree.refresh();
      uutLogInfo.refresh();
      uutVersionInfo.refresh();
      oteLogInfo.refresh();
      updateGridData(true);
   }

   public void addContent(StringBuilder sb) {

      sb.append(overviewStringInfo);
      sb.append("\n\nVERSION INFO:\n");
      IOutfileTreeItem item = uutVersionInfo.getRootItem();
      if (item != null) {
         for (IOutfileTreeItem children : item.getChildren()) {
            recursiveAdd(children, sb, 0);
         }
      }
      sb.append("\n\nUUT LOG\n");
      item = uutLogInfo.getRootItem();
      if (item != null) {
         for (IOutfileTreeItem children : item.getChildren()) {
            recursiveAdd(children, sb, 0);
         }
      }
      sb.append("\n\n");
   }

   private void recursiveAdd(IOutfileTreeItem item, StringBuilder sb, int level) {
      for (int i = 0; i < level; i++) {
         sb.append("\t");
      }
      sb.append(item.toString());
      sb.append("\n");
      for (IOutfileTreeItem child : item.getChildren()) {
         recursiveAdd(child, sb, level + 1);
      }
   }

   public void clear() {
      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            for (Control ctrl : overviewSectionContent.getChildren()) {
               ctrl.dispose();
            }
            overviewSectionContent.layout();
            outfileTree.getRootItem().getChildren().clear();
            uutLogInfo.getRootItem().getChildren().clear();
            uutVersionInfo.getRootItem().getChildren().clear();
            oteLogInfo.getRootItem().getChildren().clear();
            outfileTree.refresh();
            uutLogInfo.refresh();
            uutVersionInfo.refresh();
            oteLogInfo.refresh();
            overviewStringInfo.setLength(0);
            updateGridData(true);
         }

      });

   }

   public void setNeedRefresh(final boolean b) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               if (b) {
                  refresh.setImageDescriptor(ImageManager.getImageDescriptor(OteOutputImage.REFRESH_DIRTY));
                  refresh.setToolTipText("Refresh Output Editor (Modified)");
               } else {
                  refresh.setImageDescriptor(ImageManager.getImageDescriptor(OteOutputImage.REFRESH));
                  refresh.setToolTipText("Refresh Output Editor");
               }
               getManagedForm().getForm().updateToolBar();
            } catch (Throwable th) {
               th.printStackTrace();
            }
         }

      });
   }

   public void addSummaryHeader(String header) {
      summarySection.setText("Results Summary   -   " + header);
      summarySection.layout();
   }

   public void setSummaryData(final IOutfileTreeItem rootTestPointSummaryItem) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            outfileTree.setInput(rootTestPointSummaryItem);
            updateGridData(true);
         }
      });
   }
   
   private void updateGridData(boolean reflow){
      Composite base = null;
      int otherWidgetHeight = 0;
      if (overviewBaseSection.isExpanded()){
         otherWidgetHeight = (uutLogSection.getClientArea().height + PAD) * OTHER_ROWS;
         base = overviewSectionContentScrollable;
         overviewSectionContent.setSize(uutLogSection.getClientArea().width, overviewSectionContent.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
         overviewSectionContentScrollable.setMinSize(overviewSectionContent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      } else if(versionBaseSection.isExpanded()){
         otherWidgetHeight = (overviewBaseSection.getClientArea().height + PAD) * OTHER_ROWS;
         base = versionBaseSectionContentScrollable;
         versionBaseSectionContentScrollable.setMinSize(versionBaseSectionContent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      } else if(summarySection.isExpanded()){         
         otherWidgetHeight = (overviewBaseSection.getClientArea().height + PAD) * SUMMARY_ROWS;
         base = outfileTree.getTree();
      } else if(uutLogSection.isExpanded()){
         otherWidgetHeight = (overviewBaseSection.getClientArea().height + PAD) * OTHER_ROWS;
         base = uutLogInfo.getTree();
      } else if(uutVersionSection.isExpanded()){
         otherWidgetHeight = (overviewBaseSection.getClientArea().height + PAD) * OTHER_ROWS;
         base = uutVersionInfo.getTree();
      } else if(oteLogSection.isExpanded()){
         otherWidgetHeight = (overviewBaseSection.getClientArea().height + PAD) * OTHER_ROWS;
         base = oteLogInfo.getTree();
      }
         
      if (base!= null){
         
         GridData gridData = new GridData();
         gridData.grabExcessHorizontalSpace = true;
         gridData.grabExcessVerticalSpace = true;
         gridData.widthHint = notScrolledComposite.getClientArea().width;
         gridData.heightHint = notScrolledComposite.getClientArea().height - otherWidgetHeight;
         gridData.minimumHeight = 55;
         base.setLayoutData(gridData);
         notScrolledComposite.layout(true);
      }
   }

   public void setLargeFile(boolean isLarge) {
      this.isLargeFile = isLarge;
      updateFormText();
      
   }

   private void updateFormText() {
      final StringBuilder sb = new StringBuilder();
      if(isLargeFile || failCount > 50 ){
         sb.append("!!WARNING!!");
         sb.append(" ");
         if(isLargeFile){
            sb.append("[Large File: Stacktrace Info Disabled]");         
         }
         if (failCount > 50 ){
            sb.append("[Only first 50 Markers Created]");
         }
      } else {
         sb.append(OTE_TEST_OUTPUT);
      }
      Display.getDefault().asyncExec(new Runnable(){

         @Override
         public void run() {
            form.setText(sb.toString());
         }
         
      });
   }

   public void setFailCount(int failCount) {
      this.failCount = failCount;
      updateFormText();
   }
}
