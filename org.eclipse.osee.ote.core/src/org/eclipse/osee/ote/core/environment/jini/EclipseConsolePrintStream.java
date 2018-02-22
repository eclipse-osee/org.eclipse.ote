/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.core.environment.jini;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.IUserSession;
import org.eclipse.osee.ote.core.environment.AsynchRemoteJobs;
import org.eclipse.osee.ote.core.environment.ConsoleOutputJob;
import org.eclipse.osee.ote.core.environment.TestEnvironment;

/**
 * @author Andrew M. Finkbeiner
 */
public class EclipseConsolePrintStream extends PrintStream {

   private final IUserSession callback;
   private final StringBuffer builder;

   public EclipseConsolePrintStream(OutputStream stream, IUserSession callback) {
      super(stream);
      this.callback = callback;
      builder = new StringBuffer();
   }

   public EclipseConsolePrintStream(final InputStream instream, final OutputStream outstream, IUserSession callback) {
      this(outstream, callback);

      new Thread(new Runnable() {

         @Override
         public void run() {
            try {
               String line;
               BufferedReader in = new BufferedReader(new InputStreamReader(instream));
               while ((line = in.readLine()) != null) {
                  println(line);
               }
               flush();
            } catch (IOException e) {
               OseeLog.log(TestEnvironment.class, Level.SEVERE, e);
            }
         }
      }).start();
   }

   //   public void write(byte[] cbuf, int off, int len) {
   //	  super.write(cbuf, off, len);
   //      buffer.append(cbuf, off, len);
   //      masterBuffer.append(cbuf, off, len);
   //   }

   @Override
   public void flush() {
      if (builder.length() == 0) {
         return;
      }
      AsynchRemoteJobs.getInstance(this).addJob(new ConsoleOutputJob(callback, builder.toString()));
      builder.delete(0, builder.length());
   }

   @Override
   public void close() {
      flush();
   }

   //public PrintStream append(char c) {
   //	return super.append(c);
   //}
   //public PrintStream append(CharSequence csq, int start, int end) {
   //	CharSequence cs = (csq == null ? "null" : csq);
   //	write(cs.subSequence(start, end).toString());
   //	return super.append(csq, start, end);
   //}
   //public PrintStream append(CharSequence csq) {
   //	return super.append(csq);
   //}
   @Override
   public boolean checkError() {
      return super.checkError();
   }

   //public PrintStream format(Locale l, String format, Object[] args) {
   //	return super.format(l, format, args);
   //}
   //public PrintStream format(String format, Object[] args) {
   //	return super.format(format, args);
   //}

   private void newLine() {
      flush();
   }

   @Override
   public void print(boolean b) {
      builder.append(b ? "true" : "false");
      super.print(b);
   }

   @Override
   public void print(char c) {
      builder.append(c);
      super.print(c);
   }

   @Override
   public void print(char[] s) {
      builder.append(s);
      super.print(s);
   }

   @Override
   public void print(double d) {
      builder.append(d);
      super.print(d);
   }

   @Override
   public void print(float f) {
      builder.append(f);
      super.print(f);
   }

   @Override
   public void print(int i) {
      builder.append(i);
      super.print(i);
   }

   @Override
   public void print(long l) {
      builder.append(l);
      super.print(l);
   }

   @Override
   public void print(Object obj) {
      builder.append(String.valueOf(obj));
      super.print(obj);
   }

   @Override
   public void print(String s) {
      builder.append(s);
      super.print(s);
   }

   //public PrintStream printf(Locale l, String format, Object[] args) {
   //	return super.printf(l, format, args);
   //}
   //public PrintStream printf(String format, Object[] args) {
   //	return super.printf(format, args);
   //}
   @Override
   public void println() {
      super.println();
      newLine();
   }

   @Override
   public void println(boolean x) {
      super.println(x);
      newLine();
   }

   @Override
   public void println(char x) {
      super.println(x);
      newLine();
   }

   @Override
   public void println(char[] x) {
      super.println(x);
      newLine();
   }

   @Override
   public void println(double x) {
      newLine();
      super.println(x);
   }

   @Override
   public void println(float x) {
      super.println(x);
      newLine();
   }

   @Override
   public void println(int x) {
      super.println(x);
      newLine();
   }

   @Override
   public void println(long x) {
      super.println(x);
      newLine();
   }

   @Override
   public void println(Object x) {
      super.println(x);
      newLine();
   }

   @Override
   public void println(String x) {
      super.println(x);
      newLine();
   }

   @Override
   public void write(int b) {
      builder.append(b);
      super.write(b);
   }

}
