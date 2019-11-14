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

import java.util.Hashtable;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.osee.ote.server.TestEnvironmentFactory;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Andy Jury
 */
public class SimpleTestEnvironmentStartupFactory implements TestEnvironmentFactory {

   @Override
   public MessageSystemTestEnvironment createEnvironment(IRuntimeLibraryManager runtimeLibraryManager) {

      SimpleTestEnvironment env = new SimpleTestEnvironment(runtimeLibraryManager);

      FrameworkUtil.getBundle(getClass()).getBundleContext().registerService(
         new String[] {SimpleTestEnvironment.class.getName()}, env, new Hashtable<String, Object>());
      return env;
   }

}
