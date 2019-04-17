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
package org.eclipse.ote.test.manager.uut.selector;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ote.test.manager.uut.selector.internal.UutAvailableChangeListener;
import org.eclipse.ote.test.manager.uut.selector.internal.UutAvailableEventHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author David N. Phillips
 * @author Andy Jury
 */
public class UutLabelsComposite extends Composite implements UutAvailableChangeListener {
   private final Color DEFAULT_COLOR;
   private final Color SELECTED_COLOR;
   private boolean uutSelected;
   private boolean noDefaults;
   private Map<Label, String> paths;

   public UutLabelsComposite(Composite parent, int style) {
      super(parent, style);
      DEFAULT_COLOR = new Color(null, 0x99, 0x99, 0x99);
      SELECTED_COLOR = new Color(null, 0x99, 0xFF, 0x99);

      noDefaults = false;
      paths = new HashMap<>();
      RowLayout uutLabelLayout = new RowLayout(SWT.HORIZONTAL);
      uutLabelLayout.spacing = 5;
      uutLabelLayout.center = true;
      setLayout(uutLabelLayout);
      UutAvailableEventHandler handler = UutAvailableEventHandler.getHandler();
      if (handler != null) {
         handler.addListener(this);
      }
   }

   public void updateLabels(UutItemCollection collection, boolean uutRequired) {
      for (Control control : getChildren()) {
         control.dispose();
      }
      paths.clear();
      if (uutRequired) {
         boolean uutAdded = false;
         new Label(this, SWT.NONE).setText("UUT Selection:");
         UutItemPartition uuts[] = collection.getPartitions();
         uutSelected = false;
         for (IUutItem uut : uuts) {
            if (uut.isSelected()) {
               Label label = createUutLabel(uut.getPartition());
               label.setToolTipText("Build file will be run\n"+uut.getPath());
               paths.put(label, uut.getPath());
               uutSelected = true;
               uutAdded = true;
            }
         }
         if (!noDefaults && !uutSelected) {
            for (IUutItem uut : uuts) {
               if (!uut.getPath().isEmpty()) {
                  Label label = createUutLabel(uut.getPartition());
                  label.setToolTipText("Default build file set for "+uut.getPartition()+"\n"+uut.getPath());
                  paths.put(label, uut.getPath());
                  uutAdded = true;
               }
            }
         }
         if (!uutAdded) {
            Label label = new Label(this, SWT.BORDER);
            label.setText("NOTICE: No UUT is currently selected");
         }
      }
      else {
         Label label = new Label(this, SWT.BORDER);
         label.setText(" UUT selection not required when connected to non-simulated environment.  Load host manually. ");
      }
      uutAvailableChange();
      layout(true);
   }

   private Label createUutLabel(String uut) {
      Label label = new Label(this, SWT.BORDER);
      label.setText(" "+uut+" ");
      return label;
   }

   public void setNoDefaults(boolean noDefaults) {
      this.noDefaults = noDefaults;
   }

   @Override
   public void uutAvailableChange() {
      if (isDisposed()) {
         UutAvailableEventHandler.getHandler().removeListener(this);
         return;
      }
      getDisplay().asyncExec(new Runnable() {

         @Override
         public void run() {
            for (Label label : paths.keySet()) {
               final UutAvailableEventHandler handler = UutAvailableEventHandler.getHandler();
               final String file = paths.get(label);
               if (handler != null && !handler.getAvailability(file)) {
                  label.setBackground(new Color(null, 0xff, 0x99, 0x99));
               } else {
                  if (!noDefaults && !uutSelected) {
                     label.setBackground(DEFAULT_COLOR);
                  } else {
                     label.setBackground(SELECTED_COLOR);
                  }
               }
            }
         }
      });
   }
}
