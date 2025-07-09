/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.ote.io.pio;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.function.Consumer;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.ote.tools.util.Message;

/**
 * A factory class for creating sinks that write the contents of a stream to file. This factory creates sinks for use
 * with the {@link PipedInputOutputOperation} class.
 * 
 * @author Loren K. Ashley
 */

public class InputStreamFileWriterFactory {

   /**
    * Factory method to create a file writing sink for use with the class {@link PipedInputOutputOperation}. The sink
    * has the following properties:
    * <ul>
    * <li>If the parent directories for the file to be written do not exist, the parent directories will be
    * created.</li>
    * <li>If the file already exists, it will be overwritten.</li>
    * </ul>
    * The sink will may throw the following exceptions:
    * <dl>
    * <dt>NullPointerException</dt>
    * <dd>When the {@code inputStream} is {@code null}.</dd>
    * <dt>PIOException</dt>
    * <dd>
    * <ul>
    * <li>When the parent directory for the {@code file} does not exist and cannot be created.</li>
    * <li>When writing the stream contents to the {@code file} fails.</li>
    * </ul>
    * </dd>
    * </dl>
    * 
    * @param file the {@link File} that will be written with stream contents.
    * @return a {@link Consumer} that can be used as a sink with {@link PipedInputOutputOperation}.
    * @throws NullPointerException when {@code file} is {@code null}.
    * @throws InvalidPathException when the abstract path represented by {@code file} is not a valid path for the file
    * system.
    */

   public static @NonNull Consumer<@NonNull InputStream> create(@NonNull File file) {
      Objects.requireNonNull(file);
      //@formatter:off
      return
         new Consumer<@NonNull InputStream>() {
         
            private final @NonNull File outputFile = file;
            private final @Nullable Path directoryPath = this.outputFile.toPath().getParent();
            
            @Override
            public void accept(@NonNull InputStream inputStream) {
               
               Objects.requireNonNull( inputStream );
               
               
               if (Objects.nonNull( directoryPath ) && Files.notExists(directoryPath)) {
                  try( InputStream autoCloser = inputStream ) {
                     Files.createDirectories(directoryPath);
                  } catch( Exception e ) {
                     throw
                        new PIOException
                               (
                                  new Message()
                                         .title( "Input stream file writer failed to create parent directories for file." )
                                         .indentInc()
                                         .segment( "Directory Path", directoryPath )
                                         .reasonFollows( e )
                                         .toString(),
                                  e
                               );
                  }
               }

               try (InputStream autoCloser = inputStream) {
                  Files.copy(inputStream, this.outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
               } catch (Exception e) {
                  throw
                     new PIOException
                            (
                               new Message()
                                      .title( "Input stream file writer failed to write stream contents to file. " )
                                      .indentInc()
                                      .segment( "File", this.outputFile.getAbsolutePath().toString() )
                                      .toString(),
                               e
                            );
               }
         }

      };
      //@formatter:on
   }

}
