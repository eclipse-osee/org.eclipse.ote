/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.ote.simple.test.environment;

import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.osee.ote.core.framework.testrun.OteTestFactory;

/**
 * @author Andy Jury
 */
public class SimpleTestFactory extends OteTestFactory {

   public SimpleTestFactory(IRuntimeLibraryManager rtLibManager) {
      super(rtLibManager);
   }

   @Override
   protected Class<? extends TestEnvironment> getTestEnvironmentClass() {
      return SimpleTestEnvironment.class;
   }

}
