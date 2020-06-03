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

package org.eclipse.ote.test.manager;

import org.eclipse.osee.ote.ui.test.manager.ITestManagerFactory;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.osee.ote.ui.test.manager.pages.AdvancedPage;
import org.eclipse.osee.ote.ui.test.manager.pages.ScriptPage;
import org.eclipse.ote.test.manager.pages.OteAdvancedPage;
import org.eclipse.ote.test.manager.pages.OteScriptPage;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class OteTestManagerFactory implements ITestManagerFactory {

   public static final String EDITOR_ID = "org.eclipse.ote.test.manager.editor.OteTestManagerEditor";
   public static final String LAST_OPENED_KEY = "LAST_OPENED_OTE_TM";
   public static final String TEST_MANAGER_EXTENSION = "ote";
   public static final String TEST_MANAGER_FILENAME = "TestManager";

   private static OteTestManagerFactory instance = null;

   public static OteTestManagerFactory getInstance() {
      if (instance == null) {
         instance = new OteTestManagerFactory();
      }
      return instance;
   }

   protected OteTestManagerFactory() {
   }

   @Override
   public AdvancedPage getAdvancedPageNewInstance(Composite parent, int style, TestManagerEditor parentTestManager) {
      return new OteAdvancedPage(parent, style, parentTestManager);
   }

   @Override
   public String getEditorId() {
      return EDITOR_ID;
   }

   @Override
   public String getEditorLastOpenedKey() {
      return LAST_OPENED_KEY;
   }

   @Override
   public ScriptPage getScriptPageNewInstance(Composite parent, int style, TestManagerEditor parentTestManager) {
      return new OteScriptPage(parent, style, parentTestManager);
   }

   @Override
   public String getTestManagerExtension() {
      return TEST_MANAGER_EXTENSION;
   }

   @Override
   public String getTestManagerFileName() {
      return TEST_MANAGER_FILENAME;
   }
}
