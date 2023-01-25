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
package org.eclipse.osee.ote.rest.internal;

import java.util.Comparator;

import org.eclipse.osee.ote.rest.model.OTEConfigurationItem;

public class OTEConfigItemSort implements Comparator<OTEConfigurationItem> {

   @Override
   public int compare(OTEConfigurationItem arg0, OTEConfigurationItem arg1) {
      return arg0.getBundleName().compareTo(arg1.getBundleName());
   }

}
