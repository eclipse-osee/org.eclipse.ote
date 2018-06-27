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
package org.eclipse.osee.ote.ui.test.manager.pages;

import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.ui.test.manager.OteTestManagerImage;
import org.eclipse.osee.ote.ui.test.manager.configuration.LoadWidget;
import org.eclipse.osee.ote.ui.test.manager.configuration.SaveWidget;
import org.eclipse.osee.ote.ui.test.manager.connection.ScriptManager;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.osee.ote.ui.test.manager.internal.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.jobs.ScriptRunJob;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public abstract class ScriptPage extends TestManagerPage {

   private static final String NOT_CONNECTED = "<< NOT_CONNECTED >>";
   private static final int ABORT_BTN_TIMER = 5000;
   
   public enum UpdateableLabel {
      HOSTLABEL,
      CONFIGPATHLABEL;
   }

   public static final OseeUiActivator plugin = TestManagerPlugin.getInstance();
   private static final String pageName = "Scripts";
   private ToolItem abortButton;
   private ToolItem abortBatchButton;
   private CoolBar coolBar;
   private ToolItem deleteButton;
   private Label hostConnectLabel;
   private LoadWidget loadWidget;
   protected ToolItem runButton;
   private SaveWidget saveWidget;
   private ScriptTableViewer scriptTable;
   private StatusWindowWidget statusWindow;
   private final TestManagerEditor testManagerEditor;

   public ScriptPage(Composite parent, int style, TestManagerEditor parentTestManager) {
      super(parent, style, parentTestManager);
      this.testManagerEditor = parentTestManager;
   }

   public void addFile(String fullPath) {
      scriptTable.addFile(fullPath);
   }

   @Override
   public void createPage() {
      super.createPage();
      Composite parent = (Composite) getContent();
      coolBar = new CoolBar(parent, SWT.FLAT);

      createControlsToolBar(coolBar);
      createConfigurationToolBar(coolBar);
      packCoolBar();

      SashForm sashForm = new SashForm(parent, SWT.NONE);
      sashForm.setLayout(new GridLayout());
      sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      sashForm.setOrientation(SWT.VERTICAL);
      sashForm.SASH_WIDTH = 3;

      createScriptTableSection(sashForm);
      createStatusWindow(sashForm);

      sashForm.setWeights(new int[] {8, 2});
      setMinSize(0, 0);
      
      // TODO: Change to use OteHelpContext
      HelpUtil.setHelp(this, "test_manager_scripts_page", "org.eclipse.osee.framework.help.ui");
   }

   public void loadStorageString() {
      scriptTable.loadStorageString(testManagerEditor.loadValue(testManagerEditor.scriptsQualName));
   }

   @Override
   public void dispose() {
      scriptTable.dispose();
      testManagerEditor.storeValue(testManagerEditor.scriptsQualName, scriptTable.getStorageString());
      OseeLog.log(TestManagerPlugin.class, Level.INFO, "ScriptPage Dispose Called");
      super.dispose();
   }

   public CoolBar getCoolBar() {
      return coolBar;
   }

   public String getOFP() {
      if (hostConnectLabel == null) {
         return "";
      }
      return hostConnectLabel.getText();
   }

   @Override
   public String getPageName() {
      return pageName;
   }

   public String getScripts() {
      if (scriptTable == null) {
         return "";
      } else if (scriptTable.getTaskList() == null) {
         return "";
      }
      return scriptTable.getTaskList().toString();
   }

   public ScriptTableViewer getScriptTableViewer() {
      return scriptTable;
   }

   public StatusWindowWidget getStatusWindow() {
      return statusWindow;
   }

   public void onScriptRunning(final boolean running) {
      AWorkbench.getDisplay().asyncExec(new Runnable() {
         @Override
         public void run() {
            if(runButton.isDisposed()){
               return;
            }
            if (running) {
               runButton.setEnabled(false);
               abortButton.setEnabled(true);
               abortBatchButton.setEnabled(true);
            } else {
               runButton.setEnabled(true);
               abortButton.setEnabled(false);
               abortBatchButton.setEnabled(false);
            }
         }
      });
   }

   public void packCoolBar() {
      Point size = this.getSize();
      coolBar.setSize(coolBar.computeSize(size.x, size.y));
   }

   private void createConfigurationToolBar(CoolBar coolBar) {
      CoolItem configCoolItem = new CoolItem(coolBar, SWT.NONE);
      ToolBar configToolBar = new ToolBar(coolBar, SWT.FLAT | SWT.HORIZONTAL);

      saveWidget = new SaveWidget(this);
      saveWidget.createToolItem(configToolBar);

      loadWidget = new LoadWidget(this);
      loadWidget.createToolItem(configToolBar);

      deleteButton = new ToolItem(configToolBar, SWT.PUSH | SWT.CENTER);
      deleteButton.setImage(ImageManager.getImage(OteTestManagerImage.FILE_DELETE));
      deleteButton.setToolTipText("Deletes Selected (highlighted) Scripts");
      deleteButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleDeleteButton();
         }
      });
      deleteButton.addListener(SWT.MouseUp, new Listener() {
         @Override
         public void handleEvent(Event e) {
            if (e.button == 3) {
               handleDeleteButton();
            }
         }
      });

      configToolBar.pack();

      Point size = configToolBar.getSize();
      configCoolItem.setControl(configToolBar);
      configCoolItem.setSize(configCoolItem.computeSize(size.x, size.y));
      configCoolItem.setMinimumSize(size);
   }

   private void createControlsToolBar(CoolBar coolBar) {
      CoolItem controlsCoolItem = new CoolItem(coolBar, SWT.NONE);
      ToolBar controlsToolBar = new ToolBar(coolBar, SWT.FLAT | SWT.HORIZONTAL);

      runButton = new ToolItem(controlsToolBar, SWT.PUSH | SWT.CENTER);
      runButton.setImage(ImageManager.getImage(OteTestManagerImage.SEL_RUN_EXEC));
      runButton.setDisabledImage(ImageManager.getImage(OteTestManagerImage.UNSEL_RUN_EXEC));
      runButton.setToolTipText("Runs the Checked Scripts");
      runButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleRunButton();
         }
      });
      runButton.addListener(SWT.MouseUp, new Listener() {
         @Override
         public void handleEvent(Event e) {
            if (e.button == 3) {
               handleRunButton();
            }
         }
      });
      runButton.setEnabled(false);

      // Create and configure the "Abort" button
      abortButton = new ToolItem(controlsToolBar, SWT.PUSH | SWT.CENTER);
      abortButton.setImage(ImageManager.getImage(OteTestManagerImage.SEL_ABORT_STOP));
      abortButton.setDisabledImage(ImageManager.getImage(OteTestManagerImage.UNSEL_ABORT_STOP));
      abortButton.setToolTipText("Abort Currently Running Script");
      abortButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleAbortButton();
            abortBatchButton.setEnabled(false);
            abortButton.setEnabled(false);
            Timer timer = new Timer();
            timer.schedule(new EnabledAbortsTimer(), ABORT_BTN_TIMER);
         }
      });
      abortButton.addListener(SWT.MouseUp, new Listener() {
         @Override
         public void handleEvent(Event e) {
            if (e.button == 3) {
               handleAbortButton();
               abortBatchButton.setEnabled(false);
               abortButton.setEnabled(false);
               Timer timer = new Timer();
               timer.schedule(new EnabledAbortsTimer(), ABORT_BTN_TIMER);
            }
         }
      });
      abortButton.setEnabled(false);

      abortBatchButton = new ToolItem(controlsToolBar, SWT.PUSH | SWT.CENTER);
      abortBatchButton.setImage(ImageManager.getImage(OteTestManagerImage.SEL_BATCH_ABORT_STOP));
      abortBatchButton.setDisabledImage(ImageManager.getImage(OteTestManagerImage.UNSEL_BATCH_ABORT_STOP));
      abortBatchButton.setToolTipText("Abort Script Batch");
      abortBatchButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleBatchAbortButton();
            abortBatchButton.setEnabled(false);
            abortButton.setEnabled(false);
            Timer timer = new Timer();
            timer.schedule(new EnabledAbortsTimer(), ABORT_BTN_TIMER);
         }
      });
      abortBatchButton.addListener(SWT.MouseUp, new Listener() {
         @Override
         public void handleEvent(Event e) {
            if (e.button == 3) {
               handleBatchAbortButton();
               abortBatchButton.setEnabled(false);
               abortButton.setEnabled(false);
               Timer timer = new Timer();
               timer.schedule(new EnabledAbortsTimer(), ABORT_BTN_TIMER);
            }
         }
      });
      abortBatchButton.setEnabled(false);

      controlsToolBar.pack();

      Point size = controlsToolBar.getSize();
      controlsCoolItem.setControl(controlsToolBar);
      controlsCoolItem.setSize(controlsCoolItem.computeSize(size.x, size.y));
      controlsCoolItem.setMinimumSize(size);
   }

   private void createScriptTableSection(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      scriptTable = new ScriptTableViewer(composite, this.getTestManager());
      //      scriptTable.addDisposeListener(new DisposeListener() {
      //         public void widgetDisposed(DisposeEvent e) {
      //            testManagerEditor.storeValue(testManagerEditor.scriptsQualName, scriptTable.getStorageString());
      //         }
      //      });

   }

   private void createStatusWindow(Composite parent) {
      statusWindow = new StatusWindowWidget(parent);

      statusWindow.setLabelAndValue(UpdateableLabel.HOSTLABEL.name(), "Selected Host", NOT_CONNECTED, SWT.BOLD,
         SWT.COLOR_DARK_RED);

      String selectedFile = testManagerEditor.loadValue(testManagerEditor.configFileName);
      if (!Strings.isValid(selectedFile)) {
         selectedFile = testManagerEditor.getDefaultConfigPath();
         testManagerEditor.storeValue(testManagerEditor.configFileName, selectedFile);
      }
      OseeLog.log(TestManagerPlugin.class, Level.INFO, "The default config is: " + selectedFile);

      statusWindow.setLabelAndValue(UpdateableLabel.CONFIGPATHLABEL.name(), "Config File Path", selectedFile);

      saveWidget.setStatusLabel(statusWindow);
      loadWidget.setStatusLabel(statusWindow);

      // // Status ICON Labels
      // connectStatusIconLabel = new
      // Label(statusWindow.getStatusIconComposite(), SWT.NONE);
      // hostWidget.setConnectStatusLabel(connectStatusIconLabel);
      // connectStatusIconLabel.setVisible(false);

      statusWindow.refresh();
   }

   // TODO this stuff needs some updating too...
   protected void handleAbortButton() {
      TestManagerPlugin.getInstance().getOteConsoleService().write("Aborting Test Script...");
      try {
         getScriptManager().abortScript(false);
      } catch (RemoteException e) {
         TestManagerPlugin.getInstance().getOteConsoleService().writeError(Lib.exceptionToString(e));
      }
   }

   // TODO this stuff needs some updating too...
   protected void handleBatchAbortButton() {
      TestManagerPlugin.getInstance().getOteConsoleService().write("Aborting Test Script Batch...");
      try {
         getScriptManager().abortScript(true);
      } catch (RemoteException e) {
         TestManagerPlugin.getInstance().getOteConsoleService().writeError(Lib.exceptionToString(e));
      }
   }

   private void handleDeleteButton() {
      scriptTable.removeSelectedTasks();
   }

   private void handleRunButton() {
      ScriptRunJob runJob = new ScriptRunJob(getTestManager());
      if (runJob.isRunAllowed()) {
         runJob.setPriority(Job.LONG);
         runJob.setUser(true);
         runJob.schedule();
      } else {
         MessageDialog.openError(Displays.getActiveShell(), "Error", runJob.getErrorMessage());
      }
   }

   private class EnabledAbortsTimer extends TimerTask {

      @Override
      public void run() {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               try {
                  if (!runButton.isEnabled()) {
                     abortBatchButton.setEnabled(true);
                     abortButton.setEnabled(true);
                  }
               } catch (Throwable th) {

               }
            }
         });

      }

   }

   @Override
   public boolean areSettingsValidForRun() {
      return getScriptTableViewer().getRunTasks().size() > 0;
   }

   @Override
   public String getErrorMessage() {
      StringBuilder builder = new StringBuilder();
      if (getScriptTableViewer().getRunTasks().size() <= 0) {
         builder.append("Scripts not selected.");
      }
      return builder.toString();
   }

   @Override
   public boolean onConnection(final ConnectionEvent event) {
      boolean result = getScriptManager().connect(event);
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            runButton.setEnabled(true);
            abortButton.setEnabled(false);
            abortBatchButton.setEnabled(false);
            scriptTable.onConnectionChanged(true);
            statusWindow.setValue(UpdateableLabel.HOSTLABEL.name(), event.getProperties().getStation(), SWT.BOLD,
               SWT.COLOR_DARK_GREEN);
            statusWindow.refresh();
         }

      });
      return result;
   }

   @Override
   public boolean onDisconnect(ConnectionEvent event) {
      boolean result = getScriptManager().disconnect(event);
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            runButton.setEnabled(false);
            abortButton.setEnabled(false);
            abortBatchButton.setEnabled(false);
            scriptTable.onConnectionChanged(false);
            statusWindow.setValue(UpdateableLabel.HOSTLABEL.name(), NOT_CONNECTED, SWT.BOLD, SWT.COLOR_DARK_RED);
            statusWindow.refresh();
         }
      });
      return result;
   }

   @Override
   public boolean onConnectionLost() {
      boolean result = getScriptManager().onConnectionLost();
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            runButton.setEnabled(false);
            abortButton.setEnabled(false);
            abortBatchButton.setEnabled(false);
            scriptTable.onConnectionChanged(false);
            statusWindow.setValue(UpdateableLabel.HOSTLABEL.name(), NOT_CONNECTED, SWT.BOLD, SWT.COLOR_DARK_RED);
            statusWindow.refresh();
         }
      });
      return result;
   }

   public abstract ScriptManager getScriptManager();

   public void addFiles(String[] files) {
      scriptTable.addFiles(files);
   }
}