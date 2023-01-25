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

import java.io.File;
import java.util.Comparator;

public class ReverseAlphabeticalSort implements Comparator<File> {

   @Override
   public int compare(File arg0, File arg1) {
      return arg1.getName().compareToIgnoreCase(arg0.getName());
   }

}
