package org.eclipse.ote.ui.eviewer.test;

import org.eclipse.osee.ote.message.IMessageHeader;
import org.eclipse.osee.ote.message.data.IMessageDataVisitor;
import org.eclipse.osee.ote.message.data.MessageData;
public class ElementViewerTestMessageData extends MessageData{

   public ElementViewerTestMessageData(byte[] data, int dataByteSize, int offset) {
      super(data, dataByteSize, offset);
   }

   @Override
   public IMessageHeader getMsgHeader() {
      return null;
   }

   @Override
   public void visit(IMessageDataVisitor visitor) {
   }

   @Override
   public void initializeDefaultHeaderValues() {
   }
}
