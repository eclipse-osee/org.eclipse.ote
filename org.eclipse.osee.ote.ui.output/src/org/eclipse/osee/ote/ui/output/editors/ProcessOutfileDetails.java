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

package org.eclipse.osee.ote.ui.output.editors;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.ws.AWorkspace;
import org.eclipse.osee.ote.core.framework.saxparse.IBaseSaxElementListener;
import org.eclipse.osee.ote.core.framework.saxparse.OteSaxHandler;
import org.eclipse.osee.ote.core.framework.saxparse.elements.CheckGroupData;
import org.eclipse.osee.ote.core.framework.saxparse.elements.InfoData;
import org.eclipse.osee.ote.core.framework.saxparse.elements.InfoGroupData;
import org.eclipse.osee.ote.core.framework.saxparse.elements.OteLogData;
import org.eclipse.osee.ote.core.framework.saxparse.elements.StacktraceData;
import org.eclipse.osee.ote.ui.markers.MarkerPlugin;
import org.eclipse.osee.ote.ui.output.Activator;
import org.eclipse.osee.ote.ui.output.OteOutputImage;
import org.eclipse.osee.ote.ui.output.tree.items.BaseOutfileTreeItem;
import org.eclipse.osee.ote.ui.output.tree.items.IOutfileTreeItem;
import org.eclipse.osee.ote.ui.output.tree.items.OutfileRowType;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class ProcessOutfileDetails implements IExceptionableRunnable {

   private final IEditorInput input;
   private final IOutputDataCallback callback;
   private boolean isInTraceEnd = false;
   private final BaseOutfileTreeItem rootScriptItem = new BaseOutfileTreeItem(OutfileRowType.unknown);
   private BaseOutfileTreeItem currentScriptItem = rootScriptItem;
   private boolean isInAttention = false;
   private int currentTestCasePass = 0;
   private int currentTestCaseFail = 0;
   private boolean largeFile = false;

   public ProcessOutfileDetails(IEditorInput input, IOutputDataCallback callback) {
      this.input = input;
      this.callback = callback;
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

   /**
    * This method is used to ensure the Location child isn't added twice to the same item.
    * 
    * @return
    */
   private boolean currentItemIsEmbeddedTestPoint() {
      OutfileRowType currentType = currentScriptItem.getType();
      OutfileRowType parentType = currentScriptItem.getParent().getType();
      return (currentType == OutfileRowType.testpoint) && (parentType != OutfileRowType.testcase);
   }

   private static final int _1_MB = 1048576;
   private static final int _20_MB = _1_MB * 20;

   @Override
   public IStatus run(IProgressMonitor monitor) throws Exception {
      InputStream inputStream = null;
      if (input instanceof IFileEditorInput) {
         IFile inputFile = ((IFileEditorInput) input).getFile();
         inputStream = inputFile.getContents();

         File outfile = AWorkspace.iFileToFile(inputFile);
         if (outfile.length() > _20_MB) {
            largeFile = true;
         }

         MarkerPlugin.addMarkers(inputFile);
      } else if (input instanceof FileStoreEditorInput) {
         inputStream = ((FileStoreEditorInput) input).getURI().toURL().openStream();
      } else {
         return Status.CANCEL_STATUS;
      }
      long time = System.currentTimeMillis();

      monitor.setTaskName(String.format("Computing overview information for [%s].", input.getName()));

      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      OteSaxHandler handler = new OteSaxHandler();
      xmlReader.setContentHandler(handler);
      xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler); // This is the important part

      handler.getHandler("ScriptInit").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            currentScriptItem.setColumnText(0, "ScriptInit");
            int totalPoints = currentTestCasePass + currentTestCaseFail;
            String tallyString = String.format("Total: %d Fail: %d", totalPoints, currentTestCaseFail);
            currentScriptItem.setColumnText(1, tallyString);
            currentScriptItem.setColumnText(2, "");
            currentScriptItem.setColumnText(3, "");
            currentScriptItem.setColumnText(4, "");
            currentTestCasePass = 0;
            currentTestCaseFail = 0;
            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
            BaseOutfileTreeItem init = new BaseOutfileTreeItem(OutfileRowType.scriptinit);
            init.setColumnText(1, "ScriptInit");
            relateAndSetCurrent(init);
         }
      });
      handler.getHandler("TestCase").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            currentScriptItem.generateTitle();
            int totalPoints = currentTestCasePass + currentTestCaseFail;
            currentScriptItem.setColumnText(0, String.format("%s", currentScriptItem.getField("Name")));
            currentScriptItem.setColumnText(1, String.format("Total: %d Fail: %d", totalPoints, currentTestCaseFail));
            currentScriptItem.setColumnText(2, "");
            currentScriptItem.setColumnText(3, "");
            currentScriptItem.setColumnText(4, "");
            currentTestCasePass = 0;
            currentTestCaseFail = 0;
            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
            BaseOutfileTreeItem testCase = new BaseOutfileTreeItem(OutfileRowType.testcase);
            relateAndSetCurrent(testCase);
         }
      });
      handler.getHandler("Tracability").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            // Intentionally Empty Block
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("Trace").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            currentScriptItem.setColumnText(0, String.format("%s.%s", currentScriptItem.getField("ObjectName"),
               currentScriptItem.getField("MethodName")));
            currentScriptItem.setColumnText(1,
               String.format("(%s)", currentScriptItem.getFieldListValuesString("ArgumentValue")));

            String returnValue = currentScriptItem.getFieldListValuesString("ReturnValue");
            if (returnValue != null && returnValue.length() > 0) {
               currentScriptItem.setColumnText(2, returnValue);
            }

            if (currentScriptItem.getField("MethodName").startsWith("set")) {
               currentScriptItem.setSoftHighlight(true);
            }
            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
            BaseOutfileTreeItem item = new BaseOutfileTreeItem(OutfileRowType.method);
            relateAndSetCurrent(item);
         }
      });
      handler.getHandler("TraceEnd").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            isInTraceEnd = false;
         }

         @Override
         public void onStartElement(Object obj) {
            isInTraceEnd = true;
         }
      });
      handler.getHandler("ReturnValue").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (isInTraceEnd) {
               String returnValue = obj.toString();
               if (returnValue != null) {
                  returnValue = returnValue.trim();
                  if (returnValue.length() > 0) {
                     currentScriptItem.addFieldListValue("ReturnValue", obj.toString());
                  }
               }
            }
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("Support").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            currentScriptItem.setColumnText(1, "Message");
            currentScriptItem.setColumnText(2, currentScriptItem.getField("Message"));
            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
            BaseOutfileTreeItem item = new BaseOutfileTreeItem(OutfileRowType.method);
            item.setColumnText(0, "Support");
            relateAndSetCurrent(item);
         }
      });
      handler.getHandler("Attention").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            backUpTheTree();
            isInAttention = false;
         }

         @Override
         public void onStartElement(Object obj) {
            isInAttention = true;
            BaseOutfileTreeItem item = new BaseOutfileTreeItem(OutfileRowType.info);
            item.setColumnText(0, "Info");
            relateAndSetCurrent(item);
         }
      });
      handler.getHandler("Name").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
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
            if (isInAttention) {
               String str = obj.toString();
               str = str.replaceAll("\\n", "");
               currentScriptItem.setColumnText(1, str);
            } else {
               currentScriptItem.setField("Message", obj.toString());
            }
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("Number").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
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
            if (currentItemIsEmbeddedTestPoint()) {
               return;
            }
            currentScriptItem.setColumnText(1, String.format("Time: [%s ms]", currentScriptItem.getField("Time")));

            String line = "";

            for (IOutfileTreeItem child : currentScriptItem.getChildren()) {
               if (child.getType() == OutfileRowType.stacktrace) {
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
            if (currentItemIsEmbeddedTestPoint()) {
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
            currentScriptItem.setColumnText(2, currentScriptItem.getField("Message"));
            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
            OteLogData data = (OteLogData) obj;
            BaseOutfileTreeItem child =
               new BaseOutfileTreeItem(OutfileRowType.log, "Log", data.getLevel(), data.getLogger(), null);
            relateAndSetCurrent(child);
         }
      });
      handler.getHandler("Time").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
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
            if (largeFile || currentItemIsEmbeddedTestPoint()) {
               return;
            }
            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
            if (largeFile || currentItemIsEmbeddedTestPoint()) {
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
            // Intentionally Empty Block
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("Value").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
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
            String resultField = currentScriptItem.getField("Result");
            if (resultField.equalsIgnoreCase("PASSED")) {
               currentTestCasePass++;
            } else {
               currentTestCaseFail++;
            }
            currentScriptItem.setItemKey("TP" + currentScriptItem.getField("Number"));
            currentScriptItem.setColumnText(0, String.format("TestPoint:%s", currentScriptItem.getField("Number")));
            currentScriptItem.setColumnText(1, resultField);

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
            BaseOutfileTreeItem item = new BaseOutfileTreeItem(OutfileRowType.testpoint);
            item.setColumnText(0, "TestPoint");
            relateAndSetCurrent(item);
         }
      });
      handler.getHandler("CheckPoint").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            currentScriptItem.setColumnText(1, currentScriptItem.getField("TestPointName"));
            currentScriptItem.setColumnText(2, String.format("Exp: %s", currentScriptItem.getField("Expected")));
            currentScriptItem.setColumnText(3, String.format("Act: %s", currentScriptItem.getField("Actual")));
            currentScriptItem.setColumnText(4, String.format("%s ms [%s MT]", currentScriptItem.getField("ElapsedTime"),
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
            BaseOutfileTreeItem item = new BaseOutfileTreeItem(OutfileRowType.checkpoint);
            item.setColumnText(0, "CheckPoint");
            relateAndSetCurrent(item);
         }
      });
      handler.getHandler("CheckGroup").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            currentScriptItem.setColumnText(1,
               String.format("%s [%s]", currentScriptItem.getField("GroupName"), currentScriptItem.getField("Mode")));
            currentScriptItem.setColumnText(2, currentScriptItem.getField("Result"));
            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
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
            currentScriptItem.setColumnText(1, String.format("%s", currentScriptItem.getField("GroupName")));
            currentScriptItem.setColumnText(2, currentScriptItem.getField("Result"));
            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
            BaseOutfileTreeItem item = new BaseOutfileTreeItem(OutfileRowType.retrygroup);
            item.setColumnText(0, "RetryGroup");
            relateAndSetCurrent(item);
         }
      });
      handler.getHandler("Result").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
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
      handler.getHandler("Throwable").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            String[] lines = obj.toString().split("\r\n");
            if (lines.length == 1) {
               lines = obj.toString().split("\n");
            }
            if (lines != null) {
               for (int i = 0; i < lines.length; i++) {
                  BaseOutfileTreeItem item = new BaseOutfileTreeItem(OutfileRowType.log);
                  item.setColumnText(1, lines[i]);
                  relateAndSetCurrent(item);
                  backUpTheTree();
               }
            }
            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
            BaseOutfileTreeItem item = new BaseOutfileTreeItem(OutfileRowType.log);
            item.setColumnText(0, "Exception");
            relateAndSetCurrent(item);
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
            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
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
            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
            BaseOutfileTreeItem item = new BaseOutfileTreeItem(OutfileRowType.infogroup);
            item.setColumnText(0, "SoftKeyInfoGroup");
            item.setColumnText(1, "SoftKeyInfoGroup");
            relateAndSetCurrent(item);
         }
      });
      handler.getHandler("Info").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            currentScriptItem.setColumnText(2, obj.toString());
            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
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
            currentScriptItem.setColumnText(1, String.format("MsgType: %s", currentScriptItem.getField("Type")));
            currentScriptItem.setColumnText(2, String.format("MsgName: %s", currentScriptItem.getField("Name")));
            backUpTheTree();
         }

         @Override
         public void onStartElement(Object obj) {
            BaseOutfileTreeItem item = new BaseOutfileTreeItem(OutfileRowType.additionalinfo);
            item.setColumnText(0, "AdditionalInfo");
            relateAndSetCurrent(item);
         }
      });
      handler.getHandler("Type").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            currentScriptItem.setField("Type", obj.toString());
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });

      xmlReader.parse(new InputSource(inputStream));

      callback.addDetailsData(rootScriptItem);

      long all = System.currentTimeMillis() - time;

      OseeLog.logf(Activator.class, Level.INFO, "It took %d ms total to process the details page of %s.", all,
         input.getName());
      return Status.OK_STATUS;
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
