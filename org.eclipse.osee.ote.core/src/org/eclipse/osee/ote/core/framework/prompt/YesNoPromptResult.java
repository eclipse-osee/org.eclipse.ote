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

public class YesNoPromptResult {
   private final boolean yes;

   public YesNoPromptResult(boolean yes) {
      this.yes = yes;
   }

   public boolean isYes() {
      return yes;
   }
}