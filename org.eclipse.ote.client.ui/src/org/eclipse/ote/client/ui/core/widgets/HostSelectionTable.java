/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.ote.client.ui.core.widgets;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.core.OSEEPerson1_4;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.properties.OteProperties;
import org.eclipse.osee.ote.service.IOteClientService;
import org.eclipse.osee.ote.service.TestSessionException;
import org.eclipse.ote.client.ui.OteClientServiceTracker;
import org.eclipse.ote.client.ui.core.OteSessionDelegateViewImpl;
import org.eclipse.ote.client.ui.core.TestHostItem;
import org.eclipse.ote.client.ui.core.widgets.xhost.HostTable;
import org.eclipse.ote.client.ui.core.widgets.xhost.HostTableLabelProvider;
import org.eclipse.ote.client.ui.core.widgets.xhost.HostTableXContentProvider;
import org.eclipse.ote.client.ui.internal.OteClientUiPlugin;
import org.eclipse.ote.client.ui.job.OteConnectionJob;
import org.eclipse.ote.client.ui.job.OteLoginJob;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.ServiceReference;

/**
 * @author Ken J. Aguilar
 */
public class HostSelectionTable {
   private static final String DEFAULT_ZIP_PATTERN =
      "\\pathToGetTo\\installs\\@OTE_%s\\archives\\ote.server.runtime.zip";
   private static final String DEFAULT_LINUX_PATTERN = "/pathToWhereServersAreLaunchedFrom/";
   protected static final String ZIP_FILE_PROPERTY = "ote.server.zip";
   private static final String LINUX_PROPERTY = "ote.server.linux.path";
   private static final int CONNECTION_TIMEOUT = 35000;
   private final HostTable hostTable;

   private final OteClientServiceTracker tracker = new OteClientServiceTracker() {

      @SuppressWarnings({"rawtypes", "unchecked"})
      @Override
      public Object addingService(ServiceReference reference) {
         IOteClientService service = (IOteClientService) super.addingService(reference);
         hostTable.setInput(service);
         return service;
      }

      @SuppressWarnings({"rawtypes", "unchecked"})
      @Override
      public void removedService(ServiceReference reference, Object service) {
         if (!hostTable.getTree().isDisposed()) {
            hostTable.setInput(null);
         }
         super.remove(reference);
      }

   };
   private ScheduledExecutorService executor;
   private ScheduledFuture<?> monitorRestLookup;
   private final Color goldenRod;
   private final Color normalBackground;
   private Font bigFont;
   private final Image copyImage;
   private final Image downloadImage;

   public void setFilter(String filter) {
      hostTable.setFilter(filter);
   }

   /**
    * @param parent
    * @param style
    */
   public HostSelectionTable(Composite parent, int style) {
      Display display = Display.getCurrent();
      this.goldenRod = new Color(display, 255, 193, 37);
      this.normalBackground = new Color(display, 202, 225, 255);
      this.copyImage = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_COPY);
      this.downloadImage = OteClientUiPlugin.getImageDescriptor("OSEE-INF/images/arrow_down_end.png").createImage();

