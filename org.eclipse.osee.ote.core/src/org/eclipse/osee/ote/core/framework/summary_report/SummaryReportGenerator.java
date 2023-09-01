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

package org.eclipse.osee.ote.core.framework.summary_report;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import com.sun.java.swing.plaf.windows.WindowsTreeUI.CollapsedIcon;

/**
 * 
 * SummaryReportGenerator provides a function to generate a {@link SummaryReport} from every TMO in a given a folder. <br>
 * This will spawn up to {@value SummaryReportGenerator#THREAD_COUNT} threads, unless there are less than that number of files.
 * 
 * @author Dominic Leiner
 */
public class SummaryReportGenerator {
   private static final int THREAD_COUNT = 8;

   /**
    * 
    * Generates a {@link SummaryReport} given a directory containing TMOs. <br>
    * Spawning up to {@value SummaryReportGenerator#THREAD_COUNT} threads, unless there are less than that number of files.
    * 
    * @param tmoDirectory A directory containing TMOs to parse.
    * @return {@link SummaryReport}
    */
   
   public static SummaryReport generate(File tmoDirectory) {
      return generate(tmoDirectory, false);
   }
   
   /**
    *  Extension to {@link #generate(File)}.
    */
   public static SummaryReport generate(File tmoDirectory, Boolean importTestPoints) {
      if(tmoDirectory.isDirectory()) {
         BlockingQueue<File> listOfFiles = new LinkedBlockingQueue<File>();
         listOfFiles.addAll(Lib.recursivelyListFiles(tmoDirectory, Pattern.compile(".*\\.tmo")));

         SummaryReport reportToReturn = new SummaryReport();
         
         int threadCount = (listOfFiles.size() < THREAD_COUNT) ? listOfFiles.size() : THREAD_COUNT;
         
         final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(threadCount, new SummaryReportThreadFactory());
         executor.setKeepAliveTime(500, TimeUnit.MILLISECONDS);
         
         int partitionSize = listOfFiles.size() / threadCount;
         int remainder = listOfFiles.size() % threadCount;
         int startIndex = 0;
         int endIndex = 0;
         List<Future<List<SummaryItem>>> futures = new LinkedList<>();
         
         for (int i = 0; i < threadCount; i++) {
            startIndex = endIndex;
            endIndex = startIndex + partitionSize;
            if (i == 0) {
               endIndex += remainder;
            }
  
            Worker worker = new Worker(listOfFiles, importTestPoints);
            Future<List<SummaryItem>> future;
            try {
               future = executor.submit(asRenamingCallable("SummaryReport " + i, worker)); 
               futures.add(future);
            } catch (Exception ex) {
               
            }
            
         }
         
         for (Future<List<SummaryItem>> future : futures) {
            try {
               reportToReturn.addAllItems(future.get());
            } catch (Exception ex) {
               
            }
         }
         
         executor.shutdown();
         return reportToReturn;
      }
      
      return null;
   }
   
   /**
    * 
    * Worker to run {@link OutFileResultProcessor} on each file. <br>
    * Returns a list of {@link SummaryItem}.
    */
   private static final class Worker implements Callable<List<SummaryItem>> {

      private BlockingQueue<File> sharedlist;
      private Boolean importTestPoints;

      public Worker(BlockingQueue<File> fullList, Boolean importTestPoints) {
         this.sharedlist = fullList;
         this.importTestPoints = importTestPoints;
      }

      @Override
      public List<SummaryItem> call() throws Exception {
         List<SummaryItem> fromThread = new LinkedList<>();
         while(! sharedlist.isEmpty()) {
            File file = sharedlist.poll();
            if(file==null) {
               continue;
            }
            
            OutFileResultProcessor resultProcessor = new OutFileResultProcessor(file, importTestPoints);
            resultProcessor.run();
            
            fromThread.add(resultProcessor.getSummaryItem());
         }

         return fromThread;
      }
      
   }
   
   /**
    * Below this is Convenient thread stuff
    */
   private static <T> Callable<T> asRenamingCallable(String name, Callable<T> task) {
      return new Callable<T>() {

         @Override
         public T call() throws Exception {
            Thread thisThread = Thread.currentThread();
            String oldName = thisThread.getName();
            thisThread.setName(name);
            T result = task.call();
            thisThread.setName(oldName);
            return result;
         }
      };
   }
   
   private static class SummaryReportThreadFactory implements ThreadFactory {
      private final ThreadGroup threadGroup = new ThreadGroup("SummaryReport Threads");
      private final AtomicInteger threadNumber = new AtomicInteger(1);

      @Override
      public Thread newThread(Runnable runnable) {
         Thread thread = new Thread(threadGroup, runnable, "SummaryReport thread " + threadNumber.getAndIncrement());
         return thread;
      }
   }
}
