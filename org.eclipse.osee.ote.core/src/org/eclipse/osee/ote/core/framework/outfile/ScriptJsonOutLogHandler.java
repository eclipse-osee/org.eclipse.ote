/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ote.core.framework.outfile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.eclipse.osee.framework.jdk.core.persistence.XmlizableStream;
import org.eclipse.osee.ote.core.environment.interfaces.ITestPoint;
import org.eclipse.osee.ote.core.framework.outfile.xml.TestPointResults;
import org.eclipse.osee.ote.core.framework.outfile.xml.TimeSummary;
import org.eclipse.osee.ote.core.log.record.ScriptResultRecord;
import org.eclipse.osee.ote.core.log.record.TestPointRecord;
import org.eclipse.osee.ote.core.log.record.json.LogRecordModule;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.core.testPoint.CheckPoint;
import org.eclipse.osee.ote.core.testPoint.Operation;

/**
 * @author Andy Jury
 */
public class ScriptJsonOutLogHandler extends Handler {
   private final Map<String, Object> minimum = new HashMap<String, Object>();
   private final ObjectMapper mapper = new ObjectMapper();
   private final File outfile;
   private ZipOutputStream zip;
   private final String distrStatement;

   public ScriptJsonOutLogHandler(final File outFile, final String distributionStatement) {
      super();
      outfile = outFile;
      distrStatement = distributionStatement;
   }

   public ScriptJsonOutLogHandler(File outFile) {
      this(outFile, "DISTRO_STATEMENT_HERE");
   }

   @Override
   public synchronized void publish(final LogRecord logRecord) {
      if (isLoggable(logRecord)) {
         try {
            publisihMinimum(logRecord);
         } catch (Exception ex) {
            logError(ex);
         }
      }
   }

   private void publisihMinimum(LogRecord logRecord) {
      try {
         minimum.put("machineName", InetAddress.getLocalHost().getHostName());
      } catch (UnknownHostException ex) {
         logError(ex, "Unable to determine machine name");
      }
      if (logRecord instanceof ScriptResultRecord) {
         ScriptResultRecord srr = (ScriptResultRecord) logRecord;
         minimum.put("ScriptName", logRecord.getMessage());
         for (XmlizableStream rec : srr.getResults()) {
            if (rec instanceof TimeSummary) {
               TimeSummary ts = (TimeSummary) rec;
               minimum.put("elapsedTime", ts.getElapsedTime());
               minimum.put("startTime", ts.getStartTime());
               minimum.put("endTime", ts.getEndTime());
            } else if (rec instanceof TestPointResults) {
               Map<String, Object> results = new HashMap<String, Object>();
               results.put("passes", ((TestPointResults) rec).getPasses());
               results.put("fails", ((TestPointResults) rec).getFails());
               results.put("aborted", ((TestPointResults) rec).isAborted());
               results.put("total", ((TestPointResults) rec).getTotal());
               minimum.put("results", results);
            }
         }
      } else if (logRecord instanceof TestPointRecord) {
         @SuppressWarnings("unchecked")
         List<Object> testPoints = (List<Object>) minimum.get("testPoints");
         if (testPoints == null) {
            testPoints = new ArrayList<Object>();
            minimum.put("testPoints", testPoints);
         }
         ITestPoint testPoint = ((TestPointRecord) logRecord).getTestPoint();
         int number = ((TestPointRecord) logRecord).getNumber();
         boolean overall = testPoint.isPass();
         handleTestPoint(testPoint, testPoints, number, "", overall, String.valueOf(number));
      }
   }

