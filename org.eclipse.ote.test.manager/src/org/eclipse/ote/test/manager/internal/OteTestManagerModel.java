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

package org.eclipse.ote.test.manager.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.ote.ui.test.manager.core.ITestManagerModel;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.ote.test.manager.uut.selector.UutItemCollection;
import org.eclipse.ote.test.manager.uut.selector.UutItemPartition;
import org.eclipse.ote.test.manager.uut.selector.xml.TestManagerXmlInterface;

/**
 * Base Class for all TestManagers
 * 
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class OteTestManagerModel implements ITestManagerModel {

   private final TestManagerXmlInterface xmlInterface;
   private boolean hasParseErrors;
   private TestManagerEditor testManagerEditor;

   public OteTestManagerModel() {
      this.testManagerEditor = null;
      xmlInterface = new TestManagerXmlInterface();
      hasParseErrors = false;
   }
   
   public void setTestManagerEditor(TestManagerEditor testManagerEditor) {
      this.testManagerEditor = testManagerEditor;
   }

   @Override
   public synchronized String getRawXml() {
      return xmlInterface.getXml();
   }

   @Override
   public boolean hasParseExceptions() {
      return hasParseErrors;
   }
   
   @Override
   public Pair<Integer, Integer> getParseErrorRange() {
      return xmlInterface.getErrorRange();
   }

   /**
    * load model from xmlString; return true if successful
    */
   @Override
   public synchronized boolean setFromXml(String xmlString) {
      hasParseErrors = !xmlInterface.setXml(xmlString);
      return !hasParseErrors;
   }

   public synchronized List<UutItemPartition> getUUTs() {
      List<UutItemPartition> uuts = new ArrayList<>();
      for (UutItemPartition item : getUutItemCollectionCopy().getPartitions()) {
         if (!item.getPath().isEmpty()) {
            uuts.add(item);
         }
      }
      return uuts;
   }
   
   public UutItemCollection getUutItemCollectionCopy() {
      TestManagerXmlInterface xmlCopy = new TestManagerXmlInterface();
      xmlCopy.setXml(getRawXml());
      return xmlCopy.getUutItemCollection();
   }
   
   public void setUutItemCollection(UutItemCollection collection) {
      xmlInterface.setUutItemCollection(collection);
      updateEditor();
   }

   public void setDistribution(String distributionStatement) {
      xmlInterface.setDistributionStatement(distributionStatement);
      updateEditor();

   }
   
   private void updateEditor() {
      if (testManagerEditor != null) {
         testManagerEditor.updateFromTestManagerModel();
      }
   }
   
   public String getDistributionStatement() {
      return xmlInterface.getDistributionStatement();
   }

   @Override
   public String getParseError() {
      return xmlInterface.getErrorMessage();
   }
   
}
