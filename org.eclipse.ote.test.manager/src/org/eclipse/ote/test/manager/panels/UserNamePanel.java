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

package org.eclipse.ote.test.manager.panels;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.ote.ui.test.manager.TestManagerImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Michael P. Masterson
 */
public class UserNamePanel extends Composite {
   private final Image REMOVE_IMAGE = ImageManager.getImage(TestManagerImage.DELETE);
   private final Pattern NAME_PATTERN = Pattern.compile("(.*?),\\s+(.*?)\\s+(.*?)\\.");
   private List<UserName> names;
   private Composite nameComp;
   private Text middleInitialEntry;
   private Text firstNameEntry;
   private Text lastNameEntry;

   public UserNamePanel(Composite parent, int style) {
      super(parent, style);
      this.setLayout(new GridLayout(4, false));
      this.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      createControl(this);
   }

   private void createControl(Composite parent) {
      this.names = new ArrayList<>();

      this.nameComp = new Composite(this, SWT.NONE);
      this.nameComp.setLayout(new RowLayout());

      GridData nameLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
      nameLayoutData.horizontalSpan = 4;
      this.nameComp.setLayoutData(nameLayoutData);

      final Label lastNameLabel = new Label(this, SWT.NONE);
      final GridData gd_lastNameLabel = new GridData();
      gd_lastNameLabel.minimumWidth = 50;
      lastNameLabel.setLayoutData(gd_lastNameLabel);
      lastNameLabel.setText("Last Name");

      final Label firstLabel = new Label(this, SWT.NONE);
      firstLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
      firstLabel.setText("First Name");

      final Label middleInitialLabel = new Label(this, SWT.NONE);
      middleInitialLabel.setText("M.I.");
      new Label(this, SWT.NONE);

      GridData nameEntryData = new GridData(SWT.FILL, SWT.CENTER, false, false);
      nameEntryData.widthHint = 105;

      lastNameEntry = new Text(this, SWT.BORDER);
      lastNameEntry.setLayoutData(nameEntryData);

      firstNameEntry = new Text(this, SWT.BORDER);
      firstNameEntry.setLayoutData(nameEntryData);

      middleInitialEntry = new Text(this, SWT.BORDER);
      middleInitialEntry.setTextLimit(1);
      GridData gd_middleEntry = new GridData(SWT.LEFT, SWT.CENTER, false, false);

      gd_middleEntry.widthHint = 10;
      middleInitialEntry.setLayoutData(gd_middleEntry);

      final Button button = new Button(this, SWT.PUSH);
      button.setEnabled(false);
      button.setText("Add");
      button.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            UserName newName = new UserName(lastNameEntry.getText(), firstNameEntry.getText(), middleInitialEntry.getText());
            addName(newName);
            lastNameEntry.setText("");
            firstNameEntry.setText("");
            middleInitialEntry.setText("");
         }

      });

      lastNameEntry.addModifyListener(new ModifyListener() {

         @Override
         public void modifyText(ModifyEvent e) {
            button.setEnabled(lastNameEntry.getText().length() > 0 && firstNameEntry.getText().length() > 0);
         }

      });

      firstNameEntry.addModifyListener(new ModifyListener() {

         @Override
         public void modifyText(ModifyEvent e) {
            button.setEnabled(lastNameEntry.getText().length() > 0 && firstNameEntry.getText().length() > 0);
         }

      });
   }

   public List<String> getNames() {
      List<String> retVal = new ArrayList<>();
      for (UserName name : names) {
         retVal.add(name.toString());
      }

      return retVal;
   }

   private void addName(UserName name) {
      new RemoveNameComposite(nameComp, name);
      names.add(name);

      nameComp.getDisplay().update();
      getParent().getDisplay().update();
      nameComp.layout();

      getParent().layout();
      getParent().getParent().layout();
      getParent().getParent().getParent().getParent().layout();
      getParent().getParent().getParent().getParent().getParent().layout();
      getParent().getParent().getParent().getParent().getParent().getParent().layout();
   }

   private void removeName(Composite composite, UserName name) {
      composite.dispose();
      nameComp.getDisplay().update();
      nameComp.layout();
      getParent().layout();

      names.remove(name);
   }

   private class RemoveNameComposite extends Composite {
      private final UserName name;

      public RemoveNameComposite(Composite parent, UserName name) {
         super(parent, SWT.BORDER);
         this.name = name;
         this.setLayoutData(new RowData());
         createControl(this);
      }

      private void createControl(Composite parent) {
         GridLayout layout = new GridLayout(2, false);
         layout.marginHeight = 1;
         layout.marginWidth = 1;
         setLayout(layout);

         Label nameLabel = new Label(this, SWT.None);
         nameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
         nameLabel.setText(name.toString());

         final Button deleteButton = new Button(this, SWT.PUSH);
         deleteButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
         deleteButton.setImage(REMOVE_IMAGE);
         deleteButton.setData(name);
         deleteButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               removeName(deleteButton.getParent(), (UserName) deleteButton.getData());
            }
         });

      }
   }

   public void loadFromArray(String[] value) {
      if (value != null) {
         for (String entry : value) {
            Matcher matcher = NAME_PATTERN.matcher(entry);
            if (matcher.find()) {
               String last = matcher.group(1);
               String first = matcher.group(2);
               String middle = matcher.group(3);
               UserName name = new UserName(last, first, middle);
               this.addName(name);
            }
         }
      }
   }
}
