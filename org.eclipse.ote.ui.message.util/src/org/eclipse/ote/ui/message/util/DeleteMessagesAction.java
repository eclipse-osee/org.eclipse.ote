/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.ote.ui.message.util;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

public class DeleteMessagesAction extends Action{
	private final MessageSelectComposite composite;
	
	public DeleteMessagesAction(MessageSelectComposite composite) {
		super("Delete Selected Messages", IAction.AS_PUSH_BUTTON);
		setToolTipText("Delete all selected from the recording list");
		setImageDescriptor(Images.DELETE.getImageDescriptor());
		this.composite = composite;
	}

	public void run() {
		composite.deleteSelection();
	}
}
