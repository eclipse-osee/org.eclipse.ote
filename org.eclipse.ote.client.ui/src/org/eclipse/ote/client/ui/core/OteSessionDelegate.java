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
package org.eclipse.ote.client.ui.core;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Level;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.plugin.util.IShellCloseEvent;
import org.eclipse.osee.framework.ui.plugin.util.ModelessDialog;
import org.eclipse.osee.framework.ui.plugin.util.OseeConsole;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.core.framework.prompt.IPassFailPromptResponse;
import org.eclipse.osee.ote.core.framework.prompt.IResumeResponse;
import org.eclipse.osee.ote.core.framework.prompt.IUserInputPromptResponse;
import org.eclipse.osee.ote.core.framework.prompt.IYesNoPromptResponse;
import org.eclipse.osee.ote.service.SessionDelegate;
import org.eclipse.osee.ote.ui.IOteConsoleService;
import org.eclipse.ote.client.ui.internal.OteClientUiPlugin;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.PlatformUI;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class OteSessionDelegate implements SessionDelegate {

   @Override
   public byte[] getFile(String path) throws Exception {
      File file = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString() + File.separator + path);
      if (file.exists()) {
         try {
            return Lib.fileToBytes(file);
         } catch (IOException ex) {
            OteClientUiPlugin.log(Level.SEVERE, "failed to get file contents", ex);
         }
      }
      return null;
   }

   @Override
   public long getFileDate(String path) throws Exception {
      File file = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString() + File.separator + path);
      if (file.exists()) {
         return file.lastModified();
      }
      return 0;
   }

   @Override
   public void handleInformationPrompt(String message) throws Exception {
      for (IOteConsoleService console : OteClientUiPlugin.getDefault().getConsole()) {
         console.write(message, OseeConsole.CONSOLE_PROMPT, false);
      }
   }

   @Override
   public void handlePassFail(final IPassFailPromptResponse prompt) throws Exception {
      final String message = prompt.getPromptMessage();

      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            final EntryDialog ed =
               new EntryDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Pass/Fail", null,
                  message, MessageDialog.QUESTION, new String[] {"PASS", "FAIL"}, 1);
            ed.setModeless();
            ed.open();
            ed.addShellCloseEventListeners(new IShellCloseEvent() {
               @Override
               public void onClose() {
                  handle(ed.getReturnCode(), ed.getEntry());
               }

            });

            ed.setSelectionListener(new SelectionAdapter() {

               @Override
               public void widgetSelected(SelectionEvent e) {
                  handle(ed.getReturnCode(), ed.getEntry());
               }
            });
         }

         private void handle(int selection, String newEntry) {
            String prefix = "";
            if (selection == 0) {
               prefix = "PASS";
            } else {
               prefix = "FAIL";
            }
            if (!newEntry.equals("")) {
               prefix = prefix + " => ";
            }
            String result = prefix + newEntry;
            try {
               prompt.respond(selection == 0, result);
            } catch (Exception ex) {
               OteClientUiPlugin.log(Level.SEVERE, "Error occurred while sending response", ex);
            }
            for (IOteConsoleService console : OteClientUiPlugin.getDefault().getConsole()) {
               console.write("PROMPT: \"" + message + "\"\n         RESULT: \"" + result + "\"",
                  OseeConsole.CONSOLE_PROMPT, false);
            }
         }
      });
   }

   @Override
   public void handlePause(final IResumeResponse prompt) throws Exception {
      final String message = prompt.getPromptMessage();
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {

            final ModelessDialog od =
               new ModelessDialog(Displays.getActiveShell(), "Script Pause", null, message, MessageDialog.INFORMATION,
                  new String[] {"CONTINUE"}, 0);
            od.setModeless();

            od.open();
            od.addShellCloseEventListeners(new IShellCloseEvent() {
               @Override
               public void onClose() {
                  handle();
               }

            });

            od.setSelectionListener(new SelectionAdapter() {

               @Override
               public void widgetSelected(SelectionEvent e) {
                  // int selection = od.getReturnCode();
                  handle();

               }
            });
         }

         private void handle() {
            try {
               prompt.resume();
            } catch (RemoteException ex) {
               OteClientUiPlugin.log(Level.SEVERE, "Error occurred while sending 'resume' response", ex);
            }
            for (IOteConsoleService console : OteClientUiPlugin.getDefault().getConsole()) {
               console.write("PROMPT: \"" + message + "\"\n         RESULT: \"CONTINUE\"", OseeConsole.CONSOLE_PROMPT,
                  false);
            }
         }
      });
   }

   @Override
   public void handleUserInput(final IUserInputPromptResponse prompt) throws Exception {
      final String message = prompt.getPromptMessage();

      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            final EntryDialog ed =
               new EntryDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "User Input", null,
                  message, MessageDialog.QUESTION, new String[] {"CONTINUE"}, 0);
            ed.setModeless();
            ed.open();
            ed.addShellCloseEventListeners(new IShellCloseEvent() {
               @Override
               public void onClose() {
                  handle(ed.getEntry());
               }
            });

            ed.setSelectionListener(new SelectionAdapter() {
               @Override
               public void widgetSelected(SelectionEvent e) {
                  handle(ed.getEntry());
               }
            });

         }

         private void handle(String entry) {
            try {
               prompt.respond(entry);
            } catch (RemoteException ex) {
               OteClientUiPlugin.log(Level.SEVERE, "Error occurred while sending prompt response", ex);
            }
            for (IOteConsoleService console : OteClientUiPlugin.getDefault().getConsole()) {
               console.write("PROMPT: \"" + message + "\"\n         RESULT: \"" + entry + "\"",
                  OseeConsole.CONSOLE_PROMPT, false);
            }
         }

      });

   }

   @Override
   public void handleYesNo(final IYesNoPromptResponse prompt) throws Exception {
      final String message = prompt.getPromptMessage();

      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            final EntryDialog ed =
               new EntryDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Yes/No", null,
                  message, MessageDialog.QUESTION, new String[] {"YES", "NO"}, 1);
            ed.setModeless();
            ed.open();
            ed.addShellCloseEventListeners(new IShellCloseEvent() {
               @Override
               public void onClose() {
                  handle(ed.getReturnCode(), ed.getEntry());
               }

            });

            ed.setSelectionListener(new SelectionAdapter() {

               @Override
               public void widgetSelected(SelectionEvent e) {
                  handle(ed.getReturnCode(), ed.getEntry());
               }
            });
         }

         private void handle(int selection, String newEntry) {
            String prefix = "";
            if (selection == 0) {
               prefix = "YES";
            } else {
               prefix = "NO";
            }
            if (!newEntry.equals("")) {
               prefix = prefix + " => ";
            }
            String result = prefix + newEntry;
            try {
               prompt.respond(selection == 0);
            } catch (Exception ex) {
               OteClientUiPlugin.log(Level.SEVERE, "Error occurred while sending response", ex);
            }
            for (IOteConsoleService console : OteClientUiPlugin.getDefault().getConsole()) {
               console.write("PROMPT: \"" + message + "\"\n         RESULT: \"" + result + "\"",
                  OseeConsole.CONSOLE_PROMPT, false);
            }
         }
      });

   }

   @Override
   public void cancelPrompts() throws Exception {
      // INTENTIONALLY EMPTY BLOCK
   }

}
