package org.eclipse.osee.ote.client.msg.core.internal;

import org.eclipse.osee.ote.message.tool.SubscriptionDetails;

public interface SubscriptionHandler {

   void onSubscriptionComplete(SubscriptionDetails details);
   
}
