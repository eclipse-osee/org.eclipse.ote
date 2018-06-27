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
package org.eclipse.osee.ote.ui.test.manager.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.service.ITestConnectionListener;
import org.eclipse.osee.ote.ui.test.manager.ITestManagerFactory;
import org.eclipse.osee.ote.ui.test.manager.OteTestManagerImage;
import org.eclipse.osee.ote.ui.test.manager.internal.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.pages.contributions.TestManagerStorageKeys;
import org.eclipse.osee.ote.ui.test.manager.util.ClassServerInst;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * Resource Test Manager Editor Pages:
 * <ul>
 * <li>Overview Page
 * <li>Target Page
 * <li>Scripts Page
 * <li>Advanced Page
 * <li>Source Page
 * </ul>
 */
public abstract class TestManagerEditor extends MultiPageEditorPart implements ITestConnectionListener {
   private static final Image errorImage = ImageManager.getImage(OteTestManagerImage.ERROR);
   public static final String namespace = "org.eclipse.osee.ote.ui.test.manager.editors.TestManagerEditor";

   public final QualifiedName clearCaseViewName = new QualifiedName(namespace, "CLEARCASEVIEW");
   public final QualifiedName configFileName = new QualifiedName(namespace, "CONFIGFILENAME");
   public final QualifiedName ofpQualName = new QualifiedName(namespace, "OFP");
   public final QualifiedName scriptsQualName = new QualifiedName(namespace, "SCRIPTS");

   private boolean fileIsDirty = false;

   private boolean fileWasSaved = false;

   private int lastPageIndex = 0;

   private final ITestManagerModel model;

   private boolean reloadSourcePage = false;

   private int scriptPageIndex = 1;

   private TextEditor sourceEditor;

   private int sourcePage;

   private final ITestManagerFactory testManagerFactory;

   private IFile thisIFile = null;

   private final IPropertyStore propertyStore;

   private final PageManager pageManager;

   private ITestEnvironment connectedEnv = null;
   private IServiceConnector connector = null;
   private IHostTestEnvironment connectedHost;

   public TestManagerEditor(final ITestManagerFactory testManagerFactory, ITestManagerModel model) {
      super();

      this.testManagerFactory = testManagerFactory;
      this.pageManager = new PageManager(testManagerFactory, this);
      this.model = model;
      this.propertyStore = new PropertyStore(testManagerFactory.getClass().getSimpleName());
   }

   public void activateScriptsPage() {
      setActivePage(scriptPageIndex);
   }
   

   public void addFile(String fullPath) {
      pageManager.getScriptPage().addFile(fullPath);
   }

   @Override
   public void dispose() {
      super.dispose();
      TestManagerPlugin.getInstance().getOteClientService().removeConnectionListener(this);
      try {
         pageManager.dispose();
      } catch (Throwable t) {
         TestManagerPlugin.log(Level.SEVERE, "exception while disposing test manager", t);
      }
   }

   /**
    * Saves the multi-page editor's document.
    */
   @Override
   public void doSave(IProgressMonitor monitor) {
      if (getActivePage() != sourcePage) {
         pageSourceLoad();
      }
      if(isDirty()){
         getEditor(sourcePage).doSave(monitor);
      }
      fileIsDirty = false;
      fileWasSaved = true;
      doSave();
      firePropertyChange(PROP_DIRTY);
   }

   /**
    * Saves the multi-page editor's document as another file. Also updates the text for page 0's tab, and updates this
    * multi-page editor's input to correspond to the nested editor's.
    */
   @Override
   public void doSaveAs() {
      if (getActivePage() != sourcePage) {
         pageSourceLoad();
      }
      IEditorPart editor = getEditor(sourcePage);
      editor.doSaveAs();
      setPageText(sourcePage, "Source");
      setInput(editor.getEditorInput());
      doSave();
   }

   protected void registerPage(int pageNumber, String pageName, boolean isScriptPage) {
      setPageText(pageNumber, pageName);
      if(isScriptPage){
         scriptPageIndex = pageNumber;
      }
   }

   public void executionCompleted() {
      pageManager.getScriptPage().onScriptRunning(false);
   }

