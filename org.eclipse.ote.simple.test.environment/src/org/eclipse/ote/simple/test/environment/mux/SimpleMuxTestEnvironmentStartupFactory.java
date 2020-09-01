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

package org.eclipse.ote.simple.test.environment.mux;

import java.util.Hashtable;

import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.osee.ote.server.TestEnvironmentFactory;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Michael P. Masterson
 */
public class SimpleMuxTestEnvironmentStartupFactory implements TestEnvironmentFactory {

   @Override
   public MessageSystemTestEnvironment createEnvironment(IRuntimeLibraryManager runtimeLibraryManager) {

      SimpleMuxTestEnvironment env = new SimpleMuxTestEnvironment(runtimeLibraryManager);

      FrameworkUtil.getBundle(getClass()).getBundleContext().registerService(
         new String[] {TestEnvironment.class.getName(), SimpleMuxTestEnvironment.class.getName()}, env, new Hashtable<String, Object>());
      return env;
   }

}
