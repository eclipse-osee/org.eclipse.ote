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

package org.eclipse.osee.ote.ui.output.convert;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.ote.core.framework.saxparse.IBaseSaxElementListener;
import org.eclipse.osee.ote.core.framework.saxparse.OteSaxHandler;
import org.eclipse.osee.ote.core.framework.saxparse.elements.CheckGroupData;
import org.eclipse.osee.ote.core.framework.saxparse.elements.InfoData;
import org.eclipse.osee.ote.core.framework.saxparse.elements.InfoGroupData;
import org.eclipse.osee.ote.core.framework.saxparse.elements.OteLogData;
import org.eclipse.osee.ote.core.framework.saxparse.elements.StacktraceData;
import org.eclipse.osee.ote.ui.output.Activator;
import org.eclipse.osee.ote.ui.output.OteOutputImage;
import org.eclipse.osee.ote.ui.output.editors.IOutputDataCallback;
import org.eclipse.osee.ote.ui.output.editors.NavigateToFile;
import org.eclipse.osee.ote.ui.output.tree.items.BaseOutfileTreeItem;
import org.eclipse.osee.ote.ui.output.tree.items.IOutfileTreeItem;
import org.eclipse.osee.ote.ui.output.tree.items.OutfileRowType;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IFileEditorInput;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class CondenseOutfile {
   private final IFileEditorInput input;
   private IOutputDataCallback callback;

   private final BaseOutfileTreeItem rootScriptItem = new BaseOutfileTreeItem(OutfileRowType.unknown);
   private BaseOutfileTreeItem currentScriptItem = rootScriptItem;
   private boolean isInTestCase = false;
   private int currentTestCasePass = 0;
   private int currentTestCaseFail = 0;

   public CondenseOutfile(IFileEditorInput input) {
      this.input = input;
   }

   private void relateAndSetCurrent(BaseOutfileTreeItem newOne) {
      currentScriptItem.getChildren().add(newOne);
      newOne.setParent(currentScriptItem);
      currentScriptItem = newOne;
   }

   private void backUpTheTree() {
      currentScriptItem.cacheColumnStyledString();
      currentScriptItem = (BaseOutfileTreeItem) currentScriptItem.getParent();
   }

   public void run(IProgressMonitor monitor) throws Exception {

      long time = System.currentTimeMillis();

      monitor.setTaskName(String.format("Computing overview information for [%s].", input.getName()));

      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      OteSaxHandler handler = new OteSaxHandler();
      xmlReader.setContentHandler(handler);
      xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler); // This is the important part

      handler.getHandler("ScriptInit").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            currentScriptItem.setColumnText(1,
               String.format("ScriptInit      Pass: %d Fail: %d", currentTestCasePass, currentTestCaseFail));
            currentTestCasePass = 0;
            currentTestCaseFail = 0;
            backUpTheTree();
            isInTestCase = false;
         }

         @Override
         public void onStartElement(Object obj) {
            BaseOutfileTreeItem init = new BaseOutfileTreeItem(OutfileRowType.scriptinit);
            init.setColumnText(1, "ScriptInit");
            relateAndSetCurrent(init);
            isInTestCase = true;
         }
      });
      handler.getHandler("TestCase").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            currentScriptItem.generateTitle();
            currentScriptItem.setColumnText(1, String.format("%s      Pass: %d Fail: %d",
               currentScriptItem.getField("Name"), currentTestCasePass, currentTestCaseFail));
            currentTestCasePass = 0;
            currentTestCaseFail = 0;
            backUpTheTree();
            isInTestCase = false;
         }

         @Override
         public void onStartElement(Object obj) {
            BaseOutfileTreeItem testCase = new BaseOutfileTreeItem(OutfileRowType.testcase);
            relateAndSetCurrent(testCase);
            isInTestCase = true;
         }
      });
      handler.getHandler("Tracability").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            isInTestCase = true;
         }

         @Override
         public void onStartElement(Object obj) {
            isInTestCase = false;
         }
      });
      handler.getHandler("Trace").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            currentScriptItem.setColumnText(
               0,
               String.format("%s.%s", currentScriptItem.getField("ObjectName"),
                  currentScriptItem.getField("MethodName")));
            currentScriptItem.setColumnText(1,
               String.format("(%s)", currentScriptItem.getFieldListValuesString("ArgumentValue")));
            if (currentScriptItem.getField("MethodName").startsWith("set")) {
               currentScriptItem.setSoftHighlight(true);
            }
            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            BaseOutfileTreeItem item = new BaseOutfileTreeItem(OutfileRowType.method);
            relateAndSetCurrent(item);
         }
      });
      handler.getHandler("Support").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            currentScriptItem.setColumnText(1, "Message");
            currentScriptItem.setColumnText(2, currentScriptItem.getField("Message"));
            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            BaseOutfileTreeItem item = new BaseOutfileTreeItem(OutfileRowType.method);
            item.setColumnText(0, "Support");
            relateAndSetCurrent(item);
         }
      });
      handler.getHandler("Name").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            currentScriptItem.setField("Name", obj.toString());
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("Message").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            currentScriptItem.setField("Message", obj.toString());
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("Number").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            currentScriptItem.setField("Number", obj.toString());
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("ObjectName").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            currentScriptItem.setField("ObjectName", obj.toString());
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("MethodName").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            currentScriptItem.setField("MethodName", obj.toString());
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("Location").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (!isInTestCase || currentScriptItem.getType() == OutfileRowType.testpoint) {
               return;
            }
            currentScriptItem.setColumnText(1, String.format("Time: [%s ms]", currentScriptItem.getField("Time")));

            String line = "";

            for (IOutfileTreeItem child : currentScriptItem.getChildren()) {
               if (child.getType() == OutfileRowType.stacktrace && currentScriptItem.getParent().getType() != OutfileRowType.testpoint) {
                  boolean nolinefound = true;
                  String source = child.getColumnText(0);
                  if (nolinefound && source.contains(callback.getScriptName())) {
                     nolinefound = false;

                     IOutfileTreeItem update = child.getParent();
                     while (update != null) {
                        line = child.getColumnText(1);
                        update.setLineNumber(line);
                        update.setRunnable(new NavigateToFile(source, child.getColumnText(1)));
                        update = update.getParent();
                     }
                  }
                  if (!nolinefound) {
                     currentScriptItem.getParent().setColumnText(-2, line);
                  }
                  currentScriptItem.getParent().setColumnText(-1, currentScriptItem.getField("Time"));
               }
            }

            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
            if (!isInTestCase || currentScriptItem.getType() == OutfileRowType.testpoint) {
               return;
            }
            BaseOutfileTreeItem item = new BaseOutfileTreeItem(OutfileRowType.location);
            item.setColumnText(0, "Location");
            relateAndSetCurrent(item);
         }
      });
      handler.getHandler("OteLog").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            currentScriptItem.setColumnText(2, currentScriptItem.getField("Message"));
            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            OteLogData data = (OteLogData) obj;
            BaseOutfileTreeItem child =
               new BaseOutfileTreeItem(OutfileRowType.log, "Log", data.getLevel(), data.getLogger(), null);
            relateAndSetCurrent(child);
         }
      });
      handler.getHandler("Time").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            currentScriptItem.setField("Time", obj.toString());
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("Stacktrace").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (!isInTestCase || currentScriptItem.getType() == OutfileRowType.testpoint) {
               return;
            }
            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
            if (!isInTestCase || currentScriptItem.getType() == OutfileRowType.testpoint) {
               return;
            }
            BaseOutfileTreeItem item = new BaseOutfileTreeItem(OutfileRowType.stacktrace);
            StacktraceData data = (StacktraceData) obj;
            item.setColumnText(1, data.getLine());
            item.setColumnText(0, data.getSource());
            item.setRunnable(new NavigateToFile(data.getSource(), data.getLine()));
            item.setLineNumber(data.getLine());
            relateAndSetCurrent(item);
         }
      });
      handler.getHandler("Argument").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
         }

         @Override
         public void onStartElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
         }
      });
      handler.getHandler("Value").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            currentScriptItem.addFieldListValue("ArgumentValue", obj.toString());
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("TestPoint").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            if (currentScriptItem.getField("Result").equalsIgnoreCase("PASSED")) {
               currentTestCasePass++;
            } else {
               currentTestCaseFail++;
            }
            currentScriptItem.setItemKey("TP" + currentScriptItem.getField("Number"));
            currentScriptItem.setColumnText(0, String.format("TestPoint:%s", currentScriptItem.getField("Number")));
            currentScriptItem.setColumnText(1, currentScriptItem.getField("Result"));

            // do some trickery to propagate the passfail image up
            // the tree
            Image passFail = currentScriptItem.getImage();
            boolean pass = false;
            if (passFail == ImageManager.getImage(OteOutputImage.PASS)) {
               pass = true;
            }
            if (passFail != null) {
               IOutfileTreeItem item = currentScriptItem.getParent();
               while (item != null) {
                  item.setImage(passFail);
                  item.childTestPointResult(pass);
                  item = item.getParent();
               }
            }

            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            BaseOutfileTreeItem item = new BaseOutfileTreeItem(OutfileRowType.testpoint);
            item.setColumnText(0, "TestPoint");
            relateAndSetCurrent(item);
         }
      });
      handler.getHandler("CheckPoint").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (!isInTestCase) {
               return;
            }

            currentScriptItem.setColumnText(1, currentScriptItem.getField("TestPointName"));
            currentScriptItem.setColumnText(2, String.format("Exp: %s", currentScriptItem.getField("Expected")));
            currentScriptItem.setColumnText(3, String.format("Act: %s", currentScriptItem.getField("Actual")));
            currentScriptItem.setColumnText(
               4,
               String.format("%s ms [%s MT]", currentScriptItem.getField("ElapsedTime"),
                  currentScriptItem.getField("NumberOfTransmissions")));

            if (currentScriptItem.getParent().getType() == OutfileRowType.testpoint) {
               IOutfileTreeItem updateme = currentScriptItem.getParent();
               while (updateme != null) {
                  for (int i = 2; i < 5; i++) {
                     updateme.setColumnText(i, currentScriptItem.getColumnText(i));
                  }
                  updateme = updateme.getParent();
               }
            }

            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            BaseOutfileTreeItem item = new BaseOutfileTreeItem(OutfileRowType.checkpoint);
            item.setColumnText(0, "CheckPoint");
            relateAndSetCurrent(item);
         }
      });
      handler.getHandler("CheckGroup").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            currentScriptItem.setColumnText(1,
               String.format("%s [%s]", currentScriptItem.getField("GroupName"), currentScriptItem.getField("Mode")));
            currentScriptItem.setColumnText(2, currentScriptItem.getField("Result"));
            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            BaseOutfileTreeItem item = new BaseOutfileTreeItem(OutfileRowType.checkgroup);
            item.setColumnText(0, "CheckGroup");
            CheckGroupData data = (CheckGroupData) obj;
            item.setField("Mode", data.getMode());
            relateAndSetCurrent(item);
         }
      });
      handler.getHandler("RetryGroup").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            currentScriptItem.setColumnText(1, String.format("%s", currentScriptItem.getField("GroupName")));
            currentScriptItem.setColumnText(2, currentScriptItem.getField("Result"));
            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            BaseOutfileTreeItem item = new BaseOutfileTreeItem(OutfileRowType.retrygroup);
            item.setColumnText(0, "RetryGroup");
            relateAndSetCurrent(item);
         }
      });
      handler.getHandler("Result").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            currentScriptItem.setField("Result", obj.toString());
            if ("PASSED".equals(obj)) {
               currentScriptItem.setImage(ImageManager.getImage(OteOutputImage.PASS));
               currentScriptItem.childTestPointResult(true);
            } else if ("FAILED".equals(obj)) {
               currentScriptItem.setImage(ImageManager.getImage(OteOutputImage.FAIL));
               currentScriptItem.childTestPointResult(false);
            }
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("TestPointName").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            currentScriptItem.setField("TestPointName", obj.toString());
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("Expected").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            currentScriptItem.setField("Expected", spaceProcessing(obj.toString()));
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("Actual").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            currentScriptItem.setField("Actual", spaceProcessing(obj.toString()));
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("ElapsedTime").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            currentScriptItem.setField("ElapsedTime", obj.toString());
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("TestPointName").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            currentScriptItem.setField("TestPointName", obj.toString());
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("NumberOfTransmissions").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            currentScriptItem.setField("NumberOfTransmissions", obj.toString());
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("GroupName").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            currentScriptItem.setField("GroupName", obj.toString());
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("InfoGroup").addListener(new IBaseSaxElementListener() {

         @Override
         public void onEndElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            BaseOutfileTreeItem item = new BaseOutfileTreeItem(OutfileRowType.infogroup);
            item.setColumnText(0, "InfoGroup");
            item.setColumnText(1, "InfoGroup");
            InfoGroupData data = (InfoGroupData) obj;
            item.setColumnText(2, data.getTitle());
            relateAndSetCurrent(item);
         }
      });
      handler.getHandler("SoftKeyInfoGroup").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            BaseOutfileTreeItem item = new BaseOutfileTreeItem(OutfileRowType.infogroup);
            item.setColumnText(0, "SoftKeyInfoGroup");
            item.setColumnText(1, "SoftKeyInfoGroup");
            relateAndSetCurrent(item);
         }
      });
      handler.getHandler("Info").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            currentScriptItem.setColumnText(2, obj.toString());
            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            BaseOutfileTreeItem item = new BaseOutfileTreeItem(OutfileRowType.infogroup);
            InfoData data = (InfoData) obj;
            item.setColumnText(0, "Info");
            item.setColumnText(1, data.getTitle());
            relateAndSetCurrent(item);
         }
      });
      handler.getHandler("AdditionalInfo").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            currentScriptItem.setColumnText(1, String.format("MsgType: %s", currentScriptItem.getField("Type")));
            currentScriptItem.setColumnText(2, String.format("MsgName: %s", currentScriptItem.getField("Name")));
            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            BaseOutfileTreeItem item = new BaseOutfileTreeItem(OutfileRowType.additionalinfo);
            item.setColumnText(0, "AdditionalInfo");
            relateAndSetCurrent(item);
         }
      });
      handler.getHandler("Type").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (!isInTestCase) {
               return;
            }
            currentScriptItem.setField("Type", obj.toString());
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });

      xmlReader.parse(new InputSource(input.getFile().getContents()));

      callback.addDetailsData(rootScriptItem);

      long all = System.currentTimeMillis() - time;

      OseeLog.logf(Activator.class, Level.INFO,
         "It took %d ms total to process the details page of %s.", all, input.getName());

   }

   private String spaceProcessing(String expected) {
      int index = 0, preCount = 0, postCount = 0;
      while (index < expected.length() && expected.charAt(index) == ' ') {
         index++;
         preCount++;
      }
      index = expected.length() - 1;
      while (index >= 0 && expected.charAt(index) == ' ') {
         index--;
         postCount++;
      }

      expected = expected.trim();
      if (preCount > 0) {
         expected = String.format("#sp%d#%s", preCount, expected);
      }
      if (postCount > 0) {
         expected = String.format("%s#sp%d#", expected, postCount);
      }

      return expected;
   }
}
