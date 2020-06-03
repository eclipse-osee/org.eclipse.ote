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

package org.eclipse.osee.ote.core;

import java.io.IOException;
import java.util.Collection;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Andrew M. Finkbeiner
 */
public interface OteBundleLocator {

   Collection<BundleInfo> getRuntimeLibs() throws IOException, CoreException;

   Collection<BundleInfo> consumeModifiedLibs() throws IOException, CoreException;

}
