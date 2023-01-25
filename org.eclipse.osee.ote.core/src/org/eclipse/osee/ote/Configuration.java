/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ote;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This defines the bundles to be loaded into the OTE Server.
 * 
 * @author Andrew M. Finkbeiner
 *
 */
public class Configuration implements Serializable {

   private static final long serialVersionUID = -3395485777990884086L;

   private ArrayList<ConfigurationItem> items;

   public Configuration() {
      items = new ArrayList<>();
   }
   
   public List<ConfigurationItem> getItems() {
      return items;
   }

   public void addItem(ConfigurationItem config) {
      items.add(config);
   }

}