   private void handleTestPoint(ITestPoint testPoint, List<Object> testPoints, int number, String groupName, boolean overallPass, String levelNum) {
      if (testPoint instanceof CheckPoint) {
         Map<String, Object> point =
            convertCheckPoint((CheckPoint) testPoint, number, groupName, overallPass, levelNum);
         testPoints.add(point);
      } else if (testPoint instanceof CheckGroup) {
         CheckGroup group = (CheckGroup) testPoint;
         ArrayList<ITestPoint> groupPoints = group.getTestPoints();
         Operation op = group.getOperation();
         String curGroupName = group.getGroupName() + " [" + op.getName() + "]";
         for (int i = 0; i < groupPoints.size(); i++) {
            ITestPoint tp = groupPoints.get(i);
            handleTestPoint(tp, testPoints, number, curGroupName, overallPass, levelNum + "." + (i + 1));
         }
      }
   }

   private Map<String, Object> convertCheckPoint(CheckPoint checkPoint, int number, String groupName, boolean overallPass, String levelNum) {
      Map<String, Object> tpMap = new HashMap<String, Object>();
      tpMap.put("name", checkPoint.getTestPointName());
      tpMap.put("expected", checkPoint.getExpected());
      tpMap.put("actual", checkPoint.getActual());
      tpMap.put("pass", checkPoint.isPass());
      tpMap.put("number", number);
      tpMap.put("overall", overallPass);
      if (!groupName.isEmpty()) {
         tpMap.put("groupName", groupName);
      }
      tpMap.put("tpLevel", levelNum);
      return tpMap;
   }

   private boolean prepareToFlush() {
      setupJson();
      return setupOutputFile();
   }

   private void setupJson() {
      mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
      mapper.configure(MapperFeature.AUTO_DETECT_FIELDS, false);
      mapper.configure(MapperFeature.AUTO_DETECT_GETTERS, false);
      mapper.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
      mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
      mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      mapper.registerModule(new LogRecordModule());
      TypeResolverBuilder<?> typeResolver = new TmoTypeResolverBuilder();
      typeResolver.init(JsonTypeInfo.Id.CLASS, null);
      typeResolver.inclusion(JsonTypeInfo.As.PROPERTY);
      typeResolver.typeProperty("@CLASS");
      mapper.setDefaultTyping(typeResolver);
   }

   private boolean setupOutputFile() {
      boolean result = true;
      String path;
      try {
         path = outfile.getCanonicalPath();
         path = path.substring(0, path.length() - 1) + 'z';
         File file = new File(path);
         FileOutputStream fos;
         try {
            fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            zip = new ZipOutputStream(bos);
         } catch (FileNotFoundException ex) {
            logError(ex, "Zip file creation failed");
            result = false;
         }
      } catch (IOException ex) {
         logError(ex, "Failed to get canaonical path from outFile");
         result = false;
      }
      return result;
   }

   public synchronized void flushRecords() {
      if (prepareToFlush()) {
         writeZipEntry("Minimum", minimum);
         writeZipEntry("DistributionStatement", distrStatement);
      }
   }

   private void writeZipEntry(final String basename, final Object object) {
      try {
         byte[] buffer = new byte[1024];
         File temp = File.createTempFile(basename, "json");
         mapper.writeValue(temp, object);
         FileInputStream input = new FileInputStream(temp);
         ZipEntry entry = new ZipEntry(basename + ".json");
         zip.putNextEntry(entry);
         int len;
         while ((len = input.read(buffer)) > 0) {
            zip.write(buffer, 0, len);
         }
         input.close();
         temp.delete();
      } catch (JsonGenerationException ex) {
         logError(ex);
      } catch (JsonMappingException ex) {
         logError(ex);
      } catch (IOException ex) {
         logError(ex);
      }
   }

   @Override
   public void close() throws SecurityException {
      try {
         zip.close();
      } catch (Exception ex) {
         logError(ex);
      }
   }

   @Override
   public void flush() {
      // don't call this method
   }

   private void logError(final Exception ex) {
      logError(ex);
   }

   private void logError(final Exception ex, final String message) {
      if (message != null && message.trim().length() > 0) {
         System.err.println(message);
      }
      if (ex != null) {
         ex.printStackTrace();
      } else {
         Throwable throwable = new Throwable();
         throwable.printStackTrace();
      }
   }
}