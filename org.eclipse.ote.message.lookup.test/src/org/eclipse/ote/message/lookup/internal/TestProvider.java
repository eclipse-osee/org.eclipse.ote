/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.ote.message.lookup.internal;

import java.io.IOException;

import org.eclipse.ote.message.lookup.MessageLookupOperator;
import org.eclipse.ote.message.lookup.MessageLookupProvider;

/**
 * @author Michael P. Masterson
 */
class TestProvider implements MessageLookupProvider {

		private static final int UNIQUE_PROVIDER_ID = 0;

		@Override
		public void addToDb(MessageLookupOperator lookupOperator) {
			try {
				MessageLookupTestUtil.loadSampleData(lookupOperator, UNIQUE_PROVIDER_ID);
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}

		@Override
		public void removeFromDb(MessageLookupOperator lookupOperator) {
			lookupOperator.removeFromLookup(UNIQUE_PROVIDER_ID);
		}

		@Override
		public String getDescriptiveProviderName() {
			return "";
		}
		
	}