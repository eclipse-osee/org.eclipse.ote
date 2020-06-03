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

package org.eclipse.osee.ote.message.interfaces;

import java.util.Set;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.message.enums.DataType;

/**
 * @author Andrew M. Finkbeiner
 */
public interface ITestEnvironmentMessageSystemAccessor extends ITestEnvironmentAccessor {
   IMessageManager getMsgManager();

   boolean isPhysicalTypeAvailable(DataType physicalType);

   Set<? extends DataType> getDataTypes();
}
