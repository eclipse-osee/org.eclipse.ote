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

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.ote.ui.output.OteOutputImage;
import org.eclipse.osee.ote.ui.output.tree.items.IOutfileTreeItem;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class DetailPage extends FormPage {

   private static final String TEST_PROGRAM_DETAILS = "Test Program Details";
   private final DetailSection currentDetailSection = new DetailSection(this, "output");
   private JumpToItem currentJumpToItem;
   private Action downAction;
   private Action upAction;
   private IEditorInput input;
   private IOutputDataCallback callback;
   private boolean runProcessJob = false;
   private final OteOutput editor;
   private boolean isLargeFile;
   private int failCount;
   private ScrolledForm form;

   @Override
   public void setInput(IEditorInput input) {
      this.input = input;
   }

   public boolean navigateToTreeItem(IOutfileTreeItem item) {
      return currentDetailSection.navigateToTreeItem(item);
   }

   public DetailPage(OteOutput editor) {
      super(editor, String.format("Details%d", editor.hashCode()), "Details");
      this.editor = editor;
   }

   @Override
   public void setActive(boolean active) {
      super.setActive(active);
      if (runProcessJob) {
         runProcessJob = false;
         final OteJob oteJob = getProcessDetailsJob();
         Job job = new Job(oteJob.toString()) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
               return oteJob.run(monitor);
            }
         };
         job.schedule();
      }
   }

   public boolean needToRunProcessJob() {
      return runProcessJob;
   }

   public OteJob getProcessDetailsJob() {
      OteJob processOutFile = new OteJob(String.format("Process Details for [%s]", input.getName())) {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               new ProcessOutfileDetails(input, callback).run(monitor);
            } catch (Exception ex) {
               OseeLog.log(DetailPage.class, Level.SEVERE, ex);
               return Status.CANCEL_STATUS;
            }
            return Status.OK_STATUS;
         }

      };
      return processOutFile;
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {

      super.createFormContent(managedForm);
      form = managedForm.getForm();

      final Composite body = managedForm.getForm().getBody();
      body.setLayout(new GridLayout());

      /**
       * Create the composite which should not have the scrollbar and set its layout data
       * to GridData with width and height hints equal to the size of the form's body
       */
      final Composite notScrolledComposite = managedForm.getToolkit().createComposite(body);
      final GridData gdata = GridDataFactory.fillDefaults()
              .grab(true, true)
              .hint(body.getClientArea().width, body.getClientArea().height)
              .create();
      notScrolledComposite.setLayoutData(gdata);
      
      
      form.setText(TEST_PROGRAM_DETAILS);

      Action openInTextEditorAction = new Action("Open In TextEditor", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            editor.openInFlatTextFile();
         }
      };
      openInTextEditorAction.setToolTipText("Transform output to a flat text file.");
      openInTextEditorAction.setImageDescriptor(ImageManager.getImageDescriptor(OteOutputImage.EDIT));
      managedForm.getForm().getToolBarManager().add(openInTextEditorAction);
      managedForm.getForm().updateToolBar();

      downAction = new Action("Jump Down", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            if (currentJumpToItem != null) {
               IOutfileTreeItem item = currentJumpToItem.getNextItem();
               navigateToTreeItem(item);
            }
         }
      };
      downAction.setToolTipText("Next Failed Testpoint");
      downAction.setImageDescriptor(ImageManager.getImageDescriptor(OteOutputImage.ARROW_DOWN_YELLOW));
      downAction.setEnabled(true);
      managedForm.getForm().getToolBarManager().add(downAction);

      upAction = new Action("Jump Up", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            if (currentJumpToItem != null) {
               IOutfileTreeItem item = currentJumpToItem.getPreviousItem();
               navigateToTreeItem(item);
            }
         }
      };
      upAction.setToolTipText("Previous Failed Testpoint");
      upAction.setImageDescriptor(ImageManager.getImageDescriptor(OteOutputImage.ARROW_UP_YELLOW));
      upAction.setEnabled(true);
      managedForm.getForm().getToolBarManager().add(upAction);

      managedForm.getForm().updateToolBar();

      currentDetailSection.createFormContent(notScrolledComposite);
      
   // Add resize listener to sash form's parent so that sash form always fills the page
      form.getBody().addControlListener(new ControlAdapter() {
         
          @Override
          public void controlResized(ControlEvent e)
          {
             super.controlResized(e);
             notScrolledComposite.layout(true);
          }
      });
      form.getBody().pack();
   }

   public void addDetailsData(IOutfileTreeItem item) {
      currentDetailSection.addDetailsData(item);
   }

   public void refresh() {
      currentDetailSection.refresh();
      if (getManagedForm() != null) {
         getManagedForm().reflow(true);
      }
   }

   public void addJumpToList(IOutfileTreeItem testpoint) {
      if( currentJumpToItem == null )
         currentJumpToItem = new JumpToItem();

      currentJumpToItem.add(testpoint);
   }

   private class JumpToItem {

      private final List<IOutfileTreeItem> items;
      private int currentItemIndex = -1;

      public JumpToItem() {
         this.items = new LinkedList<>();
      }

      public void add(IOutfileTreeItem testpoint) {
         items.add(testpoint);
      }

      public IOutfileTreeItem getNextItem() {
         currentItemIndex++;
         if (currentItemIndex >= items.size()) {
            currentItemIndex = 0;
         }
         IOutfileTreeItem item = items.get(currentItemIndex);
         return item;
      }

      public IOutfileTreeItem getPreviousItem() {
         currentItemIndex--;
         if (currentItemIndex < 0) {
            currentItemIndex = items.size() - 1;
         }
         IOutfileTreeItem item = items.get(currentItemIndex);
         return item;
      }

      public void setCurrentItem(IOutfileTreeItem firstElement) {
         for (int i = 0; i < items.size(); i++) {
            if (items.get(i).equals(firstElement)) {
               currentItemIndex = i;
               break;
            }
         }
      }

   }

   public void checkCurrentJumpTo(IOutfileTreeItem firstElement) {
      if (currentJumpToItem != null) {
         currentJumpToItem.setCurrentItem(firstElement);
      }
   }

   public void addContent(StringBuilder sb) {
      currentDetailSection.addContent(sb);
   }

   public void clear() {
      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            currentDetailSection.clear();
            currentJumpToItem = null;
            getManagedForm().reflow(true);
            setRunProcessJob(true);
         }

      });

   }

   public void setCallback(IOutputDataCallback callback) {
      this.callback = callback;
   }

   public void setRunProcessJob(boolean runProcessJob) {
      this.runProcessJob = runProcessJob;
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
         sb.append(TEST_PROGRAM_DETAILS);
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
   
   @Override
   public void dispose(){
      super.dispose();
      currentDetailSection.dispose();
   }

}
