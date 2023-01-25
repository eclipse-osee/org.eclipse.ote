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

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.eclipse.osee.ote.rest.model.OTEJobStatus;

public class OteJobStoreImpl implements OteJobStore {

   ConcurrentHashMap<String, OteJob> jobs;
   
   public OteJobStoreImpl(){
      jobs = new ConcurrentHashMap<>();
   }
   
   @Override
   public OTEJobStatus get(String uuid) throws InterruptedException, ExecutionException {
      OteJob job = jobs.get(uuid);
      if(job != null){
         return job.getStatus(); 
      }
      return null;
   }

   @Override
   public Collection<String> getAll() {
      return jobs.keySet();
   }

   @Override
   public void add(OteJob job) {
      jobs.put(job.getId(), job);
   }

}
