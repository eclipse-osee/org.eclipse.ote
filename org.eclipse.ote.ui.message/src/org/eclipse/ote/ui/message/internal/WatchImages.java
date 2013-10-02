package org.eclipse.ote.ui.message.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.ote.ui.util.IKeyedImageHelped;
import org.eclipse.ote.ui.util.KeyedImageHelper;
import org.eclipse.swt.graphics.Image;

public enum WatchImages implements KeyedImage, IKeyedImageHelped {
   PART_MARK("part_mark.png"),
   PART_READ("part_read.png"),
   PART_WRITE("part_write.png"),
   PART_TYPE_AI("part_type_ai.png"),
   PART_TYPE_AR("part_type_ar.png"),
   PART_TYPE_D("part_type_d.png"),
   PART_TYPE_E("part_type_e.png"),
   PART_TYPE_IG("part_type_ig.png"),
   PART_TYPE_M("part_type_m.png"),
   PART_TYPE_P("part_type_p.png"),
   PART_TYPE_PA("part_type_pa.png"),
   PART_TYPE_S("part_type_s.png"),
   PART_TYPE_W("part_type_w.png"),
   PART_TYPE_UNK("part_type_unknown.png"),
   PART_OUTLINE_BLUE("part_outline_blue.png"),
   PART_OUTLINE_BROWN("part_outline_brown.png"),
   PART_OUTLINE_GRAY("part_outline_gray.png"),
   PART_OUTLINE_GREEN("part_outline_green.png"),
   PART_OUTLINE_ORANGE("part_outline_orange.png"),
   PART_OUTLINE_PEACH("part_outline_peach.png"),
   PART_OUTLINE_PINK("part_outline_pink.png"),
   PART_OUTLINE_PURPLE("part_outline_purple.png"),
   PART_OUTLINE_PURPLE2("part_outline_purple2.png"),
   PART_OUTLINE_RED("part_outline_red.png"),
   PART_OUTLINE_TEAL("part_outline_teal.png"),
   PART_OUTLINE_YELLOW("part_outline_yellow.png"),

   BINARY("binary.gif"),
   BINOCULARS("binoculars.gif"),
   BUG("bug.gif"),
   COLLAPSE_STATE("collapse_state.gif"),
   CONFIG("config.gif"),
   DATABASE("database.png"),
   DELETE("delete.gif"),
   DELETE_ALL("deleteAll.gif"),
   ELEMENT("element.gif"),
   ERROR_SM("errorSm.gif"),
   EXPAND_STATE("expand_state.gif"),
   GEAR("gear.png"),
   GLASSES("glasses.gif"),
   HEX("hex.gif"),
   MESSAGE_OLD("message_old.gif"),
   MSG_READ_IMG("msgReadImg.gif"),
   MSG_WRITE_IMG("msgWriteImg.gif"),
   OPEN("open.gif"),
   PIPE("pipe.png"),
   REFRESH("refresh.gif"),
   REMOVE("remove.gif"),
   SAVE("save.gif"),
   SHOW_NAMES("showNames.gif"),
   WATCHLIST_VIEW("watchlist_view.gif"),
   WIRE_AIU("wire_aiu.gif");


   private final KeyedImageHelper helper;

   private WatchImages(String filename) {
      helper = new KeyedImageHelper(Activator.PLUGIN_ID, filename);
   }

   @Override
   public Image getImage() {
      return helper.getImage(this);
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return helper.createImageDescriptor();
   }

   @Override
   public String getImageKey() {
      return helper.getImageKey();
   }

}