   public void fireSaveNeeded() {
      fileIsDirty = true;
      firePropertyChange(PROP_DIRTY);
   }

   public String getAlternateOutputDir() {
      String scriptOutput = "";

      IPropertyStore propertyStore = getPropertyStore();
      scriptOutput = propertyStore.get(TestManagerStorageKeys.SCRIPT_OUTPUT_DIRECTORY_KEY);
      if (scriptOutput == null) {
         scriptOutput = "";
         // TODO: Escobar
         // try {
         // IEditorInput coreinput = getEditorInput();
         // if (coreinput instanceof IFileEditorInput) {
         // scriptOutput =
         // thisIFile.getPersistentProperty(scriptOutputQualName);
         // } else if (coreinput instanceof TestManagerInput) {
         // TestManagerInput input = (TestManagerInput) getEditorInput();
         // scriptOutput =
         // input.getValue(scriptOutputQualName.getLocalName());
         // }
         //
         // scriptOutput =
         // thisIFile.getPersistentProperty(scriptOutputQualName);
         // } catch (CoreException e) {
         // e.printStackTrace();
         // }
      }
      return scriptOutput;
   }

   public String getDefaultConfigPath() {
      Location user = Platform.getUserLocation();
      String path = user.getURL().getPath();
      File file = new File(path + File.separator + "org.eclipse.osee.ote.ui.test.manager");
      file.mkdirs();
      file =
         new File(
            path + File.separator + "org.eclipse.osee.ote.ui.test.manager" + File.separator + this.getClass().getName() + ".scriptConfig.xml");
      file.getParentFile().mkdirs();
      return file.getAbsolutePath();
   }

   /**
    * @return Returns the model.
    */
   public ITestManagerModel getModel() {
      return model;
   }

   public String getName() {
      return this.getTitle();
   }

   public ClassServerInst getScriptClassServer() {
      return ClassServerInst.getInstance();
   }

   public ITestManagerFactory getTestManagerFactory() {
      return testManagerFactory;
   }

