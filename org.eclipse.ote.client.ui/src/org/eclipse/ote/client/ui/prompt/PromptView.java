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

import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.framework.prompt.IPassFailPromptResponse;
import org.eclipse.osee.ote.core.framework.prompt.IResumeResponse;
import org.eclipse.osee.ote.core.framework.prompt.IUserInputPromptResponse;
import org.eclipse.osee.ote.core.framework.prompt.IYesNoPromptResponse;
import org.eclipse.ote.client.ui.OteClientUiPlugin;
import org.eclipse.ote.client.ui.PromptViewPreferencePage;
import org.eclipse.ote.client.ui.ViewUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Andrew M. Finkbeiner
 */
public class PromptView extends ViewPart implements PromptComplete {

   public static String VIEW_ID = "org.eclipse.ote.client.ui.prompt";
   private StackLayout stackLayout;
   private Blank blankPrompt;
   private PassFail passFailPrompt;
   private Pause pausePrompt;
   private UserInput userInputPrompt;
   private YesNo yesNoPrompt;
   private Composite composite;

   private ScrolledComposite passFailPromptScrolled;
   private ScrolledComposite pausePromptScrolled;
   private ScrolledComposite userInputPromptScrolled;
   private ScrolledComposite yesNoPromptScrolled;
   @Override
   public void createPartControl(Composite parent) {
      composite = new Composite(parent, SWT.NONE);

      blankPrompt = new Blank(composite, SWT.NONE);

      passFailPromptScrolled = new ScrolledComposite(composite, SWT.H_SCROLL
            | SWT.V_SCROLL);
      passFailPromptScrolled.setLayout(new FormLayout());
      passFailPrompt = new PassFail(passFailPromptScrolled, SWT.NONE);
      FormData fd_passFailPrompt = new FormData();
      fd_passFailPrompt.top = new FormAttachment(0);
      fd_passFailPrompt.left = new FormAttachment(0);
      fd_passFailPrompt.bottom = new FormAttachment(100);
      fd_passFailPrompt.right = new FormAttachment(100);
      passFailPrompt.setLayoutData(fd_passFailPrompt);
      passFailPromptScrolled.setContent(passFailPrompt);
      passFailPrompt.setPromptComplete(this);
      passFailPromptScrolled.setMinSize(passFailPrompt.computeSize(
            SWT.DEFAULT, SWT.DEFAULT));
      passFailPromptScrolled.setExpandHorizontal(true);
      passFailPromptScrolled.setExpandVertical(true);

      pausePromptScrolled = new ScrolledComposite(composite, SWT.H_SCROLL
            | SWT.V_SCROLL);
      pausePrompt = new Pause(pausePromptScrolled, SWT.NONE);
      FormData fd_pausePrompt = new FormData();
      fd_pausePrompt.top = new FormAttachment(0);
      fd_pausePrompt.left = new FormAttachment(0);
      fd_pausePrompt.bottom = new FormAttachment(100);
      fd_pausePrompt.right = new FormAttachment(100);
      pausePrompt.setLayoutData(fd_pausePrompt);
      pausePromptScrolled.setContent(pausePrompt);
      pausePrompt.setPromptComplete(this);
      pausePromptScrolled.setMinSize(pausePrompt.computeSize(SWT.DEFAULT,
            SWT.DEFAULT));
      pausePromptScrolled.setExpandHorizontal(true);
      pausePromptScrolled.setExpandVertical(true);

      userInputPromptScrolled = new ScrolledComposite(composite, SWT.H_SCROLL
            | SWT.V_SCROLL);
      userInputPromptScrolled.setLayout(new FormLayout());
      userInputPrompt = new UserInput(userInputPromptScrolled, SWT.NONE);
      FormData fd_userInputPrompt = new FormData();
      fd_userInputPrompt.top = new FormAttachment(0);
      fd_userInputPrompt.left = new FormAttachment(0);
      fd_userInputPrompt.bottom = new FormAttachment(100);
      fd_userInputPrompt.right = new FormAttachment(100);
      userInputPrompt.setLayoutData(fd_userInputPrompt);
      userInputPrompt.setPromptComplete(this);
      userInputPromptScrolled.setContent(userInputPrompt);
      userInputPromptScrolled.setMinSize(userInputPrompt.computeSize(
            SWT.DEFAULT, SWT.DEFAULT, true));
      userInputPromptScrolled.setExpandHorizontal(true);
      userInputPromptScrolled.setExpandVertical(true);

      yesNoPromptScrolled = new ScrolledComposite(composite, SWT.H_SCROLL
            | SWT.V_SCROLL);
      yesNoPromptScrolled.setLayout(new FormLayout());
      yesNoPrompt = new YesNo(yesNoPromptScrolled, SWT.NONE);
      FormData fd_yesNoPrompt = new FormData();
      fd_yesNoPrompt.top = new FormAttachment(0);
      fd_yesNoPrompt.left = new FormAttachment(0);
      fd_yesNoPrompt.bottom = new FormAttachment(100);
      fd_yesNoPrompt.right = new FormAttachment(100);
      yesNoPrompt.setLayoutData(fd_yesNoPrompt);
      yesNoPrompt.setPromptComplete(this);
      yesNoPromptScrolled.setContent(yesNoPrompt);
      yesNoPromptScrolled.setMinSize(userInputPrompt.computeSize(SWT.DEFAULT,
            SWT.DEFAULT, true));
      yesNoPromptScrolled.setExpandHorizontal(true);
      yesNoPromptScrolled.setExpandVertical(true);

      stackLayout = new StackLayout();
      composite.setLayout(stackLayout);
      stackLayout.topControl = blankPrompt;
      composite.layout();

   }
   public void showUserInput(IUserInputPromptResponse prompt) {
      setPartName("OTE Prompt Input");
      userInputPrompt.setPromptData(prompt);
      stackLayout.topControl = userInputPrompt.getParent();
      setSize(userInputPromptScrolled, userInputPrompt);
      userInputPromptScrolled.setContent(userInputPrompt);
      composite.layout(true);
      userInputPrompt.setFocus();
      forceFocus();
      userInputPrompt.redraw();
      userInputPrompt.pack();
      userInputPrompt.layout(true);
      forceFocus();
   }

