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

package org.eclipse.osee.ote.ui.internal.wizard;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.ote.ui.OteConsole;

/**
 * @author Michael P. Masterson
 */
public class OteConsolePrefsWizard extends Wizard {
   
   OteConsolePrefsWizPage mainPage;
   private OteConsole console;

   /**
    * @param console
    */
   public OteConsolePrefsWizard(OteConsole console) {
      this.console = console;
      mainPage = new OteConsolePrefsWizPage();
   }

   @Override
   public boolean performFinish() {
      int bufferLimit = mainPage.getBufferLimit();
      boolean noLimitSelected = mainPage.getNoLimitSelection();
      
      console.setLimit(bufferLimit);
      console.setNoLimit(noLimitSelected);
      
      return true;
   }
   
   @Override
   public void addPages() {
      addPage(mainPage);
   }

}
