/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.ote.simple.io;

import org.eclipse.osee.ote.core.environment.TestEnvironment;

/**
 * Mostly empty for now.  This will be the injection point for any Simple IO specific set up with the env
 * 
 * @author Michael P. Masterson
 */
public class StartSimpleIoComponent {
   
   public void start() {
      System.out.println("Started Simple IO Comp");
   }
   
   public void bindEnv(TestEnvironment env) {

   }
}
