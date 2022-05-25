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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.eclipse.osee.ote.core.log.record.ScriptResultRecord;
import org.eclipse.osee.ote.core.log.record.json.LogRecordModule;

/**
 * @author Andy Jury
 */
public class ScriptJsonOutLogHandler extends Handler {
   private final Map<String, Object> minimum = new HashMap<String, Object>();
   private final MinimumPublisher minimumPublisher;
   private final ObjectMapper mapper = new ObjectMapper();
   private final File outfile;
   private ZipOutputStream zip;
   private final String distrStatement;
   private final StringBuilder testRunTransactionEndpointJson = new StringBuilder();
   private final TestRunTransactionEndpointPublisher testRunTransactionEndpointPublisher;

   public ScriptJsonOutLogHandler(final File outFile, final String distributionStatement) {
      super();
      outfile = outFile;
      distrStatement = distributionStatement;
      this.minimumPublisher = new MinimumPublisher(minimum);
      this.testRunTransactionEndpointPublisher = new TestRunTransactionEndpointPublisher();
   }

   public ScriptJsonOutLogHandler(File outFile) {
      this(outFile, "DISTRO_STATEMENT_HERE");
   }

   @Override
   public synchronized void publish(final LogRecord logRecord) {
      if (isLoggable(logRecord)) {
         try {
            minimumPublisher.publish(logRecord);
            if (logRecord instanceof ScriptResultRecord) {
               testRunTransactionEndpointJson.append(testRunTransactionEndpointPublisher.publish(logRecord));
            }
         } catch (Exception ex) {
            logError(ex, "Exception publishing items from outfile to json entries!");
         }
      }
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
         logError(ex, "Failed to get canonical path from outFile");
         result = false;
      }
      return result;
   }

   public synchronized void flushRecords() {
      if (prepareToFlush()) {
         writeZipEntry("Minimum", minimum);
         writeZipEntry("DistributionStatement", distrStatement);
         writeZipEntrySB("TestRunTransactionEndpoint", testRunTransactionEndpointJson);
      }
   }

   private void writeZipEntrySB(String basename, StringBuilder testRunTransactionEndpointJson2) {
      try {
         ZipEntry entry = new ZipEntry(basename + ".json");
         zip.putNextEntry(entry);

         byte[] data = testRunTransactionEndpointJson2.toString().getBytes();
         zip.write(data, 0, data.length);
      } catch (IOException ex) {
         logError(ex, "Exception generating zip archive with json files in it!");
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
         logError(ex, "Exception generating json!");
      } catch (JsonMappingException ex) {
         logError(ex, "Exception mapping json!");
      } catch (IOException ex) {
         logError(ex, "Exception generating zip archive with json files in it!");
      }
   }

   @Override
   public void close() throws SecurityException {
      try {
         zip.close();
      } catch (Exception ex) {
         logError(ex, "Exception closing zip archive!");
      }
   }

   @Override
   public void flush() {
      // don't call this method
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