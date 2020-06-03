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

package org.eclipse.ote.ui.message.watch.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

/**
 * @author Ken J. Aguilar
 */
public class DisabledAction extends Action {

   public DisabledAction(String name) {
      super(name, IAction.AS_PUSH_BUTTON);
      setEnabled(false);
   }
}
