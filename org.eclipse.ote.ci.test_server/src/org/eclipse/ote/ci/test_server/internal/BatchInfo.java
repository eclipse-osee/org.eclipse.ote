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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andy Jury
 */
public class BatchInfo {

   private RunTestArguments args;

   public BatchInfo(RunTestArguments args) {
      this.args = args;
   }

   public List<String> getTests() {
      List<String> tests = new ArrayList<String>();
      String csv = args.getTestsToRunCSV();
      if (csv != null) {
         if (!(new File(csv)).exists()) {// it's a list of scripts as an arg
            splitAndAdd(csv, tests);
         } else {// it's a path to a file that has the scripts
            BufferedReader br = null;
            try {
               FileInputStream fis = new FileInputStream(csv);
               br = new BufferedReader(new InputStreamReader(fis));
               String line;
               while ((line = br.readLine()) != null) {
                  splitAndAdd(line, tests);
               }
            } catch (IOException ex) {
               ex.printStackTrace();
            } finally {
               if (br != null) {
                  try {
                     br.close();
                  } catch (IOException e) {
                     e.printStackTrace();
                  }
               }
            }
         }
      }
      return tests;
   }

   private void splitAndAdd(String line, List<String> tests) {
      String[] temp = line.split(",");
      for (String str : temp) {
         if (!str.startsWith("#")) {
            tests.add(str);
         }
      }
   }
}
