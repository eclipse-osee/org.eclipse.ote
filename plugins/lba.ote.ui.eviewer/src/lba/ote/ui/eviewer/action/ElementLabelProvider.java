package lba.ote.ui.eviewer.action;

import lba.ote.ui.eviewer.Activator;
import lba.ote.ui.eviewer.view.ElementColumn;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

final class ElementLabelProvider implements ILabelProvider {
	final Image activeImg = Activator.getImageDescriptor("icons/active.png").createImage();
	final Image inactive = Activator.getImageDescriptor("icons/inactive.png").createImage();

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// do nothing
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void dispose() {
		activeImg.dispose();
		inactive.dispose();
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// do nothing
		
	}

	@Override
	public String getText(Object element) {
		return ((ElementColumn) element).getName();
	}

	@Override
	public Image getImage(Object element) {
		return ((ElementColumn) element).isActive() ? activeImg : inactive;
	}
}