   public void showPause(IResumeResponse prompt) {
      setPartName("OTE Prompt Pause");
      pausePrompt.setPromptData(prompt);
      stackLayout.topControl = pausePrompt.getParent();
      setSize(pausePromptScrolled, pausePrompt);
      pausePromptScrolled.setContent(pausePrompt);
      composite.layout(true);
      pausePrompt.setFocus();
      forceFocus();
      pausePrompt.redraw();
      pausePrompt.pack();
      pausePrompt.layout(true);
      forceFocus();
   }

   private void forceFocus(){
      int count = 0;
      while (Display.getCurrent().readAndDispatch() && count < 10000) {
         count++;
      }
      setFocus();
   }
   
   public void showPassFail(IPassFailPromptResponse prompt) {
      setPartName("OTE Prompt Pass/Fail");
      passFailPrompt.setPromptData(prompt);
      stackLayout.topControl = passFailPromptScrolled;
      setSize(passFailPromptScrolled, passFailPrompt);
      passFailPromptScrolled.setContent(passFailPrompt);
      composite.layout(true);
      passFailPrompt.setFocus();
      forceFocus();
      passFailPrompt.redraw();
      passFailPrompt.pack();
      passFailPrompt.layout(true);
      forceFocus();
   }

   public void showYesNo(IYesNoPromptResponse prompt) {
      setPartName("OTE Prompt Yes/No");
      yesNoPrompt.setPromptData(prompt);
      stackLayout.topControl = yesNoPromptScrolled;
      setSize(yesNoPromptScrolled, yesNoPrompt);
      yesNoPromptScrolled.setContent(yesNoPrompt);
      composite.layout(true);
      yesNoPrompt.setFocus();
      forceFocus();
      yesNoPrompt.redraw();
      yesNoPrompt.pack();
      yesNoPrompt.layout(true);
      forceFocus();
   }

   void setSize(ScrolledComposite scrolledComposite, Composite content){
      scrolledComposite.setMinSize(content.computeSize(scrolledComposite.getSize().x, SWT.DEFAULT, true));
      scrolledComposite.setExpandHorizontal(true);
      scrolledComposite.setExpandVertical(true);
   }

   @Override
   public void promptComplete() {
      stackLayout.topControl = blankPrompt;
      setPartName("OTE Prompt");
      composite.layout();

      boolean promptDisplayed = false;
      try{
         promptDisplayed = OteClientUiPlugin.getDefault().getPreferenceStore().getBoolean(PromptViewPreferencePage.PROMPT_DISPLAYED);
      } catch (Throwable th){
         OseeLog.log(getClass(), Level.SEVERE, th);
      }
      // Do not close the prompt view if it is the only item in the tab container
      // prompt will be closed automatically if it comes in too soon after this one.
      boolean onlyTab = false;
      try {
         Composite comp = composite;
         while(comp != null){
            comp = comp.getParent();
            if(comp instanceof CTabFolder){
               break;
            }
         }
         if(comp != null){
            onlyTab = ((CTabFolder)comp).getItemCount() == 1; 
         }
      } catch (Throwable e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      }
      if(!promptDisplayed && !onlyTab){
         ViewUtil.closeViewAcrossPerspectives(VIEW_ID);
      }
   }

   @Override
   public void dispose() {
      if (stackLayout.topControl == passFailPromptScrolled) {
         passFailPrompt.close();
      } else if (stackLayout.topControl == pausePromptScrolled) {
         pausePrompt.close();
      } else if (stackLayout.topControl == userInputPromptScrolled) {
         userInputPrompt.close();
      } else if (stackLayout.topControl == yesNoPromptScrolled) {
         yesNoPrompt.close();
      }
      super.dispose();
   }

   @Override
   public void setFocus() {
      if (composite != null) {
         composite.setFocus();
      }
   }




}
