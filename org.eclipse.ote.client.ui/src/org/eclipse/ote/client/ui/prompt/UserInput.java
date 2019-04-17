/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.client.ui.prompt;

import java.rmi.RemoteException;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.ote.core.framework.prompt.IUserInputPromptResponse;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
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
public class UserInput extends Composite {
   private final Text text;
   private final Text label;
   private IUserInputPromptResponse prompt;
   private PromptComplete promptComplete;
   private final Font courier;

   public UserInput(Composite parent, int style) {
      super(parent, style);
      setLayout(new FormLayout());
      setBackground(Displays.getSystemColor(SWT.COLOR_RED));
      Composite composite_1 = new Composite(this, SWT.NONE);

      Composite composite = new Composite(this, SWT.NONE);
      composite.setLayout(new FormLayout());
      FormData fd_composite = new FormData();
      fd_composite.right = new FormAttachment(100, -10);
      fd_composite.top = new FormAttachment(0, 10);
      fd_composite.left = new FormAttachment(0, 10);
      composite.setLayoutData(fd_composite);

      label = new Text(composite, SWT.MULTI | SWT.WRAP);
      label.setEditable(false);
      text = new Text(composite, SWT.BORDER);
      text.addKeyListener(new KeyListener() {
         @Override
         public void keyReleased(KeyEvent e) {
            // INTENTIONALLY EMPTY BLOCK
         }

         @Override
         public void keyPressed(KeyEvent e) {
            if (e.character == SWT.CR || e.character == SWT.LF) {
               try {
                  prompt.respond(text.getText());
               } catch (RemoteException e1) {
                  OseeLog.log(UserInput.class, Level.SEVERE, e1);
               } finally {
                  promptComplete.promptComplete();
               }
            }
         }
      });

      FormData fd_label = new FormData();
      fd_label.top = new FormAttachment(0, 5);
      fd_label.left = new FormAttachment(0, 5);
      fd_label.right = new FormAttachment(100, -5);

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

      FormData fd_text = new FormData();
      fd_text.top = new FormAttachment(label, 5, SWT.BOTTOM);
      fd_text.left = new FormAttachment(0, 5);
      fd_text.right = new FormAttachment(100, -5);
      text.setLayoutData(fd_text);

      composite_1.setLayout(new FormLayout());

      FormData fd_composite_1 = new FormData();
      fd_composite_1.bottom = new FormAttachment(100, -10);
      fd_composite_1.right = new FormAttachment(100, -10);
      fd_composite_1.left = new FormAttachment(0, 10);
      fd_composite_1.top = new FormAttachment(composite, 0, SWT.BOTTOM);
      composite_1.setLayoutData(fd_composite_1);

      Button btnContinue = new Button(composite_1, SWT.NONE);
      FormData fd_btnContinue = new FormData();
      fd_btnContinue.top = new FormAttachment(0, 5);
      fd_btnContinue.left = new FormAttachment(0, 5);
      btnContinue.setLayoutData(fd_btnContinue);
      btnContinue.setText("CONTINUE");
      btnContinue.setFont(courier);

      btnContinue.addSelectionListener(new SelectionListener() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               resumePrompt(text.getText());
            } finally {
               promptComplete.promptComplete();
            }
         }

         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            // INTENTIONALLY EMPTY BLOCK
         }
      });
   }

   public void setPromptData(IUserInputPromptResponse prompt) {
      this.prompt = prompt;
      try {
         label.setText(prompt.getPromptMessage());
         text.setText("");
         pack();
      } catch (RemoteException e) {
         OseeLog.log(UserInput.class, Level.SEVERE, e);
      }
   }

   public void setPromptComplete(PromptComplete promptComplete) {
      this.promptComplete = promptComplete;
   }

   public void close() {
      resumePrompt("");
   }

   @Override
   public boolean setFocus() {
      return text.setFocus();
   }

   private void resumePrompt(final String input) {
      // Put this on the end of the display thread queue, so window management
      // has completed before allowing the environment to continue
      final IUserInputPromptResponse response = prompt;
      Display.getDefault().asyncExec(new Runnable() {
         @Override
         public void run() {
            try {
               response.respond(input);
            } catch (RemoteException ex) {
               OseeLog.log(Pause.class, Level.SEVERE, ex);
            }
         }
      });
   }

}
