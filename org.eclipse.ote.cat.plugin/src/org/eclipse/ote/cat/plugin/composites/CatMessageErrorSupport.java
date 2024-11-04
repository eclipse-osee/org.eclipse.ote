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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * A {@link Composite} extension to display an exception message and trace.
 * 
 * @author Loren K. Ashley
 */

public class CatMessageErrorSupport extends Composite {

   /**
    * Label for the trace display button when the trace is visible.
    */

   private static final String closeTraceLabel = "<< Debug Trace";

   /**
    * Label for the trace display button when the trace is not visible.
    */

   private static final String openTraceLabel = "Debug Trace >>";

   /**
    * Saves the {@link Text} {@link Control} used to display the exception message.
    */

   private Text message;

   /**
    * Saves the {@link Text} {@link Control} used to display the exception title.
    */

   private Text title;

   /**
    * Saves the {@link Text} {@link Control} used to display the exception trace.
    */

   private Text trace;

   /**
    * Saves the {@link Button} {@link Control} used to toggle visibility of the exception trace.
    */

   private Button traceButton;

   /**
    * Creates a new {@link Composite} used to display an exception message and trace.
    * 
    * @param parent the {@link Composite} the exception display is attached to.
    * @param title a title for the exception display.
    * @param message the exception message.
    * @param trace the exception stack trace.
    */

   public CatMessageErrorSupport(Composite parent, String title, String message, String trace) {

      super(parent, SWT.NONE);
      this.addDisposeListener(this::dispose);

      /*
       * Title Viewer
       */

      GridData titleGridData = new GridData();

      titleGridData.horizontalAlignment = SWT.FILL;
      titleGridData.grabExcessHorizontalSpace = true;
      titleGridData.minimumWidth = 128;
      titleGridData.widthHint = 128;

      titleGridData.verticalAlignment = SWT.BEGINNING;
      titleGridData.grabExcessVerticalSpace = false;

      this.title = new Text(parent, SWT.WRAP);
      this.title.setLayoutData(titleGridData);
      this.title.setText(title);

      /*
       * Message Viewer
       */

      GridData messageGridData = new GridData();

      messageGridData.horizontalAlignment = SWT.FILL;
      messageGridData.grabExcessHorizontalSpace = true;
      messageGridData.minimumWidth = 128;
      messageGridData.widthHint = 128;

      messageGridData.verticalAlignment = SWT.BEGINNING;
      messageGridData.grabExcessVerticalSpace = false;

      FontData messageFontData = new FontData("Courier", 10, SWT.NORMAL);
      Font messageFont = new Font(parent.getDisplay(), messageFontData);

      this.message = new Text(parent, SWT.WRAP);
      this.message.setLayoutData(messageGridData);
      this.message.setFont(messageFont);
      this.message.setText(message);

      /*
       * Details Button
       */

      this.traceButton = new Button(parent, SWT.NONE);
      SelectionListener buttonSelectionListener = SelectionListener.widgetSelectedAdapter((event) -> buttonPress());
      this.traceButton.addSelectionListener(buttonSelectionListener);
      this.traceButton.setText(openTraceLabel);

      /*
       * Stack trace viewer
       */

      GridData traceGridData = new GridData();

      traceGridData.horizontalAlignment = SWT.FILL;
      traceGridData.grabExcessHorizontalSpace = true;
      traceGridData.minimumWidth = 128;
      traceGridData.widthHint = 128;

      traceGridData.verticalAlignment = SWT.FILL;
      traceGridData.grabExcessVerticalSpace = true;
      traceGridData.minimumHeight = 256;
      traceGridData.heightHint = 256;

      this.trace = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
      this.trace.setLayoutData(traceGridData);
      this.trace.setEditable(false);
      this.trace.setText(trace);
      this.trace.setVisible(false);
   }

   /**
    * Call back method for {@link traceButton} presses. When the exception trace is visible it will be made invisible
    * and vice-versa.
    */

   private void buttonPress() {
      if (this.trace.isVisible()) {
         this.traceButton.setText(openTraceLabel);
         this.trace.setVisible(false);
      } else {
         this.traceButton.setText(closeTraceLabel);
         this.trace.setVisible(true);
      }
   }

   /**
    * Releases operating system resources for the error support message controls.
    * 
    * @param disposeEvent unused
    */

   private void dispose(DisposeEvent disposeEvent) {
      this.title.dispose();
      this.title = null;
      this.message.dispose();
      this.message = null;
      this.traceButton.dispose();
      this.traceButton = null;
      this.trace.dispose();
      this.trace = null;
   }
}
