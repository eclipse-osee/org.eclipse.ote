/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.ote.rest.multipart;

import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author Michael P. Masterson
 */
public interface Part {

   List<String> getContentHeaders();

   Supplier<InputStream> getContentStream();
}