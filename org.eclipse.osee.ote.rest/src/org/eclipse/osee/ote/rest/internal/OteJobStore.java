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
import java.util.concurrent.ExecutionException;

import org.eclipse.osee.ote.rest.model.OTEJobStatus;

public interface OteJobStore {

   OTEJobStatus get(String uuid) throws InterruptedException, ExecutionException;

   Collection<String> getAll();

   void add(OteJob job);

}
