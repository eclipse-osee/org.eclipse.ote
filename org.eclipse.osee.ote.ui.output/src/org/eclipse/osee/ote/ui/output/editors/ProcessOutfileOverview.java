/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
import org.eclipse.osee.ote.core.framework.saxparse.elements.ConfigData;
import org.eclipse.osee.ote.core.framework.saxparse.elements.UutErrorEntryData;
import org.eclipse.osee.ote.core.framework.saxparse.elements.OteLogData;
import org.eclipse.osee.ote.core.framework.saxparse.elements.QualificationData;
import org.eclipse.osee.ote.core.framework.saxparse.elements.RetryGroupData;
import org.eclipse.osee.ote.core.framework.saxparse.elements.ScriptVersionData;
import org.eclipse.osee.ote.core.framework.saxparse.elements.StacktraceData;
import org.eclipse.osee.ote.core.framework.saxparse.elements.SummaryData;
import org.eclipse.osee.ote.core.framework.saxparse.elements.SystemInfoData;
import org.eclipse.osee.ote.core.framework.saxparse.elements.TestPointResultsData;
import org.eclipse.osee.ote.core.framework.saxparse.elements.TimeSummaryData;
import org.eclipse.osee.ote.core.framework.saxparse.elements.UserData;
import org.eclipse.osee.ote.core.framework.saxparse.elements.VersionData;
import org.eclipse.osee.ote.ui.markers.MarkerPlugin;
import org.eclipse.osee.ote.ui.output.Activator;
import org.eclipse.osee.ote.ui.output.OteOutputImage;
import org.eclipse.osee.ote.ui.output.tree.items.BaseOutfileTreeItem;
import org.eclipse.osee.ote.ui.output.tree.items.IOutfileTreeItem;
import org.eclipse.osee.ote.ui.output.tree.items.OutfileRowType;
import org.eclipse.osee.ote.ui.output.tree.items.TestPointSummary;
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
public class ProcessOutfileOverview implements IExceptionableRunnable {

   private final IEditorInput input;
   private final IOutputDataCallback callback;

   private boolean runtimeVersion = false;
   private String runtimeVersions = "";
   private final BaseOutfileTreeItem rootTestPointSummaryItem = new BaseOutfileTreeItem(OutfileRowType.unknown);
   private BaseOutfileTreeItem currentTestpointSummaryItem = rootTestPointSummaryItem;
   private IOutfileTreeItem versionInformationItem;
   private IOutfileTreeItem uutLog;
   private UutErrorEntryData currentErrorEntryData;
   private IOutfileTreeItem oteLogRoot = new BaseOutfileTreeItem(OutfileRowType.unknown);
   private boolean isInsideOteLog = false;
   private boolean isInQualification = false;
   private final StringBuilder qualificationInfo = new StringBuilder();
   private int failCount;
   private boolean largeFile;
   private static final int _1_MB = 1048576;
   private static final int _20_MB = _1_MB * 20;

