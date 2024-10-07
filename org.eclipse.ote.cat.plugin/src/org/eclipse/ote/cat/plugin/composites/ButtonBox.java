/*********************************************************************
 * Copyright (c) 2024 Boeing
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.ote.cat.plugin.composites;

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * A {@link Composite} extension containing "Add" and "Remove" {@link Button}s arranged vertically.
 * 
 * @author Loren K. Ashley
 */

public class ButtonBox extends Composite {

   /**
    * A functional interface for the button pressed callbacks.
    */

   @FunctionalInterface
   public interface ButtonAction {

      /**
       * Callback method for a button press.
       */

      public void pressed();
   }

   /**
    * A functional interface for a call back to get bit mask for enabling and disabling the buttons.
    */

   @FunctionalInterface
   public interface EnableMaskSupplier {

      /**
       * Gets a bit mask to indicated which buttons should be enabled or disabled. When a bit is set the corresponding
       * button is enabled. When a bit is unset the corresponding button is disabled. The button bits are defined with
       * the constants:
       * <ul>
       * <li>{@link ButtonBox#AddButton AddButton}, and</li>
       * <li>{@link ButtonBox#RemoveButton RemoveButton}.</li>
       * </ul>
       * 
       * @return button mask.
       */

      public int getMask();
   }

   /**
    * Enable/Disable mask bit ({@value #AddButton}) for the "Add" button.
    */

   public static final int AddButton = 0x1;

   /**
    * Enable/Disable mask bit ({@value #RemoveButton}) for the "Remove" button.
    */

   public static final int RemoveButton = 0x2;

   /**
    * Saves the {@link Control} for the "Add" button.
    */

   private Button addButton;

   /**
    * Save the callback for the "Add" button.
    */

   private ButtonAction addButtonAction;

   /**
    * Saves the callback to get the button enable/disable bit mask.
    */

   private EnableMaskSupplier enableMaskSupplier;

   /**
    * Saves the {@link Control} for the "Remove" button.
    */

   private Button removeButton;

   /**
    * Saves the call back for the "Remove" button.
    */

   private ButtonAction removeButtonAction;

   /**
    * Creates a new {@link Composite} containing "Add" and "Remove" buttons placed vertically.
    * 
    * @param parent the {@link Composite} the button box is to be attached to.
    * @param addButtonAction this callback is invoked when the "Add" button is pressed.
    * @param removeButtonAction this callback is invoked when the "Remove" button is pressed.
    * @param enableMaskSupplier this callback is invoked when enabling or disabling buttons.
    */

   public ButtonBox(Composite parent, ButtonAction addButtonAction, ButtonAction removeButtonAction, EnableMaskSupplier enableAction) {

      super(parent, SWT.NULL);

      this.addButtonAction = addButtonAction;
      this.removeButtonAction = removeButtonAction;
      this.enableMaskSupplier = enableAction;

      GridLayout gridLayout = new GridLayout();
      gridLayout.marginWidth = 0;

      this.setLayout(gridLayout);
      this.createButtons();
      this.addDisposeListener(this::disposer);
      this.enableButtons();
   };

   /**
    * Creates the "Add", and "Remove" buttons on this {@link Composite}.
    */

   private void createButtons() {
      this.addButton = this.createPushButton("Add", this.addButtonAction);
      this.removeButton = this.createPushButton("Remove", this.removeButtonAction);
   }

   /**
    * Creates a button with:
    * <ul>
    * <li>the specified <code>label</code>,</li>
    * <li>set to scale horizontally,</li>
    * <li>a width hint large enough to accommodate the <code>label</code>, and</li>
    * <li>sets up a selection listener with the <code>buttonAction</code>.</li>
    * </ul>
    * 
    * @param key the resource name used to supply the button's label text.
    * @param buttonAction the callback to be invoked with the button is pressed.
    * @return Button the created button.
    */

   private Button createPushButton(String label, ButtonAction buttonAction) {
      Button button = new Button(this, SWT.PUSH);
      button.setText(label);
      button.setFont(this.getFont());
      GridData data = new GridData(GridData.FILL_HORIZONTAL);
      int widthHint = CompositeUtil.convertHorizontalDLUsToPixels(button, IDialogConstants.BUTTON_WIDTH);
      data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
      button.setLayoutData(data);
      SelectionListener selectionListener = widgetSelectedAdapter((event) -> buttonAction.pressed());
      button.addSelectionListener(selectionListener);
      return button;
   }

   /**
    * Releases operating system resources for the buttons.
    * 
    * @param event unused
    */

   private void disposer(DisposeEvent event) {
      this.addButton.dispose();
      this.addButton = null;
      this.removeButton.dispose();
      this.removeButton = null;
   }

   /**
    * Enables or disables the buttons according the the bit mask obtained from calling {@link #enableMaskSupplier}.
    */

   public void enableButtons() {
      int mask = this.enableMaskSupplier.getMask();
      this.addButton.setEnabled((mask & ButtonBox.AddButton) != 0);
      this.removeButton.setEnabled((mask & ButtonBox.RemoveButton) != 0);
   }

}
