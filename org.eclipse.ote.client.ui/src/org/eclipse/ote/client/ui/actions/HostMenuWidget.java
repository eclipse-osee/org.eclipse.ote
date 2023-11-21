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

package org.eclipse.ote.client.ui.actions;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.service.IOteClientService;
import org.eclipse.osee.ote.service.ITestConnectionListener;
import org.eclipse.osee.ote.service.OteServiceProperties;
import org.eclipse.osee.ote.service.TestSessionException;
import org.eclipse.ote.client.ui.OteClientUiPlugin;
import org.eclipse.ote.client.ui.core.widgets.HostSelectionTable;
import org.eclipse.ote.client.ui.core.widgets.RestLookup;
import org.eclipse.ote.client.ui.internal.ServiceUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Andrew M. Finkbeiner
 */
public class HostMenuWidget implements ITestConnectionListener {

   private final Image connectedIcon;
   private Label connectStatusLabel;
   private final Image disconnectedIcon;

   private ToolItem hostConnectButton;
   private Menu hostMenu;

   private final IOteClientService oteService;

   public HostMenuWidget(IOteClientService oteService) {
      this.oteService = oteService;
      this.connectedIcon = OteClientUiPlugin.getImageDescriptor("OSEE-INF/images/connect.gif").createImage();
      this.disconnectedIcon = OteClientUiPlugin.getImageDescriptor("OSEE-INF/images/disconnect_co.png").createImage();
      this.connectStatusLabel = null;
   }

   public MenuItem createDefaultMenuItem() {
      MenuItem defaultItem = new MenuItem(hostMenu, SWT.NONE);
      defaultItem.setText(" NO HOSTS AVAILABLE - Waiting for Host Registration");
      return defaultItem;
   }

   public void createToolItem(final ToolBar toolBar) {
      Shell shell = toolBar.getShell();

      /* Host Services Menu */
      hostMenu = new Menu(shell, SWT.POP_UP);
      hostMenu.addMenuListener(new MenuAdapter() {
         @Override
         public void menuShown(MenuEvent e) {
            populateHostServices();
         }
      });

      createDefaultMenuItem();

      /* Connect Button */
      hostConnectButton = new ToolItem(toolBar, SWT.DROP_DOWN);
      hostConnectButton.setImage(disconnectedIcon);
      hostConnectButton.setText("Host");
      hostConnectButton.setToolTipText("Connects/Disconnects selected host");
      hostConnectButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            if (event.detail == SWT.ARROW || !oteService.isConnected()) {
               Rectangle rect = hostConnectButton.getBounds();
               Point pt = new Point(rect.x, rect.y + rect.height);
               pt = toolBar.toDisplay(pt);
               hostMenu.setLocation(pt.x, pt.y);
               hostMenu.setVisible(true);
            } else {
               try {
                  oteService.disconnect();
               } catch (TestSessionException e) {
                  OteClientUiPlugin.log(Level.SEVERE, "exception while disconnecting", e);
                  MessageDialog.openError(Displays.getActiveShell(), "Error",
                     "Problem occurred while disconnecting. see error log for details");
               }
            }
         }
      });
      toolBar.pack();
      oteService.addConnectionListener(this);
   }

   public void dispose() {
      oteService.removeConnectionListener(this);
      if (connectedIcon != null) {
         connectedIcon.dispose();
      }
      if (disconnectedIcon != null) {
         disconnectedIcon.dispose();
      }
   }

   @Override
   public void onConnectionLost(IServiceConnector connector) {
      handleDisconnectStatus();
   }

   @Override
   public void onPostConnect(ConnectionEvent event) {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            if (hostConnectButton != null && !hostConnectButton.isDisposed()) {
               hostConnectButton.setImage(connectedIcon);
            }
            if (connectStatusLabel != null && !connectStatusLabel.isDisposed()) {
               connectStatusLabel.setVisible(true);
            }
         }
      });

   }

   @Override
   public void onPreDisconnect(ConnectionEvent event) {
      handleDisconnectStatus();
   }

   private void handleDisconnectStatus() {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            if (hostConnectButton != null && !hostConnectButton.isDisposed()) {
               hostConnectButton.setImage(disconnectedIcon);
            }
            if (connectStatusLabel != null && !connectStatusLabel.isDisposed()) {
               connectStatusLabel.setVisible(false);
            }
         }

      });

   }

   public void setConnectStatusLabel(Label statusLabel) {
      this.connectStatusLabel = statusLabel;
      this.connectStatusLabel.setImage(connectedIcon);
   }

   private void populateHostServices() {
      // Get rid of existing menu items
      RestLookup restLookup = ServiceUtil.getService(RestLookup.class);
      if (restLookup != null) {
         restLookup.getLatest();
      }

      for (MenuItem item : hostMenu.getItems()) {
         item.dispose();
      }
      List<IServiceConnector> hostList = oteService.getAvailableTestHosts();
      ;
      if (hostList.isEmpty()) {
         createDefaultMenuItem();
      } else {
         IHostTestEnvironment currentHost = oteService.getConnectedHost();
         String currentHostId = "";
         if (currentHost != null) {
            try {
               currentHostId = (String) currentHost.getProperties().getProperty("id");
            } catch (RemoteException e) {
               OseeLog.log(getClass(), Level.SEVERE, "Unable to make RMI call on connected environment");
            }
         }
         // Sort Host List Before Populating the Menu
         Collections.sort(hostList, new Comparator<IServiceConnector>() {
            @Override
            public int compare(IServiceConnector service1, IServiceConnector service2) {
               OteServiceProperties props1 = new OteServiceProperties(service1);//oteService.getProperties(testHost)(service1);
               OteServiceProperties props2 = new OteServiceProperties(service2);//oteService.getProperties(service2);

               if (props1.getType().equals(props2.getType())) {
                  if (props1.getStation().equals(props2.getStation())) {
                     return props1.getDateStarted().compareTo(props2.getDateStarted());
                  }
                  return props1.getStation().compareTo(props2.getStation());
               }
               return props1.getType().compareTo(props2.getType());
            }
         });
         // Add menu items for current selection
         for (IServiceConnector host : hostList) {
            OteServiceProperties properties = new OteServiceProperties(host);
            MenuItem newItem = new MenuItem(hostMenu, SWT.RADIO);
            newItem.setText(String.format("%s : %s : %s : %s", properties.getStation(), properties.getType(),
               properties.getName(), properties.getUserList()));

            newItem.setData(host);
            if (currentHost != null && currentHostId.length() > 0) {
               if (currentHostId.equals(host.getProperty("id", "N/A"))) {
                  newItem.setSelection(true);
               }
            }
            newItem.addListener(SWT.Selection, new Listener() {
               @Override
               public void handleEvent(Event event) {
                  MenuItem itemSelected = (MenuItem) event.widget;
                  IServiceConnector connection = (IServiceConnector) itemSelected.getData();
                  HostSelectionTable.doConnection(connection);
               }
            });
         }
      }
   }
}
