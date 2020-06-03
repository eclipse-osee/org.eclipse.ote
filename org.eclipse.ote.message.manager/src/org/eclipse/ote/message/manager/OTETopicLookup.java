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

package org.eclipse.ote.message.manager;

import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.messaging.dds.service.TopicDescription;

/**
 * @author Michael P. Masterson
 */
public interface OTETopicLookup {

   MessageData getReader(TopicDescription topic);

}
