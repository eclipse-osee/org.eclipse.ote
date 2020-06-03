/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ote.messaging.dds.listener;

import org.eclipse.osee.ote.messaging.dds.entity.Subscriber;

/**
 * The listener interface for receiving notification that data is available to <code>DataReader</code> objects in a
 * <code>Subscriber</code>.
 * 
 * @see org.eclipse.osee.ote.messaging.dds.entity.Subscriber
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public interface SubscriberListener extends DataReaderListener {

   /**
    * This is the first listener invoked when data becomes available. A reference to the <code>Subscriber</code> with
    * available information is passed to the method.
    */
   public void onDataOnReaders(Subscriber theSubscriber);
}
