/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.ote.core.framework.summary_report;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.ote.core.framework.saxparse.IBaseSaxElementListener;
import org.eclipse.osee.ote.core.framework.saxparse.OteSaxHandler;
import org.eclipse.osee.ote.core.framework.saxparse.elements.TestPointResultsData;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import com.sun.accessibility.internal.resources.accessibility;

/**
 * 
 * This class parses a tmo (xml) file and gathers the Test point results
 * and a list of Test Case information.
 * 
 * @author Andy Jury, Dominic Leiner
 */
public class OutFileResultProcessor extends AbstractOperation {

   private final File source;
   private boolean isFromTestPoint = false;
   private boolean gotTestCaseName = false;
   private boolean gotTestCaseNumber = false;
   private boolean isAdditionalInfo = false;

   private List<TestCaseInfo> testCases;
   TestPointResultsData testPointsResults;

   /**
    * @param source outfile to be processed into test case info
    */
   public OutFileResultProcessor(File source) {
      super("File Conversion", "lba.ote.outfile.conversion");
      this.source = source;

      testCases = new ArrayList<TestCaseInfo>();
      TestCaseInfo info = new TestCaseInfo();
      info.setName("Initialization");
      info.setNumber("-1");
      testCases.add(info);
   }


   public void run() {
      this.run(SubMonitor.convert(new IProgressMonitor() {
         @Override
         public void worked(int work) {
         }

         @Override
         public void subTask(String name) {
         }

         @Override
         public void setTaskName(String name) {
         }

         @Override
         public void setCanceled(boolean value) {
         }

         @Override
         public boolean isCanceled() {
            return false;
         }

         @Override
         public void internalWorked(double work) {
         }

         @Override
         public void done() {
         }

         @Override
         public void beginTask(String name, int totalWork) {
         }
      }));
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {

      monitor.setTaskName(String.format("Computing overview information for [%s].", source.getName()));

      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      OteSaxHandler handler = new OteSaxHandler();
      xmlReader.setContentHandler(handler);
      xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler); // This is the
                                                                                       // important part
      handler.getHandler("TestCase").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            gotTestCaseName = false;
            gotTestCaseNumber = false;
         }

         @Override
         public void onStartElement(Object obj) {
            testCases.add(new TestCaseInfo());
            testCases.get(testCases.size() - 1).setScriptName(source.getName().replaceAll("\\..*", ""));
         }
      });
      handler.getHandler("Name").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (isAdditionalInfo) {
               return;
            }
            if (!gotTestCaseName) {
               testCases.get(testCases.size() - 1).setName(obj.toString());
               gotTestCaseName = true;
            } else {
            }
         }

         @Override
         public void onStartElement(Object obj) {
         }
      });
      handler.getHandler("Number").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (!gotTestCaseNumber) {
               testCases.get(testCases.size() - 1).setNumber(obj.toString());
               gotTestCaseNumber = true;
            } else if (isFromTestPoint) {
               testCases.get(testCases.size() - 1).getLastTestPoint().setNumber(obj.toString());
            }
         }

         @Override
         public void onStartElement(Object obj) {
         }
      });
      handler.getHandler("Time").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (isFromTestPoint) {
               int time = 0;
               try {
                  time = Integer.parseInt(obj.toString());
                  time = time / 1000;// switch to MS
                  testCases.get(testCases.size() - 1).getLastTestPoint().setTime(Integer.toString(time));
               } catch (NumberFormatException ex) {
                  testCases.get(testCases.size() - 1).getLastTestPoint().setTime("unknown");
                  ex.toString();
               }

            }
         }

         @Override
         public void onStartElement(Object obj) {
         }
      });
      handler.getHandler("Attention").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
         }

         @Override
         public void onStartElement(Object obj) {
         }
      });
      handler.getHandler("MethodArguments").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
         }

         @Override
         public void onStartElement(Object obj) {
         }
      });
      handler.getHandler("TestPoint").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
         }

         @Override
         public void onStartElement(Object obj) {
            testCases.get(testCases.size() - 1).addTestPoint();
            isFromTestPoint = true;
         }
      });
      handler.getHandler("CheckPoint").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
         }

         @Override
         public void onStartElement(Object obj) {
            isFromTestPoint = false;
         }
      });
      handler.getHandler("CheckGroup").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
         }

         @Override
         public void onStartElement(Object obj) {
            isFromTestPoint = false;
         }
      });
      handler.getHandler("RetryGroup").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
         }

         @Override
         public void onStartElement(Object obj) {
            isFromTestPoint = false;
         }
      });
      handler.getHandler("Result").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (isFromTestPoint) {
               if ("FAILED".equals(obj)) {
                  testCases.get(testCases.size() - 1).getLastTestPoint().setPass(false);
                  testCases.get(testCases.size() - 1).incrementFail();
               } else {
                  testCases.get(testCases.size() - 1).getLastTestPoint().setPass(true);
                  testCases.get(testCases.size() - 1).incrementPass();
               }
            }
         }

         @Override
         public void onStartElement(Object obj) {
         }
      });
      handler.getHandler("TestPointName").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            testCases.get(testCases.size() - 1).getLastTestPoint().setName((String) obj);
         }

         @Override
         public void onStartElement(Object obj) {
         }
      });
      handler.getHandler("AdditionalInfo").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            isAdditionalInfo = false;
         }

         @Override
         public void onStartElement(Object obj) {
            isAdditionalInfo = true;
         }
      });
      handler.getHandler("TestPointResults").addListener(new IBaseSaxElementListener() {
         @Override
         public void onStartElement(Object obj) {
            if (obj instanceof TestPointResultsData) {
               testPointsResults = ((TestPointResultsData) obj);
            }
         }

         @Override
         public void onEndElement(Object obj) {
         }
      });
      

      xmlReader.parse(new InputSource(new FileInputStream(source)));

   }
   
   public List<TestCaseInfo> getTestCases() {
      return testCases;
   }
   
   public TestPointResultsData getTestPointResults() {
      return testPointsResults;
   }

}