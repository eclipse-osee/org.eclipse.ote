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
package org.eclipse.osee.ote.ui.test.manager.core;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.ui.test.manager.ITestManagerFactory;
import org.eclipse.osee.ote.ui.test.manager.pages.AdvancedPage;
import org.eclipse.osee.ote.ui.test.manager.pages.HostPage;
import org.eclipse.osee.ote.ui.test.manager.pages.ScriptPage;
import org.eclipse.osee.ote.ui.test.manager.pages.TestManagerPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Roberto E. Escobar
 */
public class PageManager {

   protected AdvancedPage advancedPage;
   protected HostPage hostPage;
   protected ScriptPage scriptPage;
   private final List<TestManagerPage> pages;

   private final TestManagerEditor testManager;
   private final ITestManagerFactory factory;

   public PageManager(ITestManagerFactory factory, TestManagerEditor testManager) {
      this.factory = factory;
      this.testManager = testManager;
      this.pages = new ArrayList<>();
   }

   protected void createPages(Composite parent) {
      hostPage = new HostPage(parent, SWT.NONE, testManager);
      registerPage(hostPage, false);

      scriptPage = this.factory.getScriptPageNewInstance(parent, SWT.NONE, testManager);
      scriptPage.createPage();
      registerPage(scriptPage, true);

      advancedPage = this.factory.getAdvancedPageNewInstance(parent, SWT.NONE, testManager);
      advancedPage.createPage();
      registerPage(advancedPage, false);

   }

   private void registerPage(TestManagerPage page, boolean isScriptPage) {
      pages.add(page);
      testManager.registerPage(testManager.addPage(page), page.getPageName(), isScriptPage);
   }

   /**
    * Dispose pages
    */
   public void dispose() {
      for (TestManagerPage page : pages) {
         page.dispose();
      }
      pages.clear();
   }

   /**
    * Save page settings to storage
    */
   public void save() {
      for (TestManagerPage page : pages) {
         page.saveData();
      }
   }

   /**
    * Restore page from stored settings
    */
   public void restore() {
      for (TestManagerPage page : pages) {
         page.restoreData();
      }
   }

   /**
    * @return the advancedPage
    */
   public AdvancedPage getAdvancedPage() {
      return advancedPage;
   }

   /**
    * @return the hostPage
    */
   public HostPage getHostPage() {
      return hostPage;
   }

   /**
    * @return the scriptPage
    */
   public ScriptPage getScriptPage() {
      return scriptPage;
   }

   /**
    * Checks that all page setting are set correctly for a script run
    * 
    * @return <b>True</b> if page setting are valid for script run
    */
   public boolean areSettingsValidForRun() {
      boolean result = true;
      for (TestManagerPage page : pages) {
         result &= page.areSettingsValidForRun();
      }
      return result;
   }

   /**
    * Gets page error message
    * 
    * @return The Page error message
    */
   public String getErrorMessage() {
      StringBuilder builder = new StringBuilder();
      for (TestManagerPage page : pages) {
         String pageMessage = page.getErrorMessage();
         if (Strings.isValid(pageMessage)) {
            if (builder.length() > 0) {
               builder.append("\n");
            }
            builder.append(page.getPageName());
            builder.append(" Page:\n");
            builder.append(pageMessage);
            builder.append("\n");
         }
      }
      return builder.toString();
   }

   public boolean onPostConnect(ConnectionEvent event) {
      boolean problemEncountered = false;
      for (TestManagerPage page : pages) {
         problemEncountered |= page.onConnection(event);
      }
      return problemEncountered;
   }

   public boolean onPreDisconnect(ConnectionEvent event) {
      boolean problemEncountered = false;
      for (TestManagerPage page : pages) {
         problemEncountered |= page.onDisconnect(event);
      }
      return problemEncountered;
   }

   public boolean onConnectionLost() {
      boolean problemEncountered = false;
      for (TestManagerPage page : pages) {
         problemEncountered |= page.onConnectionLost();
      }
      return problemEncountered;
   }

}
