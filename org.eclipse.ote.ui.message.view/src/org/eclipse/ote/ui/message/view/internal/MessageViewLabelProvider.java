package org.eclipse.ote.ui.message.view.internal;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ote.message.lookup.MessageInputItem;
import org.eclipse.ote.ui.message.view.OteMessageViewImage;
import org.eclipse.swt.graphics.Image;

public class MessageViewLabelProvider extends StyledCellLabelProvider {

	private static final Image elementImg = ImageManager.getImage(OteMessageViewImage.PIPE);
	private static final Image messageImg = ImageManager.getImage(OteMessageViewImage.GEAR);

	public void update(ViewerCell cell) {
	   Object element = cell.getElement();
	   if(element instanceof MessageInputItem){
	      MessageInputItem item = (MessageInputItem)element;
	      cell.setImage(getImage(element));
	      StyledString text = new StyledString();
	      text.append(item.getName());
	      if(item.getType().length() > 0){
	         text.append("  [" +item.getType() + "] ", StyledString.QUALIFIER_STYLER);
	      }
	      cell.setText(text.toString());
	      cell.setStyleRanges(text.getStyleRanges());
	   } else {
	      cell.setText(getText(cell.getElement()));
	   }
	   super.update(cell);
	}
	
	private String getText(Object element) {
		if(element instanceof MessageInputItem){
			return ((MessageInputItem)element).getName();
		}
		return element.toString();
	}
	
	private Image getImage(Object element) {
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
