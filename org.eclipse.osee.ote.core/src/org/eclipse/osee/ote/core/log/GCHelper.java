package org.eclipse.osee.ote.core.log;

import java.lang.management.GarbageCollectorMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import org.eclipse.osee.framework.logging.OseeLog;
public class GCHelper {
   
   private static final String installGCMonitoring = System.getProperty("org.eclipse.osee.ote.gcmonitor", "true");
   
   
   private static volatile boolean enableLog = false;
   
   public static void enable(boolean enable){
      enableLog = enable;
   }
   
   public static void installGCMonitoring(){
      if(Boolean.parseBoolean(installGCMonitoring)){
         List<GarbageCollectorMXBean> gcbeans = java.lang.management.ManagementFactory.getGarbageCollectorMXBeans();
         for (GarbageCollectorMXBean gcbean : gcbeans) {
            NotificationEmitter emitter = (NotificationEmitter) gcbean;
            NotificationListener listener = new GCListener();
            emitter.addNotificationListener(listener, null, null);
         }
      }
    }
   
   private static class GCListener implements NotificationListener {

      private GCNotification gcData;

      public void printCompositeDataSupport(Object obj, int level){
         if(obj instanceof CompositeDataSupport){
            CompositeDataSupport dataSupport = (CompositeDataSupport)obj;
            CompositeType type = dataSupport.getCompositeType();
            for(String key : type.keySet()){
               Object value = dataSupport.get(key);
               if(value instanceof CompositeDataSupport || value instanceof TabularDataSupport){
                  printCompositeDataSupport(value, level + 1);
               } else {
                  if(!populateCompositeData(key, type, value)){
                     for(int i = 0; i < level; i++){
                        System.out.print("   ");
                     }
                     System.out.printf("key[%s] desc[%s] value[%s] class[%s]\n", key , type.getDescription(key), value, value.getClass().getName());
                  }
               }
            }
         } else if (obj instanceof TabularDataSupport){
            TabularDataSupport dataSupport = (TabularDataSupport)obj;
            TabularType type = dataSupport.getTabularType();
            for(Object value : dataSupport.values()){
               if(value instanceof CompositeDataSupport || value instanceof TabularDataSupport){
                  printCompositeDataSupport(value, level + 1);
               } else {
                  System.out.println("nope");
               }
            }
         }
         else {
            if(!populateGenericValue(obj)){
               for(int i = 0; i < level; i++){
                  System.out.print("   ");
               }
               System.out.printf("valud[%s] class[%s]\n", obj, obj.getClass().getName());
            }
         }
      }

      private boolean populateCompositeData(String key, CompositeType type, Object value) {
         if(key.equals("GcThreadCount")){
            gcData.threadCount = (Integer)value;
            return true;
         } else if (key.equals("duration")){
            gcData.duration = (Long)value;
            return true;
         } else if (key.equals("endTime")){
            gcData.endtime = (Long)value;
            return true;
         } else if (key.equals("startTime")){
            gcData.startTime = (Long)value;
            return true;
         } else if (key.equals("id")){
            //ignore
            return true;
         } else if (key.equals("key")){
            gcData.currentGcMemory = new GCMemory();
            gcData.currentGcMemory.memType = GCMemory.getType(value);
            if(gcData.currentGcMemory.memType == GCMemory.type.unknown){
               System.out.printf("unknown mem type [%s]\n", value);
            }
            gcData.memory.add(gcData.currentGcMemory);
            return true;
         } else if (key.equals("committed")){
            gcData.currentGcMemory.committed = (Long)value;
            return true;
         }else if (key.equals("init")){
            gcData.currentGcMemory.init = (Long)value;
            return true;
         }else if (key.equals("max")){
            gcData.currentGcMemory.max = (Long)value;
            return true;
         }else if (key.equals("used")){
            gcData.currentGcMemory.used = (Long)value;
            return true;
         }
         return false;
      }

      private boolean populateGenericValue(Object obj) {
         if(obj.toString().equals("end of minor GC")){
            gcData.isMinor = true;
            return true;
         } else if(obj.toString().equals("end of major GC")){
            gcData.isMajor = true;
            return true;
         } else if (obj.toString().equals("System.gc()") || obj.toString().equals("Metadata GC Threshold") || obj.toString().equals("Allocation Failure") || 
               obj.toString().equals("PS Scavenge") || obj.toString().equals("PS MarkSweep") || obj.toString().equals("ParNew") || obj.toString().equals("GCLocker Initiated GC")){
            if(gcData.reason == null){
               gcData.reason = obj.toString();
            } else {
               gcData.reason = gcData.reason +", " + obj.toString();
            }
            return true;
         }
         return false;
      }

      //implement the notifier callback handler
      @Override
      public void handleNotification(Notification notification, Object handback) {
         if(enableLog){
            gcData = new GCNotification();
            Object userData = notification.getUserData();
            if(userData instanceof CompositeDataSupport){
               CompositeDataSupport dataSupport = (CompositeDataSupport)userData;
               for(Object obj :dataSupport.values()){
                  printCompositeDataSupport(obj, 0);
               }

            } else {
               System.out.printf("%s - %s\n", userData.getClass().getName(), userData);
            }
            OseeLog.log(GCHelper.class, Level.INFO, gcData.toString());
         }
      }
   }
   
   public static class GCNotification {
      
      public String reason;
      public Long startTime;
      public GCMemory currentGcMemory;

      enum gcType { psScavenge, psMarkSweep; }
      
      private gcType type;
      private boolean isMajor = false;
      private boolean isMinor = false;
      private int threadCount;
      private long duration;
      private long endtime;
      private long id;
      
      private List<GCMemory> memory;
      
      public GCNotification(){
         memory = new ArrayList<GCHelper.GCMemory>();
         currentGcMemory = new GCMemory();
      }
      
      public String toString(){
         return String.format("GC %s - %s - elapsedTime[%d]", (isMajor ? "major" : "minor"), reason, duration);
      }
   }
   
   public static class GCMemory {
      enum type { compressedClassSpace, psSurvivorSpace, parSurvivorSpace, psOldGen, parOldGen, psEdenSpace, parEdenSpace, Metaspace, CodeCache, unknown; }
      
      private type memType;
      private long committed;
      private long init;
      private long max;
      private long used;
      
      public static org.eclipse.osee.ote.core.log.GCHelper.GCMemory.type getType(Object value) {
         if(value.equals("Compressed Class Space")){
            return type.compressedClassSpace;
         } else if (value.equals("PS Survivor Space")){
            return type.psSurvivorSpace;
         } else if (value.equals("PS Old Gen")){
            return type.psOldGen;
         } else if (value.equals("Metaspace")){
            return type.Metaspace;
         } else if (value.equals("PS Eden Space")){
            return type.psEdenSpace;
         } else if (value.equals("Code Cache")){
            return type.CodeCache;
         } else if (value.equals("Par Survivor Space")){
            return type.parSurvivorSpace;
         } else if (value.equals("CMS Old Gen")){
            return type.parOldGen;
         } else if (value.equals("Par Eden Space")){
            return type.parEdenSpace;
         } 
         return type.unknown;
      }
   }
   
}
