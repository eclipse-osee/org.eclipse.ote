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
package org.eclipse.osee.ote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Store, find and remove jars that are used when running an OTE test server.
 * 
 * The format is  <pre>
 *                rootFolder/symbolicName/md5.jar - the jar
 *                rootFolder/symbolicName/md5.jar.date - the last time it was accessed
 *                </pre>
 * @author Andrew M. Finkbeiner
 *
 */
public interface OTEServerRuntimeCache {

   void clearJarCache();

   File save(String symbolicName, String md5Digest, InputStream servedBundleIn) throws IOException;

   File get(String symbolicName, String md5Digest) throws FileNotFoundException;

}
