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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import org.apache.commons.io.IOUtils;

/**
 * @author Michael P. Masterson
 */
public class MultipartMessageBodyWriter implements MessageBodyWriter<MultiPartMessage> {
   private static final String HTTP_LINE_DELIMITER = "\r\n";

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return MultiPartMessage.class.isAssignableFrom(type) && MediaType.MULTIPART_FORM_DATA_TYPE.equals(mediaType);
   }

   @Override
   public void writeTo(MultiPartMessage t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {

      String boundary = "-----------" + UUID.randomUUID().toString().replace("-", "");

      List<Object> contentTypeHeader = new ArrayList<>();
      contentTypeHeader.add(MediaType.MULTIPART_FORM_DATA + "; boundary=" + boundary + "");
      httpHeaders.put("Content-type", contentTypeHeader);

      for (Part part : t.getParts()) {
         writePart(boundary, entityStream, part);
      }
      String endBoundary = "--" + boundary + "--" + HTTP_LINE_DELIMITER;
      print(entityStream, endBoundary);
   }

   private void writePart(String boundary, OutputStream entityStream, Part part) throws IOException {
      String startBoundary = "--" + boundary + HTTP_LINE_DELIMITER;

      print(entityStream, startBoundary);
      for (String contentHeader : part.getContentHeaders()) {
         print(entityStream, contentHeader + HTTP_LINE_DELIMITER);
      }
      print(entityStream, HTTP_LINE_DELIMITER);

      try (InputStream contentStream = part.getContentStream().get()) {
         IOUtils.copy(contentStream, entityStream);
      }
      print(entityStream, HTTP_LINE_DELIMITER);
   }

   private void print(OutputStream stream, String str) throws IOException {
      stream.write(str.getBytes(StandardCharsets.US_ASCII));
   }

}