      addClipBoard(parent);
      addLabels(parent);
      this.hostTable = createHostTable(parent);
      startLookupMonitor();

   }

   private void addClipBoard(Composite parent) {
      // INTENTIONALLY EMPTY BLOCK
   }

   /**
    * @param parent
    */
   private void addLabels(Composite parent) {
      final Clipboard cb = new Clipboard(parent.getDisplay());
      String clientSideVersion = ClientServerBundleVersionChecker.getClientVersion();
      final String shortenedClientVersion;
      Pattern pattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+).*");
      Matcher clientMatcher = pattern.matcher(clientSideVersion);
      if (clientMatcher.matches()) {
         shortenedClientVersion = clientMatcher.group(1);
      } else {
         shortenedClientVersion = clientSideVersion;
      }

      Composite comp = new Composite(parent, SWT.NONE);
      GridLayoutFactory.fillDefaults().numColumns(3).applyTo(comp);

      Composite labelComp = new Composite(comp, SWT.NONE);
      GridLayoutFactory.fillDefaults().numColumns(1).applyTo(labelComp);
      GridDataFactory.swtDefaults().span(1, 2).applyTo(labelComp);

      Group group = new Group(labelComp, SWT.NONE);
      group.setText("Client version");
      GridDataFactory.swtDefaults().span(1, 2).applyTo(group);
      GridLayoutFactory.fillDefaults().applyTo(group);

      Label label = new Label(group, SWT.CENTER);
      label.setText(shortenedClientVersion);
      GridDataFactory.fillDefaults().grab(true, false).applyTo(label);
      FontDescriptor boldDesc = FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD).setHeight(16);
      this.bigFont = boldDesc.createFont(label.getDisplay());
      label.setFont(bigFont);

      Button linuxBtn = new Button(comp, SWT.PUSH);
      linuxBtn.setImage(copyImage);
      linuxBtn.setToolTipText(
         "Paste this into a linux shell to access the correct version of the Test Server you wish to launch.");
      linuxBtn.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            String path = System.getProperty(LINUX_PROPERTY, DEFAULT_LINUX_PATTERN);
            if (path.contains("%s")) {
               path = String.format(path, shortenedClientVersion);
            }

            TextTransfer trans = TextTransfer.getInstance();
            cb.setContents(new Object[] {path}, new Transfer[] {trans});

            MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Path copied",
               "Your clipboard now contains the currnet solaris/linux server folder.\n" + "Feel free to paste directly into a unix shell.");
         }
      });

      label = new Label(comp, SWT.NONE);
      label.setText("Copy linux path to clipboard");
      label.setToolTipText(
         "Paste this into a linux shell to access the correct version of the Test Server you wish to launch.");

      Button winBtn = new Button(comp, SWT.PUSH);
      winBtn.setImage(downloadImage);
      winBtn.setToolTipText(
         "Unzip this file to your hardrive to access the correct version of the Test Server you wish to launch.");

      winBtn.addSelectionListener(new WindowsDownloadSelection(shortenedClientVersion));

      label = new Label(comp, SWT.NONE);
      label.setText("Download windows server");
      label.setToolTipText(
         "Unzip this file to your hardrive to access the correct version of the Test Server you wish to launch.");

      label = new Label(comp, SWT.NONE);
      label.setText(
         "Golden highlights below indicate servers that may not be compatible with this client.  Caution is advised if you choose to connect to a highlighted server.");
      GridDataFactory.swtDefaults().span(3, 1).applyTo(label);
   }

   /**
    * 
    */
   private void startLookupMonitor() {
      tracker.open();
      executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {

         @Override
         public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("OTE Rest Lookup Monitor");
            t.setDaemon(true);
            return t;
         }
      });

      monitorRestLookup =
         executor.scheduleWithFixedDelay(new MonitorRestLookup(hostTable.getTree()), 5, 5, TimeUnit.SECONDS);
   }

   /**
    * @param parent
    * @return
    */
   private HostTable createHostTable(Composite parent) {
      HostTable hostTable2 = new HostTable(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
      hostTable2.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
      hostTable2.setContentProvider(new HostTableXContentProvider());
      hostTable2.setLabelProvider(new HostTableLabelProvider(hostTable2));
      addDoubleClickListener(hostTable2);
      addSelectionBackgroundChanger(hostTable2);
      return hostTable2;
   }

   /**
    * 
    */
   private void addDoubleClickListener(HostTable hostTable2) {
      hostTable2.addDoubleClickListener(new IDoubleClickListener() {

         @Override
         public void doubleClick(DoubleClickEvent event) {
            IStructuredSelection selection = (IStructuredSelection) hostTable.getSelection();
            boolean connect = true;
            if (selection != null && !selection.isEmpty()) {
               final TestHostItem item = (TestHostItem) selection.getFirstElement();
               boolean clientAndServerVersionsMatch =
                  ClientServerBundleVersionChecker.clientAndServerVersionsMatch(item);
               if (!clientAndServerVersionsMatch) {
                  connect = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), "Connection Warning",
                     String.format(
                        "Server Version [%s] and Client Version[%s] do not match, are you sure you want to attempt to connect?",
                        ClientServerBundleVersionChecker.getServerVersion(item),
                        ClientServerBundleVersionChecker.getClientVersion()));
               }
               if (connect) {
                  new Thread(new Runnable() {
                     @Override
                     public void run() {
                        handleConnection(item);
                     }
                  }).start();
               }
            }
         }

      });
   }

   /**
    * This code ensures that the Writer only background color is preserved when the row is selected. Otherwise, the
    * selection would hide whether the message is a reader or a writer.
    * 
    * @param viewer
    */
   private void addSelectionBackgroundChanger(final HostTable viewer) {
      viewer.getTree().addListener(SWT.EraseItem, new Listener() {

         @Override
         public void handleEvent(Event event) {

            Tree table = (Tree) event.widget;
            TreeItem item = (TreeItem) event.item;
            Object data = item.getData();
            if (data instanceof TestHostItem) {
               TestHostItem watchedMessageNode = (TestHostItem) data;
               boolean clientAndServerVersionsMatch =
                  ClientServerBundleVersionChecker.clientAndServerVersionsMatch(watchedMessageNode);
               if (clientAndServerVersionsMatch) {
                  return;
               }

               event.detail &= ~SWT.HOT;

               if ((event.detail & SWT.SELECTED) == 0) return; /// item not selected

               int clientWidth = table.getClientArea().width;

               GC gc = event.gc;
               Color oldForeground = gc.getForeground();

               gc.setBackground(goldenRod);
               gc.setForeground(oldForeground);
               gc.fillRectangle(0, event.y, clientWidth, event.height);

               gc.setForeground(oldForeground);
               gc.setBackground(normalBackground);
               event.detail &= ~SWT.SELECTED;
            } else {
               return;
            }
         }

      });
   }

   public XViewer getTable() {
      return hostTable;
   }

   private void handleConnection(TestHostItem item) {

      if (!item.isConnected()) {
         doConnection(item.getConnector());
      } else {
         try {
            OteClientUiPlugin.getDefault().getOteClientService().disconnect();
         } catch (TestSessionException ex) {
            MessageDialog.openError(hostTable.getControl().getShell(), "Disconnect Error",
               "Exception while trying to disconnect. See Error Log for details");
            OteClientUiPlugin.log(Level.SEVERE, "Exception while trying to disconnect. See Error Log for details", ex);

         }
      }
   }

   public static IOteClientService waitForClientService(int timeout) {
      OteClientServiceTracker oteClientServiceTracker = new OteClientServiceTracker();
      oteClientServiceTracker.open();
      IOteClientService oteClientService;
      try {
         oteClientService = (IOteClientService) oteClientServiceTracker.waitForService(timeout);
         return oteClientService;
      } catch (InterruptedException e) {
         return null;
      } finally {
         oteClientServiceTracker.close();
      }
   }

   public static void doConnection(final IHostTestEnvironment testHost, OSEEPerson1_4 user) {
      doConnection("Initializing connection", testHost, user);
   }

   public static void doConnection(final IServiceConnector serviceConnector, OSEEPerson1_4 user) {
      doConnection("Initializing connection", serviceConnector, user);
   }

   public static void doConnection(final String jobName, final IHostTestEnvironment testHost, OSEEPerson1_4 user) {
      doConnection(jobName, null, testHost, user);
   }

   public static void doConnection(final String jobName, final IServiceConnector serviceConnector, OSEEPerson1_4 user) {
      doConnection(jobName, serviceConnector, null, user);
   }

   private static void doConnection(final String jobName, final IServiceConnector serviceConnector, final IHostTestEnvironment testHost, OSEEPerson1_4 user) {
      doConnection(waitForClientService(5000), jobName, serviceConnector, testHost, user);
   }

   private static void doConnection(final IOteClientService service, final String jobName, final IServiceConnector serviceConnector, final IHostTestEnvironment testHost, OSEEPerson1_4 user) {

      if (service == null) {
         throw new IllegalStateException("can't acquire OTE client service");
      }
      if (service.getUser() == null) {
         try {
            OteLoginJob job = new OteLoginJob(user);
            job.addJobChangeListener(new JobChangeAdapter() {

               @Override
               public void done(final IJobChangeEvent event) {
                  Displays.ensureInDisplayThread(new Runnable() {

                     @Override
                     public void run() {
                        if (!event.getResult().isOK()) {

                           String user = System.getProperty("user.name");
                           OseeLog.log(OteClientUiPlugin.class, Level.WARNING,
                              "Could not log you in using OSEE Authentication. You will be logged in as " + user);
                           try {
                              service.setUser(new OSEEPerson1_4(user, "", user), OteProperties.getDefaultInetAddress());
                              service.setSessionDelegate(new OteSessionDelegateViewImpl());
                           } catch (Exception e) {
                              OteClientUiPlugin.log(Level.SEVERE, "failed to login into OTE", e);
                              MessageDialog.openError(Displays.getActiveShell(), "Connection Aborted",
                                 "Cannot login to OTE system. See Error Log for details.");
                              return;
                           }

                        }
                        if (serviceConnector == null) {
                           OteConnectionJob job = new OteConnectionJob(jobName, testHost, CONNECTION_TIMEOUT);
                           job.schedule();
                        } else {
                           OteConnectionJob job = new OteConnectionJob(jobName, serviceConnector, CONNECTION_TIMEOUT);
                           job.schedule();
                        }
                     }
                  });
               }
            });
            job.schedule();
         } catch (Exception e) {
            OteClientUiPlugin.log(Level.SEVERE, "failed to connect", e);
            MessageDialog.openError(Displays.getActiveShell(), "Connection Aborted",
               "Cannot login to OTE system. See Error Log for details.");

         }
      } else {
         if (serviceConnector == null) {
            OteConnectionJob job = new OteConnectionJob(jobName, testHost, CONNECTION_TIMEOUT);
            job.schedule();
         } else {
            OteConnectionJob job = new OteConnectionJob(jobName, serviceConnector, CONNECTION_TIMEOUT);
            job.schedule();
         }
      }
   }

   public static void doConnection(final IHostTestEnvironment testHost) {
      doConnection(testHost, null);
   }

   public static void doConnection(final IServiceConnector connector) {
      doConnection(connector, null);
   }

   public static void doConnection(IOteClientService service, String message, IHostTestEnvironment testHost) {
      doConnection(service, message, null, testHost, null);
   }

   /**
    * The class field copyImage is intentionally not disposed in this method. If dispose is called on it, it is then
    * disposed in the shared registry that it is pulled from. Then the next time the HostSelectionTable gets
    * instantiated within the same OTE-IDE session, it will still be disposed and cause an exception.
    */
   public void dispose() {
      try {
         monitorRestLookup.cancel(false);
         executor.shutdown();
         tracker.close();
         this.goldenRod.dispose();
         this.normalBackground.dispose();
         this.downloadImage.dispose();
         this.bigFont.dispose();
         if (hostTable != null) {
            hostTable.dispose();
         }
      } catch (Throwable th) {
         OseeLog.log(getClass(), Level.WARNING, "Failed to stop dispose host table cleanly.", th);
      }
   }

   /**
    * @author Michael P. Masterson
    */
   private final class WindowsDownloadSelection extends SelectionAdapter {
      /**
       * 
       */
      private final String shortenedClientVersion;

      /**
       * @param shortenedClientVersion
       */
      private WindowsDownloadSelection(String shortenedClientVersion) {
         this.shortenedClientVersion = shortenedClientVersion;
      }

      @Override
      public void widgetSelected(SelectionEvent e) {
         String zipFilePath = System.getProperty(ZIP_FILE_PROPERTY, DEFAULT_ZIP_PATTERN);
         if (zipFilePath.contains("%s")) {
            zipFilePath = String.format(zipFilePath, shortenedClientVersion);
         }

         File zipFile = new File(zipFilePath);
         if (!zipFile.exists()) {
            String errorMsg = String.format(
               "OTE test server archive not found at %s.\n\n" + "Contact OTE personnel to help solve this issue.",
               zipFile.getAbsolutePath());
            MessageDialog.openError(Display.getCurrent().getActiveShell(), "Zip file not found", errorMsg);
            return;
         }

         DirectoryDialog dialog = new DirectoryDialog(Display.getCurrent().getActiveShell());
         dialog.setMessage("Select a destination folder:");
         String defaultFolder = System.getProperty("user.home") + "\\OTE_WIN_SERVERS\\" + shortenedClientVersion;
         File selectedDestFolder = new File(defaultFolder);
         if (!selectedDestFolder.exists()) {
            selectedDestFolder.mkdirs();
         }
         dialog.setFilterPath(defaultFolder);
         String dir = dialog.open();
         selectedDestFolder = new File(dir);

         if (selectedDestFolder.exists()) {
            OpenOrOverwriteDialog openDialog =
               new OpenOrOverwriteDialog(Display.getCurrent().getActiveShell(), selectedDestFolder.getAbsolutePath());
            boolean shouldOpen = openDialog.open();
            if (shouldOpen) {
               openFolder(selectedDestFolder);
               return;
            }
         }
         Job unzipJob = new UnzipJob(zipFile, selectedDestFolder);
         unzipJob.setUser(true);
         unzipJob.schedule();

         String message = String.format(
            "The OTE Windows test server was successfully downloaded to\n%s\n\n" + "Do you wish to open destination folder?",
            selectedDestFolder.getAbsolutePath());
         boolean showFolder = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
            "OTE Server successfully downloaded", message);
         if (showFolder) {
            openFolder(selectedDestFolder);
         }
      }

      /**
       * @param selectedDestFolder
       */
      private void openFolder(File selectedDestFolder) {
         try {
            Runtime.getRuntime().exec("explorer.exe " + selectedDestFolder.getAbsolutePath());
         } catch (IOException ex) {
            String errorMsg = String.format("Error trying to open destination folder at \n%s\n\nError was:\n%s",
               selectedDestFolder.getAbsolutePath(), ex.getMessage());
            MessageDialog.openError(Display.getCurrent().getActiveShell(), "Zip file not found", errorMsg);
            ex.printStackTrace();
         }
      }
   }
}
