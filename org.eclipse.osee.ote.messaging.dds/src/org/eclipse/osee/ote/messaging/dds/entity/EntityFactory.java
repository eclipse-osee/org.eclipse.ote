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

package org.eclipse.osee.ote.messaging.dds.entity;

/**
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public interface EntityFactory {

   /**
    * Gets the enabled status.
    * 
    * @return Returns <b>true </b> if this has been enabled, otherwise <b>false </b>.
    */
   public boolean isEnabled();
}
