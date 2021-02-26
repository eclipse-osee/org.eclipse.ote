/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.ote.ci.test_server.internal.results;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.ote.core.framework.saxparse.IBaseSaxElementListener;
import org.eclipse.osee.ote.core.framework.saxparse.OteSaxHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Parses data from an outfile for conversion to other formats.
 * 
 * @author Andrew M. Finkbeiner
 * @author David N. Phillips
 * @author Andy Jury
 */
public class OutFileResultProcessor extends AbstractOperation {

   protected static final String HEADER_CLOSE =
      "\n\n===========================================================================\n\n";
   private final File source;
   private boolean isFromTestPoint = false;
   private boolean gotTestCaseName;
   private boolean gotTestCaseNumber;
   private boolean isAdditionalInfo = false;

   private List<ITestResultWriter> resultWriters;
   private List<TestCaseInfo> testCases;

   /**
    * @param source outfile to be processed into test case info
    * @param resultWriter does something with the test case info
    */
   public OutFileResultProcessor(File source) {
      super("File Conversion", "org.eclipse.ote.ci.test_server");
      this.source = source;

      resultWriters = new ArrayList<ITestResultWriter>();

      testCases = new ArrayList<TestCaseInfo>();
      TestCaseInfo info = new TestCaseInfo();
      info.setName("Initialization");
      info.setNumber("-1");
      testCases.add(info);
   }

   public void addResultWriter(ITestResultWriter resultWriter) {
      resultWriters.add(resultWriter);
   }

   public void run() {
      this.run(SubMonitor.convert(new IProgressMonitor() {
         @Override
         public void worked(int work) {
            //INTENTIONALLY EMPTY
         }

         @Override
         public void subTask(String name) {
            //INTENTIONALLY EMPTY
         }

         @Override
         public void setTaskName(String name) {
            //INTENTIONALLY EMPTY
         }

         @Override
         public void setCanceled(boolean value) {
            //INTENTIONALLY EMPTY
         }

         @Override
         public boolean isCanceled() {
            return false;
         }

         @Override
         public void internalWorked(double work) {
            //INTENTIONALLY EMPTY
         }

         @Override
         public void done() {
            //INTENTIONALLY EMPTY
         }

         @Override
         public void beginTask(String name, int totalWork) {
            //INTENTIONALLY EMPTY
         }
      }));
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {

      monitor.setTaskName(String.format("Computing overview information for [%s].", source.getName()));

      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      OteSaxHandler handler = new OteSaxHandler();
      xmlReader.setContentHandler(handler);
      xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler); // This is the important part
      handler.getHandler("TestCase").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            gotTestCaseName = false;
            gotTestCaseNumber = false;
         }

         @Override
         public void onStartElement(Object obj) {
            testCases.add(new TestCaseInfo());
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
               //INTENTIONALLY EMPTY
            }
         }

         @Override
         public void onStartElement(Object obj) {
            //INTENTIONALLY EMPTY
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
            //INTENTIONALLY EMPTY
         }
      });
      handler.getHandler("Time").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (isFromTestPoint) {
               int time = 0;
               try {
                  time = Integer.parseInt(obj.toString());
                  time = time / 1000;//switch to MS
                  testCases.get(testCases.size() - 1).getLastTestPoint().setTime(Integer.toString(time));
               } catch (NumberFormatException ex) {
                  testCases.get(testCases.size() - 1).getLastTestPoint().setTime("unknown");
                  ex.toString();
               }

            }
         }

         @Override
         public void onStartElement(Object obj) {
            //INTENTIONALLY EMPTY
         }
      });
      handler.getHandler("Attention").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            //INTENTIONALLY EMPTY
         }

         @Override
         public void onStartElement(Object obj) {
            //INTENTIONALLY EMPTY
         }
      });
      handler.getHandler("MethodArguments").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            //INTENTIONALLY EMPTY
         }

         @Override
         public void onStartElement(Object obj) {
            //INTENTIONALLY EMPTY
         }
      });
      handler.getHandler("TestPoint").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            //INTENTIONALLY EMPTY
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
            //INTENTIONALLY EMPTY
         }

         @Override
         public void onStartElement(Object obj) {
            isFromTestPoint = false;
         }
      });
      handler.getHandler("CheckGroup").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            //INTENTIONALLY EMPTY
         }

         @Override
         public void onStartElement(Object obj) {
            isFromTestPoint = false;
         }
      });
      handler.getHandler("RetryGroup").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            //INTENTIONALLY EMPTY
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
            //INTENTIONALLY EMPTY
         }
      });
      handler.getHandler("TestPointName").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            //INTENTIONALLY EMPTY
         }

         @Override
         public void onStartElement(Object obj) {
            //INTENTIONALLY EMPTY
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

      xmlReader.parse(new InputSource(new FileInputStream(source)));

      for (ITestResultWriter writer : resultWriters) {
         writer.process(source.getName(), testCases);
      }
   }
}
