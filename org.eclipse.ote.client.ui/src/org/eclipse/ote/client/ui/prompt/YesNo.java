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

package org.eclipse.ote.client.ui.prompt;

import java.rmi.RemoteException;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.OseeConsole;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.ote.core.framework.prompt.IYesNoPromptResponse;
import org.eclipse.osee.ote.ui.IOteConsoleService;
import org.eclipse.ote.client.ui.internal.OteClientUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

/**
 * @author Andrew M. Finkbeiner
 */
public class YesNo extends Composite {
   private IYesNoPromptResponse prompt;
   private final Text label;
   private PromptComplete promptComplete;
   private final Button btnNo;
   private final Font courier;

   public YesNo(Composite parent, int style) {
      super(parent, style);
      setLayout(new FormLayout());
      setBackground(Displays.getSystemColor(SWT.COLOR_RED));
      Composite composite_1 = new Composite(this, SWT.NONE);
      FormData fd_composite_1 = new FormData();

      Composite composite = new Composite(this, SWT.NONE);
      composite.setLayout(new FormLayout());
      composite_1.setLayout(new FormLayout());

      fd_composite_1.top = new FormAttachment(0, 10);
      fd_composite_1.left = new FormAttachment(0, 10);
      fd_composite_1.right = new FormAttachment(100, -10);
      composite_1.setLayoutData(fd_composite_1);

      label = new Text(composite_1, SWT.MULTI | SWT.WRAP);
      label.setEditable(false);
      FormData fd_label = new FormData();
      fd_label.top = new FormAttachment(0, 5);
      fd_label.left = new FormAttachment(0, 5);
      fd_label.right = new FormAttachment(100, -5);
      fd_label.bottom = new FormAttachment(100, -5);
      label.setLayoutData(fd_label);
      label.setText("New Label");
      int tempFontHeight, maxFontHeight = 0;
      for (FontData fontData : label.getFont().getFontData()) {
         tempFontHeight = fontData.getHeight();
         if (tempFontHeight > maxFontHeight) {
            maxFontHeight = tempFontHeight;
         }
      }
      courier = FontManager.getFont("Courier", maxFontHeight, SWT.NORMAL);
      label.setFont(courier);

      FormData fd_composite = new FormData();
      fd_composite.top = new FormAttachment(composite_1, 0, SWT.BOTTOM);
      fd_composite.left = new FormAttachment(0, 10);
      fd_composite.right = new FormAttachment(100, -10);
      fd_composite.bottom = new FormAttachment(100, -10);
      composite.setLayoutData(fd_composite);

      Button btnYes = new Button(composite, SWT.NONE);

      btnNo = new Button(composite, SWT.NONE);

      FormData fd_btnPass = new FormData();
      fd_btnPass.top = new FormAttachment(0, 5);
      fd_btnPass.left = new FormAttachment(0, 5);
      btnYes.setLayoutData(fd_btnPass);
      btnYes.setText("YES");
      btnYes.setFont(courier);

      btnYes.addSelectionListener(new SelectionListener() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            handleYesNo(true);
         }

         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            // INTENTIONALLY EMPTY BLOCK
         }

      });

      FormData fd_btnFail = new FormData();
      fd_btnFail.left = new FormAttachment(btnYes, 5, SWT.RIGHT);
      fd_btnFail.top = new FormAttachment(0, 5);
      btnNo.setLayoutData(fd_btnFail);
      btnNo.setText("NO");

      btnNo.addSelectionListener(new SelectionListener() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            handleYesNo(false);
         }

         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            // INTENTIONALLY EMPTY BLOCK
         }
      });
   }

   private void handleYesNo(boolean yes) {
      try {
         resumePrompt(yes);
         if (yes) {
            for (IOteConsoleService console : OteClientUiPlugin.getDefault().getConsole()) {
               console.write("PROMPT YES/NO RESULT: YES\n", OseeConsole.CONSOLE_PROMPT, false);
            }
         } else {
            for (IOteConsoleService console : OteClientUiPlugin.getDefault().getConsole()) {
               console.write("PROMPT YES/NO RESULT: NO\n", OseeConsole.CONSOLE_PROMPT, false);
            }
         }
      } finally {
         promptComplete.promptComplete();
      }
   }

   public void setPromptData(IYesNoPromptResponse prompt) {
      this.prompt = prompt;
      try {
         label.setText(prompt.getPromptMessage());
         this.pack();
      } catch (RemoteException e) {
         OseeLog.log(YesNo.class, Level.SEVERE, e);
      }
   }

   public void setPromptComplete(PromptComplete promptView) {
      promptComplete = promptView;
   }

   public void close() {
      resumePrompt(false);
      for (IOteConsoleService console : OteClientUiPlugin.getDefault().getConsole()) {
         console.write("PROMPT YES/NO RESULT: NO\n", OseeConsole.CONSOLE_PROMPT, false);
      }
   }

   @Override
   public boolean setFocus() {
      return btnNo.setFocus();
   }

   private void resumePrompt(final boolean yes) {
      // Put this on the end of the display thread queue, so window management
      // has completed before allowing the environment to continue
      final IYesNoPromptResponse response = prompt;
      Display.getDefault().asyncExec(new Runnable() {
         @Override
         public void run() {
            try {
               response.respond(yes);
            } catch (RemoteException ex) {
               OseeLog.log(Pause.class, Level.SEVERE, ex);
            }
         }
      });
   }

}
