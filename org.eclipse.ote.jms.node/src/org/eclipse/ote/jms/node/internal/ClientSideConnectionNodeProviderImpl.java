/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.jms.node.internal;

import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.ote.jms.node.JmsConnectionNodeProvider;


public final class ClientSideConnectionNodeProviderImpl implements JmsConnectionNodeProvider{

	private final ConnectionNode node;

	ClientSideConnectionNodeProviderImpl(ConnectionNode node) {
		this.node = node;
	}

	@Override
	public ConnectionNode getConnectionNode() {
		return node;
	}
}
