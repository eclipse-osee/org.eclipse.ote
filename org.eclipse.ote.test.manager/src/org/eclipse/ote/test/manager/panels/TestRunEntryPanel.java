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
package org.eclipse.ote.test.manager.panels;

import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Michael P. Masterson
 */
public class TestRunEntryPanel extends Composite {

   private Text notesTextEntry;
   private UserNamePanel runnerComp;
   private UserNamePanel witnessNamesComp;
   private Text buildIdEntry;

   public TestRunEntryPanel(Composite parent, int style) {
      super(parent, style);
      this.setLayout(new GridLayout(2, false));
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      createControl(this);
   }

   private void createControl(Composite parent) {
      final Label buildIdLabel = new Label(this, SWT.None);
      buildIdLabel.setText("Enter Build ID:");

      buildIdEntry = new Text(this, SWT.BORDER);
      final GridData gd_buildIdEntry = new GridData(SWT.LEFT, SWT.CENTER, false, false);
      gd_buildIdEntry.widthHint = 150;
      buildIdEntry.setLayoutData(gd_buildIdEntry);

      final Label usersLabel = new Label(this, SWT.NONE);
      GridData gd_leftTop = new GridData(SWT.LEFT, SWT.TOP, false, false);
      usersLabel.setLayoutData(gd_leftTop);
      usersLabel.setText("Who ran:");

      runnerComp = new UserNamePanel(this, SWT.NONE);

      final Label witnessesLabel = new Label(this, SWT.NONE);
      witnessesLabel.setLayoutData(gd_leftTop);
      witnessesLabel.setText("Witnesses:");

      witnessNamesComp = new UserNamePanel(this, SWT.NONE);

      final Label notesLabel = new Label(this, SWT.NONE);
      notesLabel.setText("Notes");

      notesTextEntry = new Text(this, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
      final GridData gd_text_3 = new GridData(SWT.FILL, SWT.CENTER, true, false);
      gd_text_3.heightHint = 50;
      gd_text_3.minimumHeight = 50;
      notesTextEntry.setLayoutData(gd_text_3);
   }

   public String[] getRunnerNames() {
      List<String> items = this.runnerComp.getNames();
      return items.toArray(new String[items.size()]);
   }

   public String[] getWitnessNames() {
      List<String> items = this.witnessNamesComp.getNames();
      return items.toArray(new String[items.size()]);
   }

   public String getNotes() {
      String text = this.notesTextEntry.getText();
      return Strings.isValid(text) ? text : "";
   }

   public String getBuildId() {
      String text = this.buildIdEntry.getText();
      return Strings.isValid(text) ? text : "";
   }

   public void setNotes(String value) {
      if (Strings.isValid(value)) {
         this.notesTextEntry.setText(value);
      }
   }

   public void setBuildId(String value) {
      if (Strings.isValid(value)) {
         this.buildIdEntry.setText(value);
      }
   }

   public void setWitnessNames(String[] value) {
      witnessNamesComp.loadFromArray(value);
   }

   public void setExecutedByNames(String[] value) {
      runnerComp.loadFromArray(value);
   }
}
