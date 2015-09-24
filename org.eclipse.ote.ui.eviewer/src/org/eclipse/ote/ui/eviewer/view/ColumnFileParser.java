package org.eclipse.ote.ui.eviewer.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.ElementPath;

/**
 * helper class that parses column files
 * @author Ken J. Aguilar
 *
 */
public final class ColumnFileParser {

   private static final String COLUMN_PATTERN = "([^\\+]+\\+(HEADER\\([^\\+]+\\)\\+)?[^\\+]+)=(active|inactive)";
   public static enum ParseCode {
      /**
       * the file does not exist or is a directory
       */
      FILE_NOT_FOUND,
      
      /**
       * denotes the file being empty (but exists) or fails to parse any
       * columns 
       */
      FILE_HAS_NO_VALID_COLUMNS,
      
      /**
       * IO operation failed
       */
      FILE_IO_EXCEPTION,
      
      /**
       * we have at least one column
       */
      SUCCESS;
   }
   
   public static ParseResult parse(File file) {
      if (!file.exists() || file.isDirectory()) {
         return new ParseResult(ParseCode.FILE_NOT_FOUND);
      }
      try {
         BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
         try {
            String line = reader.readLine();
            if (line == null) {
               // empty file
               return new ParseResult(ParseCode.FILE_HAS_NO_VALID_COLUMNS);
            }
            String[] columns = line.split(",");
            if (columns.length <= 0) {
               // no column delimiter in file
               return new ParseResult(ParseCode.FILE_HAS_NO_VALID_COLUMNS);
            }
            List<ColumnEntry> columEntries = parseColumns(columns);
            if (columEntries.size() == 0) {
               // looks like after parsing and filtering out bad columns we have nothing valid left
               return new ParseResult(ParseCode.FILE_HAS_NO_VALID_COLUMNS);
            }
            // success with at least one valid column entry
            return new ParseResult(ParseCode.SUCCESS, columEntries);
         } finally {
            reader.close();
         }
      } catch (Exception e) {
         OseeLog.log(ColumnFileParser.class, Level.SEVERE, "Exception while processing column file " + file.getAbsolutePath(), e);
         return new ParseResult(ParseCode.FILE_IO_EXCEPTION);
      }
   }

   private static List<ColumnEntry> parseColumns(String[] columns) {
      Pattern pattern = Pattern.compile(COLUMN_PATTERN);
      LinkedList<ColumnEntry> paths = new LinkedList<ColumnEntry>();
      for (String column : columns) {
         Matcher matcher = pattern.matcher(column);
         if (!matcher.matches()) {
            // ignored column
            continue;
         }
         String path = matcher.group(1);
         boolean isActive = "active".equals(matcher.group(3));
         
         ColumnEntry entry = new ColumnEntry(ElementPath.decode(path), isActive);
         paths.add(entry);
      }
      return paths;
   }
}
