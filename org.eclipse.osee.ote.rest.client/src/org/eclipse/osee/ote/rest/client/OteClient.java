/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.ote.rest.client;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.concurrent.Future;

import org.eclipse.osee.ote.rest.model.OTEConfiguration;
import org.eclipse.osee.ote.rest.model.OTETestRun;


/**
 * @author Andrew Finkbeiner
 */
public interface OteClient {
   Future<Progress> getFile(URI uri, File destination, String filePath, final Progress progress);
   Future<Progress> configureServerEnvironment(URI uri, List<File> jars, final Progress progress);
   Future<Progress> configureServerEnvironment(URI uri, OTEConfiguration configuration, final Progress progress);
   Future<Progress> updateServerJarCache(URI uri, String baseJarURL, List<OTECacheItem> jars, final Progress progress);
   Future<ProgressWithCancel> runTest(URI uri, OTETestRun tests, Progress progress);
}
