/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ote.message.io;

import java.util.List;

/**
 * Provides the list of IOWriters that apply to the given namespace
 * @author Michael P. Masterson
 */
public interface IOWriterProvider {

   List<IOWriter> getWriters(String namespace);
}
