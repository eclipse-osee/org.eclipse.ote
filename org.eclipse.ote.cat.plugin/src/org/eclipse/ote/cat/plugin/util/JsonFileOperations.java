/*********************************************************************
 * Copyright (c) 2024 Boeing
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.ote.cat.plugin.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Objects;
import org.eclipse.core.resources.IFile;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * An {@link ObjectMapper} wrapper with methods for reading and writing JSON files.
 * 
 * @author Loren K. Ashley
 * @param <T> The type of object marshaled to and from JSON.
 */

public class JsonFileOperations<T> {

   /**
    * An extension of {@link ByteArrayOutputStream} that provides access to it's internal buffer.
    */

   private class BufferByteArrayOutputStream extends ByteArrayOutputStream {
      //TODO: With Java 9+ an anonymous class can be used and this internal class can be removed.

      /**
       * Gets the streams byte buffer. Do not access the byte buffer until after writing to the stream is complete. The
       * wrapped {@link ByteArrayOutputStream} may reallocate it's internal buffer during writing.
       * 
       * @return the byte buffer.
       */

      byte[] getBuffer() {
         return this.buf;
      }
   }

   /**
    * Saves a description of the type of file being read or written for use in exception messages.
    */

   private final String fileDescription;

   /**
    * Saves the {@link ObjectMapper} used for marshaling objects of class &lt;T&gt;.
    */

   ObjectMapper objectMapper;

   /**
    * Saves the {@link Class} of the object being marshaled to and from file.
    */

   private final Class<T> pojoClass;

   /**
    * Creates a JSON file reader writer for objects of the class &lt;T&gt;.
    * 
    * @param pojoClass the class of the object to marshaled to and from JSON files.
    * @param fileDescription a description of the type of file being read or written for use in exception messages.
    */

   public JsonFileOperations(Class<T> pojoClass, String fileDescription) {
      this.pojoClass = pojoClass;
      this.fileDescription = fileDescription;
      this.objectMapper = new ObjectMapper();
   }

   /**
    * Reads the JSON <code>file</code> ({@link File}) and returns the contents as an object of class &lt;T&gt;.
    * 
    * @param file the file to be read.
    * @return an object of class &lt;T&gt; containing the file contents.
    * @throws OseeCoreException when unable to access the <code>file</code> or unable to parse the JSON.
    */

   public T read(File file) {

      Exception fileException = null;

      try {

         if (Objects.isNull(file) || !file.canRead()) {
            //@formatter:off
               fileException =
                  new OseeCoreException
                         (
                              "The JSON file does not exsit or cannot be read."                                + "\n"
                            + "   File Description: " + this.fileDescription                                   + "\n"
                            + "   File:             " + ( Objects.isNull( file ) ? "(null)" : file.getPath() ) + "\n"
                         );
               //@formatter:on
         }

      } catch (Exception e) {
         fileException = e;
      }

      if (Objects.nonNull(fileException)) {
         if (fileException instanceof OseeCoreException) {
            throw (OseeCoreException) fileException;
         } else {
            //@formatter:off
               OseeCoreException systemFileException =
                  new OseeCoreException
                         (
                              "An error occurred testing the accessability of the JSON file."                     + "\n"
                               + "   File Description: " + this.fileDescription                                   + "\n"
                               + "   File:             " + ( Objects.isNull( file ) ? "(null)" : file.getPath() ) + "\n",
                               fileException
                         );
               //@formatter:on
            throw systemFileException;
         }
      }

      InputStream inputStream = null;

      try {
         inputStream = new FileInputStream(file);
      } catch (Exception e) {
         //@formatter:off
         OseeCoreException getInputStreamException =
            new OseeCoreException
                   (
                        "Failed to get InputStream for the JSON file."                                   + "\n"
                      + "   File Description: " + this.fileDescription                                   + "\n"
                      + "   File:             " + ( Objects.isNull( file ) ? "(null)" : file.getPath() ) + "\n",
                      e
                   );
         //@formatter:on
         throw getInputStreamException;
      }

      T pojo = this.read(inputStream);

      return pojo;
   }

   /**
    * Reads the JSON <code>iFile</code> ({@link IFile}) and returns the contents as an object of class &lt;T&gt;.
    * 
    * @param file the file to be read.
    * @return an object of class &lt;T&gt; containing the file contents.
    * @throws OseeCoreException when unable to access the <code>iFile</code> or unable to parse the JSON.
    */

