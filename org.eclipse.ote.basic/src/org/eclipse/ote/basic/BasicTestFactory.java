/*********************************************************************
 * Copyright (c) 2019 Boeing
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

import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.osee.ote.core.framework.testrun.OteTestFactory;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;

/**
 * @author Andy Jury
 */
public class BasicTestFactory extends OteTestFactory {

   public BasicTestFactory(IRuntimeLibraryManager rtLibManager) {
      super(rtLibManager);
   }

   @Override
   protected Class<? extends TestEnvironment> getTestEnvironmentClass() {
      return MessageSystemTestEnvironment.class;
   }

}
