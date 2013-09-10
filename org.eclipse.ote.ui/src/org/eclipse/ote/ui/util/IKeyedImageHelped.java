package org.eclipse.ote.ui.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public interface IKeyedImageHelped {
   public Image getImage();
   public ImageDescriptor createImageDescriptor();
   public String getImageKey();
}