   public T read(IFile iFile) {

      Exception fileException = null;

      try {

         if (Objects.isNull(iFile) || !iFile.exists()) {
            //@formatter:off
               fileException =
                  new OseeCoreException
                         (
                              "The JSON file does not exsit."                                                                     + "\n"
                            + "   File Description: " + this.fileDescription                                                      + "\n"
                            + "   IFile:            " + ( Objects.isNull( iFile ) ? "(null)" : iFile.getLocation().toOSString() ) + "\n"
                         );
               //@formatter:on
         }

      } catch (Exception e) {
         fileException = e;
      }

      if (Objects.nonNull(fileException)) {
         if (fileException instanceof OseeCoreException) {
            throw (OseeCoreException) fileException;
         } else {
            //@formatter:off
               OseeCoreException systemFileException =
                  new OseeCoreException
                         (
                              "An error occurred testing the accessability of the JSON file."                                        + "\n"
                               + "   File Description: " + this.fileDescription                                                      + "\n"
                               + "   File:             " + ( Objects.isNull( iFile ) ? "(null)" : iFile.getLocation().toOSString() ) + "\n",
                               fileException
                         );
               //@formatter:on
            throw systemFileException;
         }
      }

      InputStream inputStream = null;

      try {
         inputStream = iFile.getContents();
      } catch (Exception e) {
         //@formatter:off
         OseeCoreException getInputStreamException =
            new OseeCoreException
                   (
                        "Failed to get InputStream for the JSON file."                                                      + "\n"
                      + "   File Description: " + this.fileDescription                                                      + "\n"
                      + "   File:             " + ( Objects.isNull( iFile ) ? "(null)" : iFile.getLocation().toOSString() ) + "\n",
                      e
                   );
         //@formatter:on
         throw getInputStreamException;
      }

      T pojo = this.read(inputStream);

      return pojo;
   }

   /**
    * Reads contents of the <code>inputStream</code> as JSON and returns the contents as an object of class &lt;T&gt;.
    * 
    * @param inputStream the {@link InputStream} to read from.
    * @return an object of class &lt;T&gt; containing the <code>inputStream</code> contents.
    * @throws OseeCoreException when unable to read from the <code>inputStream</code> or unable to parse the JSON.
    */

   public T read(InputStream inputStream) {

      if (Objects.isNull(inputStream)) {
         //@formatter:off
         OseeCoreException inputStreamException =
            new OseeCoreException
                   (
                        "The JSON InputStream is null."                + "\n"
                      + "   File Description: " + this.fileDescription + "\n"
                   );
         //@formatter:on
         throw inputStreamException;
      }

      T pojo = null;
      Exception pojoReadException = null;

      try (InputStream autoCloser = inputStream) {
         pojo = this.objectMapper.readValue(inputStream, this.pojoClass);
      } catch (Exception e) {
         pojoReadException = e;
      }

      if (Objects.isNull(pojo) || Objects.nonNull(pojoReadException)) {
         //@formatter:off
            OseeCoreException failedToReadPojoException =
               new OseeCoreException
                      (
                           "Failed to parse the JSON file."                + "\n"
                         + "   File Description: " + this.fileDescription  + "\n",
                         pojoReadException // <- might be null
                      );
            //@formatter:on
         throw failedToReadPojoException;
      }

      return pojo;
   };

   /**
    * Writes the <code>pojo</code> of class &lt;T&gt; to the <code>file</code> ({@link File}).
    * 
    * @param file the file to write the <code>pojo</code> to in JSON.
    * @param pojo the object of class &lt;T&gt; to be marshaled to JSON.
    * @throws OseeCoreException when unable to write the <code>file</code> of JSON marshaling fails.
    */

   public void write(File file, T pojo) {

      try {
         this.objectMapper.writeValue(file, pojo);
      } catch (Exception e) {
         //@formatter:off
            OseeCoreException writeException =
               new OseeCoreException
                      (
                           "Failed to write JSON file." + "\n"
                         + "   File Description: " + this.fileDescription                                   + "\n"
                         + "   File:             " + ( Objects.isNull( file ) ? "(null)" : file.getPath() ) + "\n",
                         e
                      );
            //@formatter:on
         throw writeException;
      }
   }

   /**
    * Writes the <code>pojo</code> of class &lt;T&gt; to the <code>file</code> ({@link File}).
    * 
    * @param file the file to write the <code>pojo</code> to in JSON.
    * @param pojo the object of class &lt;T&gt; to be marshaled to JSON.
    * @throws OseeCoreException when unable to write the <code>file</code> of JSON marshaling fails.
    */

   public void write(IFile iFile, T pojo) {

      try {

         BufferByteArrayOutputStream outputStream = new BufferByteArrayOutputStream();

         this.objectMapper.writeValue(outputStream, pojo);

         InputStream inputStream = new ByteArrayInputStream(outputStream.getBuffer(), 0, outputStream.size());

         if (iFile.exists()) {
            iFile.setContents(inputStream, 0, null);
         } else {
            iFile.create(inputStream, 0, null);
         }

      } catch (Exception e) {
         //@formatter:off
            OseeCoreException writeException =
               new OseeCoreException
                      (
                           "Failed to write JSON file."                                                                        + "\n"
                         + "   File Description: " + this.fileDescription                                                      + "\n"
                         + "   File:             " + ( Objects.isNull( iFile ) ? "(null)" : iFile.getLocation().toOSString() ) + "\n",
                         e
                      );
            //@formatter:on
         throw writeException;
      }
   };

}
