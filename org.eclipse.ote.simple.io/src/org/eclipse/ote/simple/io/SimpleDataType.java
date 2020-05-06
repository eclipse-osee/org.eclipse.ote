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

package org.eclipse.ote.simple.io;

import org.eclipse.osee.ote.message.enums.DataType;

/**
 * 
 * @author Michael P. Masterson
 */
public enum SimpleDataType implements DataType {
   SIMPLE;
   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.enums.DataType#getToolingDepth()
    */
   @Override
   public int getToolingDepth() {
      return 1;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.enums.DataType#getToolingBufferSize()
    */
   @Override
   public int getToolingBufferSize() {
      return 4096;
   }

}
