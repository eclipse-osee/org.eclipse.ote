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

package org.eclipse.ote.io.json;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.ote.io.pio.InputStreamFileWriterFactory;
import org.eclipse.ote.io.pio.PipedInputOutputOperation;
import org.eclipse.ote.tools.util.Message;

/**
 * An {@link ObjectMapper} wrapper with methods for reading and writing JSON files.
 * 
 * @author Loren K. Ashley
 * @param <T> The type of object marshaled to and from JSON.
 */

public class JsonFileOperations<T> {

   /**
    * Saves a description of the type of file being read or written for use in exception messages.
    */

   private final @Nullable String fileDescription;

   /**
    * Saves the {@link JavaType} used to instruct the {@link #objectMapper} how to deserialize the JSON array.
    */

   private final @NonNull CollectionType javaType;

   /**
    * Saves the {@link ObjectMapper} used for marshaling objects of class &lt;T&gt;.
    */

   private final @NonNull ObjectMapper objectMapper;

   /**
    * Saves the {@link Class} of the object being marshaled to and from file.
    */

   private final @NonNull Class<T> pojoClass;

   /**
    * Creates a JSON file reader writer for objects of the class &lt;T&gt; using the supplied {@link ObjectMapper}.
    * 
    * @param objectMapper the {@link ObjectMapper} to using for marshaling the JSON.
    * @param pojoClass the class of the object to marshaled to and from JSON files.
    * @param fileDescription a description of the type of file being read or written for use in exception messages.
    * @throws NullPointerException when {@code objectMapper} or {@code pojoClass} is {@code null}.
    */

   public JsonFileOperations(@NonNull ObjectMapper objectMapper, @NonNull Class<T> pojoClass, @Nullable String fileDescription) {
      this.objectMapper = Objects.requireNonNull(objectMapper);
      this.pojoClass = Objects.requireNonNull(pojoClass);
      this.fileDescription = fileDescription;
      this.javaType = this.objectMapper.getTypeFactory().constructCollectionType(Set.class, this.pojoClass);
   }

   /**
    * Creates a JSON file reader writer for objects of the class &lt;T&gt;. A new {@link ObjectMapper} is created for
    * use by the class.
    * 
    * @param pojoClass the class of the object to marshaled to and from JSON files.
    * @param fileDescription a description of the type of file being read or written for use in exception messages.
    * @throws NullPointerException when {@code pojoClass} is {@code null}.
    */

   public JsonFileOperations(@NonNull Class<T> pojoClass, @Nullable String fileDescription) {
      this(new ObjectMapper(), pojoClass, fileDescription);
   }

   /**
    * Generates a JSON array containing a JSON object for each member of the <code>pojoSet</code> and writes it to an
    * output stream.
    * 
    * @param outputStream to write the generated JSON to.
    * @param pojoSet the set of values to be converted to JSON.
    * @throws NullPointerException when {@code outputStream} or {@code pojoSet} is {@code null}.
    * @throws JsonFileOperationsException when unable to generate the JSON for a member of the <code>pojoSet</code>.
    */

   private void objectMapperWriteValue(@NonNull OutputStream outputStream, @NonNull Set<T> pojoSet) {

      Objects.requireNonNull(outputStream);
      Objects.requireNonNull(pojoSet);

      try {
         this.objectMapper.writeValue(outputStream, pojoSet);
      } catch (Exception e) {
         //@formatter:off
         throw
            new JsonFileOperationsException
                   (
                      new Message()
                             .title( "Failed to generate JSON." )
                             .indentInc()
                             .segmentIndexed( "POJO Set", pojoSet, Function.identity(), 12 )
                             .reasonFollows( e )
                             .toString(),
                      e
                   );
      }
   }

   /**
    * Generates a JSON object for the <code>pojo</code> and writes it to an output stream.
    * 
    * @param outputStream to write the generated JSON to.
    * @param pojo the value to be converted to JSON.
    * 
    * @throws NullPointerException when {@code outputStream} or {@code pojo} is {@code null}.
    * @throws JsonFileOperationsException when unable to write to the <code>outputStream</code> or generation of the JSON fails.
    */

