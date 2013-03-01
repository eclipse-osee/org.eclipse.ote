/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.ui.eviewer.view;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.client.msg.IOteMessageService;
import org.eclipse.osee.ote.message.ElementPath;
import org.eclipse.ote.ui.eviewer.Activator;
import org.eclipse.ote.ui.eviewer.ClientMessageServiceTracker;
import org.eclipse.ote.ui.eviewer.action.AddElementAction;
import org.eclipse.ote.ui.eviewer.action.AddHeaderElementAction;
import org.eclipse.ote.ui.eviewer.action.ClearAllUpdatesAction;
import org.eclipse.ote.ui.eviewer.action.ConfigureColumnsAction;
import org.eclipse.ote.ui.eviewer.action.CopyAllAction;
import org.eclipse.ote.ui.eviewer.action.OpenNewElementViewer;
import org.eclipse.ote.ui.eviewer.action.RemoveColumnAction;
import org.eclipse.ote.ui.eviewer.action.SaveLoadAction;
import org.eclipse.ote.ui.eviewer.action.SetActiveColumnAction;
import org.eclipse.ote.ui.eviewer.action.StreamToFileAction;
import org.eclipse.ote.ui.eviewer.action.ToggleAutoRevealAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class ElementViewer extends ViewPart {
	public static final String VIEW_ID = "org.eclipse.ote.ui.eviewer.view.ElementViewer";
	private TableViewer viewer;
	private AddElementAction addElementAction;
	private AddHeaderElementAction addHeaderElementAction;
	private ClearAllUpdatesAction clearAllUpdatesAction;
	private ToggleAutoRevealAction toggleAutoRevealAction;
	private RemoveColumnAction removeColumnAction;
	private CopyAllAction copyAction;
	private SetActiveColumnAction activeColumnAction;
	private SaveLoadAction saveLoadAction;
	private StreamToFileAction streamToFileAction;
	private ConfigureColumnsAction configureColumnAction;
	private final ElementContentProvider elementContentProvider = new ElementContentProvider(
			8194);
	private final ClientMessageServiceTracker tracker;

	/**
	 * The constructor.
	 */
	public ElementViewer() {
		tracker = new ClientMessageServiceTracker(this);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {


		viewer = new TableViewer(parent, SWT.DOUBLE_BUFFERED
				| SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setUseHashlookup(true);
		viewer.setContentProvider(elementContentProvider);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);


		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		tracker.open(true);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				ElementViewer.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(addElementAction);
		manager.add(new Separator());
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(addElementAction);
		manager.add(addHeaderElementAction);
		if (!elementContentProvider.getColumns().isEmpty()) {
			manager.add(new Separator());
			manager.add(activeColumnAction);

			manager.add(removeColumnAction);
		}
		manager.add(new Separator());
		manager.add(clearAllUpdatesAction);
		manager.add(copyAction);
		manager.add(new Separator());
		manager.add(toggleAutoRevealAction);

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(addElementAction);
		manager.add(configureColumnAction);
		manager.add(clearAllUpdatesAction);
		manager.add(toggleAutoRevealAction);
		manager.add(saveLoadAction);
		manager.add(streamToFileAction);
		manager.add(new OpenNewElementViewer());
	}

	private void makeActions() {
	   addHeaderElementAction = new AddHeaderElementAction(elementContentProvider);
		addElementAction = new AddElementAction(elementContentProvider);
		addElementAction.setEnabled(false);
		clearAllUpdatesAction = new ClearAllUpdatesAction(
				elementContentProvider);
		toggleAutoRevealAction = new ToggleAutoRevealAction(
				elementContentProvider);
		activeColumnAction = new SetActiveColumnAction(elementContentProvider);
		removeColumnAction = new RemoveColumnAction(elementContentProvider);
		saveLoadAction = new SaveLoadAction(elementContentProvider);
		streamToFileAction = new StreamToFileAction(this);

		copyAction = new CopyAllAction(Display.getDefault(),
				elementContentProvider);
		configureColumnAction = new ConfigureColumnsAction(
				elementContentProvider);
	}

	private void hookDoubleClickAction() {

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}


	public void startStreaming(final String columnSetFile, final String fileName, final boolean disableRendering) {
		final Display display = PlatformUI.getWorkbench().getDisplay();
		Runnable task = new Runnable() {

			@Override
			public void run() {
				if (columnSetFile != null) {
					elementContentProvider.clearAllUpdates();
					File file = new File(columnSetFile);
					if (file.exists() && file.isFile()) {
						try {
							elementContentProvider.removeAll();
							elementContentProvider.loadColumnsFromFile(file);
						} catch (IOException ex) {
							MessageDialog.openError(
									Display.getCurrent().getActiveShell(),
									"Error",
									"Could not save file:\n"
											+ file.getAbsolutePath());
						}
					} else {
						MessageDialog.openError(Display.getCurrent().getActiveShell(), "File Error", "The file " + file.getAbsolutePath() + " does not exist or is a directory");
						return;
					}
				}
				File file = new File(fileName);
				try {
					elementContentProvider.streamToFile(file);
				} catch (Exception e) {
					OseeLog.log(Activator.class, Level.SEVERE,
							"Could not start streaming", e);
					MessageDialog
							.openError(display.getActiveShell(),
									"Stream Error",
									"Could not stream to file. See Error Log for details");
					return;
				}
				streamToFileAction.setChecked(true);
				configureColumnAction.setEnabled(false);
				addElementAction.setEnabled(false);
				addHeaderElementAction.setEnabled(false);
				removeColumnAction.setEnabled(false);
				if (disableRendering) {
					viewer.getTable().update();
					viewer.getTable().setRedraw(false);
				}
			}
		};
		if (display.getThread() != Thread.currentThread()) {
			display.syncExec(task);
		} else {
			task.run();
		}
	}
	
	

	public void stopStreaming() {
		Displays.ensureInDisplayThread(new Runnable() {

			@Override
			public void run() {
				try {
					viewer.getTable().update();
					viewer.getTable().setRedraw(true);		
					elementContentProvider.streamToFile(null);
					configureColumnAction.setEnabled(true);
					addElementAction.setEnabled(true);
					addHeaderElementAction.setEnabled(true);
					removeColumnAction.setEnabled(true);
					streamToFileAction.setChecked(false);
					
				} catch (Exception e) {
					OseeLog.log(Activator.class, Level.SEVERE,
							"Erri while attempting to stop streaming", e);
				}
			}

		});

	}

	@Override
	public void dispose() {
		tracker.close();
		copyAction.dispose();
		super.dispose();
	}

	public void serviceStarted(final IOteMessageService service) {
		Displays.pendInDisplayThread(new Runnable() {

			@Override
			public void run() {
				addElementAction.setEnabled(true);
				addHeaderElementAction.setEnabled(true);
				viewer.setInput(service);
				if (getViewSite().getSecondaryId() == null) {
					elementContentProvider.loadLastColumns();
				}
			}

		});

	}

	public void serviceStopping(final IOteMessageService service) {
		Displays.pendInDisplayThread(new Runnable() {
			@Override
			public void run() {
				addElementAction.setEnabled(false);
				addHeaderElementAction.setEnabled(false);
				if (viewer.getTable().isDisposed()) {
					return;
				}
				viewer.setInput(null);
			}
		});

	}
	
	public void addElement(ElementPath elementPath){
		elementContentProvider.add(elementPath);
	}
	
}