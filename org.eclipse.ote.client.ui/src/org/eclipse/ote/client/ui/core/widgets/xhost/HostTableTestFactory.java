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
package org.eclipse.ote.client.ui.core.widgets.xhost;

import java.io.File;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.customize.FileStoreCustomizations;
import org.eclipse.nebula.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomMenu;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class HostTableTestFactory extends XViewerFactory {
   private static String COLUMN_NAMESPACE = "xviewer.host.table";
   public static XViewerColumn CONNECTED = new XViewerColumn(COLUMN_NAMESPACE + ".connected", "", 42, XViewerAlign.Left,
      true, SortDataType.String, false, null);
   public static XViewerColumn HOST_COLUMN = new XViewerColumn(COLUMN_NAMESPACE + ".Host", "Host", 100,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn COMMENT_COLUMN = new XViewerColumn(COLUMN_NAMESPACE + ".Comment", "Comment", 175,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn USERS_COLUMN = new XViewerColumn(COLUMN_NAMESPACE + ".Users", "Users", 150,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn TYPE_COLUMN = new XViewerColumn(COLUMN_NAMESPACE + ".Type", "Type", 70,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn UPDATE_COLUMN = new XViewerColumn(COLUMN_NAMESPACE + ".LastUpdate", "Last Update", 160,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn VERSION_COLUMN = new XViewerColumn(COLUMN_NAMESPACE + ".Version", "Version", 120,
      XViewerAlign.Left, true, SortDataType.String, false, null);

   private final FileStoreCustomizations propertyStoreCustomizations;

   private static final String defaultCustomXml =
      "<XTreeProperties name=\"default\" namespace=\"xviewer.host.table\" guid=\"710r3sah5dtt01364nvkkv\"><xSorter><id>xviewer.host.table.Host</id><id>xviewer.host.table.LastUpdate</id></xSorter><xFilter></xFilter><xCol><id>xviewer.host.table.connected</id><name></name><wdth>42</wdth><algn>left</algn><srtFwd>true</srtFwd><show>true</show></xCol><xCol><id>xviewer.host.table.Host</id><name>Host</name><wdth>150</wdth><algn>left</algn><srtFwd>true</srtFwd><show>true</show></xCol><xCol><id>xviewer.host.table.Comment</id><name>Comment</name><wdth>240</wdth><algn>left</algn><srtFwd>true</srtFwd><show>true</show></xCol><xCol><id>xviewer.host.table.Users</id><name>Users</name><wdth>240</wdth><algn>left</algn><srtFwd>true</srtFwd><show>true</show></xCol><xCol><id>xviewer.host.table.Type</id><name>Type</name><wdth>70</wdth><algn>left</algn><srtFwd>true</srtFwd><show>true</show></xCol><xCol><id>xviewer.host.table.LastUpdate</id><name>Last Update</name><wdth>160</wdth><algn>left</algn><srtFwd>true</srtFwd><show>true</show></xCol><xCol><id>xviewer.host.table.Version</id><name>Version</name><wdth>120</wdth><algn>left</algn><srtFwd>true</srtFwd><show>true</show></xCol></XTreeProperties>";

   public HostTableTestFactory() {
      super(COLUMN_NAMESPACE);
      File folder;
      try {
         folder = OseeData.getFolder("OteHostTable").getLocation().toFile();
      } catch (OseeCoreException ex) {
         OseeLog.log(HostTableTestFactory.class, Level.SEVERE, ex);
         folder = new File(System.getProperty("java.io.tmpdir"));
      }
      propertyStoreCustomizations =
         new FileStoreCustomizations(folder, "OteHost", ".xml", "DefaultOteHost.xml", defaultCustomXml);
      registerColumns(CONNECTED, HOST_COLUMN, COMMENT_COLUMN, USERS_COLUMN, TYPE_COLUMN, UPDATE_COLUMN, VERSION_COLUMN);
   }

   @Override
   public XViewerCustomMenu getXViewerCustomMenu() {
      return new HostTableCustomize();
   }

   @Override
   public IXViewerCustomizations getXViewerCustomizations() {
      return propertyStoreCustomizations;
   }

   @Override
   public boolean isAdmin() {
      return true;
   }
}
