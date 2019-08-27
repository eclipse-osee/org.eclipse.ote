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