   public ProcessOutfileOverview(IEditorInput input, IOutputDataCallback callback) {
      this.input = input;
      this.callback = callback;
   }

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
      failCount = 0;
      long time = System.currentTimeMillis();
      monitor.setTaskName(String.format("Computing overview information for [%s].", input.getName()));

      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      OteSaxHandler handler = new OteSaxHandler();
      xmlReader.setContentHandler(handler);
      xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler); // This is the important part

      handler.getHandler("Config").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            // Intentionally Empty Block
         }

         @Override
         public void onStartElement(Object obj) {
            ConfigData data = (ConfigData) obj;
            callback.addOverviewData("Host Machine", data.getMachineName());
         }
      });

      handler.getHandler("ScriptVersion").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            // Intentionally Empty Block
         }

         @Override
         public void onStartElement(Object obj) {
            ScriptVersionData data = (ScriptVersionData) obj;
            callback.addOverviewData("Script Version",
               String.format("Last Changed Revision [ %s ] Last Author [ %s ] Last Modified [ %s ]", data.getRevision(),
                  data.getLastAuthor(), data.getLastModified()));
            // }
         }
      });
      handler.getHandler("User").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            // Intentionally Empty Block
         }

         @Override
         public void onStartElement(Object obj) {
            UserData data = (UserData) obj;
            callback.addOverviewData("User", data.getName());
         }
      });

      handler.getHandler("ScriptName").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            callback.addOverviewData("Script Name", obj.toString());
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("RuntimeVersions").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            runtimeVersion = false;
            callback.addOverviewData("Jar Versions", runtimeVersions);
         }

         @Override
         public void onStartElement(Object obj) {
            runtimeVersion = true;
         }
      });
      handler.getHandler("Version").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (runtimeVersion) {
               runtimeVersions = runtimeVersions + obj.toString() + "\n";
            }
         }

         @Override
         public void onStartElement(Object obj) {
            if (versionInformationItem != null) {
               VersionData data = (VersionData) obj;
               versionInformationItem.getChildren().add(
                  new BaseOutfileTreeItem(OutfileRowType.versionentry, data.getName(), data.getVersion(),
                     String.format("unitId[%s] underTest[%s]", data.getVersionUnit(), data.getUnderTest()), null));
            }
         }
      });
      handler.getHandler("SystemInfo").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            // Intentionally Empty Block
         }

         @Override
         public void onStartElement(Object obj) {
            if (obj instanceof SystemInfoData) {
               SystemInfoData data = (SystemInfoData) obj;
               callback.addOverviewData("OS",
                  String.format("%s %s %s", data.getOsName(), data.getOsVersion(), data.getOsArch()));
               callback.addOverviewData("OSEE Version", data.getOseeVersion());
               if (data.getOseeServerTitle() != null && data.getOseeServerTitle().length() > 0) {
                  callback.addOverviewData("Server Title", data.getOseeServerTitle());
               }
            }
         }
      });
      handler.getHandler("CurrentProcessor").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            // Intentionally Empty Block
         }

         @Override
         public void onStartElement(Object obj) {
            callback.addOverviewData("Current Processor", obj.toString());
         }
      });
      handler.getHandler("TimeSummary").addListener(new IBaseSaxElementListener() {

         @Override
         public void onEndElement(Object obj) {
            // Intentionally Empty Block
         }

         @Override
         public void onStartElement(Object obj) {
            if (obj instanceof TimeSummaryData) {
               TimeSummaryData data = (TimeSummaryData) obj;
               callback.addOverviewData("Time Info", String.format("Elapsed [ %s ]  Start [ %s ] Stop [ %s ]",
                  data.getElapsed(), data.getStartDate(), data.getEndDate()));
            }
         }

      });
      handler.getHandler("TestPointResults").addListener(new IBaseSaxElementListener() {

         @Override
         public void onEndElement(Object obj) {
            // Intentionally Empty Block
         }

         @Override
         public void onStartElement(Object obj) {
            if (obj instanceof TestPointResultsData) {
               TestPointResultsData data = (TestPointResultsData) obj;
               String fail = data.getFail();
               String pass = data.getPass();
               String aborted = data.getAborted();
               try {
                  int failedTestPoints = Integer.parseInt(fail);
                  int passedTestPoints = Integer.parseInt(pass);
                  int totalTestPoints = passedTestPoints + failedTestPoints;
                  boolean abort = false;
                  if (aborted != null && aborted.length() > 0) {
                     abort = Boolean.parseBoolean(aborted);
                  }
                  String results = "";

                  if (abort) {
                     results = String.format("ABORTED  -  Total[%d] Fail[%d]", totalTestPoints, failedTestPoints);
                  } else if (failedTestPoints > 0) {
                     results = String.format("FAILED  -  Total[%d] Fail[%d]", totalTestPoints, failedTestPoints);
                  } else {
                     results = String.format("PASSED  -  Total[%d] Fail[%d]", totalTestPoints, failedTestPoints);
                  }
                  callback.addOverviewData("Results", results);
                  callback.addSummaryHeader(results);
               } catch (NumberFormatException ex) {
                  // Intentionally Empty Block
               }
            }
         }

      });

      /*
       * The following code will compose the checkpoint summary tree structure
       */
      handler.getHandler("TestPoint").addListener(new IBaseSaxElementListener() {

         @Override
         public void onEndElement(Object obj) {
            if (currentTestpointSummaryItem instanceof TestPointSummary) {
               ((TestPointSummary) currentTestpointSummaryItem).generateLabel();
            }
            currentTestpointSummaryItem.setItemKey("TP" + currentTestpointSummaryItem.getField("Number"));
            currentTestpointSummaryItem = (BaseOutfileTreeItem) currentTestpointSummaryItem.getParent();
         }

         @Override
         public void onStartElement(Object obj) {
            TestPointSummary summary = new TestPointSummary();
            summary.setTopLevelTestPoint(true);
            currentTestpointSummaryItem.getChildren().add(summary);
            summary.setParent(currentTestpointSummaryItem);
            currentTestpointSummaryItem = summary;
         }
      });
      handler.getHandler("CheckPoint").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            currentTestpointSummaryItem = (BaseOutfileTreeItem) currentTestpointSummaryItem.getParent();
         }

         @Override
         public void onStartElement(Object obj) {
            if (currentTestpointSummaryItem instanceof TestPointSummary) {

               TestPointSummary summary = new TestPointSummary();
               summary.setTopLevelTestPoint(false);
               currentTestpointSummaryItem.getChildren().add(summary);
               summary.setParent(currentTestpointSummaryItem);
               currentTestpointSummaryItem = summary;
               summary.set("checkpoint");
            }
         }
      });
      handler.getHandler("CheckGroup").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (currentTestpointSummaryItem instanceof TestPointSummary) {
               ((TestPointSummary) currentTestpointSummaryItem).generateLabel();
            }
            currentTestpointSummaryItem = (BaseOutfileTreeItem) currentTestpointSummaryItem.getParent();
         }

         @Override
         public void onStartElement(Object obj) {
            if (currentTestpointSummaryItem instanceof TestPointSummary) {

               TestPointSummary summary = new TestPointSummary();
               summary.setTopLevelTestPoint(false);
               currentTestpointSummaryItem.getChildren().add(summary);
               summary.setParent(currentTestpointSummaryItem);
               currentTestpointSummaryItem = summary;
               summary.setGroupType("CheckGroup");
               summary.setMode(((CheckGroupData) obj).getMode());
            }
         }
      });
      handler.getHandler("RetryGroup").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (currentTestpointSummaryItem instanceof TestPointSummary) {
               ((TestPointSummary) currentTestpointSummaryItem).generateLabel();
            }
            currentTestpointSummaryItem = (BaseOutfileTreeItem) currentTestpointSummaryItem.getParent();

         }

         @Override
         public void onStartElement(Object obj) {
            if (currentTestpointSummaryItem instanceof TestPointSummary) {
               TestPointSummary summary = new TestPointSummary();
               currentTestpointSummaryItem.getChildren().add(summary);
               summary.setParent(currentTestpointSummaryItem);
               currentTestpointSummaryItem = summary;
               summary.setMode(((RetryGroupData) obj).getMode());
               summary.setGroupType("RetryGroup");
            }
         }
      });
      handler.getHandler("Result").addListener(new IBaseSaxElementListener() {

         @Override
         public void onEndElement(Object obj) {
            if ("PASSED".equals(obj)) {
               currentTestpointSummaryItem.setImage(ImageManager.getImage(OteOutputImage.PASS));
               if (currentTestpointSummaryItem instanceof TestPointSummary) {
                  ((TestPointSummary) currentTestpointSummaryItem).setPassed(true);
               }
            } else if ("FAILED".equals(obj)) {
               currentTestpointSummaryItem.setImage(ImageManager.getImage(OteOutputImage.FAIL));
               if (currentTestpointSummaryItem instanceof TestPointSummary) {
                  TestPointSummary pointSummary = (TestPointSummary) currentTestpointSummaryItem;
                  pointSummary.setPassed(false);
                  if (pointSummary.isTopLevelTestPoint()) {
                     callback.addJumpToList(currentTestpointSummaryItem);
                     failCount++;
                  }
               }
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
            currentTestpointSummaryItem.setColumnText(0, obj.toString());
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("Expected").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (currentTestpointSummaryItem instanceof TestPointSummary) {
               ((TestPointSummary) currentTestpointSummaryItem).setExpected(spaceProcessing(obj.toString()));
            }
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("Actual").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (currentTestpointSummaryItem instanceof TestPointSummary) {
               ((TestPointSummary) currentTestpointSummaryItem).setActual(spaceProcessing(obj.toString()));
            }
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("ElapsedTime").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (currentTestpointSummaryItem instanceof TestPointSummary) {
               ((TestPointSummary) currentTestpointSummaryItem).setElpasedTime(obj.toString());
            }
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("GroupName").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (currentTestpointSummaryItem instanceof TestPointSummary) {
               ((TestPointSummary) currentTestpointSummaryItem).setGroupName(obj.toString());
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
            if (currentTestpointSummaryItem instanceof TestPointSummary) {
               ((TestPointSummary) currentTestpointSummaryItem).setNumber(obj.toString());
               currentTestpointSummaryItem.setField("Number", obj.toString());
            }
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("Location").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (currentTestpointSummaryItem instanceof TestPointSummary) {
               ((TestPointSummary) currentTestpointSummaryItem).processStacktrace(callback.getScriptName());
            }
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("Stacktrace").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            // Intentionally Empty Block
         }

         @Override
         public void onStartElement(Object obj) {
            if (currentTestpointSummaryItem instanceof TestPointSummary) {
               ((TestPointSummary) currentTestpointSummaryItem).addStackTrace((StacktraceData) obj);
            }
         }
      });

      handler.getHandler("VersionInformation").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            callback.addUutVersionData(versionInformationItem);
            versionInformationItem = null;

         }

         @Override
         public void onStartElement(Object obj) {
            versionInformationItem = new BaseOutfileTreeItem(OutfileRowType.unknown);
         }
      });
      handler.getHandler("UutLoggingInfo").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            callback.addUutLogData(uutLog);
            uutLog = null;
         }

         @Override
         public void onStartElement(Object obj) {
            uutLog = new BaseOutfileTreeItem(OutfileRowType.unknown, "", "UUT Log Info", "", null);
         }
      });
      handler.getHandler("Summary").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            uutLog = uutLog.getParent();

         }

         @Override
         public void onStartElement(Object obj) {
            SummaryData data = (SummaryData) obj;
            IOutfileTreeItem item = new BaseOutfileTreeItem(OutfileRowType.summary, "Summary",
               String.format("node id [%s]", data.getNodeId()),
               String.format("critical[%s] serious[%s] exception[%s] info[%s] minor[%s] start#[%s]",
                  data.getCriticalCount(), data.getSeriousCount(), data.getExceptionCount(), data.getInformationCount(),
                  data.getMinorCount(), data.getStartNumber()),
               null);
            uutLog.getChildren().add(item);
            item.setParent(uutLog);
            uutLog = item;

         }

      });
      handler.getHandler("UutErrorEntry").addListener(new IBaseSaxElementListener() {

         @Override
         public void onEndElement(Object obj) {

            IOutfileTreeItem entryTreeItem =
               new BaseOutfileTreeItem(OutfileRowType.uutinfo, currentErrorEntryData.getSeverity(),
                  currentErrorEntryData.getVersion(), String.format("%s node[%s] count[%s]", obj.toString(),
                     currentErrorEntryData.getNodeId(), currentErrorEntryData.getCount()),
                  null);
            uutLog.getChildren().add(entryTreeItem);

         }

         @Override
         public void onStartElement(Object obj) {

            currentErrorEntryData = (UutErrorEntryData) obj;

         }
      });
      handler.getHandler("OteLog").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            isInsideOteLog = false;
            oteLogRoot = oteLogRoot.getParent();
         }

         @Override
         public void onStartElement(Object obj) {
            OteLogData data = (OteLogData) obj;
            IOutfileTreeItem child =
               new BaseOutfileTreeItem(OutfileRowType.log, "Log", data.getLevel(), data.getLogger(), null);
            oteLogRoot.getChildren().add(child);
            child.setParent(oteLogRoot);
            oteLogRoot = child;
            isInsideOteLog = true;
         }
      });
      handler.getHandler("Message").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (isInsideOteLog) {
               IOutfileTreeItem child = new BaseOutfileTreeItem(OutfileRowType.unknown, "", obj.toString(), "", null);
               oteLogRoot.getChildren().add(child);
               child.setParent(oteLogRoot);
            }
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
                  oteLogRoot.getChildren().add(item);
                  item.setParent(oteLogRoot);

               }
            }
         }

         @Override
         public void onStartElement(Object obj) {
            BaseOutfileTreeItem child = new BaseOutfileTreeItem(OutfileRowType.log);
            child.setColumnText(0, "Exception");
            oteLogRoot.getChildren().add(child);
            child.setParent(oteLogRoot);
         }
      });
      handler.getHandler("Qualification").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            isInQualification = false;
            if (qualificationInfo.length() > 0) {
               callback.addOverviewData("Qualification", qualificationInfo.toString());
            }
         }

         @Override
         public void onStartElement(Object obj) {
            isInQualification = true;
            if (obj instanceof QualificationData) {
               QualificationData data = (QualificationData) obj;
               String buildId = data.getBuildId();
               if (buildId != null && buildId.length() > 0) {
                  qualificationInfo.append("BuildId[");
                  qualificationInfo.append(data.getBuildId());
                  qualificationInfo.append("] ");
               }
               qualificationInfo.append(data.getLevel());
               qualificationInfo.append("; ");
            }
         }
      });
      handler.getHandler("ExecutedBy").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            // Intentionally Empty Block
         }

         @Override
         public void onStartElement(Object obj) {
            if (isInQualification) {
               qualificationInfo.append("ExecutedBy: ");
            }
         }
      });
      handler.getHandler("Witnesses").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            // Intentionally Empty Block
         }

         @Override
         public void onStartElement(Object obj) {
            if (isInQualification) {
               qualificationInfo.append("Witnesses: ");
            }
         }
      });
      handler.getHandler("Notes").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (isInQualification) {
               qualificationInfo.append("Notes: ");
               qualificationInfo.append(obj.toString());
            }
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      handler.getHandler("Name").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (isInQualification) {
               qualificationInfo.append(obj.toString());
               qualificationInfo.append("; ");
            }
         }

         @Override
         public void onStartElement(Object obj) {
            // Intentionally Empty Block
         }
      });
      xmlReader.parse(new InputSource(inputStream));
      // collapse first level TestPoints
      for (IOutfileTreeItem item : rootTestPointSummaryItem.getChildren()) {
         if (item.getChildren().size() == 1) {
            IOutfileTreeItem childItem = item.getChildren().get(0);
            item.getChildren().clear();
            ((TestPointSummary) item).specialCopy((TestPointSummary) childItem);
         }
      }

      callback.addOteLogData(oteLogRoot);
      callback.setSummaryData(rootTestPointSummaryItem);
      callback.setLargeFile(largeFile);
      callback.setFailCount(failCount);

      long all = System.currentTimeMillis() - time;

      OseeLog.logf(Activator.class, Level.INFO, "It took %d ms total to process %s.", all, input.getName());
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
