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

package org.eclipse.osee.ote.core.framework.prompt;

public class PassFailPromptResult {
   private final boolean pass;
   private final String text;

   public PassFailPromptResult(boolean pass, String text) {
      this.pass = pass;
      this.text = text;
   }

   /**
    * @return the pass
    */
   public boolean isPass() {
      return pass;
   }

   /**
    * @return the text
    */
   public String getText() {
      return text;
   }

}