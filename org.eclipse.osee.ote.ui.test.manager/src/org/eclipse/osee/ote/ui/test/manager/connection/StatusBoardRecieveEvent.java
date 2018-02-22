/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.test.manager.connection;

import java.util.concurrent.Callable;

/**
 * @author Andrew M. Finkbeiner
 */
public abstract class StatusBoardRecieveEvent<V> implements Callable<V> {

   private final V data;

   public StatusBoardRecieveEvent(V data) {
      this.data = data;
   }

   @Override
   public V call() throws Exception {
      run();
      return data;
   }

   public abstract void run();

}
