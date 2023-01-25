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

public class DeleteAllMessagesAction extends Action{
	private final MessageSelectComposite composite;

	public DeleteAllMessagesAction(MessageSelectComposite composite) {
		super("Delete All Messages", IAction.AS_PUSH_BUTTON);
		setToolTipText("Delete all messages from the recording list");
		this.composite = composite;
		setImageDescriptor(Images.DELETE_ALL.getImageDescriptor());

	}

	public void run() {
		composite.deleteAll();
	}
}
