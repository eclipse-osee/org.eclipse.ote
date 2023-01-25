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
package org.eclipse.osee.ote.internal;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.osee.ote.ConfigurationStatus;

public class OTEFutureImpl implements Future<ConfigurationStatus> {

   private final Future<ConfigurationStatus> submit;
   private ConfigurationStatus oteConfigurationStatus;
   
   public OTEFutureImpl(Future<ConfigurationStatus> submit) {
      this.submit = submit;
   }

   public OTEFutureImpl(ConfigurationStatus oteConfigurationStatus) {
      this.submit = null;
      this.oteConfigurationStatus = oteConfigurationStatus;
   }

   @Override
   public boolean cancel(boolean mayInterruptIfRunning) {
      if(submit == null){
         return false;
      }
      return submit.cancel(mayInterruptIfRunning);
   }

   @Override
   public ConfigurationStatus get() throws InterruptedException, ExecutionException {
      if(submit == null){
         return oteConfigurationStatus;
      }
      return submit.get();
   }

   @Override
   public ConfigurationStatus get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      if(submit == null){
         return oteConfigurationStatus;
      }
      return submit.get(timeout, unit);
   }

   @Override
   public boolean isCancelled() {
      if(submit == null){
         return false;
      }
      return submit.isCancelled();
   }

   @Override
   public boolean isDone() {
      if(submit == null){
         return true;
      }
      return submit.isDone();
   }

}
