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

package org.eclipse.osee.ote.ui.test.manager;

import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.osee.ote.ui.test.manager.pages.AdvancedPage;
import org.eclipse.osee.ote.ui.test.manager.pages.ScriptPage;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Add a summary of extension points and other classes that will need to be added to create a new test manager.
 * 
 * @author Roberto E. Escobar
 */
public interface ITestManagerFactory {

   public AdvancedPage getAdvancedPageNewInstance(Composite parent, int style, TestManagerEditor parentTestManager);

   public String getEditorId();

   public String getEditorLastOpenedKey();

   public ScriptPage getScriptPageNewInstance(Composite parent, int style, TestManagerEditor parentTestManager);

   public String getTestManagerExtension();

   public String getTestManagerFileName();

}
