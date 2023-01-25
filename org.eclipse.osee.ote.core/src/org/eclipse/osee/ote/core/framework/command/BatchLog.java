/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ote.core.framework.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.ILoggerListener;
import org.eclipse.osee.framework.logging.OseeLog;

public class BatchLog implements ILoggerListener {
   
   private File fileToWriteTo;
   private FileOutputStream fos;
   private PrintWriter out;
   
   public BatchLog(File fileToWriteTo){
      this.fileToWriteTo = fileToWriteTo;
   }
   
   public void open() throws FileNotFoundException{
      fos = new FileOutputStream(fileToWriteTo);
      out = new PrintWriter(fileToWriteTo);
      OseeLog.registerLoggerListener(this);
   }
   
   public void close(){
      try {
         fos.close();
         out.close();
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         OseeLog.unregisterLoggerListener(this);
      }
   }
   
   public void flush(){
      out.flush();
   }
   
   @Override
   public void log(String loggerName, Level level, String message, Throwable th) {
      if(level.intValue() >= Level.SEVERE.intValue()){
         out.printf("%s %s\n", level.getName(), message);
         if(th != null){
            th.printStackTrace(out);
         }
      }
   }

}
