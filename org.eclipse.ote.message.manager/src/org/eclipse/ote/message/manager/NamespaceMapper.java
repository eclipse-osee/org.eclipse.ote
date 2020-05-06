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

import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.Namespace;

/**
 * Maps a Datatype to the DDS NameSpace
 * 
 * @author Michael P. Masterson
 */
public interface NamespaceMapper {

   /**
    * @param type
    * @return The Namespace associated with this specific datatype
    */
   Namespace getNamespace(DataType type);

}
