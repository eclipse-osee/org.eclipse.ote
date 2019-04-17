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
package org.eclipse.ote.test.manager.pages;

import java.util.logging.Level;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.message.MessageDefinitionProvider;
import org.eclipse.osee.ote.message.MessageProviderVersion;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.service.OteServiceProperties;
import org.eclipse.osee.ote.ui.test.manager.connection.ScriptManager;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.osee.ote.ui.test.manager.pages.ScriptPage;
import org.eclipse.osee.ote.ui.test.manager.pages.StatusWindowWidget;
import org.eclipse.ote.client.ui.actions.HostMenuWidget;
import org.eclipse.ote.test.manager.connection.OteScriptManager;
import org.eclipse.ote.test.manager.editor.OteTestManagerEditor;
import org.eclipse.ote.test.manager.internal.OteTestManagerImage;
import org.eclipse.ote.test.manager.internal.OteTestManagerModel;
import org.eclipse.ote.test.manager.internal.OteTestManagerPlugin;
import org.eclipse.ote.test.manager.uut.selector.UutLabelsComposite;
import org.eclipse.ote.test.manager.uut.selector.UutSelectionDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.osgi.framework.FrameworkUtil;


/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class OteScriptPage extends ScriptPage {

   private ToolItem uutSelectMenuButton;
   UutLabelsComposite uutLabelComposite;
   private String messageJarVersion;
   private final TestManagerEditor parentTestManager;
   private OteScriptManager manager;
   private boolean uutRequired = true;
   private HostMenuWidget hostMenuWidget = null;
   private MessageProviderVersion messageProviderVersion;
   private ScriptPageMessageDefinitionProviderTracker scriptPageMessageDefinitionProviderTracker;
   private OteTestManagerModel model;

   public OteScriptPage(Composite parent, int style, TestManagerEditor parentTestManager) {
      super(parent, style, parentTestManager);
      this.parentTestManager = parentTestManager;
      messageProviderVersion = new MessageProviderVersion();
      model = ((OteTestManagerEditor)parentTestManager).getTestManagerModel();
   }

   @Override
   public void createPage() {
      super.createPage();
      createOteToolBar(getCoolBar());
      packCoolBar();
      createOteStatusWindow(getStatusWindow());
      createUutSelectionBar();
      
      layout(true, true);

      getStatusWindow().setLabelAndValue("MSG_JAR_VER", "Message JAR version", "UNKNOWN");

      manager = new OteScriptManager(parentTestManager, getScriptTableViewer().getXViewer());
      loadStorageString();
      
      scriptPageMessageDefinitionProviderTracker = new ScriptPageMessageDefinitionProviderTracker(FrameworkUtil.getBundle(getClass()).getBundleContext(), this);
      scriptPageMessageDefinitionProviderTracker.open(true);
   }

   private void createUutSelectionBar() {
      uutLabelComposite = new UutLabelsComposite((Composite) getContent(), SWT.NONE);
      uutLabelComposite.moveAbove(((Composite)getContent()).getChildren()[1]);
      uutLabelComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).create());
      updateUutsItems();
   }

   private void createOteToolBar(CoolBar coolBar) {
      CoolItem oteCoolItem = new CoolItem(coolBar, SWT.NONE);
      ToolBar oteToolBar = new ToolBar(coolBar, SWT.FLAT | SWT.HORIZONTAL);

      hostMenuWidget = new HostMenuWidget(OteTestManagerPlugin.getInstance().getOteClientService());
      hostMenuWidget.createToolItem(oteToolBar);

      uutSelectMenuButton = new ToolItem(oteToolBar, SWT.PUSH);
      uutSelectMenuButton.setImage(OteTestManagerImage.loadImage(OteTestManagerImage.CHECK_GREEN_SMALL));
      uutSelectMenuButton.setText("UUT");
      uutSelectMenuButton.addSelectionListener(new SelectionListener() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               UutSelectionDialog dialog = new UutSelectionDialog();
               dialog.create();
               dialog.getUutSelectionComposite().setCollection(model.getUutItemCollectionCopy());
               if (dialog.open() == Window.OK) {
                  model.setUutItemCollection(dialog.getUutSelectionComposite().getCollection());
                  getTestManager().doSave(null);
                  updateUutsItems();
               }
            } catch (Throwable th) {
               th.printStackTrace();
            }
         }

         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            // INTENTIONALLY EMPTY BLOCK
            }
      });

      oteToolBar.pack();

      Point size = oteToolBar.getSize();
      oteCoolItem.setControl(oteToolBar);
      oteCoolItem.setSize(oteCoolItem.computeSize(size.x, size.y));
      oteCoolItem.setMinimumSize(size);
   }

   private void updateUutsItems() {
      for (Control control : uutLabelComposite.getChildren()) {
         control.dispose();
      }
      uutSelectMenuButton.setEnabled(uutRequired);
      if (uutRequired) {
         uutSelectMenuButton.setToolTipText("Select UUT to run script against.");
      } 
      else {
         uutSelectMenuButton.setToolTipText("Test Manager is connected to a non-simulated environment. \n" + "UUT loading needs to be performed manually.");
      }
      uutLabelComposite.updateLabels(model.getUutItemCollectionCopy(), uutRequired);
   }
   
   private void createOteStatusWindow(StatusWindowWidget statusWindow) {
      statusWindow.refresh();
   }

   @Override
   public boolean areSettingsValidForRun() {
      boolean result = super.areSettingsValidForRun();
      if (uutRequired && model.getUUTs().size() > 0) {
         result &= true;
      }
      return result;
   }

   @Override
   public String getErrorMessage() {
      StringBuilder builder = new StringBuilder();
      builder.append(super.getErrorMessage());
      if (uutRequired && model.getUUTs().size() > 0) {
         if (builder.length() > 0) {
            builder.append("\n");
         }
         builder.append("Select Build to run.");
      }
      return builder.toString();
   }

   @Override
   public void restoreData() {
      // Do Nothing
   }

   @Override
   public void saveData() {
      // INTENTIONALLY EMPTY BLOCK
   }

   @Override
   public ScriptManager getScriptManager() {
      return manager;
   }

   @Override
   public boolean onConnection(final ConnectionEvent event) {

      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            String type = null;
            try {
               type = new OteServiceProperties(event.getConnector()).getType();
            } catch (Exception ex) {
               // Do Nothing;
            }
            uutRequired = type != null && type.contains("Sun");
            updateUutsItems();
         }
      });

      return super.onConnection(event);
   }

   @Override
   public boolean onDisconnect(ConnectionEvent event) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            uutRequired = true;
            updateUutsItems();
         }
      });

      return super.onDisconnect(event);
   }

   @Override
   public void dispose() {
      scriptPageMessageDefinitionProviderTracker.close();
      hostMenuWidget.dispose();
      super.dispose();
   }

   @Override
   public boolean onConnectionLost() {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            uutRequired = true;
            updateUutsItems();
         }

      });
      return super.onConnectionLost();
   }

   public void addMessageDefinitionProvider(MessageDefinitionProvider provider) {
	   messageProviderVersion.add(provider);
	   Displays.ensureInDisplayThread(new Runnable() {

		   @Override
		   public void run() {
			   try {
				   messageJarVersion = messageProviderVersion.getVersion();
				   getStatusWindow().setLabelAndValue("MSG_JAR_VER", "Message JAR version", messageJarVersion);
				   getStatusWindow().refresh();
			   } catch (Exception e) {
				   OteTestManagerPlugin.log(Level.SEVERE, "exception getting library versions", e);
				   MessageDialog.openError(Displays.getActiveShell(), "Error",
						   "Error processing library loaded event. See Error Dialog for details");
			   }
		   }
	   });
   }

   public void removeMessageDefinitionProvider(MessageDefinitionProvider service) {
	   messageProviderVersion.remove(service);
	   Displays.ensureInDisplayThread(new Runnable() {

		   @Override
		   public void run() {
			   if(messageProviderVersion.isAnyAvailable()){
				   messageJarVersion = messageProviderVersion.getVersion();
				   getStatusWindow().setLabelAndValue("MSG_JAR_VER", "Message JAR version", messageJarVersion);
				   getStatusWindow().refresh();
			   } else {
				   getStatusWindow().setLabelAndValue("MSG_JAR_VER", "Message JAR version", "<not loaded>");
			   }
		   }

	   });
   }

}
