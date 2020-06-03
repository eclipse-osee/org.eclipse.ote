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

package org.eclipse.osee.ote.core.enums;

import java.io.Serializable;
import java.util.EnumSet;

public class SupportedScriptTypes implements Serializable {

   private static final long serialVersionUID = 8403281090133318485L;
   public EnumSet<ScriptTypeEnum> supportedClasses = EnumSet.noneOf(ScriptTypeEnum.class);

   public void add(ScriptTypeEnum scriptType) {
      supportedClasses.add(scriptType);
   }

   /*
    * public HashSet<ScriptTypeEnum> getSupportedClasses(){ return supportedClasses; }
    */

   public boolean isSupported(ScriptTypeEnum type) {
      return supportedClasses.contains(type);
   }

   public String getFormmatedString() {
      return "Supported Script Types : " + supportedClasses + "\n";
   }
}
