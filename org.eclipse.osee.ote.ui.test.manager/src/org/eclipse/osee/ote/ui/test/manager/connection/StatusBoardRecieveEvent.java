/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
