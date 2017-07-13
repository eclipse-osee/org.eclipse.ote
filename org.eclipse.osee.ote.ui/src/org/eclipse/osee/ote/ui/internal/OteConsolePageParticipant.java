/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.internal;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ote.ui.OteConsole;
import org.eclipse.osee.ote.ui.internal.wizard.OteConsolePrefsWizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.IPageSite;

public class OteConsolePageParticipant implements IConsolePageParticipant {

   private IPageBookViewPage page;
   private IActionBars bars;
   private Action optionsButton;
   private OteConsole console;

   @Override
   public <T> T getAdapter(Class<T> adapter) {
      return null;
   }

   @Override
   public void init(IPageBookViewPage page, IConsole console) {
      this.console = (OteConsole) console;
      this.page = page;
      IPageSite site = page.getSite();
      this.bars = site.getActionBars();
      
      createOptionsButton();
      
      bars.getMenuManager().add(optionsButton);
      
      bars.updateActionBars();
   }

   private void createOptionsButton() {
      this.optionsButton = new Action("Preferences") {
         /* (non-Javadoc)
          * @see org.eclipse.jface.action.Action#run()
          */
         @Override
         public void run() {
            WizardDialog wd = new WizardDialog(Display.getCurrent().getActiveShell(), new OteConsolePrefsWizard(console));
            wd.open();
         }
      };
   }

   @Override
   public void dispose() {
      this.page = null;
      this.bars = null;
      this.optionsButton = null;
   }

   @Override
   public void activated() {
      update();
   }

   private void update() {
      if(page == null) {
         return;
      }
      
      optionsButton.setEnabled(true);
      
      bars.updateActionBars();
   }

   @Override
   public void deactivated() {
      update();
   }

}
