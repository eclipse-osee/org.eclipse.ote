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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.osee.ote.core.framework.command.RunTests;

public class OteRunTestCommandsImpl implements OteRunTestCommands {

   private Map<String,  WeakReference<RunTests>> tests;
   
   public OteRunTestCommandsImpl(){
      tests = new HashMap<>();
   }
   
   @Override
   public RunTests getCommand(String id) {
      WeakReference<RunTests> ref = tests.get(id);
      return ref.get();
   }

   @Override
   public void putCommand(String id, RunTests envTestRun) {
      tests.put(id, new WeakReference<RunTests>(envTestRun));
   }

}
