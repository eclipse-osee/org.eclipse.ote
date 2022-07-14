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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author Michael P. Masterson
 */
public class FieldPart implements Part {

   private final String name;
   private final String value;

   public FieldPart(String name, String value) {
      this.name = name;
      this.value = value;
   }

   @Override
   public List<String> getContentHeaders() {
      return Arrays.asList(new String[] {"Content-Disposition: form-data; name=\"" + name + "\""});
   }

   @Override
   public Supplier<InputStream> getContentStream() {
      return () -> new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
   }

}