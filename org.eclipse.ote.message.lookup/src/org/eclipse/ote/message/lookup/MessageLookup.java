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

package org.eclipse.ote.message.lookup;

import java.util.List;

/**
 * 
 * @author David N. Phillips
 */
public interface MessageLookup {
	List<MessageLookupResult> lookup(String searchString);
	List<MessageLookupResult> lookup(String searchString, String... messageType);
	MessageLookupResult lookupClass(String messageName);
	List<String> getAvailableMessageTypes();
	List<String> getMessageSources();
}
