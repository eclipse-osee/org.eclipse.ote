package org.eclipse.ote.ui.eviewer.action;


import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ote.ui.eviewer.Activator;
import org.eclipse.ote.ui.eviewer.view.ElementColumn;
import org.eclipse.swt.graphics.Image;

final class ElementMessageLabelProvider extends LabelProvider {
	final Image activeImg = Activator.getDefault().getImageRegistry().get("ACTIVE_PNG");
	final Image inactive = Activator.getDefault().getImageRegistry().get("INACTIVE_PNG");

	@Override
	public String getText(Object element) {
		return ((ElementColumn) element).getVerboseName();
	}

	@Override
	public Image getImage(Object element) {
		return ((ElementColumn) element).isActive() ? activeImg : inactive;
	}
}