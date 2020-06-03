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
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.ote.core.framework.prompt.IResumeResponse;
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
public class Pause extends Composite {

   private IResumeResponse prompt;
   private final Text label;
   private PromptComplete promptComplete;
   private final Button btnContinue;
   private final Font courier;

   public Pause(Composite parent, int style) {
      super(parent, style);
      setLayout(new FormLayout());
      setBackground(Displays.getSystemColor(SWT.COLOR_RED));

      Composite composite_1 = new Composite(this, SWT.NONE);

      Composite composite = new Composite(this, SWT.NONE);

      composite_1.setLayout(new FormLayout());
      FormData fd_composite_1 = new FormData();
      fd_composite_1.top = new FormAttachment(0, 10);
      fd_composite_1.left = new FormAttachment(0, 10);
      fd_composite_1.right = new FormAttachment(100, -10);
      composite_1.setLayoutData(fd_composite_1);

      label = new Text(composite_1, SWT.MULTI | SWT.WRAP);
      label.setEditable(false);
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
      FormData fd_label = new FormData();
      fd_label.top = new FormAttachment(0, 5);
      fd_label.left = new FormAttachment(0, 5);
      fd_label.right = new FormAttachment(100, -5);
      fd_label.bottom = new FormAttachment(100, -5);
      label.setLayoutData(fd_label);

      composite.setLayout(new FormLayout());
      FormData fd_composite = new FormData();
      fd_composite.bottom = new FormAttachment(100, -10);
      fd_composite.right = new FormAttachment(100, -10);
      fd_composite.left = new FormAttachment(0, 10);
      fd_composite.top = new FormAttachment(composite_1, 0, SWT.BOTTOM);
      composite.setLayoutData(fd_composite);

      btnContinue = new Button(composite, SWT.NONE);
      FormData fd_btnContinue = new FormData();
      fd_btnContinue.left = new FormAttachment(0, 5);
      fd_btnContinue.top = new FormAttachment(0, 5);
      btnContinue.setLayoutData(fd_btnContinue);
      btnContinue.setText("CONTINUE");
      btnContinue.setFont(courier);

      btnContinue.addSelectionListener(new SelectionListener() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               resumePrompt();
            }
            finally {
               promptComplete.promptComplete();
            }
         }
         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            // INTENTIONALLY EMPTY BLOCK
         }
      });
   }

   public void setPromptData(IResumeResponse prompt) {
      this.prompt = prompt;
      try {
         label.setText(prompt.getPromptMessage());
         this.pack();
      } catch (RemoteException e) {
         OseeLog.log(Pause.class, Level.SEVERE, e);
      }
   }

   public void setPromptComplete(PromptComplete promptView) {
      this.promptComplete = promptView;
   }

   public void close() {
      resumePrompt();
   }

   @Override
   public boolean setFocus() {
      return btnContinue.setFocus();
   }

   private void resumePrompt() {
      // Put this on the end of the display thread queue, so window management
      // has completed before allowing the environment to continue
      final IResumeResponse response = prompt;
      Display.getDefault().asyncExec(new Runnable() {
         @Override
         public void run() {
            try {
               response.resume();
            } catch (RemoteException ex) {
               OseeLog.log(Pause.class, Level.SEVERE, ex);
            }
         }
      });
   }

}
