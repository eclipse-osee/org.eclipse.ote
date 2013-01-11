/*
 * Created on Apr 22, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package lba.ote.ui.eviewer.view;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;

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
	private final CopyOnWriteArrayList<ElementColumn> columns = new CopyOnWriteArrayList<ElementColumn>();
	private final IMessageSubscription subscription;
	private Message<?, ?, ?> message;

	private final IUpdateListener listener;
	private final HashSet<ElementColumn> deltaSet = new HashSet<ElementColumn>();

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
		if (subscription.getMemType() != type) {
			return;
		}
		boolean changed = false;
		for (ElementColumn column : columns) {
			boolean valueChanged = column.update();
			changed |= valueChanged && column.isActive();
		}
		if (changed) {
			listener.update(this);
		}

	}

	@Override
	public void onInitListener() throws MessageSystemException {
	}
}
