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
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author Michael P. Masterson
 */
public class FileStreamPart implements Part {

   private final String name;
   private final InputStream stream;
   private final String contentType;
   private final String filename;

   public FileStreamPart(String partName, String filename, String contentType, InputStream stream) {
      this.name = partName;
      this.filename = filename;
      this.stream = stream;
      this.contentType = contentType;
   }

   @Override
   public List<String> getContentHeaders() {
      String contentDisposition =
         "Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"";
      String contentTypeHeader = "Content-Type: " + contentType;

      return Arrays.asList(new String[] {contentDisposition, contentTypeHeader});
   }

   @Override
   public Supplier<InputStream> getContentStream() {
      return () -> stream;
   }

}