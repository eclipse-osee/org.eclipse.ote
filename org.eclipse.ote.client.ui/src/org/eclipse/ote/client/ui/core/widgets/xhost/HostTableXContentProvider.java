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

package org.eclipse.ote.client.ui.core.widgets.xhost;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.connection.service.IServicePropertyChangeListener;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.service.IOteClientService;
import org.eclipse.osee.ote.service.ITestConnectionListener;
import org.eclipse.osee.ote.service.ITestEnvironmentAvailibilityListener;
import org.eclipse.osee.ote.service.OteServiceProperties;
import org.eclipse.ote.client.ui.core.TestHostItem;

/**
 * @author Ken J. Aguilar
 */
public class HostTableXContentProvider implements ITestConnectionListener, ITestEnvironmentAvailibilityListener, IServicePropertyChangeListener, ITreeContentProvider {

   private TreeViewer viewer;
   private IOteClientService clientService;
   private final ConcurrentHashMap<String, TestHostItem> items = new ConcurrentHashMap<>();

   @Override
   public void dispose() {
      if (clientService != null) {
         clientService.removeEnvironmentAvailibiltyListener(this);
         clientService.removeConnectionListener(this);
         clientService = null;
      }
      items.clear();
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      this.viewer = (TreeViewer) viewer;
      if (newInput != null) {
         if (clientService != null) {
            dispose();
         }
         clientService = (IOteClientService) newInput;
         clientService.addEnvironmentAvailibiltyListener(this);
         clientService.addConnectionListener(this);
      } else {
         dispose();
      }

   }

   @Override
   public Object[] getElements(Object inputElement) {
      return items.values().toArray();
   }

   @Override
   public void environmentAvailable(final IServiceConnector connector, OteServiceProperties properties) {
	   TestHostItem item = items.get(connector.getUniqueServerId()); 
	   if(item == null){
		  final TestHostItem newitem = new TestHostItem(connector);
    	  items.put(connector.getUniqueServerId(), newitem);
    	  connector.addPropertyChangeListener(this);
          Displays.ensureInDisplayThread(new Runnable() {
             @Override
             public void run() {
                viewer.add(clientService, newitem);
             }
          });
      } 
   }

   @Override
   public void environmentUnavailable(IServiceConnector connector, OteServiceProperties properties) {
      final TestHostItem item = items.remove(connector.getUniqueServerId());
      if (item != null) {
         connector.removePropertyChangeListener(this);
         // we removed an item from our list

         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               viewer.remove(item);
            }
         });
      }
   }

   @Override
   public void onConnectionLost(IServiceConnector connector) {
      final TestHostItem item = items.get(connector.getUniqueServerId());
      if (item != null) {
         item.setConnectedEnvironment(null);
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               viewer.update(item, null);
            }
         });
      }
   }

   @Override
   public void onPostConnect(ConnectionEvent event) {
      final TestHostItem item = items.get(event.getConnector().getUniqueServerId());
      if (item != null) {
         item.setConnectedEnvironment(event.getEnvironment());
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               try {
                  viewer.update(item, null);
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }

         });
      }
   }

   @Override
   public void onPreDisconnect(ConnectionEvent event) {
      final TestHostItem item = items.get(event.getConnector().getUniqueServerId());
      if (item != null) {
         item.setConnectedEnvironment(null);
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               viewer.update(item, null);
            }
         });
      }
   }

   @Override
   public void propertyChanged(IServiceConnector connector, String key, Serializable value) {
      final TestHostItem item = items.get(connector.getUniqueServerId());
      if (item != null) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               viewer.update(item, null);
            }
         });
      }
   }

   @Override
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof IOteClientService) {
         return items.values().toArray();
      } else {
         return null;
      }
   }

   @Override
   public Object getParent(Object element) {
      return null;
   }

   @Override
   public boolean hasChildren(Object element) {
      if (element instanceof IOteClientService) {
         return true;
      } else {
         return false;
      }
   }

}
