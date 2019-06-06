/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.ui.message.tree;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.client.msg.core.IMessageSubscription;
import org.eclipse.osee.ote.message.commands.RecordCommand.MessageRecordDetails;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.tool.MessageMode;
import org.eclipse.ote.ui.message.internal.WatchImages;
import org.eclipse.ote.ui.message.messageXViewer.MessageXViewerFactory;
import org.eclipse.ote.ui.message.watch.ElementPath;
import org.eclipse.ote.ui.message.watch.WatchView;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

/**
 * @author Ken J. Aguilar
 */
public class WatchedMessageNode extends MessageNode {
   private static final String DEFAULT_ICON_TYPE = "default";
   private static final int READER_INDEX = 0;
   private static final int WRITER_INDEX = 1;
   private static final int READER_INDEX_MARK = 2;
   private static final int WRITER_INDEX_MARK = 3;

   private static Map<String, Image[]> messageIcons = null;

   private final RecordingState recordingState = new RecordingState();
   private final IMessageSubscription subscription;
   private final AtomicLong numUpdates = new AtomicLong(0);
   private volatile long lastUpdateNumber = -1;

   private Map<ElementPath, String> valueMap;
   private Set<DataType> availableTypes;
   
   public WatchedMessageNode(IMessageSubscription subscription) {
      super(subscription.getMessageClassName());
      this.subscription = subscription;
   }

   public IMessageSubscription getSubscription() {
      return subscription;
   }

   public RecordingState getRecordingState() {
      return this.recordingState;
   }

   public MessageRecordDetails createRecordingDetails() {
      return null;
   }

   public DataType getMemType() {
      return subscription.getMemType();
   }

   public void determineDeltas(Collection<AbstractTreeNode> deltas) {
      if (!isEnabled()) {
         return;
      }
      long currentUpdate = numUpdates.get();
      if (currentUpdate != lastUpdateNumber) {
         deltas.add(this);
         for (ElementNode node : getChildren()) {
            ((WatchedElementNode) node).determineDeltas(deltas);
         }
         lastUpdateNumber = currentUpdate;
      }
   }

   public void incrementCounter() {
      numUpdates.incrementAndGet();
   }

   public void clearUpdateCounter() {
      numUpdates.set(0);
      lastUpdateNumber = -1l;
   }

   public void setResolved(boolean isResolved) {
      for (ElementNode child : getChildren()) {
         WatchedElementNode elementNode = (WatchedElementNode) child;
         elementNode.setResolved(isResolved);
      }
      if(isResolved){
         availableTypes = getSubscription().getAvailableTypes();
      }
   }

   @Override
   public String getLabel(XViewerColumn columns) {
      if (columns == null) {
         return "";
      }
      if (columns.equals(MessageXViewerFactory.name)) {
         return getName();
      }
      if (columns.equals(MessageXViewerFactory.psUpdateCount)) {
         return numUpdates.toString();
      }
      return "";
   }

   public class RecordingState {
      private final List<ElementPath> headerElements = new ArrayList<ElementPath>();
      private final List<ElementPath> bodyElements = new ArrayList<ElementPath>();
      private boolean headerDump = false;
      private boolean bodyDump = false;

      public void reset() {
         headerElements.clear();
         bodyElements.clear();
         headerDump = false;
         bodyDump = false;
      }

      public void setHeaderDump(boolean header) {
         headerDump = header;
      }

      public void setBodyDump(boolean body) {
         bodyDump = body;
      }

      public void addHeader(ElementPath path) {
         headerElements.add(path);
      }

      public void addBody(ElementPath path) {
         bodyElements.add(path);
      }

      public boolean getBodyDump() {
         return bodyDump;
      }

      public boolean getHeaderDump() {
         return headerDump;
      }

      public List<ElementPath> getHeaderElements() {
         return headerElements;
      }

      public List<ElementPath> getBodyElements() {
         return bodyElements;
      }

      public void write(OutputStreamWriter writer) throws IOException {
         if (bodyDump) {
            writer.write(String.format("#rec#,%s,bodyHex,true", getMessageClassName()));
            writer.write("\n");
         }
         if (headerDump) {
            writer.write(String.format("#rec#,%s,headerHex,true", getMessageClassName()));
            writer.write("\n");
         }
         for (ElementPath path : headerElements) {
            writer.write(String.format("#rec#,%s,header,%s", getMessageClassName(), path.asString()));
            writer.write("\n");
         }
         for (ElementPath path : bodyElements) {
            writer.write(String.format("#rec#,%s,body,%s", getMessageClassName(), path.asString()));
            writer.write("\n");
         }
      }
   }

   @Override
   protected void dispose() {
      subscription.cancel();
      super.dispose();
   }
   
   @Override
   public Color getBackground() {
      if (!isEnabled()) {
         return null;
      }
      
      boolean isWriter = subscription.getMessageMode() == MessageMode.WRITER;
      if( isWriter) {
         return Displays.getColor(WatchView.COLOR_GOLDENROD.red, WatchView.COLOR_GOLDENROD.green, WatchView.COLOR_GOLDENROD.blue);
      } 
      return null;
   }

