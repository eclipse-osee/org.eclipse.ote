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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael P. Masterson
 */
public class MultiPartMessage {

   private final List<Part> parts = new ArrayList<>();

   public void addPart(Part part) {
      parts.add(part);
   }

   public List<Part> getParts() {
      return new ArrayList<>(parts);
   }
}
