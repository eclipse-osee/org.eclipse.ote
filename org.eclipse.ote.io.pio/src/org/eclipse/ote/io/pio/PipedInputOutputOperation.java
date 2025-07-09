/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Objects;
import java.util.function.Consumer;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.ote.tools.util.Message;
import org.eclipse.ote.tools.util.Singlet;

/**
 * A class that can be used to facilitate a stream transfer between a source and a sink. A {@link PipedOutputStream} and
 * {@link PipedInputStream} pair are created to connect the source and sink. The source is expected to write into the
 * {@link PipedOutputStream} and the sink is expected to read from the {@link PipedInputStream}. The {@link #run} method
 * will invoke the source to write it's data into the {@link PipedOutputStream} in a sub-thread while invoking the sink
 * to read data from the {@link PipedInputStream} in the current thread.
 * 
 * @author Loren K. Ashley
 */

public class PipedInputOutputOperation {

   /**
    * Saves the action that generates data for the {@link PipedOutputStream}.
    */

   private final @NonNull Consumer<@NonNull OutputStream> source;

   /**
    * Saves the action that reads data from the {@link PipedInputStream}.
    */

   private final @NonNull Consumer<@NonNull InputStream> sink;

   /**
    * Creates a new operator to transfer data from the source to the sink.
    * 
    * @param source a {@link Consumer} that writes to the provided {@link OutputStream}.
    * @param sink a {@link Consumer} that reads from the provided {@link InputStream}.
    * @throws NullPointerException when either parameter {@code source} or {@code sink} is {@code null}.
    */

   public PipedInputOutputOperation(@NonNull Consumer<@NonNull OutputStream> source, @NonNull Consumer<@NonNull InputStream> sink) {
      this.source = Objects.requireNonNull(source);
      this.sink = Objects.requireNonNull(sink);
   }

   /**
    * Invokes the source {@link Consumer} in another thread to fill the {@link PipedOutputStream} while invoking the
    * sink {@link Consumer} in the current thread to read and process data from the {@link PipedInputStream}.
    */

   public void run() {

      Singlet<Exception> sourceExceptionSinglet = new Singlet<>();
      Singlet<Exception> sinkExceptionSinglet = new Singlet<>();

      PipedOutputStream pipedOutputStream;
      PipedInputStream pipedInputStream;

      try {
         pipedOutputStream = new PipedOutputStream();
         pipedInputStream = new PipedInputStream(pipedOutputStream);
      } catch (Exception e) {
         //@formatter:off
         throw
            new PIOException
                   (
                      "PipedInputOutputOperation::run, failed to create piped streams.",
                      e
                   );
         //@formatter:on
      }

      //@formatter:off
      new Thread
             (
                () -> 
                {
                   try( OutputStream autoCloser = pipedOutputStream ) {
                      source.accept( pipedOutputStream );
                   } catch( Exception e ) {
                      sourceExceptionSinglet.set(e);
                   }
                }
             ).start();
      //@formatter:on      

      try (InputStream autoCloser = pipedInputStream) {
         sink.accept(pipedInputStream);
      } catch (Exception e) {
         sinkExceptionSinglet.set(e);
      }

      if (sourceExceptionSinglet.isEmpty() && sinkExceptionSinglet.isEmpty()) {
         return;
      }

      //@formatter:off
      throw
         new PIOException
                (
                   new Message()
                          .title( "PipedInputOutputOperation::run, operation failed." )
                          .indentInc()
                          .segmentIfNotNull( "Source Error", sourceExceptionSinglet.isEmpty() ? null : sourceExceptionSinglet.getFirst().getMessage() )
                          .reasonFollowsWithTraceIfNonNull( sourceExceptionSinglet.getFirst() )
                          .segmentIfNotNull( "Sink Error",   sinkExceptionSinglet.isEmpty()   ? null : sinkExceptionSinglet.getFirst().getMessage()   )
                          .reasonFollowsWithTraceIfNonNull( sinkExceptionSinglet.getFirst() )
                          .toString()
                );
      //@formatter:on
   }

}
