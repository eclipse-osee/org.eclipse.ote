package org.eclipse.ote.ui.eviewer.view;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.osee.ote.client.msg.core.IMessageSubscription;
import org.eclipse.osee.ote.client.msg.core.ISubscriptionListener;
import org.eclipse.osee.ote.message.ElementPath;
import org.eclipse.osee.ote.message.elements.DiscreteElement;
import org.eclipse.osee.ote.message.elements.Element;

public class ColumnElement implements ISubscriptionListener {
   private ViewerColumnElement viewerColumn;

   private static final String UNKNOWN_VALUE = "?";
   private final String message;
   private final String verbosetext;
   private final ElementPath path;
   private DiscreteElement<?> element;
   private final AtomicReference<Object> lastValueReference = new AtomicReference<Object>(new Object());
   private final AtomicBoolean valueUpdatedFlag = new AtomicBoolean(false);
   private boolean unsupportedType = false;
   
   ColumnElement(ViewerColumnElement viewerColumn, ElementPath path) {
      this.viewerColumn = viewerColumn;
      this.path = path;
      message = path.getMessageClass();
      verbosetext = getMessageName(message) + "." + path.toString();
   }

   public ElementPath getElementPath() {
      return path;
   }

   public boolean update() {
      if(element != null){
         Object current = element.getValue();
         Object lastValue = lastValueReference.get();
         if (!current.equals(lastValue)) {
            lastValueReference.set(current);
            valueUpdatedFlag.set(true);
            return true;
         }
      }
      return false;
   }

   public String getVerboseName() {
      return verbosetext;
   }

   public String getMessageClassName() {
      return message;
   }

   public boolean getAndClearUpdateState() {
      return valueUpdatedFlag.getAndSet(false);
   }

   public Object getValue() {
      return lastValueReference.get();
   }

   public void clearValue() {
      lastValueReference.set(null);
   }
   
   public void setToolTip(){
      String tip = "";
      if (unsupportedType) {
    	  tip = "This element type cannot be displayed";
      } else if (element == null) {
         tip = "The element " + getElementPath() + " does not exist on " + getMessageClassName();
      } else {
         tip = String.format("%s.%s\nByte Offset: %d\nMSB: %d\nLSB: %d",  getMessageName(getMessageClassName()), path.toString(), element.getByteOffset(), element.getMsb(),
               element.getLsb());
         if(viewerColumn.isDuplicateName()){
            tip = "Note: Duplicate name in view\n" + tip;
         }
      }
      viewerColumn.setToolTip(tip);
   }

   public void dispose() {
      element = null;
      viewerColumn.getColumn().dispose();
   }

   public boolean isActive() {
      return viewerColumn.isActive();
   }

   @Override
   public void subscriptionActivated(IMessageSubscription subscription) {
   }

   @Override
   public void subscriptionCanceled(IMessageSubscription subscription) {
      element = null;
   }

   @Override
   public void subscriptionInvalidated(IMessageSubscription subscription) {
      element = null;
      lastValueReference.set(UNKNOWN_VALUE);
   }

   @Override
   public void subscriptionNotSupported(IMessageSubscription subscription) {
      element = null;
      lastValueReference.set(UNKNOWN_VALUE);
   }

   @Override
   public void subscriptionResolved(IMessageSubscription subscription) {
	   Element resolvedElement = subscription.getMessage().getElementByPath(path);
	   if (!(resolvedElement instanceof DiscreteElement)) {
		   unsupportedType = true;
		   element = null;
		   lastValueReference.set(UNKNOWN_VALUE);
		   return;
	   }
      element = (DiscreteElement<?>) resolvedElement;
      setToolTip();
      lastValueReference.set(element != null ? element.getValue() : UNKNOWN_VALUE);
   }

   public long getMessageEnvTime() {
      long time = -1;
      try {
         time = element.getMessage().getActiveDataSource().getTime();
      } catch (Exception e) {
         e.printStackTrace();
      }
      return time;
   }


   @Override
   public void subscriptionUnresolved(IMessageSubscription subscription) {
      viewerColumn.setToolTip("Not Found");
      element = null;
      lastValueReference.set(UNKNOWN_VALUE);

   }

   protected static String getMessageName(String msgClassName) {
      return msgClassName.substring(msgClassName.lastIndexOf('.') + 1);
   }

}