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

package org.eclipse.ote.test.manager.pages;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.osee.ote.ui.test.manager.pages.AdvancedPage;
import org.eclipse.ote.test.manager.editor.OteTestManagerEditor;
import org.eclipse.ote.test.manager.internal.OteTestManagerModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class OteAdvancedPage extends AdvancedPage {
   private OteTestManagerModel model;
   
   public OteAdvancedPage(Composite parent, int style, TestManagerEditor parentTestManager) {
      super(parent, style, parentTestManager);
      model = ((OteTestManagerEditor)parentTestManager).getTestManagerModel();
   }

   private Text distText;
   @Override
   public void createPage() {
      super.createPage();

      Group group = new Group((Composite) getContent(), SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.numColumns = 2;
      group.setLayout(layout);
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      group.setText("Distribution Statement");

      distText = new Text(group, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
      distText.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.FILL).hint(SWT.DEFAULT, 100).create());
      
      final Button saveButton = new Button(group, SWT.PUSH);
      saveButton.setText("Save");
      saveButton.setEnabled(false);
      saveButton.addSelectionListener(new SelectionListener() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            model.setDistribution(distText.getText());
            saveButton.setEnabled(false);
         }
         
         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            // INTENTIONALLY EMPTY BLOCK
         }
      });
      distText.addListener(SWT.CHANGED, new Listener() {
         @Override
         public void handleEvent(Event event) {
            saveButton.setEnabled(true);
         }
      });
      computeScrollSize();
   }
   
   @Override
   public void restoreData() {
      super.restoreData();
      distText.setText(model.getDistributionStatement());
   }

}
