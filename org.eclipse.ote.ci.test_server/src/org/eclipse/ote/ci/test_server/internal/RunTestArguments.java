/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.ote.ci.test_server.internal;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.eclipse.osee.framework.jdk.core.util.network.PortUtil;
import org.eclipse.osee.ote.properties.OtePropertiesCore;

/**
 * @author Andy Jury
 */
public class RunTestArguments {

   private static final String NEWLINE = "\n";
   private static final String TAB = "\t";
   private static final String OPEN =
      "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<";
   private static final String CLOSE =
      ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>";
   private boolean isDone = false;
   private String serverName;
   private String serverHost;
   private String headlessDirs;
   private String headlessOutfileDir;
   private String headlessTestRunList;
   private String propertiesFile = "";
   private File[] dirClasses;
   private int port;
   private Double hours;
   private boolean abortOnFirstFail = false, safetyOnly = false;

   private final String[] appArgs;

   public RunTestArguments(String[] appArgs) {
      this.appArgs = appArgs;
   }

   private synchronized void getArgs() {
      if (!isDone) {
         isDone = true;
         System.out.println("Test Args: " + Arrays.toString(appArgs));

         int i = 0;
         while (i < appArgs.length) {
            if (appArgs[i].equals("-h")) {
               printUsage();
            } else if (appArgs[i].equals("-o")) {
               i++;
               if (i < appArgs.length) {
                  headlessOutfileDir = appArgs[i];
               } else {
                  System.out.println("ERROR: -o <outfile directory>");
                  printUsage();
               }
            } else if (appArgs[i].equals("-t")) {
               i++;
               if (i < appArgs.length) {
                  headlessTestRunList = appArgs[i];
               } else {
                  System.out.println("ERROR: -t <tests>");
                  printUsage();
               }
            } else if (appArgs[i].equals("-d")) {
               i++;
               if (i < appArgs.length) {
                  headlessDirs = appArgs[i];
                  if (this.headlessDirs != null) {
                     String[] someDirs = this.headlessDirs.split(",");
                     this.dirClasses = new File[someDirs.length];
                     for (int j = 0; j < someDirs.length; j++) {
                        dirClasses[j] = new File(someDirs[j]);
                     }
                  }
               } else {
                  System.out.println("ERROR: -d <workspace>");
                  printUsage();
               }
            } else if (appArgs[i].equals("-l")) {
               i++;
               if (i < appArgs.length) {
                  hours = Double.parseDouble(appArgs[i]);
               } else {
                  System.out.println("ERROR: -l <hours>");
                  printUsage();
               }
            } else if (appArgs[i].equals("-abortOnFirstFail")) {
               i++;
               if (i < appArgs.length) {
                  abortOnFirstFail = Boolean.parseBoolean(appArgs[i]);
               } else {
                  System.out.println("ERROR: -abortOnFirstFail <true|false>");
                  printUsage();
               }
            } else if (appArgs[i].equals("-safetyOnly")) {
               i++;
               if (i < appArgs.length) {
                  safetyOnly = Boolean.parseBoolean(appArgs[i]);
               } else {
                  System.out.println("ERROR: -safetyOnly <true|false>");
                  printUsage();
               }
            } else if (appArgs[i].equals("-propertiesFile")) {
               i++;
               if (i < appArgs.length) {
                  propertiesFile = appArgs[i];
               } else {
                  System.out.println("ERROR: -propertiesFile <properties file>");
                  printUsage();
               }
            }

            i++;
         }
         try {
            this.port = PortUtil.getInstance().getValidPort();
         } catch (IOException e) {
            this.port = 8888;
            e.printStackTrace();
         }
         if (this.serverHost == null) {
            try {
               serverHost = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
               e.printStackTrace();
            }
         }
         if (this.headlessOutfileDir == null) {
            headlessOutfileDir = OtePropertiesCore.userHome.getValue() + System.getProperty(
               "file.separator") + "OTESERVER" + System.getProperty("file.separator") + "outfiles";
         }
      }
   }

   public void printUsage() {
      StringBuilder sb = new StringBuilder();
      sb.append(NEWLINE).append(OPEN);
      sb.append(NEWLINE).append("TOOL USAGE").append(NEWLINE).append("Run a command line ote client.").append(NEWLINE);
      sb.append("examples:").append(NEWLINE);
      sb.append(TAB).append("run a batch:").append(NEWLINE);
      sb.append(TAB).append(TAB).append("run.sh -t `pwd`/test.csv -o `pwd`").append(NEWLINE);
      sb.append("OPTIONS:").append(NEWLINE);
      sb.append(TAB).append("-h  Show this message").append(NEWLINE);
      sb.append(TAB).append("-o  outfile directory").append(NEWLINE);
      sb.append(TAB).append(
         "-t  [REQUIRED] tests to run.  Can either be a csv file or a comma seperated list of tests.").append(NEWLINE);
      sb.append(TAB).append("-l  length to run in hours (accepts doubles).").append(NEWLINE);
      sb.append(TAB).append("-safetyOnly <true|false> When true only runs scripts with @Safety annotation").append(
         NEWLINE);
      sb.append(TAB).append("-abortOnFirstFail <true|false> When true aborts the test after first failure").append(
         NEWLINE);
      sb.append(NEWLINE).append(CLOSE);
      System.out.println(sb.toString());
   }

   public boolean isArgsValid() {
      getArgs();
      return true;
   }

   public String getOteServerName() {
      getArgs();
      return this.serverName;
   }

   public String getOteServerHostName() {
      getArgs();
      return this.serverHost;
   }

   public File[] getDirectoriesToScan() {
      getArgs();
      return dirClasses;
   }

   public int getLocalPort() {
      getArgs();
      return this.port;
   }

   public String getUserName() {
      return System.getProperty("user.name");
   }

   public String getUserEmail() {
      return "anon@email.com";
   }

   public String getUserId() {
      return System.getProperty("user.name");
   }

   public File getOutfileDirectory() {
      getArgs();
      File outfileDir = new File(this.headlessOutfileDir);
      outfileDir.mkdirs();
      return outfileDir;
   }

   public String getTestsToRunCSV() {
      getArgs();
      return this.headlessTestRunList;
   }

   public Double getHoursToRun() {
      getArgs();
      return hours;
   }

   public boolean isAbortOnFirstFail() {
      return abortOnFirstFail;
   }

   public boolean isSafetyOnly() {
      return safetyOnly;
   }

   public String getPropertiesFile() {
      return propertiesFile;
   }

}
