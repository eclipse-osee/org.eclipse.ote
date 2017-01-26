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
