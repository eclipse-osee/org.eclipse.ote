/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.ui.message.watch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.jdk.core.util.benchmark.Benchmark;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.osee.ote.client.msg.IOteMessageService;
import org.eclipse.osee.ote.client.msg.core.IMessageSubscription;
import org.eclipse.osee.ote.message.MessageDefinitionProvider;
import org.eclipse.osee.ote.message.MessageProviderVersion;
import org.eclipse.osee.ote.message.interfaces.ITestEnvironmentMessageSystem;
import org.eclipse.osee.ote.message.tool.IUdpTransferListener;
import org.eclipse.osee.ote.message.tool.MessageMode;
import org.eclipse.osee.ote.message.tool.TransferConfig;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.service.IOteClientService;
import org.eclipse.osee.ote.service.ITestConnectionListener;
import org.eclipse.ote.ui.message.internal.Activator;
import org.eclipse.ote.ui.message.internal.SWTResourceManager;
import org.eclipse.ote.ui.message.internal.WatchImages;
import org.eclipse.ote.ui.message.messageXViewer.MessageXViewer;
import org.eclipse.ote.ui.message.tree.AbstractTreeNode;
import org.eclipse.ote.ui.message.tree.ElementNode;
import org.eclipse.ote.ui.message.tree.INodeVisitor;
import org.eclipse.ote.ui.message.tree.MessageNode;
import org.eclipse.ote.ui.message.tree.MessageWatchLabelProvider;
import org.eclipse.ote.ui.message.tree.RootNode;
import org.eclipse.ote.ui.message.tree.WatchList;
import org.eclipse.ote.ui.message.tree.WatchedElementNode;
import org.eclipse.ote.ui.message.tree.WatchedMessageNode;
import org.eclipse.ote.ui.message.util.ClientMessageServiceTracker;
import org.eclipse.ote.ui.message.util.IOteMessageClientView;
import org.eclipse.ote.ui.message.watch.action.ClearUpdatesAction;
import org.eclipse.ote.ui.message.watch.action.ConvertWritersToReadersAction;
import org.eclipse.ote.ui.message.watch.action.DeleteSelectionAction;
import org.eclipse.ote.ui.message.watch.action.SendMessageAction;
import org.eclipse.ote.ui.message.watch.action.SetDataSourceMenu;
import org.eclipse.ote.ui.message.watch.action.SetMessageModeMenu;
import org.eclipse.ote.ui.message.watch.action.SetValueAction;
import org.eclipse.ote.ui.message.watch.action.WatchElementAction;
import org.eclipse.ote.ui.message.watch.action.ZeroizeElementAction;
import org.eclipse.ote.ui.message.watch.action.ZeroizeMessageAction;
import org.eclipse.ote.ui.message.watch.recording.RecordingWizard;
import org.eclipse.ote.ui.message.watch.recording.xform.CsvTransform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.FrameworkUtil;

/**
 * A view that allows the monitoring of messages and their associated elements
 * 
 * @author Ken J. Aguilar
 */
public final class WatchView extends ViewPart implements ITestConnectionListener, IOteMessageClientView {
   public static final RGB COLOR_GOLDENROD = new RGB(255,193,37);

   private MessageXViewer treeViewer;
   private final ClientMessageServiceTracker msgServiceTracker;
   private Label statusTxt;
   private Clipboard cb;
   private final File watchFile;
   private Button recordButton;
   private final Benchmark benchMark = new Benchmark("Message Watch Update Time");

   public static final String VIEW_ID = "org.eclipse.ote.ui.message.watch.WatchView";