   @Override
   public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
      if (!(editorInput instanceof IFileEditorInput || editorInput instanceof TestManagerInput || editorInput instanceof IEditorInput)) {
         throw new PartInitException("Invalid Input: Must be IFileEditorInput");
      }
      super.init(site, editorInput);
   }

   @Override
   public boolean isDirty() {
      if (super.isDirty()) {
         return true;
      }
      return fileIsDirty;
   }

   @Override
   public boolean isSaveAsAllowed() {
      return true;
   }

   /**
    * Retrieves the value for the key. See <code>storeValue</code>. If the key could not be found, an empty string is
    * returned.
    * 
    * @param key The <code>QualifiedName</code> whose value is to be retrieved.
    * @return The value of key, or an empty string if the key does not exist.
    */
   public String loadValue(QualifiedName key) {
      TestManagerPlugin.log(Level.INFO, "loadValue: " + key.getQualifier());
      try {
         IEditorInput coreinput = getEditorInput();
         if (coreinput instanceof IFileEditorInput) {
            return thisIFile.getPersistentProperty(key);
         } else if (coreinput instanceof TestManagerInput) {
            TestManagerInput input = (TestManagerInput) getEditorInput();
            return input.getValue(key.getLocalName());
         }
      } catch (CoreException ex) {
         TestManagerPlugin.log(Level.SEVERE, "Can't get value: " + ex);
      }
      return "";
   }

   public void setPageError(int page, boolean set) {
      if (set) {
         setPageImage(page, errorImage);
      } else {
         setPageImage(page, null);
      }
   }

   /**
    * Stores the value for the key. The key should be one of the publicly available <code>QualifiedName</code>'s in
    * <code>this</code>.
    * 
    * @param key The <code>QualifiedName</code> associated with the value to be stored
    * @param value What will be stored under the key.
    */
   public void storeValue(QualifiedName key, String value) {
      TestManagerPlugin.log(Level.INFO, "storeValue: " + key.getQualifier());
      try {
         IEditorInput coreinput = getEditorInput();
         if (coreinput instanceof IFileEditorInput) {
            thisIFile.setPersistentProperty(key, value);
         } else if (coreinput instanceof TestManagerInput) {
            TestManagerInput input = (TestManagerInput) getEditorInput();
            input.storeValue(key.getLocalName(), value);
         }
      } catch (Exception ex) {
         TestManagerPlugin.log(Level.SEVERE, "Can't set value: " + ex);
      }
   }

   private void pageSourceCheck() {
      setPageError(sourcePage, model.hasParseExceptions());
   }

   private void readXmlData() {
      TestManagerPlugin.log(Level.INFO, "readXmlData");
      IEditorInput coreinput = getEditorInput();
      String xmlText = "";
      if (coreinput instanceof IFileEditorInput) {
         IFileEditorInput input = (IFileEditorInput) getEditorInput();
         thisIFile = input.getFile();
         try {
            thisIFile.refreshLocal(IResource.DEPTH_ZERO, null);
         } catch (CoreException e) {
            e.printStackTrace();
         }
         String name = thisIFile.getName();
         this.setPartName(name);
         if (thisIFile != null) {
            try {
               xmlText = Lib.inputStreamToString(thisIFile.getContents());
            } catch (Exception ex) {
               ex.printStackTrace();
            }
         } else {
            TestManagerPlugin.log(Level.SEVERE, "Can't open xml file!");
         }
      } else if (coreinput instanceof TestManagerInput) {
         TestManagerInput input = (TestManagerInput) getEditorInput();
         String name = "TestManager";
         this.setPartName(name);
         xmlText = input.getDefaultXML();
      }
      model.setFromXml(xmlText);
   }

   /**
    * Creates the pages of the multi-page editor.
    */
   @Override
   protected void createPages() {
      readXmlData();

      pageManager.createPages(getContainer());
      pageSourceCreate();

      fileIsDirty = false;
      reloadSourcePage = false;
      pageSourceCheck();
      restoreSettings();
      
      // If parse errors, send to sourcePage and set error on page
      if (model.hasParseExceptions()) {
         if (sourceEditor == null) {
            pageSourceCreate();
         }
         handleSourceEditorError();
      }
      fileIsDirty = false;
      firePropertyChange(PROP_DIRTY);
      TestManagerPlugin.getInstance().getOteClientService().addConnectionListener(this);
   }

   protected void handleSelection() {
      fireSaveNeeded();
      reloadSourcePage = true;
   }

   /**
    * reloads pages as necessary
    */
   @Override
   protected void pageChange(int newPageIndex) {
      // NOTE: Hosts page will be updated continuously, even it if it is not
      // the current page.
      // so it is unnecessary to update it on pageChange.

      super.pageChange(newPageIndex);
      if (newPageIndex == sourcePage) {
         pageSourceLoad();
      } else {
         if (sourceEditor == null) {
            return;
         }
         String newXml = sourceEditor.getDocumentProvider().getDocument(sourceEditor.getEditorInput()).get();
         if (sourceEditor.isDirty() || fileWasSaved) {
            fileWasSaved = false;
            // If we just came from sourcePage, re-parse
            if (lastPageIndex == sourcePage) {
               // if parse error, goto source and error
               if (!model.setFromXml(newXml)) {
                  handleSourceEditorError();
                  return;
               }
               setPageError(sourcePage, false);
            }
         }
      }
      doSave(null);
      lastPageIndex = newPageIndex;
   }

   private void handleSourceEditorError() {
      if (model.hasParseExceptions()) {
         try {
            setActivePage(sourcePage);
            pageSourceCheck();
            MessageDialog.openError(getSite().getShell(), "Source Page Error",
               "Error parsing Source page\n  "+model.getParseError());
            Pair<Integer, Integer> parseErrorRange = model.getParseErrorRange();
            sourceEditor.setHighlightRange(parseErrorRange.getFirst(), parseErrorRange.getSecond(), false);
            sourceEditor.getSelectionProvider().setSelection(new TextSelection(parseErrorRange.getFirst(), parseErrorRange.getSecond()));
         } catch (Throwable th) {}
      }
   }

   public void updateFromTestManagerModel() {
      IDocument doc = sourceEditor.getDocumentProvider().getDocument(sourceEditor.getEditorInput());
      if (!doc.get().equals(model.getRawXml())) {
         doc.set(model.getRawXml());
         try {
            sourceEditor.getDocumentProvider().saveDocument(new NullProgressMonitor(), null, doc, true);
         } catch (CoreException e) {
            OseeLog.log(getClass(), Level.SEVERE, e);
         }
      }
   }
   
   void pageSourceCreate() {
      try {
         if (getEditorInput() instanceof IFileEditorInput) {
            sourceEditor = new TextEditor();
            int index = addPage(sourceEditor, getEditorInput());
            sourcePage = index;
            setPageText(sourcePage, "Source");
         }
      } catch (PartInitException e) {
         TestManagerPlugin.log(Level.SEVERE, "Error creating nested text editor", e);
         ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null, e.getStatus());
      }
   }

   void pageSourceLoad() {
      if (reloadSourcePage) {
         sourceEditor.getDocumentProvider().getDocument(sourceEditor.getEditorInput()).set(model.getRawXml());
         reloadSourcePage = false;
      }
      pageSourceCheck();
   }

   public void doSave() {
      pageManager.save();
      OutputStream outputStream = null;
      try {
         File file = OseeData.getFile("testManagerSettings.xml");
         outputStream = new FileOutputStream(file);
         getPropertyStore().save(outputStream);
      } catch (Exception ex) {
         TestManagerPlugin.log(Level.SEVERE, "Error storing settings.", ex);
      } finally {
         if (outputStream != null) {
            try {
               outputStream.close();
            } catch (IOException ex) {
               TestManagerPlugin.log(Level.WARNING, "Error closing stream during settings storage.", ex);
            }
         }
      }
   }

   public void restoreSettings() {
      InputStream inputStream = null;
      try {
         File file = OseeData.getFile("testManagerSettings.xml");
         if(file.exists()){
        	 inputStream = new FileInputStream(file);
        	 getPropertyStore().load(inputStream);
        	 pageManager.restore();
         }
      } catch (Exception ex) {
         TestManagerPlugin.log(Level.WARNING, "Stored settings not available. Using defaults.", ex);
      } finally {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (IOException ex) {
               TestManagerPlugin.log(Level.WARNING, "Error closing stream while loading settings.", ex);
            }
         }
      }
   }

   public IPropertyStore getPropertyStore() {
      return propertyStore;
   }

   public PageManager getPageManager() {
      return pageManager;
   }

   @Override
   public void onConnectionLost(IServiceConnector connector) {
      connectedEnv = null;
      connector = null;
      boolean problemEncountered = pageManager.onConnectionLost();
      if (problemEncountered) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               MessageDialog.openError(Displays.getActiveShell(), "Disconnect Error",
                  "Test manager has encountered a problem while processing the disconnect event. See Error Log for details");
            }
         });
      }
      connectedHost = null;
   }

   @Override
   public void onPostConnect(ConnectionEvent event) {
      connectedEnv = event.getEnvironment();
      connectedHost = event.getHostEnvironment();
      connector = event.getConnector();
      boolean problemEncountered = pageManager.onPostConnect(event);
      if (problemEncountered) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               MessageDialog.openError(Displays.getActiveShell(), "Connection Error",
                  "Test manager has encountered a problem while processing the connection event. See Error Log for details");
            }
         });
      }
   }

   @Override
   public void onPreDisconnect(ConnectionEvent event) {
      event.getEnvironment();
      connectedEnv = null;

      connector = null;
      boolean problemEncountered = pageManager.onPreDisconnect(event);
      if (problemEncountered) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               MessageDialog.openError(Displays.getActiveShell(), "Disconnect Error",
                  "Test manager has encountered a problem while processing the disconnect event. See Error Log for details");
            }
         });
      }
      connectedHost = null;
   }

   public boolean isConnected() {
      return connectedEnv != null;
   }

   public ITestEnvironment getConnectedEnvironment() {
      return connectedEnv;
   }

   public IHostTestEnvironment getConnectedHostEnvironment() {
      return connectedHost;
   }

   public IServiceConnector getConnector() {
      return connector;
   }

   public abstract void createHostWidget(Composite parent);

   public void addFiles(String[] files) {
      pageManager.getScriptPage().addFiles(files);
   }

}