/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.ote.ui.message.watch;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.ote.ui.message.internal.Activator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Andrew M. Finkbeiner
 */
public class ActionButton extends SelectionAdapter implements IExceptionableRunnable {

   private final Action action;
   private final String label;
   private final String pluginId;
   private final Button button;

   public ActionButton(Composite parent, int style, Action action, String label, String pluginId) {
      this.action = action;
      this.label = label;
      this.pluginId = pluginId;
      button = new Button(parent, style);
      button.addSelectionListener(this);
      button.setText(label);
   }

   @Override
   public void widgetSelected(SelectionEvent e) {
      Jobs.runInJob(label, this, Activator.class, pluginId);
   }

   @Override
   public IStatus run(IProgressMonitor monitor) throws Exception {
      action.run();
      return Status.OK_STATUS;
   }

   public void setToolTipText(String tooltip) {
      button.setToolTipText(tooltip);
   }

   public void setImage(Image img) {
      button.setImage(img);
   }
   
   public void setEnabled(boolean enabled) {
      button.setEnabled(enabled);
   }

}
