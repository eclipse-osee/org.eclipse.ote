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

package org.eclipse.ote.test.manager.pages;

import org.eclipse.osee.ote.message.MessageDefinitionProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M. Finkbeiner
 */
class ScriptPageMessageDefinitionProviderTracker extends ServiceTracker<MessageDefinitionProvider, MessageDefinitionProvider> {

	private final OteScriptPage view;
	
	public ScriptPageMessageDefinitionProviderTracker(BundleContext context, OteScriptPage view){
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
