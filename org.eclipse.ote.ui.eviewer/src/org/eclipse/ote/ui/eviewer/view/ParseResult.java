package org.eclipse.ote.ui.eviewer.view;

import java.util.Collections;
import java.util.List;

import org.eclipse.ote.ui.eviewer.view.ColumnFileParser.ParseCode;

public final class ParseResult {
   
   private final ParseCode parseCode;
   private final List<ColumnEntry> columnEntries;
   
   public ParseResult(ParseCode parseCode, List<ColumnEntry> paths) {
      super();
      this.parseCode = parseCode;
      this.columnEntries = paths;
   }

   public ParseResult(ParseCode parseCode) {
      this(parseCode, Collections.<ColumnEntry>emptyList());
   }

   public ParseCode getParseCode() {
      return parseCode;
   }

   public List<ColumnEntry> getColumnEntries() {
      return columnEntries;
   }


}