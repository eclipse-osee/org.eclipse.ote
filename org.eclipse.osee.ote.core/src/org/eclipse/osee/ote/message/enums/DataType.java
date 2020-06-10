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

package org.eclipse.osee.ote.message.enums;

import java.io.Serializable;

import org.eclipse.osee.ote.message.interfaces.INamespace;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public interface DataType extends Serializable, INamespace {

   String name();

   /**
    * @return The depth of the reading queue.  Set to 1 if serial reads are required.
    */
   int getToolingDepth();

   /**
    * @return the byte buffer size for this data type.  This is generally at least the maximum
    * allowable size for a single transmission.  However, any readers should be able to handle
    * splitting large messages that are bigger than this value.
    */
   int getToolingBufferSize();
}