   @Override
   public Image getImage() {
      if (!isEnabled()) {
         return MessageNode.errorImg;
      }
      boolean isWriter = subscription.getMessageMode() == MessageMode.WRITER;
      boolean hasOptions = availableTypes != null && availableTypes.size() > 1;
      return getMessageIcon(getSubscription().getMemType().name(), isWriter, hasOptions);
   }

   public static Image getMessageIcon(String memType, boolean isWriter, boolean hasOptions) {
      if (messageIcons == null) {
         setupMessageIcons();
      }
      if (!messageIcons.containsKey(memType)) {
         memType = DEFAULT_ICON_TYPE;
      }
      Image[] rwSet = messageIcons.get(memType);
      if (rwSet != null) {
         if (!hasOptions) {
            if (isWriter) {
               return rwSet[WRITER_INDEX];
            } else {
               return rwSet[READER_INDEX];
            }
         } else {
            if (isWriter) {
               return rwSet[WRITER_INDEX_MARK];
            } else {
               return rwSet[READER_INDEX_MARK];
            }
         }
      }
      return null;
   }

   private static void setupMessageIcons() {
      messageIcons = new HashMap<String, Image[]>();
      Color bgc = new Color(null, 255, 255, 255);
      Image mark = WatchImages.PART_MARK.getImage();
      Image read = WatchImages.PART_READ.getImage();
      Image write = WatchImages.PART_WRITE.getImage();

      setMessageIcon(WatchImages.PART_TYPE_AI.getImage(),  WatchImages.PART_OUTLINE_ORANGE.getImage(), read, write, mark, bgc, new String[] {"WIRE_AIU"});
      setMessageIcon(WatchImages.PART_TYPE_AR.getImage(),  WatchImages.PART_OUTLINE_BROWN.getImage(), read, write, mark, bgc, new String[] {"ARINC"});
      setMessageIcon(WatchImages.PART_TYPE_D.getImage(),  WatchImages.PART_OUTLINE_PURPLE2.getImage(), read, write, mark, bgc, new String[] {"DLIST"});
      setMessageIcon(WatchImages.PART_TYPE_E.getImage(),  WatchImages.PART_OUTLINE_GRAY.getImage(), read, write, mark, bgc, new String[] {"ETHERNET"});
      setMessageIcon(WatchImages.PART_TYPE_IG.getImage(),  WatchImages.PART_OUTLINE_GREEN.getImage(), read, write, mark, bgc, new String[] {"IGTTS_WIRE"});
      setMessageIcon(WatchImages.PART_TYPE_M.getImage(),  WatchImages.PART_OUTLINE_BLUE.getImage(), read, write, mark, bgc, new String[] {"MUX", "MUX_LM"});
      setMessageIcon(WatchImages.PART_TYPE_P.getImage(),  WatchImages.PART_OUTLINE_PINK.getImage(), read, write, mark, bgc, new String[] {"PUB_SUB"});
      setMessageIcon(WatchImages.PART_TYPE_PA.getImage(),  WatchImages.PART_OUTLINE_PEACH.getImage(), read, write, mark, bgc, new String[] {"PATS_MUX", "WIRE_PATS"});
      setMessageIcon(WatchImages.PART_TYPE_S.getImage(),  WatchImages.PART_OUTLINE_PURPLE.getImage(), read, write, mark, bgc, new String[] {"SERIAL"});
      setMessageIcon(WatchImages.PART_TYPE_W.getImage(),  WatchImages.PART_OUTLINE_YELLOW.getImage(), read, write, mark, bgc, new String[] {"WIRE_MP_DIRECT", "WIRE_DIO", "WIRE_ESDS"});
      setMessageIcon(WatchImages.PART_TYPE_UNK.getImage(), WatchImages.PART_OUTLINE_RED.getImage(), read, write, mark, bgc, new String[] {DEFAULT_ICON_TYPE});
   }

   private static void setMessageIcon(Image imageType, Image imageOutline, Image read, Image write, Image mark, Color bgc, String[] memTypes) {
      Image[] images = new Image[4];
      for (String type : memTypes) {
         messageIcons.put(type, images);
      }
      images[READER_INDEX] = createImage(bgc, imageOutline, imageType, read, null);
      images[READER_INDEX_MARK] = createImage(bgc, imageOutline, imageType, read, mark);
      images[WRITER_INDEX] = createImage(bgc, imageOutline, imageType, write, null);
      images[WRITER_INDEX_MARK] = createImage(bgc, imageOutline, imageType, write, mark);
   }

   private static Image createImage(Color bgc, Image layer1, Image layer2, Image layer3, Image layer4) {
      final Rectangle bounds = layer1.getBounds();
      Image target = new Image(null, bounds);
      GC gc = new GC(target);
      gc.setBackground(bgc);
      gc.fillRectangle(bounds);
      addLayer(layer1, gc);
      addLayer(layer2, gc);
      addLayer(layer3, gc);
      addLayer(layer4, gc);
      gc.dispose();
      return target;
   }

   private static void addLayer(Image layer, GC gc) {
      if (layer != null) {
         gc.drawImage(layer, 0, 0);
      }
   }
  
   public void setRequestedValueMap(Map<ElementPath, String> valueMap) {
	   this.valueMap = valueMap;
   }
   
   public Map<ElementPath, String> getRequestedValueMap() {
	   return valueMap;
   }
}
