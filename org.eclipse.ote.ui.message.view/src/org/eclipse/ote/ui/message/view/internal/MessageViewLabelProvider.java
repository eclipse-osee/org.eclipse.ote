package org.eclipse.ote.ui.message.view.internal;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ote.message.lookup.MessageInputItem;
import org.eclipse.ote.ui.message.view.OteMessageViewImage;
import org.eclipse.swt.graphics.Image;

public class MessageViewLabelProvider implements ILabelProvider{

	private static final Image elementImg = ImageManager.getImage(OteMessageViewImage.PIPE);
	private static final Image messageImg = ImageManager.getImage(OteMessageViewImage.GEAR);

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}
	
	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}
	
	@Override
	public void dispose() {
	}
	
	@Override
	public void addListener(ILabelProviderListener listener) {
	}
	
	@Override
	public String getText(Object element) {
		if(element instanceof MessageInputItem){
			return ((MessageInputItem)element).getName();
		}
		return element.toString();
	}
	
	@Override
	public Image getImage(Object element) {
		if(element instanceof MessageInputItem){
			if(((MessageInputItem)element).getElementPath() == null){
				return messageImg;
			} else {
				return elementImg;
			}
		}
		return null;
	}
}
