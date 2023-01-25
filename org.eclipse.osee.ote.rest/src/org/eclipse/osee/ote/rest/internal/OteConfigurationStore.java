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

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;

import org.eclipse.osee.ote.rest.model.OTEConfiguration;
import org.eclipse.osee.ote.rest.model.OTEJobStatus;

public interface OteConfigurationStore {
   OTEJobStatus setup(OTEConfiguration config, UriInfo uriInfo) throws InterruptedException, ExecutionException, MalformedURLException, IllegalArgumentException, UriBuilderException;
   OTEJobStatus reset(UriInfo uriInfo) throws InterruptedException, ExecutionException, MalformedURLException, IllegalArgumentException, UriBuilderException;
	OTEConfiguration getConfiguration(UriInfo uriInfo) throws MalformedURLException, IllegalArgumentException, UriBuilderException, InterruptedException, ExecutionException;
   OTEJobStatus getJob(String uuid) throws InterruptedException, ExecutionException;
   Collection<String> getAllJobIds();
}
