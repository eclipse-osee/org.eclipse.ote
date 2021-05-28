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

package org.eclipse.osee.ote.properties;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.plugin.core.CorePreferences;

/**
 * @author Roberto E. Escobar
 */
public class OteProperties extends OseeProperties {
   private static final OteProperties instance = new OteProperties();
   private static final String OSEE_BENCHMARK = "osee.ote.benchmark";
   private static final String OSEE_CMD_CONSOLE = "osee.ote.cmd.console";
   private static final String OSEE_OTE_SERVER_TITLE = "osee.ote.server.title";
   private static final String OSEE_OTE_BATCH = "osee.ote.batch";
   private static final String OSEE_OTE_LOG_FILE_PATH = "osee.ote.logfilepath";
   private static final String OSEE_PAUSE_ON_FAIL = "ote.pause.on.fail";
   private static final String OSEE_PRINT_FAIL_TO_CONSOLE = "ote.print.fail.to.console";
   private static final String OSEE_OTE_DEFAULT_IP = "ote.default.ip.address";

   private OteProperties() {
      super();
   }

   public static String getOseeOteServerTitle() {
      return System.getProperty(OSEE_OTE_SERVER_TITLE, "");
   }

   public static boolean isOseeOteInBatchModeEnabled() {
      return System.getProperty(OSEE_OTE_BATCH) != null;
   }

   public static String getOseeOteLogFilePath() {
      return System.getProperty(OSEE_OTE_LOG_FILE_PATH);
   }

   public static boolean isBenchmarkingEnabled() {
      return System.getProperty(OSEE_BENCHMARK) != null ? true : false;
   }

   public static boolean isOteCmdConsoleEnabled() {
      return System.getProperty(OSEE_CMD_CONSOLE) != null ? true : false;
   }

   public static boolean isPauseOnFailEnabled() {
      return System.getProperty(OSEE_PAUSE_ON_FAIL) != null ? true : false;
   }

   public static boolean isPrintFailToConsoleEnabled() {
      return System.getProperty(OSEE_PRINT_FAIL_TO_CONSOLE) != null ? true : false;
   }

   public static String getDefaultIpAddress() throws UnknownHostException {
      String prefAddress = CorePreferences.getDefaultInetAddress().getHostAddress();
      String finalAddress = System.getProperty(OSEE_OTE_DEFAULT_IP, prefAddress);
      return finalAddress;
   }

   public static InetAddress getDefaultInetAddress() throws UnknownHostException {
      return InetAddress.getByName(getDefaultIpAddress());
   }

   /**
    * A string representation of all the property setting specified by this class
    * 
    * @return settings for all properties specified by this class
    */
   public static String getAllSettings() {
      return instance.toString();
   }
}
