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

package org.eclipse.osee.ote.ui.output.tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerTextFilter;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;

/**
 * @author Donald G. Dunne
 * @author Andy Jury
 */
public class OutfileXViewerTextFilter extends XViewerTextFilter {

   private Pattern textPattern;
   private Matcher matcher;
   private final Map<String, Pattern> colIdToPattern = new HashMap<String, Pattern>();
   private static final Pattern EMPTY_STR_PATTERN = Pattern.compile("");
   private static final Pattern NOT_EMPTY_STR_PATTERN = Pattern.compile("^.+$");
   private final Set<Object> parentMatches = new HashSet<Object>();
   
   public OutfileXViewerTextFilter(XViewer xViewer) {
      super(xViewer);
   }
   /**
    * Setup all patterns for text and column text filters
    */
   @Override
   public void update() {
      parentMatches.clear();
      // Update text filter pattern
      if (!isValid(xViewer.getCustomizeMgr().getFilterText())) {
         textPattern = null;
      } else {
         int flags = Pattern.CASE_INSENSITIVE;
         if (!xViewer.getCustomizeMgr().isFilterTextRegularExpression()) {
            flags = Pattern.LITERAL | flags;
         }
         textPattern = Pattern.compile(xViewer.getCustomizeMgr().getFilterText(), flags);
      }
      // Update column filter patterns
      colIdToPattern.clear();
      for (String colId : xViewer.getCustomizeMgr().getColumnFilterData().getColIds()) {
         String colFilterText = xViewer.getCustomizeMgr().getColumnFilterText(colId);
         if (colFilterText != null) {
            boolean isNot = colFilterText.startsWith("!");
            if (isNot) {
               colFilterText = colFilterText.replaceFirst("^!", "");
            }
            // Handle != case  ^(.(?<!big))*$
            if (isNot) {
               if (colFilterText.equals("")) {
                  colIdToPattern.put(colId, NOT_EMPTY_STR_PATTERN);
               } else {
                  colIdToPattern.put(colId,
                     Pattern.compile("^(.(?<!" + colFilterText + "))*$", Pattern.CASE_INSENSITIVE));
               }
            }
            // Handle normal case
            else {
               if (colFilterText.equals("")) {
                  colIdToPattern.put(colId, EMPTY_STR_PATTERN);
               } else {
                  colIdToPattern.put(colId, Pattern.compile(
                     xViewer.getCustomizeMgr().getColumnFilterData().getFilterText(colId), Pattern.CASE_INSENSITIVE));
               }
            }
         }
      }
   }

   
   @Override
   public boolean select(Viewer viewer, Object parentElement, Object element) {
      if (textPattern == null && colIdToPattern.isEmpty()) {
         return true;
      }
      // If element matches, it's parent is added to this collection; it should always match so get full path shown
      if (parentMatches.contains(element)) {
         if (parentElement != null) {
            parentMatches.add(parentElement);
         }
         return true;
      }
      boolean match = true;
      // Must match all column filters or don't show
      for (String filteredColId : xViewer.getCustomizeMgr().getColumnFilterData().getColIds()) {
         XViewerColumn xCol = xViewer.getCustomizeMgr().getCurrentTableColumn(filteredColId);
         if (xCol.isShow() && colIdToPattern.keySet().contains(xCol.getId())) {
            String cellStr =
               xViewer.getColumnText(element, xViewer.getCustomizeMgr().getColumnNumFromXViewerColumn(xCol));
            if (cellStr != null) {
               matcher = colIdToPattern.get(xCol.getId()).matcher(cellStr);
               if (!matcher.find()) {
                  return false;
               }
            }
         }
      }
      if (!match) {
         return false;
      }

      // Must match at least one column for filter text
      if (textPattern == null) {
         if (match && parentElement != null) {
            parentMatches.add(parentElement);
         }
         return match;
      }
      if (textPattern != null) {
         for (XViewerColumn xCol : xViewer.getCustomizeMgr().getCurrentTableColumns()) {
            if (xCol.isShow()) {
               // Check text filter
               String cellStr =
                  xViewer.getColumnText(element, xViewer.getCustomizeMgr().getColumnNumFromXViewerColumn(xCol));
               if (cellStr != null) {
                  matcher = textPattern.matcher(cellStr);
                  if (matcher.find()) {
                     if (parentElement != null) {
                        parentMatches.add(parentElement);
                     }
                     return true;
                  }
               }
            }
         }
      }
      
      //check for child match
      if(viewer instanceof ContentViewer){
         IContentProvider provider = ((ContentViewer)viewer).getContentProvider();
         if(provider instanceof ITreeContentProvider){
            Object[] children = ((ITreeContentProvider)provider).getChildren(element);
            for(int i = 0; i < children.length; i++){
               if(select(viewer, element, children[i])){
                  return true;
               }
            }
         }
      }
      
      return false;
   }
   
   private boolean isValid(String value) {
      return value != null && value.length() > 0;
   }
   
}
