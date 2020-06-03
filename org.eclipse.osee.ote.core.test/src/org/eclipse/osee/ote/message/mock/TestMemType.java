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

package org.eclipse.osee.ote.message.mock;

import org.eclipse.osee.ote.message.enums.DataType;

/**
 * @author Andrew M. Finkbeiner
 */
public enum TestMemType implements DataType {
   ETHERNET,
   SERIAL;

   @Override
   public int getToolingBufferSize() {
      return 0;
   }

   @Override
   public int getToolingDepth() {
      return 0;
   }

}
