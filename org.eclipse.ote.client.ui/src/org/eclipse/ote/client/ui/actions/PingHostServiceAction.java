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
package org.eclipse.ote.client.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.ote.service.OteServiceProperties;

/**
 * @author Andrew M. Finkbeiner
 */
public class PingHostServiceAction extends Action {
   private final IServiceConnector connector;

   public PingHostServiceAction(IServiceConnector connector) {
      super("Ping server on " + new OteServiceProperties(connector).getStation(), IAction.AS_PUSH_BUTTON);
      this.connector = connector;
   }

   @Override
   public void run() {
      OteServiceProperties props = new OteServiceProperties(connector);
      String message =
         "The OTE server on " + props.getStation() + (connector.ping() ? " is alive" : " cannot be reached");
      MessageDialog.openInformation(AWorkbench.getActiveShell(), "Host Ping", message);
   }

}
