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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Michael P. Masterson
 */
public class FilePart implements Part {

   private final String name;
   private final File file;
   private String contentType;

   public FilePart(String name, File file) {
      this.name = name;
      this.file = file;
   }

   /**
    * Allows for forcing the content type. If not called the MIME type will be deciphered from the file itself.
    *
    * @param contentType
    */
   public void setContentType(String contentType) {
      this.contentType = contentType;
   }

   @Override
   public List<String> getContentHeaders() {
      String contentDisposition =
         "Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + file.getName() + "\"";
      String contentType = "Content-Type: " + getMimeType().orElse("application/octet-stream");

      return Arrays.asList(new String[] {contentDisposition, contentType});
   }

   private Optional<String> getMimeType() {
      if (Strings.isValid(this.contentType)) {
         return Optional.ofNullable(this.contentType);
      }

      String mimeType = null;
      try {
         mimeType = Files.probeContentType(file.toPath());
      } catch (IOException e) {
         OseeLog.logf(this.getClass(), Level.WARNING, e, "Exception while probing content type of file: %s",
            file.toPath());
      }
      if (mimeType == null) {
         mimeType = URLConnection.guessContentTypeFromName(file.getName());
      }
      return Optional.ofNullable(mimeType);
   }

   @Override
   public Supplier<InputStream> getContentStream() {
      return () -> createInputStreamFromFile();
   }

   private FileInputStream createInputStreamFromFile() {
      try {
         return new FileInputStream(file);
      } catch (FileNotFoundException e) {
         throw new RuntimeException(e);
      }
   }
}