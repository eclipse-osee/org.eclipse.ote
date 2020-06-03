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

package org.eclipse.osee.ote.core.environment.config;

import java.io.Serializable;

/**
 * @author Andrew M. Finkbeiner
 */
public class TestScriptConfig implements Serializable {

   private static final long serialVersionUID = 6791547338404192517L;
   private final String outFile;
   private final String fullScriptName;
   private final String[] classPathStrs;
   private final boolean isExe;
   private final ScriptVersionConfig scriptVersion;

   /**
    * TestScriptConfig Constructor.
    * 
    * @param classPathStrs The classPathStrs to set.
    * @param fullScriptName The fullScriptName to set.
    * @param outFile The outFile to set.
    */
   public TestScriptConfig(String[] classPathStrs, String fullScriptName, String outFile, ScriptVersionConfig scriptVersion) {
      this.fullScriptName = fullScriptName;
      this.outFile = outFile;
      this.isExe = false;
      this.scriptVersion = scriptVersion;
      this.classPathStrs = classPathStrs;
   }

   public String getOutFile() {
      return outFile;
   }

   public String getFullScriptName() {
      return fullScriptName;
   }

   public String[] getClassPathStrs() {
      return classPathStrs;
   }

   public boolean isExe() {
      return isExe;
   }

   public ScriptVersionConfig getScriptVersion() {
      return this.scriptVersion;
   }
}