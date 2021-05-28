/*********************************************************************
 * Copyright (c) 2010 Boeing
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
import java.io.Serializable;
import java.net.URI;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.connection.service.IServicePropertyChangeListener;
import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.master.rest.model.OTEServer;
import org.eclipse.osee.ote.properties.OteProperties;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;

/**
 * @author Andrew M. Finkbeiner
 */
public abstract class RestLookupConnector implements IServiceConnector {
   public static final String TYPE = "jmstojini";
   public static final String OTE_EMBEDDED_BROKER_PROP = "OTEEmbeddedBroker";

   private static final class ExportInfo {
      private final Exporter exporter;
      private final Object exportedObject;

      private ExportInfo(Exporter exporter, Object exportedObject) {
         this.exportedObject = exportedObject;
         this.exporter = exporter;
      }
   }

   private static final String PROPERTY_ID = "id";

   private final HashMap<Object, ExportInfo> exports = new HashMap<>();
   private final ExportClassLoader exportClassLoader;
   private final List<IServicePropertyChangeListener> propertyChangeListeners = new CopyOnWriteArrayList<>();
   private final String uniqueServerId;
   protected Object service;
   Object myLock = new Object();
   protected OTEServer server;
   private boolean connected = false;

   public RestLookupConnector(OTEServer server) {
      this.server = server;
      this.uniqueServerId = server.getUUID();
      this.exportClassLoader = ExportClassLoader.getInstance();
   }

   @Override
   public Object export(Object callback) throws ExportException {
      try {
         Exporter exporter = createExporter();
         Object exportedObject = exporter.export((Remote) callback);
         exports.put(callback, new ExportInfo(exporter, exportedObject));
         return exportedObject;
      } catch (UnknownHostException e) {
         throw new ExportException("failed to export", e);
      }
   }

   @Override
   public void unexport(Object callback) throws Exception {
      ExportInfo info = exports.remove(callback);
      if (info != null) {
         info.exporter.unexport(false);
      }
   }

   @Override
   public Object findExport(Object callback) {
      ExportInfo info = exports.get(callback);
      if (info != null) {
         return info.exportedObject;
      }
      return null;
   }

   @Override
   public void stop() throws Exception {
      for (ExportInfo info : exports.values()) {
         info.exporter.unexport(false);
      }
      exports.clear();
   }

   private Exporter createExporter() throws UnknownHostException {
      return new BasicJeriExporter(TcpServerEndpoint.getInstance(OteProperties.getDefaultIpAddress(), 0),
         new BasicILFactory(null, null, exportClassLoader), false, false);
   }

   @Override
   public String getConnectorType() {
      return TYPE;
   }

   @Override
   public Serializable getProperty(String property, Serializable defaultValue) {
      if ("name".equalsIgnoreCase(property)) {
         return server.getName();
      }
      if ("station".equalsIgnoreCase(property)) {
         return server.getStation();
      }
      if ("type".equalsIgnoreCase(property)) {
         return server.getType();
      }
      if ("version".equalsIgnoreCase(property)) {
         return server.getVersion();
      }
      if ("comment".equalsIgnoreCase(property)) {
         return server.getComment();
      }
      if ("date".equalsIgnoreCase(property)) {
         return server.getStartTime();
      }
      if ("owner".equalsIgnoreCase(property)) {
         return server.getOwner();
      }
      if (PROPERTY_ID.equalsIgnoreCase(property)) {
         return server.getUUID();
      }
      if ("appServerURI".equalsIgnoreCase(property)) {
         return server.getOteRestServer();
      }
      if ("user_list".equalsIgnoreCase(property)) {
         if (server.getConnectedUsers() == null) {
            return defaultValue;
         } else {
            return server.getConnectedUsers();
         }
      }
      return defaultValue;
   }

   @Override
   public abstract Object getService();

   @Override
   public boolean ping() {
      if (this.service == null) {
         return false;
      } else {
         try {
            EnhancedProperties props = ((IHostTestEnvironment) this.service).getProperties();
            if (props != null) {
               return uniqueServerId.equals(props.getProperty(PROPERTY_ID));
            } else {
               return false;
            }
         } catch (Throwable th) {
            return false;
         }
      }
   }

   public boolean ping(long timeout) {
      if (this.service == null) {
         return false;
      } else {
         try {
            if (this.service instanceof HostProxy) {
               EnhancedProperties prop = ((HostProxy) this.service).getProperties(timeout);
               if (prop != null) {
                  return uniqueServerId.equals(prop.getProperty(PROPERTY_ID));
               } else {
                  return false;
               }
            } else {
               return ping();
            }
         } catch (Throwable th) {
            return false;
         }
      }
   }

   @Override
   public void addPropertyChangeListener(IServicePropertyChangeListener listener) {
      propertyChangeListeners.add(listener);
   }

   @Override
   public void removePropertyChangeListener(IServicePropertyChangeListener listener) {
      propertyChangeListeners.remove(listener);
   }

   @Override
   public void setProperty(String key, Serializable value) {
      //      properties.setProperty(key, value);
      //      for (IServicePropertyChangeListener listener : propertyChangeListeners) {
      //         listener.propertyChanged(this, key, value);
      //      }
   }

   @Override
   public URI upload(File file) throws Exception {
      return null;
   }

   @Override
   public String getUniqueServerId() {
      return uniqueServerId;
   }

   @Override
   public EnhancedProperties getProperties() {
      try {
         IHostTestEnvironment hostEnv = (IHostTestEnvironment) service;
         return hostEnv.getProperties();
      } catch (RemoteException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      }
      return null;
   }

   @Override
   public void init(Object service) {
      // INTENTIONALLY EMPTY BLOCK
   }

   public void setUserList(String connectedUsers) {
      server.setConnectedUsers(connectedUsers);
      for (IServicePropertyChangeListener listener : propertyChangeListeners) {
         listener.propertyChanged(this, "user_list", connectedUsers);
      }
   }

   public void setFields(EnhancedProperties properties) {
      server.setName(properties.getProperty("name").toString());
      server.setStation(properties.getProperty("station").toString());
      server.setType(properties.getProperty("type").toString());
      server.setVersion(properties.getProperty("version").toString());
      server.setComment(properties.getProperty("comment").toString());
      server.setStartTime(properties.getProperty("date").toString());
      server.setOwner(properties.getProperty("owner").toString());
      server.setUUID(properties.getProperty(PROPERTY_ID).toString());
      server.setOteRestServer(properties.getProperty("appServerURI").toString());
      Serializable users = properties.getProperty("user_list");
      if (users != null) {
         server.setConnectedUsers(users.toString());
      } else {
         // INTENTIONALLY EMPTY BLOCK
      }
   }

   @Override
   public void setConnected(boolean connected) {
      this.connected = connected;
   }

   @Override
   public boolean isConnected() {
      return this.connected;
   }

}
