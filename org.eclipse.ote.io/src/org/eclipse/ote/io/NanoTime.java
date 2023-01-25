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
package org.eclipse.ote.io;

/**
 *  This Class is useful for tooling time critical sections of code to help determine what might be 
 *  running too slowly.  It keeps track of both the average and the largest difference.  the largest 
 *  difference is useful for tracking down when we run into GC or thread scheduling issues that can 
 *  cause poor performance at random times.
 * 
 * @author Andrew M. Finkbeiner
 *
 */
public class NanoTime {
   
   long startTime;
   long endTime;
   long count = 1;
   long longest = 0;
   long totalElapsed = 0;

   public void start(){
      count++;
      startTime = System.nanoTime();
   }

   public void stop(){
      endTime = System.nanoTime();
      long diff = endTime - startTime;
      if(diff > 0 ){
         if(diff > longest){
            longest = diff;
         }
         totalElapsed+=diff;
         if(totalElapsed < 0){//if addition wrapped reset the average
            count = 1;
            totalElapsed = 0;
         }
      }
   }

   public long average(){
      return totalElapsed/count;
   }

   public long count(){
      return count;
   }

   public long largestElapsed(){
      return longest;
   }

   public String summary(){
      return String.format("Average [%d]us Longest [%d]us count[%d]", average()/1000, largestElapsed()/1000, count());
   }

   public void print(String title, int interval){
      if(count() % interval == 0){
         System.out.println(title + " " + summary());
      }
   }
}