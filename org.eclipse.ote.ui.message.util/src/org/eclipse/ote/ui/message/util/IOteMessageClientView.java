/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.ote.ui.message.util;

import org.eclipse.osee.ote.client.msg.IOteMessageService;
import org.eclipse.ui.IViewPart;

/**
 * @author Ken J. Aguilar
 */
public interface IOteMessageClientView extends IViewPart {
   void oteMessageServiceAcquired(IOteMessageService service);

   void oteMessageServiceReleased();
}