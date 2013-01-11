package lba.ote.ui.eviewer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Andrew M. Finkbeiner
 */
public enum OteElementImage implements KeyedImage {
   ELEMENT_VIEW("sample.gif");

   private final String fileName;

   private OteElementImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(Activator.PLUGIN_ID, "icons", fileName);
   }

   @Override
   public String getImageKey() {
      return Activator.PLUGIN_ID + ".icons." + fileName;
   }
}