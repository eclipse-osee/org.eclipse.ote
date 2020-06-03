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

package org.eclipse.osee.connection.service;

import java.io.File;

/**
 * @author Ken J. Aguilar
 */
class LocalFileKey implements IFileKey {

   private final File file;

   LocalFileKey(File file) {
      this.file = file;
   }

   File getFile() {
      return file;
   }
}