   private void objectMapperWriteValue(@NonNull OutputStream outputStream, @NonNull T pojo) {

      Objects.requireNonNull(outputStream);
      Objects.requireNonNull(pojo);
      
      try {
         this.objectMapper.writeValue(outputStream, pojo);
      } catch (Exception e) {
         //@formatter:off
         throw
            new JsonFileOperationsException
                   (
                      new Message()
                             .title( "Failed to generate JSON." )
                             .indentInc()
                             .segment( "POJO", pojo )
                             .toString(),
                      e
                   );
      }
   }

   /**
    * Reads JSON data containing a JSON object from an {@link InputStream} and marshals it to an object of class &lt;T&gt;.
    * 
    * @param inputStream the {@link InputStream} to be read.
    * @return a object of class &lt;T&gt; populated with data from the JSON {@link InputStream}.
    * @throws NullPointerException when <code>inputStream</code> is <code>null</code>.
    * @throws JsonFileOperationsException when unable to read the <code>inputStream</code> or unable to parse the JSON.
    */

   public @NonNull T read(InputStream inputStream) {

      Objects.requireNonNull(inputStream);
      
      T pojo = null;

      Exception pojoReadException = null;

      try (InputStream autoCloser = inputStream) {
         pojo = this.objectMapper.readValue(inputStream, this.pojoClass);
      } catch (Exception e) {
         pojoReadException = e;
      }

      if (Objects.isNull(pojo) || Objects.nonNull(pojoReadException)) {
         //@formatter:off
         throw
            new JsonFileOperationsException
                   (
                       new Message()
                              .title( "Failed to parse the JSON input stream." )
                              .indentInc()
                              .segmentIfNotNull( "File Description", this.fileDescription )
                              .reasonFollowsIfNonNull( pojoReadException )
                              .toString(),
                      pojoReadException
                   );
         //@formatter:on
      }

      return pojo;
   };

   /**
    * Reads JSON data from an {@link InputStream} containing an array of JSON objects and marshals it to a
    * {@link Collection}&lt;T&gt;.
    * 
    * @param inputStream the {@link InputStream} to be read.
    * @param a {@link Consumer} that adds each object of type &lt;T&gt; to a {@link Collection}&lt;T&gt;.
    * @throws NullPointerException when <code>inputStream</code> or <code>adder</code> is <code>null</code>.
    * @throws JsonFileOperationsException when unable to read the <code>inputStream</code> or unable to parse the JSON.
    */

   public void read(@NonNull InputStream inputStream, @NonNull Consumer<T> adder) {

      Objects.requireNonNull(inputStream);
      Objects.requireNonNull(adder);

      Set<T> pojoSet = null;
      Exception pojoReadException = null;

      try (InputStream autoCloser = inputStream) {
         pojoSet = this.objectMapper.readValue(inputStream, this.javaType);
      } catch (Exception e) {
         pojoReadException = e;
      }

      if (Objects.isNull(pojoSet) || Objects.nonNull(pojoReadException)) {
         //@formatter:off
         throw
            new JsonFileOperationsException
                   (
                       new Message()
                              .title( "Failed to parse the JSON input stream." )
                              .indentInc()
                              .segmentIfNotNull( "File Description", this.fileDescription )
                              .reasonFollowsIfNonNull( pojoReadException )
                              .toString(),
                      pojoReadException
                   );
         //@formatter:on
      }

      pojoSet.forEach(adder);
   };

   /**
    * Reads a JSON file containing a JSON object and marshals it to an object of class &lt;T&gt;.
    * 
    * @param file the {@link File} to be read.
    * @return a object of class &lt;T&gt; populated with data from the JSON file.
    * @throws NullPointerException when <code>file</code> is <code>null</code>.
    * @throws JsonFileOperationsException when unable to access the <code>file</code> or unable to parse the JSON.
    */

   public @NonNull T read(@NonNull File file) {

      Objects.requireNonNull(file);

      InputStream inputStream = null;

      try {
         inputStream = new FileInputStream(file);
      } catch (Exception e) {
         //@formatter:off
         throw
            new JsonFileOperationsException
                   (
                      new Message()
                             .title( "Failed to get InputStream for the JSON file." )
                             .indentInc()
                             .segmentIfNotNull( "File Description", this.fileDescription )
                             .segment( "File", Objects.isNull( file ) ? "(null)" : file.getAbsolutePath() )
                             .toString(),
                      e
                   );
         //@formatter:on
      }

      return this.read(inputStream);
   }

