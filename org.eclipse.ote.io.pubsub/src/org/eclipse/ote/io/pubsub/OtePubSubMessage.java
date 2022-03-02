/*******************************************************************************
 * Copyright (c) 2022 Boeing.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.io.pubsub;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.OteMessage;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;

/**
 * @author Michael P. Masterson
 * @param <PS> Concrete PubSubMessage type that this class is wrapping
 */
public class OtePubSubMessage<PS extends PubSubMessage> extends OteMessage<PS> {

   public OtePubSubMessage(Class<PS> sourceMessageClass, IMessageRequestor<Message> requestor) {
      super(sourceMessageClass, requestor);
   }

}
