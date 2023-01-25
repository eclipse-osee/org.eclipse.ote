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
package org.eclipse.osee.ote.rest.client.internal;

import java.util.concurrent.Callable;
import org.eclipse.osee.ote.rest.client.Progress;

public abstract class BaseClientCallable<T extends Progress> implements Callable<T>{

   private T progress;
   
   public BaseClientCallable(T progress) {
      this.progress = progress;
   }

   @Override
   final public T call() throws Exception {
      try{
         doWork();
         progress.success();
      } catch (Throwable th){
         progress.fail(th);
      }
      return progress;
   }

   public abstract void doWork() throws Exception;

}