   /**
    * Reads a JSON file containing an array of JSON objects and marshals it to a {@link Collection}&lt;T&gt;.
    * 
    * @param file the {@link File} to be read.
    * @param a {@link Consumer} that adds each object of type &lt;T&gt; to a {@link Collection}&lt;T&gt;.
    * @throws NullPointerException when <code>file</code> or <code>adder</code> is <code>null</code>.
    * @throws JsonFileOperationsException when unable to access the <code>file</code> or unable to parse the JSON.
    */

   public void read(File file, Consumer<T> adder) {

      Objects.requireNonNull(file);
      Objects.requireNonNull(adder);

      InputStream inputStream = null;

      try {
         inputStream = new FileInputStream(file);
      } catch (Exception e) {
         //@formatter:off
         throw
            new JsonFileOperationsException
                   (
                      new Message()
                             .title( "Failed to get InputStream for the JSON file." )
                             .indentInc()
                             .segmentIfNotNull( "File Description", this.fileDescription )
                             .segment( "File", Objects.isNull( file ) ? "(null)" : file.getAbsolutePath() )
                             .toString(),
                      e
                   );
         //@formatter:on
      }

      this.read(inputStream, adder);
   }

   /**
    * Writes the members of the <code>pojoSet</code> of class &lt;T&gt; to the <code>file</code> ({@link PolyFile}) as a
    * JSON array of objects.
    * 
    * @param file the {@link File} to written.
    * @param pojoSet the set of objects of class &lt;T&gt; to be marshaled to JSON.
    * @throws NullPointerException when either of the parameters are <code>null</code>.
    * @throws JsonFileOperationsException when unable to write the <code>file</code> or JSON generation fails.
    */

   public void write(@NonNull File file, @NonNull Set<@NonNull T> pojoSet) {

      Objects.requireNonNull(file);
      Objects.requireNonNull(pojoSet);

      try {

         //@formatter:off
         PipedInputOutputOperation operation = 
            new PipedInputOutputOperation
                   (
                      ( source ) -> this.objectMapperWriteValue(source, pojoSet),
                      InputStreamFileWriterFactory.create( file )
                   );
         //@formatter:on

         operation.run();

      } catch (Exception e) {
         //@formatter:off
            throw
               new JsonFileOperationsException
                      (
                         new Message()
                                .title( "Failed to write JSON polyFile." )
                                .indentInc()
                                .segmentIfNotNull( "File Description", this.fileDescription )
                                .segment( "File", Objects.isNull( file ) ? "(null)" : file.getAbsolutePath() )
                                .toString(),
                         e
                      );
         //@formatter:on
      }
   };

   /**
    * Writes the serialization of the <code>pojo</code> of class &lt;T&gt; to the <code>file</code> as JSON.
    * 
    * @param file the {@link File} to written.
    * @param pojo the object of class &lt;T&gt; to be marshaled to JSON.
    * @throws NullPointerException when either of the parameters are <code>null</code>.
    * @throws JsonFileOperationException when unable to write the <code>file</code> or JSON generation fails.
    */

   public void write(@NonNull File file, @NonNull T pojo) {

      Objects.requireNonNull(file);
      Objects.requireNonNull(pojo);

      try {

         //@formatter:off
         PipedInputOutputOperation operation = 
            new PipedInputOutputOperation
                   (
                      ( source ) -> this.objectMapperWriteValue(source, pojo),
                      InputStreamFileWriterFactory.create( file )
                   );
         //@formatter:on

         operation.run();

      } catch (Exception e) {
         //@formatter:off
            throw
               new JsonFileOperationsException
                      (
                         new Message()
                                .title( "Failed to write JSON file." )
                                .indentInc()
                                .segmentIfNotNull( "File Description", this.fileDescription )
                                .segment( "File", Objects.isNull( file ) ? "(null)" : file.getAbsolutePath() )
                                .toString(),
                         e
                      );
         //@formatter:on
      }
   };

}
