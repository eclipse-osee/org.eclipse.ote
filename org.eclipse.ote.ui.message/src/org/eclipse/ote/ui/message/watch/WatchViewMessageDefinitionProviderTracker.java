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
package org.eclipse.ote.ui.message.watch;

import org.eclipse.osee.ote.message.MessageDefinitionProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

class WatchViewMessageDefinitionProviderTracker extends ServiceTracker<MessageDefinitionProvider, MessageDefinitionProvider> {

	private final WatchView view;
	
	public WatchViewMessageDefinitionProviderTracker(BundleContext context, WatchView view){
		super(context, MessageDefinitionProvider.class.getName(), null);
		this.view = view;
	}

	@Override
	public MessageDefinitionProvider addingService(ServiceReference<MessageDefinitionProvider> reference) {
		MessageDefinitionProvider provider = super.addingService(reference);
		view.addMessageDefinitionProvider(provider);
		return provider;
	}

	@Override
	public void removedService(ServiceReference<MessageDefinitionProvider> reference, MessageDefinitionProvider service) {
		view.removeMessageDefinitionProvider(service);
		super.removedService(reference, service);
	}
	
}
