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

package org.eclipse.ote.simple.io.manager;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.Namespace;
import org.eclipse.ote.message.manager.NamespaceMapper;
import org.eclipse.ote.simple.io.SimpleDataType;

/**
 * @author Michael P. Masterson
 */
public class SimpleNamespaceMapper implements NamespaceMapper {
   private Map<DataType, Namespace> namespaceMap;

   @Override
   public Namespace getNamespace(DataType type) {
      lateLoad();
      return namespaceMap.get(type);
   }

   private void lateLoad() {
      if(namespaceMap == null) {
         namespaceMap = new HashMap<>();
         namespaceMap.put(SimpleDataType.SIMPLE, new Namespace(SimpleDataType.SIMPLE.name()));
      }
   }

}
