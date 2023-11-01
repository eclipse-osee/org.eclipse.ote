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
import java.util.List;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osee.framework.ui.ws.AWorkspace;
import org.eclipse.osee.ote.core.framework.saxparse.elements.StacktraceData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.MarkerUtilities;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class TestPointSummary extends BaseOutfileTreeItem {

   private String actual;
   private String expected;
   private String requirement;
   private boolean isPassed;
   private String elpasedTime;
   private String number;
   private String mode;
   private boolean isUnmodifiedRoot = false;
   private String groupName;
   private String groupType;
   private final List<StacktraceData> stacktrace = new ArrayList<>();
   private boolean isTopLevelTestPoint;
   
   public TestPointSummary(IOutfileTreeItem item, String label, String expected, String actual, String elapsedTime, String requirement, Image image) {
      super(OutfileRowType.testpoint, item, label, image);
      this.expected = expected;
      this.actual = actual;
      this.elpasedTime = elapsedTime;
      this.requirement = requirement;
   }
   
   public void setTopLevelTestPoint(boolean isTopLevelTestPoint) {
      this.isTopLevelTestPoint = isTopLevelTestPoint;
   }
   
   public boolean isTopLevelTestPoint() {
      return isTopLevelTestPoint;
   }

   public TestPointSummary() {
      super(OutfileRowType.testpoint);
   }

   public TestPointSummary(IOutfileTreeItem item, String label, Image image) {
      super(OutfileRowType.testpoint, item, label, image);
   }

   public String getActual() {
      return actual;
   }

   public String getElapsedTime() {
      return elpasedTime;
   }

   public String getExpected() {
      return expected;
   }

   public String getNumber() {
      return number;
   }
   
   public String getRequirement() {
      return requirement;
   }

   public void setAsParentTPItem(String number) {
      this.number = number;
      this.isUnmodifiedRoot = true;
   }

   public void setActual(String actual) {
      this.actual = actual;
   }

   public void setExpected(String expected) {
      this.expected = expected;
   }

   public void setElpasedTime(String elpasedTime) {
      this.elpasedTime = elpasedTime;
   }
   
   public void setRequirement(String requirement) {
      this.requirement = requirement;
   }

   public void addRequirment(String requirement) {
      if(this.requirement == null) {
         this.requirement = requirement;         
      } else {
         this.requirement += ", " + requirement;
      }
   }
   
   public void setNumber(String number) {
      this.number = number;
   }

   public void setUnmodifiedRoot(boolean isUnmodifiedRoot) {
      this.isUnmodifiedRoot = isUnmodifiedRoot;
   }

   public boolean isUnmodifiedRoot() {
      return this.isUnmodifiedRoot;
   }
   
   public boolean isPassed() {
      return isPassed;
   }

   public void setPassed(boolean isPassed) {
      this.isPassed = isPassed;
   }

   public void set(String label, String expected, String actual, String elapsedTime) {
      this.isUnmodifiedRoot = false;
      setFirstColumn(label);
      this.expected = expected;
      this.actual = actual;
      this.elpasedTime = elapsedTime;
   }

   public void set(String label) {
      this.isUnmodifiedRoot = false;
      setFirstColumn(label);
   }

   public void setMode(String mode) {
      this.mode = mode;
   }

   public void setGroupName(String groupName) {
      this.groupName = groupName;
   }

   public void generateLabel() {
      if (groupName != null && mode != null && groupType != null) {
         setFirstColumn(String.format("%s [%s] { %s }", groupName, mode, groupType));
      }
      if (number != null) {
         setFirstColumn(String.format("TP %s", number));
      }
   }

   public void setGroupType(String groupType) {
      this.groupType = groupType;
   }

   public void specialCopy(TestPointSummary childItem) {
      for (IOutfileTreeItem item : childItem.getChildren()) {
         getChildren().add(item);
      }
      setExpected(childItem.expected);
      setActual(childItem.actual);
      setElpasedTime(childItem.elpasedTime);
      setFirstColumn(String.format("%s { %s }", getColumnText(0), childItem.getFirstColumn()));
      setRequirement(childItem.getRequirement());
   }

   public void addStackTrace(StacktraceData obj) {
      stacktrace.add(obj);
   }

   public void processStacktrace(String scriptName) {
      for (final StacktraceData stack : stacktrace) {
         if (stack.getSource().contains(scriptName)) {
            setLineNumber(stack.getLine());

            Runnable runme = new Runnable() {

               @Override
               public void run() {
                  String file = stack.getSource().substring(stack.getSource().lastIndexOf(".") + 1);
                  int innerMarker = file.indexOf("$");
                  if (innerMarker > 0) {
                     file = file.substring(0, file.indexOf("$"));
                  }
                  file += ".java";
                  try {
                     int linenumber = Integer.parseInt(stack.getLine());
                     IResource resource = AWorkspace.findWorkspaceFile(file);
                     if (resource != null) {

                        IMarker marker;
                        marker = resource.createMarker(IMarker.MARKER);
                        MarkerUtilities.setLineNumber(marker, linenumber);
                        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                        IDE.openEditor(page, marker, true);
                        marker.delete();
                     }
                  } catch (CoreException ex) {
                     // Do Nothing
                  }
               }

            };

            setRunnable(runme);
            return;

         }
      }
   }


}
