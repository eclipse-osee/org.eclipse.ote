/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.ui.eviewer.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.Window;
import org.eclipse.ote.ui.eviewer.Activator;
import org.eclipse.ote.ui.eviewer.view.ColumnConfiguration;
import org.eclipse.ote.ui.eviewer.view.ColumnConfigurationDialog;
import org.eclipse.ote.ui.eviewer.view.ElementContentProvider;
import org.eclipse.ote.ui.eviewer.view.MessageDialogs;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Ken J. Aguilar
 */
public class ConfigureColumnsAction extends Action {

   private final ElementContentProvider provider;

   public ConfigureColumnsAction(ElementContentProvider provider) {
      super("Configure Columns", IAction.AS_PUSH_BUTTON);
      setImageDescriptor(Activator.getImageDescriptor("icons/table_config.gif"));
      this.provider = provider;
   }

   @Override
   public void run() {
      Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
      ColumnConfiguration configuration = new ColumnConfiguration(provider);
      ColumnConfigurationDialog dialog = new ColumnConfigurationDialog(shell, configuration);
      if (dialog.open() == Window.OK) {
         configuration.apply(provider);
         if (!provider.updateInternalFile()) {
            MessageDialogs.saveColumnFileFail(shell);
         }

      }
   }
}
