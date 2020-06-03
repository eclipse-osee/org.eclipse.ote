/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ote.ui.output.tree.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.osee.ote.ui.output.Activator;
import org.eclipse.osee.ote.ui.output.preferences.OutputPreferencePage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class BaseOutfileTreeItem implements IOutfileTreeItem {

   private static final Styler greenStyler = new ColorStyler("ForestGreen", new RGB(34, 139, 34));
   private static final Styler redStyler = new ColorStyler("Red", new RGB(205, 0, 0));
   private static final Styler blueStyler = new ColorStyler("Blue", new RGB(51, 102, 255));

   private IOutfileTreeItem parent;
   private final List<IOutfileTreeItem> children;
   private Image image;
   private Runnable run;
   private String line;
   private String key;
   private final OutfileRowType type;
   private Object data;
   private int pass;
   private int fail;
   private boolean softHighlight = false;

   private final Map<Integer, String> columnToText = new HashMap<>();
   private final Map<Integer, StyledString> columnToStyledText = new HashMap<>();
   private final Map<String, String> fieldValues = new HashMap<>();
   private final Map<String, List<String>> fieldListValues = new HashMap<>();

   public BaseOutfileTreeItem(OutfileRowType type) {
      children = new ArrayList<>();
      this.type = type;
   }

   public BaseOutfileTreeItem(OutfileRowType type, IOutfileTreeItem parent, String firstColumn, Image image) {
      this(type);
      this.parent = parent;
      this.image = image;
      columnToText.put(0, firstColumn);
   }

   public BaseOutfileTreeItem(OutfileRowType type, String firstColumn, String secondColumn, String thirdColumn, Image image) {
      this(type);
      columnToText.put(0, firstColumn);
      columnToText.put(1, secondColumn);
      columnToText.put(2, thirdColumn);
      this.image = image;
   }

   public BaseOutfileTreeItem(OutfileRowType type, String firstColumn, String secondColumn, String thirdColumn, Image image, Runnable runable) {
      this(type, firstColumn, secondColumn, thirdColumn, image);
      this.run = runable;
   }

   public BaseOutfileTreeItem(OutfileRowType type, String firstColumn, String secondColumn, String thirdColumn, String fourthColumn, String fifthColumn, Image image) {
      this(type);
      columnToText.put(0, firstColumn);
      columnToText.put(1, secondColumn);
      columnToText.put(2, thirdColumn);
      columnToText.put(3, fourthColumn);
      columnToText.put(4, fifthColumn);
      this.image = image;
   }

   public BaseOutfileTreeItem(OutfileRowType type, String firstColumn, String secondColumn, String thirdColumn, String fourthColumn, String fifthColumn, Image image, Runnable run) {
      this(type, firstColumn, secondColumn, thirdColumn, fourthColumn, fifthColumn, image);
      this.run = run;
   }

   public BaseOutfileTreeItem(OutfileRowType testpoint, String firstColumn, String secondColumn, String thirdColumn, Image image, Object data) {
      this(testpoint, firstColumn, secondColumn, thirdColumn, image);
      this.data = data;
   }

   @Override
   public List<IOutfileTreeItem> getChildren() {
      return children;
   }

   @Override
   public String getThirdColumn() {
      return columnToText.get(2);
   }

   @Override
   public Image getImage() {
      return image;
   }

   @Override
   public IOutfileTreeItem getParent() {
      return parent;
   }

   @Override
   public void setParent(IOutfileTreeItem parent) {
      this.parent = parent;
   }

   @Override
   public String getSecondColumn() {
      return columnToText.get(1);
   }

   @Override
   public void setThirdColumn(String description) {
      columnToText.put(2, description);
   }

   @Override
   public void setSecondColumn(String title) {
      columnToText.put(1, title);
   }
   @Override
   public void setImage(Image image) {
      this.image = image;
   }

   @Override
   public void run() {
      if (run != null) {
         run.run();
      }
   }

   @Override
   public String getFirstColumn() {
      if (line != null) {
         return String.format("[%s] - %s", line, columnToText.get(0));
      }
      return columnToText.get(0);
   }

   @Override
   public String toString() {
      String col0 = columnToText.get(0);
      String col1 = columnToText.get(1);
      String col2 = columnToText.get(2);
      String col3 = columnToText.get(3);
      String col4 = columnToText.get(4);
      StringBuilder sb = new StringBuilder();
      if(col0 != null){
         sb.append(col0);         
      }
      if(col1 != null){
         sb.append("\t");
         sb.append(col1);         
      }
      if(col2 != null){
         sb.append("\t");
         sb.append(col2);         
      }
      if(col3 != null){
         sb.append("\t");
         sb.append(col3);         
      }
      if(col4 != null){
         sb.append("\t");
         sb.append(col4);         
      }
      return sb.toString();
   }

   @Override
   public void setRunnable(Runnable runme) {
      this.run = runme;
   }

   @Override
   public void setFirstColumn(String label) {
      columnToText.put(0, label);
   }

   @Override
   public void setLineNumber(String line) {
      this.line = line;
   }

   public String getItemKey() {
      return key;
   }

   public void setItemKey(String key) {
      this.key = key;
   }

   @Override
   public boolean equals(Object arg0) {
      if (arg0 instanceof BaseOutfileTreeItem) {
         BaseOutfileTreeItem arg = (BaseOutfileTreeItem) arg0;
         if (key != null && arg.key != null) {
            return key.equals(arg.key);
         }
      }
      return super.equals(arg0);
   }

   @Override
   public int hashCode() {
      if (key != null) {
         return key.hashCode(); 
      } 
      return super.hashCode();
   }
   
   public boolean equals(BaseOutfileTreeItem item) {
      return false;
   }

   @Override
   public String getFourthColumn() {
      return columnToText.get(3);
   }

   @Override
   public void setFourthColumn(String fourthColumn) {
      columnToText.put(3, fourthColumn);
   }

   @Override
   public String getFifthColumn() {
      return columnToText.get(4);
   }

   @Override
   public void setFifthColumn(String fifthColumn) {
      columnToText.put(4, fifthColumn);
   }

   @Override
   public OutfileRowType getType() {
      return type;
   }

   @Override
   public Object getData() {
      return data;
   }

   public void cacheColumnStyledString() {
      for (int i = 0; i < 4; i++) {
         getColumnStyledString(i);
      }
   }

   @Override
   public StyledString getColumnStyledString(int column) {
      StyledString str = columnToStyledText.get(column);
      if (str == null) {
         String text = columnToText.get(column);
         str = new StyledString();

         if (column == 0) {
            if (Activator.getDefault().getPreferenceStore().getBoolean(OutputPreferencePage.TIME)) {
               String time = getColumnText(-1);
               if (time != null) {
                  str.append("[" + time + "ms] ", StyledString.DECORATIONS_STYLER);
               }
            }
            if (Activator.getDefault().getPreferenceStore().getBoolean(OutputPreferencePage.LINES)) {
               String line = getColumnText(-2);

               if (line != null) {
                  str.append("[L:" + line + "] ", StyledString.DECORATIONS_STYLER);
               }
            }
         }

         if (text != null) {
            if (getChildFails() > 0) {
               str.append(text, redStyler);
            } else if (getChildPasses() > 0) {
               str.append(text, greenStyler);
            } else if (softHighlight) {
               str.append(text, blueStyler);
            } else {
               str.append(text);
            }
         }
         columnToStyledText.put(column, str);
      }
      return str;
   }

   @Override
   public String getColumnText(int column) {
      return columnToText.get(column);
   }

   @Override
   public void setColumnText(int column, String text) {
      columnToText.put(column, text);
   }

   @Override
   public void setStyledString(int column, StyledString string) {
      columnToStyledText.put(column, string);
   }

   @Override
   public void childTestPointResult(boolean pass) {
      if (pass) {
         this.pass++;
      } else {
         this.fail++;
      }
   }

   @Override
   public int getChildFails() {
      return fail;
   }

   @Override
   public int getChildPasses() {
      return pass;
   }

   @Override
   public void setSoftHighlight(boolean highlight) {
      this.softHighlight = highlight;
   }

   public void setField(String key, String value) {
      fieldValues.put(key, value);
   }

   public String getField(String key) {
      String returnVal = fieldValues.get(key);
      if (returnVal == null) {
         return "";
      } else {
         return returnVal;
      }
   }

   public void addFieldListValue(String key, String value) {
      List<String> values = fieldListValues.get(key);
      if (values == null) {
         values = new ArrayList<>();
         fieldListValues.put(key, values);
      }
      values.add(value);
   }

   public String getFieldListValuesString(String key) {
      List<String> values = fieldListValues.get(key);
      if (values == null) {
         return "";
      } else {
         StringBuilder sb = new StringBuilder();
         for (int i = 0; i < values.size(); i++) {
            sb.append(values.get(i));
            if (i + 1 < values.size()) {
               sb.append(", ");
            }
         }
         return sb.toString();
      }
   }

   public void generateTitle() {
      String title = String.format("%s - %s", getField("Name"), getField("Number"));
      setSecondColumn(title);
   }
   
   public void dispose(){
      children.clear();
      columnToText.clear();
      columnToStyledText.clear();
      fieldValues.clear();
      fieldListValues.clear();
   }
   
   @Override
   public void finalize(){
      try {
         super.finalize();
      } catch (Throwable e) {
         e.printStackTrace();
      }
   }
}
