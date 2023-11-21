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

package org.eclipse.ote.client.ui.core;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.OseeConsole;
import org.eclipse.osee.framework.ui.plugin.util.ViewPartUtil;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.core.framework.prompt.IPassFailPromptResponse;
import org.eclipse.osee.ote.core.framework.prompt.IResumeResponse;
import org.eclipse.osee.ote.core.framework.prompt.IUserInputPromptResponse;
import org.eclipse.osee.ote.core.framework.prompt.IYesNoPromptResponse;
import org.eclipse.osee.ote.service.SessionDelegate;
import org.eclipse.osee.ote.ui.IOteConsoleService;
import org.eclipse.ote.client.ui.OteClientUiPlugin;
import org.eclipse.ote.client.ui.PromptViewPreferencePage;
import org.eclipse.ote.client.ui.ViewUtil;
import org.eclipse.ote.client.ui.prompt.PromptView;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class OteSessionDelegateViewImpl implements SessionDelegate {

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
   public void cancelPrompts() throws Exception {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
        	 boolean promptDisplayed = false;
        	 try{
        		 promptDisplayed = OteClientUiPlugin.getDefault().getPreferenceStore().getBoolean(PromptViewPreferencePage.PROMPT_DISPLAYED);
        	 } catch (Throwable th){
        		 OseeLog.log(getClass(), Level.SEVERE, th);
        	 }
        	 if(!promptDisplayed){
        		 ViewUtil.closeViewAcrossPerspectives(PromptView.VIEW_ID);
        	 } else {
        		 IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        		 IPerspectiveDescriptor actPd = page.getPerspective();
        		 IViewPart viewPart = page.findView(PromptView.VIEW_ID);
        		 if(viewPart == null){
        			 IPerspectiveDescriptor[] pd = page.getOpenPerspectives();
        			 for (int i = 0; i < pd.length; i++) {
        				 try {
        					 page.setPerspective(pd[i]);
        				 } catch (Exception ex) {
        			      // INTENTIONALLY EMPTY BLOCK
        				 }
        				 viewPart = page.findView(PromptView.VIEW_ID);
        				 if(viewPart != null){
        					 break;
        				 }
        			 }
        		 }
        		 if(viewPart != null){
        			 if (viewPart instanceof PromptView) {
        				 ((PromptView) viewPart).promptComplete();
        			 }
        		 }
        		 page.setPerspective(actPd);
        	 }
         }
      });
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
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            IViewPart viewPart = ViewPartUtil.openOrShowView(PromptView.VIEW_ID);
            if (viewPart instanceof PromptView) {
               ((PromptView) viewPart).showPassFail(prompt);
            }
            try {
               for (IOteConsoleService console : OteClientUiPlugin.getDefault().getConsole()) {
                  console.write("PROMPT PASS FAIL: \n" + prompt.getPromptMessage() + "\n", OseeConsole.CONSOLE_PROMPT,
                     false);
               }
            } catch (Throwable th) {
               OseeLog.log(OteSessionDelegateViewImpl.class, Level.SEVERE, th);
            }
         }
      });
   }

   @Override
   public void handlePause(final IResumeResponse prompt) throws Exception {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {

            IViewPart viewPart = ViewPartUtil.openOrShowView(PromptView.VIEW_ID);
            if (viewPart instanceof PromptView) {
               ((PromptView) viewPart).showPause(prompt);
            }
            try {
               for (IOteConsoleService console : OteClientUiPlugin.getDefault().getConsole()) {
                  console.write("PROMPT PAUSE: \n" + prompt.getPromptMessage() + "\n", OseeConsole.CONSOLE_PROMPT,
                     false);
               }
            } catch (Throwable th) {
               OseeLog.log(OteSessionDelegateViewImpl.class, Level.SEVERE, th);
            }
         }
      });
   }

   @Override
   public void handleUserInput(final IUserInputPromptResponse prompt) throws Exception {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            IViewPart viewPart = ViewPartUtil.openOrShowView(PromptView.VIEW_ID);
            if (viewPart instanceof PromptView) {
               ((PromptView) viewPart).showUserInput(prompt);
            }
            try {
               for (IOteConsoleService console : OteClientUiPlugin.getDefault().getConsole()) {
                  console.write("PROMPT USER INPUT: \n" + prompt.getPromptMessage() + "\n", OseeConsole.CONSOLE_PROMPT,
                     false);
               }
            } catch (Throwable th) {
               OseeLog.log(OteSessionDelegateViewImpl.class, Level.SEVERE, th);
            }
         }
      });
   }

   @Override
   public void handleYesNo(final IYesNoPromptResponse prompt) throws Exception {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            IViewPart viewPart = ViewPartUtil.openOrShowView(PromptView.VIEW_ID);
            if (viewPart instanceof PromptView) {
               ((PromptView) viewPart).showYesNo(prompt);
            }
            try {
               for (IOteConsoleService console : OteClientUiPlugin.getDefault().getConsole()) {
                  console.write("PROMPT YES/NO: \n" + prompt.getPromptMessage() + "\n", OseeConsole.CONSOLE_PROMPT,
                     false);
               }
            } catch (Throwable th) {
               OseeLog.log(OteSessionDelegateViewImpl.class, Level.SEVERE, th);
            }
         }
      });
   }
}