   private DetailsBox detailsBox;
   final IUdpTransferListener recBtnListener = new IUdpTransferListener() {

      @Override
      public void onTransferComplete(final TransferConfig config) {
         OseeLog.log(Activator.class, Level.INFO, "file transfer complete");
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               openInfo("Message Recorder",
                        "Message recording file " + config.getFileName() + " is now ready for opening");
            }
         });
      }

      @Override
      public void onTransferException(final TransferConfig config, final Throwable t) {
         OseeLog.log(Activator.class, Level.SEVERE, "problems writing to recorder output file " + config.getFileName(),
                     t);
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               recordButton.setSelection(false);
               openInfo("Message Recorder",
                        "An exception occurred while writing to recorder output file " + config.getFileName());
            }
         });
      }
   };

   private IOteMessageService messageService = null;

   private final SelectionListener recBtnHandler = new SelectionListener() {

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
         widgetSelected(e);
      }

      @Override
      public void widgetSelected(SelectionEvent e) {
         if (recordButton.getSelection()) {

            RecordingWizard recordingWizard = new RecordingWizard(watchList);
            final WizardDialog recdialog = new WizardDialog(Displays.getActiveShell(), recordingWizard);
            int recResult = recdialog.open();
            if (Window.OK == recResult) {
               try {
                  saveWatchFile();
                  messageService.startRecording(recordingWizard.getFileName(),
                                                recordingWizard.getFilteredMessageRecordDetails()).addListener(recBtnListener);
               } catch (FileNotFoundException ex) {
                  MessageDialog.openError(Displays.getActiveShell(), "Recording Error",
                                          "Failed to open file for writing. " + "Make sure its not being used by another application");
                  recordButton.setSelection(false);
               } catch (Throwable ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, "Failed to start message recording", ex);
                  MessageDialog.openError(Displays.getActiveShell(), "Recording Error",
                        "Exception ocurred while recording. see error log");
                  recordButton.setSelection(false);
               }
            } else {
               recordButton.setSelection(false);
            }
         } else {
            try {
               messageService.stopRecording();
            } catch (IOException ioe) {
               OseeLog.log(Activator.class, Level.WARNING, "problem when attempting to stop recording", ioe);
            } catch (Throwable t) {
               OseeLog.log(Activator.class, Level.SEVERE, "problem when attempting to stop recording", t);
            }
         }
      }
   };

   private static enum Status {
      /**
       * no active test manager
       */
      NO_TEST_MANAGER("No test manager running"),
      /**
       * active test manager but not connected to a host
       */
      NOT_CONNECTED("%s: Not connected to a host"),
      /**
       * active test manager and connected to a host
       */
      CONNECTED("Connected to %s (%s)");

      private final String txt;

      Status(final String txt) {
         this.txt = txt;
      }

      public String asString(Object... args) {
         return String.format(txt, args);
      }

   }

   private Composite parentComposite;
   private WatchList watchList;

   private WatchViewMessageDefinitionProviderTracker watchViewMessageDefinitionProviderTracker;

   private MessageProviderVersion messageProviderVersion;

   protected boolean librariesLoaded;

   private boolean writerIsPresent;

   private ActionButton removeWritersBtn;

   public WatchView() {
      watchFile = OseeData.getFile("msgWatch.txt");
      msgServiceTracker = new ClientMessageServiceTracker(Activator.getDefault().getBundle().getBundleContext(), this);
      messageProviderVersion = new MessageProviderVersion();
   }

   @Override
   public void createPartControl(Composite parent) {
      final int numColumns = 4;
      parentComposite = parent;

      GridLayout layout = new GridLayout();
      layout.numColumns = numColumns;
      layout.verticalSpacing = 0;
      layout.marginWidth = 5;
      layout.marginHeight = 5;
      parent.setLayout(layout);

      Label label = new Label(parent, SWT.RIGHT);
      label.setText("Status:");
      Widgets.positionGridItem(label, false, false, SWT.END, SWT.CENTER, 1);

      statusTxt = new Label(parent, SWT.READ_ONLY);
      statusTxt.setText(Status.NO_TEST_MANAGER.asString());
      Widgets.positionGridItem(statusTxt, true, false, SWT.BEGINNING, SWT.BEGINNING, 3);

      Composite buttons = new Composite(parent, SWT.NONE);
      GridDataFactory.swtDefaults().grab(true, false).span(numColumns, 1).applyTo(buttons);
      buttons.setLayout(new RowLayout(SWT.HORIZONTAL));

      recordButton = new Button(buttons, SWT.TOGGLE);
      recordButton.setText("REC");
      recordButton.setToolTipText("Record the messages and elements currently shown in Message Watch.");
      recordButton.addSelectionListener(recBtnHandler);
      recordButton.setEnabled(false);

      CsvTransform csvAction = new CsvTransform();
      ActionButton btn = new ActionButton(buttons, SWT.PUSH, csvAction, "Csv Transform", VIEW_ID);
      btn.setToolTipText("Transform the base CSV format.");

      ConvertWritersToReadersAction writerAction = new ConvertWritersToReadersAction(this);
      removeWritersBtn = new ActionButton(buttons, SWT.PUSH, writerAction, "Remove writers", VIEW_ID);
      removeWritersBtn.setToolTipText("Converts all writers to readers");
      removeWritersBtn.setEnabled(false);

      final SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
      // sashForm.SASH_WIDTH = 1;
      Widgets.positionGridItem(sashForm, true, true, SWT.FILL, SWT.FILL, numColumns);

      Composite comp = new Composite(sashForm, SWT.NONE);
      comp.setLayout(new GridLayout(1, false));
      GridData gd = new GridData(GridData.FILL_BOTH);
      comp.setLayoutData(gd);

      // Create the tree treeViewer as a child of the composite parent
      treeViewer =
            new MessageXViewer(comp,
                               SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.DOUBLE_BUFFERED);
      GridData layoutData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
      layoutData.horizontalSpan = numColumns;
      treeViewer.getControl().setLayoutData(layoutData);
      watchList = new WatchList(this);
      treeViewer.setContentProvider(watchList);
      treeViewer.setLabelProvider(new MessageWatchLabelProvider(treeViewer));
      treeViewer.setUseHashlookup(true);

      treeViewer.getTree().setHeaderVisible(true);
      treeViewer.getTree().setLinesVisible(true);

      parent.addDisposeListener(new DisposeListener() {

         @Override
         public void widgetDisposed(DisposeEvent e) {
            saveWatchFile();
         }
      });
      detailsBox = new DetailsBox(sashForm);
      detailsBox.setMessageInfoSelectionListener(new MessageInfoSelectionHandler(this));

      sashForm.setWeights(new int[] {75, 25});
      // Add Listeners to the Tree
      treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
         private AbstractTreeNode lastNodeSelected = null;

         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            final IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();

            final AbstractTreeNode node = (AbstractTreeNode) selection.getFirstElement();
            if (node != null && node != lastNodeSelected) {
               if (lastNodeSelected != null) {
                  lastNodeSelected.setSelected(false);
               }
               try {
                  selectNode(node);
               } catch (ArrayIndexOutOfBoundsException t) {
                  // throw if there is an error in the message jar
                  // (usually... )
                  final String msg =
                        String.format("Problems occurred when trying to display details for %s: (See Error Log)",
                                      node.getName());
                  OseeLog.log(Activator.class, Level.SEVERE, "Error while displaying details for " + node.getName(), t);
                  openInfo("Possible Message JAR Error", msg);
               }
               lastNodeSelected = node;
            }
         }
      });
      treeViewer.getTree().addMouseListener(new MouseAdapter() {

         @Override
         public void mouseDown(MouseEvent e) {
            if (e.button == 3) {
               showContextMenu(new Point(e.x, e.y));
            }
         }
      });
      
      treeViewer.addDoubleClickListener(new IDoubleClickListener(){

         @Override
         public void doubleClick(DoubleClickEvent event) {
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();
            Object element = selection.getFirstElement();
            if(element instanceof ElementNode) {
               WatchedMessageNode messageNode = getWatchList().getMessageNode(((ElementNode) element).getMessageName());
               IMessageSubscription subscription = messageNode.getSubscription();
               if(subscription.getMessageMode() == MessageMode.WRITER) {
                  ElementNode node = (ElementNode) element;
                  SetValueAction act = new SetValueAction(WatchView.this, node);
                  act.run();
               }
            }
         }});

      // Create menu, toolbars, filters, sorters.
      createToolBar();

      getSite().setSelectionProvider(treeViewer);

      treeViewer.addCustomizeToViewToolbar(this);
      createMenuActions();

      setNoLibraryStatus();
      IOteClientService clientService = Activator.getDefault().getOteClientService();
      if (clientService == null) {
         throw new IllegalStateException("cannot acquire ote client service");
      }
      msgServiceTracker.open(true);

      cb = new Clipboard(Display.getCurrent());

      treeViewer.getControl().addKeyListener(new KeyListener() {

         @Override
         public void keyPressed(KeyEvent e) {
            if (e.stateMask == SWT.CTRL && e.keyCode == 'v') {
               TextTransfer transfer = TextTransfer.getInstance();
               String data = (String) cb.getContents(transfer);
               if (data != null) {
                  AddWatchParameter param = new AddWatchParameter();
                  addWatchMessage(param);
               }
            } else if (e.stateMask != SWT.CTRL && e.stateMask != SWT.ALT && e.keyCode == SWT.DEL){
               final IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
               watchList.deleteSelection(selection);
               refresh();
            }
         }

         @Override
         public void keyReleased(KeyEvent e) {
         }
      });

      addSelectionBackgroundChanger(treeViewer);

      int ops = DND.DROP_COPY | DND.DROP_MOVE;
      Transfer[] transfers = new Transfer[] {FileTransfer.getInstance(), TextTransfer.getInstance()};
      treeViewer.addDropSupport(ops, transfers, new WatchViewDropAdapter(this));

      watchViewMessageDefinitionProviderTracker = new WatchViewMessageDefinitionProviderTracker(FrameworkUtil.getBundle(getClass()).getBundleContext(), this);
      watchViewMessageDefinitionProviderTracker.open(true);

      clientService.addConnectionListener(this);
      loadWatchFile();

   }

   /**
    * This code ensures that the Writer only background color is preserved when the row is selected. 
    * Otherwise, the selection would hide whether the message is a reader or a writer.
    * @param viewer
    */
   private void addSelectionBackgroundChanger(final MessageXViewer viewer) {
      viewer.getTree().addListener(SWT.EraseItem, new Listener() {

         @Override
         public void handleEvent(Event event) {

            Tree table =(Tree)event.widget;
            TreeItem item =(TreeItem)event.item;
            Object data = item.getData();
            if( data instanceof WatchedMessageNode ) {
               WatchedMessageNode watchedMessageNode = (WatchedMessageNode) data;
               if( watchedMessageNode.getSubscription().getMessageMode() == MessageMode.READER){
                  return;
               }

            } else {
               return;
            }
            event.detail &= ~SWT.HOT;

            if ((event.detail & SWT.SELECTED) == 0) return; /// item not selected

            int clientWidth = table.getClientArea().width;

            GC gc = event.gc;               
            Color oldForeground = gc.getForeground();
            Color oldBackground = SWTResourceManager.getColor(202,225,255);

            final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
            final AbstractTreeNode node = findElementMatching(selection, data);
            Color background = node.getBackground();
            if( background == null )
               background = oldBackground;

            gc.setBackground(background);
            gc.setForeground(oldForeground);              
            gc.fillRectangle(0, event.y, clientWidth, event.height);

            gc.setForeground(oldForeground);
            gc.setBackground(oldBackground);
            event.detail &= ~SWT.SELECTED;
         }

         private AbstractTreeNode findElementMatching(IStructuredSelection selection, Object data) {
            Object[] array = selection.toArray();
            for (int i = 0; i < array.length; i++) {
               Object cur = array[i];
               if( cur == data) {
                  return (AbstractTreeNode) cur;
               }
            }
            return (AbstractTreeNode) data;
         }
      });
   }

   @Override
   public void dispose() {
      watchViewMessageDefinitionProviderTracker.close();
      if (detailsBox != null) {
         detailsBox.dispose();
      }
      msgServiceTracker.close();
      Activator.getDefault().getOteClientService().removeConnectionListener(WatchView.this);
      SWTResourceManager.dispose();
      super.dispose();
   }

   public void createToolBar() {
      Action expandAction = new Action("Expand All") {

         @Override
         public void run() {
            treeViewer.getTree().setRedraw(false);
            treeViewer.expandAll();
            treeViewer.getTree().setRedraw(true);
         }
      };
      expandAction.setImageDescriptor(WatchImages.EXPAND_STATE.createImageDescriptor());
      expandAction.setToolTipText("Expand All");

      Action showNameAction = new Action("Show Names", SWT.TOGGLE) {

         @Override
         public void run() {
            treeViewer.refresh();
         }
      };
      showNameAction.setImageDescriptor(WatchImages.SHOW_NAMES.createImageDescriptor());
      showNameAction.setToolTipText("Show Message Names");

      Action collapseAction = new Action("Collapse All") {

         @Override
         public void run() {
            treeViewer.getTree().setRedraw(false);
            treeViewer.collapseAll();
            treeViewer.getTree().setRedraw(true);
         }
      };
      collapseAction.setImageDescriptor(WatchImages.COLLAPSE_STATE.createImageDescriptor());
      collapseAction.setToolTipText("Collapse All");

      Action deleteAction = new Action("Delete") {
         @Override
         public void run() {
            final IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
            watchList.deleteSelection(selection);
            refresh();
         }
      };

      deleteAction.setToolTipText("Delete");

      deleteAction.setImageDescriptor(WatchImages.DELETE.createImageDescriptor());

      Action deleteAllAction = new Action("Delete All") {

         @Override
         public void run() {
            if (MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                                          "Delete All", "Delete All Watch Items?")) {
               watchList.deleteAll();
               refresh();
            }
         }
      };
      deleteAllAction.setToolTipText("Delete All");
      deleteAllAction.setImageDescriptor(WatchImages.DELETE_ALL.createImageDescriptor());

      Action refreshAction = new Action("Refresh") {
         @Override
         public void run() {
            treeViewer.refresh();
         }
      };
      refreshAction.setToolTipText("refresh");
      refreshAction.setImageDescriptor(WatchImages.REFRESH.createImageDescriptor());

      Action saveAction = new Action("Save Items") {
         private String saveFilePath = null;
         private String lastSaveFileName = null;

         @Override
         public void run() {
            final FileDialog dialog = new FileDialog(treeViewer.getTree().getShell(), SWT.SAVE);
            dialog.setFilterExtensions(new String[] {"*.mwi"});
            if (saveFilePath == null) {
               saveFilePath = System.getProperty("user.home");
               if(saveFilePath == null) {
                  saveFilePath = OseeData.getPath().toOSString();
               }
            }
            if (lastSaveFileName == null) {
               lastSaveFileName = "msgWatchItems.mwi";
            }
            dialog.setFilterPath(saveFilePath);
            dialog.setFileName(lastSaveFileName);

            String selectedFile = dialog.open();
            if (selectedFile != null) {
               if (!selectedFile.endsWith(".mwi")) {
                  selectedFile += ".mwi";
               }
               final File saveFile = new File(selectedFile);
               saveFilePath = saveFile.getAbsolutePath();
               lastSaveFileName = saveFile.getName();
               saveWatchFile(saveFile);
            }
         }
      };
      saveAction.setToolTipText("Save Watch Items");
      saveAction.setImageDescriptor(WatchImages.SAVE.createImageDescriptor());

      Action loadAction = new Action("Load Items") {
         private String loadFilePath = null;
         private String lastLoadFileName = null;

         @Override
         public void run() {
            final FileDialog dialog = new FileDialog(treeViewer.getTree().getShell(), SWT.OPEN);
            dialog.setFilterExtensions(new String[] {"*.mwi"});
            if (loadFilePath == null) {
               loadFilePath = OseeData.getPath().toOSString();
            }
            if (lastLoadFileName != null) {
               dialog.setFileName(lastLoadFileName);
            }

            dialog.setFilterPath(loadFilePath);

            String selectedFile = dialog.open();
            if (selectedFile != null) {
               if (!selectedFile.endsWith(".mwi")) {
                  selectedFile += ".mwi";
               }
               final File loadFile = new File(selectedFile);
               loadFilePath = loadFile.getAbsolutePath();
               lastLoadFileName = loadFile.getName();
               loadWatchFile(loadFile);
            }
         }
      };
      loadAction.setToolTipText("Load Watch Items");
      loadAction.setImageDescriptor(WatchImages.OPEN.createImageDescriptor());
      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(showNameAction);
      toolbarManager.add(refreshAction);
      toolbarManager.add(expandAction);
      toolbarManager.add(collapseAction);
      toolbarManager.add(deleteAction);
      toolbarManager.add(deleteAllAction);
      toolbarManager.add(saveAction);
      toolbarManager.add(loadAction);
   }

   @Override
   public void setFocus() {
      // Set focus so that context sensitive help will work as soon as this
      // view is selected.
      parentComposite.setFocus();
   }

   /**
    * display details about specified node
    * 
    * @param node node whose details will be displayed in the detail window of the GUI
    */
   public void setDetailText(final AbstractTreeNode node) {
      detailsBox.setDetailText(node);
   }

   public void selectNode(AbstractTreeNode node) {
      node.setSelected(true);
      detailsBox.selectNode(node);
   }

   /**
    * shows a context menu depending on the point
    */
   void showContextMenu(Point p) {
      final Tree tree = treeViewer.getTree();
      final Menu contextMenu = getPopupMenu(tree.getParent());
      if (contextMenu != null) {
         p = tree.toDisplay(p);
         contextMenu.setLocation(p);
         contextMenu.setVisible(true);
      }
   }

   public void addWatchMessage(final AddWatchParameter parameter) {
      new Thread(new Runnable(){
         @Override
         public void run() {
            for (MessageParameter message : parameter.getMessageParameters()) {
               Collection<ElementPath> elements = parameter.getMessageElements(message.getMessageName());
               OseeLog.logf(Activator.class, Level.FINEST, "Watch request for message %s", message);
               try {
                  if (elements == null) {
                     elements = new ArrayList<ElementPath>();
                  }
                  MessageMode mode = message.isWriter() ? MessageMode.WRITER : MessageMode.READER;
                  watchList.createElements(message.getMessageName(),message.getDataType(), mode, elements, message.getValueMap());
               } catch (ClassNotFoundException ex1) {
                  if (openProceedWithProcessing("Could not find a class definition for " + message + "\n Do you wish to continue")) {
                     continue;
                  } else {
                     return;
                  }
               } catch (InstantiationException ex1) {
                  if (openProceedWithProcessing("failed to instantiate " + message + "\n Do you wish to continue")) {
                     continue;
                  } else {
                     return;
                  }
               } catch (Exception ex1) {
                  OseeLog.log(Activator.class, Level.SEVERE, "failed to create message node", ex1);
                  if (openProceedWithProcessing("Error processing " + message + ". See Error Log for details.\n Do you wish to continue")) {
                     continue;
                  } else {
                     return;
                  }
               }
            }

            Display.getDefault().asyncExec(new Runnable(){
               @Override
               public void run() {
                  refresh();            
               }
            });
         }
      }).start();
   }

   public void refresh() {
      treeViewer.refresh();
      saveWatchFile();
   }

   /**
    * Convienence method. Opens an info dialog
    */
   private void openInfo(final String title, final String message) {
      MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title, message);
   }

   private boolean openProceedWithProcessing(final String message) {
      MessageDialog dialog =
            new MessageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Proceed?", null, message,
                              MessageDialog.QUESTION, new String[] {"Continue processing with next message", "End message processing"}, 0);
      return dialog.open() == Window.OK;
   }

   private void onDisconnected() {
      OseeLog.log(Activator.class, Level.INFO, "Entered onDisconnected()");
      if (!recordButton.isDisposed()) {
         recordButton.setSelection(false);
      }
      try {
         if (Benchmark.isBenchmarkingEnabled()) {
            OseeLog.logf(Activator.class, Level.INFO, "%s: # samples=%d, max=%d, min=%d, avg=%d", benchMark.getName(),
                         benchMark.getTotalSamples(), benchMark.getLongestSample(), benchMark.getShortestSample(),
                         benchMark.getAverage());
         }
      } catch (Throwable t) {
         OseeLog.log(Activator.class, Level.WARNING, "Exception during disconnect", t);
      }

   }

   private Menu getPopupMenu(final Composite composite) {
      Menu menu = treeViewer.getMenuManager().createContextMenu(composite);
      return menu;
   }

   public void loadWatchFile() {
      loadWatchFile(watchFile);
   }

   public void loadWatchFile(final File watchFile) {
      if (watchFile != null && watchFile.exists()) {
         final Job job = new LoadWatchListJob(this, watchFile);
         Jobs.startJob(job);
      }
   }

   public void loadWatchFile(final String watchFileText) {
      final Job job = new LoadWatchListJob(this, new File(watchFileText));
      Jobs.startJob(job);
   }

   public void saveWatchFile() {
      saveWatchFile(watchFile);
   }

   public void saveWatchFile(File watchFile) {
      if (watchFile == null) {
         return;
      }
      try {
         final FileWriter fw = new FileWriter(watchFile);
         fw.write("version=3.0\n");
         ArrayList<ElementNode> descendants = new ArrayList<ElementNode>(256);
         for (MessageNode treeItem : watchList.getMessages()) {
            WatchedMessageNode msg = (WatchedMessageNode) treeItem;
            fw.write("msg:");
            fw.write(msg.getMessageClassName());
            fw.write('\n');
            fw.write("isWriter=");
            boolean isWriter = msg.getSubscription().getMessageMode() == MessageMode.WRITER;
            fw.write(Boolean.toString(isWriter));
            fw.write('\n');

            fw.write("data type=");
            fw.write(msg.getSubscription().getMemType().name());
            fw.write('\n');

            descendants.clear();
            msg.collectDescendants(descendants);
            Map<ElementPath, String> map = msg.getRequestedValueMap();

            boolean writeElement = isWriter;
            for (ElementNode el : descendants) {
               fw.write('@');
               fw.write(el.getElementPath().asString());
               if (writeElement) {
                  String value;
                  // if a map is present then that means we have a requested value but the message
                  // subscription is not active. We want to use the requested value
                  if (map != null) {
                     value = map.get(el.getElementPath());
                  } else {
                     // upon activation of a subscription the map will be cleared so
                     // use the node's value
                     value = ((WatchedElementNode) el).getValue();
                  }
                  if (value != null) {
                     fw.write('=');
                     fw.write(value);
                  }
               }
               fw.write('\n');
            }
            msg.getRecordingState().write(fw);
         }
         fw.close();
      } catch (Exception e) {
         OseeLog.log(Activator.class, Level.SEVERE, "failed to write watch file at " + watchFile.getAbsolutePath(), e);
      }
   }

   public TreeViewer getTreeViewer() {
      return treeViewer;
   }

   public void updateMenuActions(final IMenuManager mm) {

      final IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
      if (selection.size() > 0) {
         final AbstractTreeNode node = (AbstractTreeNode) selection.getFirstElement();
         node.visit(new INodeVisitor<Object>() {

            @Override
            public Object elementNode(ElementNode node) {
               if (selection.size() == 1) {
                  mm.insertBefore(XViewer.MENU_GROUP_PRE, new SetValueAction(WatchView.this, node));
                  mm.insertBefore(XViewer.MENU_GROUP_PRE, new ZeroizeElementAction(node));
               }
               return null;
            }

            @Override
            public Object messageNode(MessageNode node) {
               if (selection.size() == 1) {
                  WatchedMessageNode msgNode = (WatchedMessageNode) node;
                  mm.insertBefore(XViewer.MENU_GROUP_PRE, new WatchElementAction(WatchView.this, (WatchedMessageNode) node));
                  mm.insertBefore(XViewer.MENU_GROUP_PRE, new Separator());
                  mm.insertBefore(XViewer.MENU_GROUP_PRE, SetDataSourceMenu.createMenu(msgNode));
                  mm.insertBefore(XViewer.MENU_GROUP_PRE, SetMessageModeMenu.createMenu(WatchView.this, msgNode));
                  mm.insertBefore(XViewer.MENU_GROUP_PRE, new SendMessageAction(msgNode));
                  mm.insertBefore(XViewer.MENU_GROUP_PRE, new ZeroizeMessageAction(msgNode));
               }
               return null;
            }

            @Override
            public Object rootNode(RootNode node) {
               return null;
            }

         });
         mm.insertBefore(XViewer.MENU_GROUP_PRE, new ClearUpdatesAction(watchList, selection));
         mm.insertBefore(XViewer.MENU_GROUP_PRE, new DeleteSelectionAction(watchList, selection));
      }
      mm.insertBefore(XViewer.MENU_GROUP_PRE, new Separator());
   }

   public void createMenuActions() {
      MenuManager mm = treeViewer.getMenuManager();
      mm.createContextMenu(treeViewer.getControl());
      mm.addMenuListener(new IMenuListener() {
         @Override
         public void menuAboutToShow(IMenuManager manager) {
            updateMenuActions(manager);
         }
      });

   }

   @Override
   public void onPostConnect(final ConnectionEvent event) {
      OseeLog.log(Activator.class, Level.INFO, "Entered onConnectionEstablished()");

      if (event.getEnvironment() instanceof ITestEnvironmentMessageSystem) {
         Displays.pendInDisplayThread(new Runnable() {
            @Override
            public void run() {
               recordButton.setEnabled(true);
            }
         });
      }
   }

   @Override
   public void onPreDisconnect(ConnectionEvent event) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            recordButton.setEnabled(false);
            onDisconnected();
         }
      });
   }

   private void setNoLibraryStatus() {
      if(!treeViewer.getTree().isDisposed()){
         treeViewer.getTree().setToolTipText("");
      }
      if(!statusTxt.isDisposed()){
         statusTxt.setText("no library detected");
      }
   }

   @Override
   public void onConnectionLost(IServiceConnector connector) {
   }

   @Override
   public void oteMessageServiceAcquired(final IOteMessageService service) {
      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            messageService = service;
            recordButton.setEnabled(true);
            treeViewer.setInput(service);
         }
      });
   }

   @Override
   public void oteMessageServiceReleased() {
      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (!recordButton.isDisposed()) {
               recordButton.setEnabled(false);
            }
            if (!treeViewer.getControl().isDisposed()) {
               treeViewer.setInput(null);
            }
            messageService = null;
         }
      });
   }



   public void addMessageDefinitionProvider(MessageDefinitionProvider provider) {
      messageProviderVersion.add(provider);
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               librariesLoaded = true;
               updateStatusLabel();
               statusTxt.setToolTipText(messageProviderVersion.getVersion());
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, "exception while processing library", ex);
            }
         }
      });
   }

   public void removeMessageDefinitionProvider(MessageDefinitionProvider provider) {
      messageProviderVersion.remove(provider);
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if(messageProviderVersion.isAnyAvailable()){
               if(!statusTxt.isDisposed()){
                  librariesLoaded = true;
                  updateStatusLabel();
                  statusTxt.setToolTipText(messageProviderVersion.getVersion());
               }
            } else {
               setNoLibraryStatus();
            }
         }
      });
   }

   private void updateStatusLabel() {
      String text = "";
      if( librariesLoaded) {
         text = "libraries loaded";
      } else {
         text = Status.NO_TEST_MANAGER.asString();
      }

      if(writerIsPresent) {
         text += ", WRITERS ARE PRESENT";
         statusTxt.setBackground(SWTResourceManager.getColor(COLOR_GOLDENROD));
      } else {
         text += ", no writers present";
         statusTxt.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
      }

      statusTxt.setText(text);
      statusTxt.getParent().layout();
   }

   public WatchList getWatchList() {
      return watchList;
   }

   public void setWriterPresent(boolean writerIsPresent) {
      if( this.writerIsPresent != writerIsPresent ) {
         this.writerIsPresent = writerIsPresent;
         updateStatusLabel();
         updateWriterButton();
      }
   }

   private void updateWriterButton() {
      removeWritersBtn.setEnabled(writerIsPresent);
   }

}
