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

package org.eclipse.ote.message.lookup;

import java.util.List;

/**
 * Handles the insertion and removal of message definitions in the lookup
 * @author Michael P. Masterson
 */
public interface MessageLookupOperator {
   void addToLookup(int uniqueProviderId, String messageClass, String messageName,
         String messageType, int messageId, int byteSize, String phase, String rate,
         String scheduled, List<String> elements);

   void addToLookup(int uniqueProviderId, String messageClass, String messageName,
         String messageType, int messageId, int byteSize, String phase, String rate,
         String scheduled, List<String> elements, List<String> sources,
         List<String> destinations);

   void removeFromLookup(int uniqueProviderId);
}
