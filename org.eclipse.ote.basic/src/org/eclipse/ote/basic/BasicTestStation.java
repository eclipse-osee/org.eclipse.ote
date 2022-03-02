/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.ote.basic;

import java.util.List;
import org.eclipse.osee.ote.core.environment.interfaces.IOTypeDefinition;
import org.eclipse.osee.ote.core.environment.interfaces.IOTypeHandlerDefinition;
import org.eclipse.osee.ote.core.environment.interfaces.ITestStation;

/**
 * This is a basic test station implementation that provides no unique driver types.
 *
 * @author Michael P. Masterson
 */
public class BasicTestStation implements ITestStation {

   @Override
   public List<IOTypeHandlerDefinition> getSupportedDriverTypes() {
      return null;
   }

   @Override
   public boolean isPhysicalTypeAvailable(IOTypeDefinition type) {
      return false;
   }

}
