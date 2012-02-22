/*
 * Created on Apr 22, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package lba.ote.ui.eviewer.view;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;

import org.eclipse.osee.ote.client.msg.core.IMessageSubscription;
import org.eclipse.osee.ote.client.msg.core.ISubscriptionListener;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemException;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.listener.IOSEEMessageListener;

/**
 * @author Ken J. Aguilar
 */
public class SubscriptionDetails implements ISubscriptionListener, IOSEEMessageListener {
   private final ArrayList<ElementColumn> columns = new ArrayList<ElementColumn>();
   private final IMessageSubscription subscription;
   private Message<?, ?, ?> message;

   private final IUpdateListener listener;
   private final BitSet deltaSet = new BitSet();

   public SubscriptionDetails(IMessageSubscription subscription, IUpdateListener listener) {
      this.subscription = subscription;
      this.listener = listener;
      subscription.addSubscriptionListener(this);
   }

   public IMessageSubscription getSubscription() {
      return subscription;
   }

   public void addColumn(ElementColumn column) {
      columns.add(column);
      subscription.addSubscriptionListener(column);
   }

   public boolean removeColumn(ElementColumn column) {
      if (columns.remove(column)) {
         subscription.removeSubscriptionListener(column);
         column.dispose();
      }
      return columns.isEmpty();
   }

   public Collection<ElementColumn> getColumns() {
      return columns;
   }

   public void dispose() {
      subscription.cancel();
      for (ElementColumn column : columns) {
         column.dispose();
      }
   }

   @Override
   public void subscriptionActivated(IMessageSubscription subscription) {
   }

   @Override
   public void subscriptionCanceled(IMessageSubscription subscription) {
      if (message != null) {
         message.removeListener(this);
         message = null;
      }
   }

   @Override
   public void subscriptionInvalidated(IMessageSubscription subscription) {
      if (message != null) {
         message.removeListener(this);
         message = null;
      }
   }

   @Override
   public void subscriptionNotSupported(IMessageSubscription subscription) {
   }

   @Override
   public void subscriptionResolved(IMessageSubscription subscription) {
      message = subscription.getMessage();
      message.addListener(this);
   }

   @Override
   public void subscriptionUnresolved(IMessageSubscription subscription) {
      message = null;
   }

   @Override
   public void onDataAvailable(MessageData data, DataType type) throws MessageSystemException {
      synchronized (listener) {
         boolean changed = false;
         deltaSet.clear();
         for (ElementColumn column : columns) {
            boolean valueChanged = column.update();
            changed |= valueChanged && column.isActive();
            deltaSet.set(column.getIndex(), valueChanged);
         }
         if (changed) {
            listener.update(this, deltaSet);
         }
      }
   }

   @Override
   public void onInitListener() throws MessageSystemException {
   }
}
