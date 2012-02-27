package lba.ote.ui.eviewer.view;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import lba.ote.ui.eviewer.Activator;
import lba.ote.ui.eviewer.ClientMessageServiceTracker;
import lba.ote.ui.eviewer.action.AddElementAction;
import lba.ote.ui.eviewer.action.ClearAllUpdatesAction;
import lba.ote.ui.eviewer.action.ConfigureColumnsAction;
import lba.ote.ui.eviewer.action.CopyAllAction;
import lba.ote.ui.eviewer.action.OpenNewElementViewer;
import lba.ote.ui.eviewer.action.RemoveColumnAction;
import lba.ote.ui.eviewer.action.SaveLoadAction;
import lba.ote.ui.eviewer.action.SetActiveColumnAction;
import lba.ote.ui.eviewer.action.StreamToFileAction;
import lba.ote.ui.eviewer.action.ToggleAutoRevealAction;

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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained from the model. The
 * sample creates a dummy model on the fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be presented in the view. Each view can present the
 * same model objects using different labels and icons, if needed. Alternatively, a single label provider can be shared
 * between views in order to ensure that objects of the same type are presented in the same way everywhere.
 * <p>
 */

public class ElementViewer extends ViewPart {
	public static final String VIEW_ID = "lba.ote.ui.eviewer.views.ElementViewer";
	private TableViewer viewer;
	private AddElementAction addElementAction;
	private ClearAllUpdatesAction clearAllUpdatesAction;
	private ToggleAutoRevealAction toggleAutoRevealAction;
	private RemoveColumnAction removeColumnAction;
	private CopyAllAction copyAction;
	private SetActiveColumnAction activeColumnAction;
	private SaveLoadAction saveLoadAction;
	private StreamToFileAction streamToFileAction;
	private ConfigureColumnsAction configureColumnAction;
	private final ElementContentProvider elementContentProvider = new ElementContentProvider(8194);
	private final ClientMessageServiceTracker tracker;

	/**
	 * The constructor.
	 */
	public ElementViewer() {
		tracker = new ClientMessageServiceTracker(this);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.DOUBLE_BUFFERED | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
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
		addElementAction = new AddElementAction(elementContentProvider);
		addElementAction.setEnabled(false);
		clearAllUpdatesAction = new ClearAllUpdatesAction(elementContentProvider);
		toggleAutoRevealAction = new ToggleAutoRevealAction(elementContentProvider);
		activeColumnAction = new SetActiveColumnAction(elementContentProvider);
		removeColumnAction = new RemoveColumnAction(elementContentProvider);
		saveLoadAction = new SaveLoadAction(elementContentProvider);
		streamToFileAction = new StreamToFileAction(this);

		copyAction = new CopyAllAction(Display.getDefault(), elementContentProvider);
		configureColumnAction = new ConfigureColumnsAction(elementContentProvider);
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

	public void startStreaming(String columnSetFile, String fileName) throws IOException{
		if (columnSetFile != null) {
			elementContentProvider.clearAllUpdates();
			File file = new File(columnSetFile);
			if (file.exists() && file.isFile()) {
				try {
					elementContentProvider.removeAll();
					elementContentProvider.loadColumnsFromFile(file);
				} catch (IOException ex) {
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Could not save file:\n" + file.getAbsolutePath());
				}
			}
		}
		File file = new File(fileName);
		elementContentProvider.streamToFile(file);
		streamToFileAction.setChecked(true);
		configureColumnAction.setEnabled(false);
		addElementAction.setEnabled(false);
		removeColumnAction.setEnabled(false);
	}


	public void stopStreaming() {
		try {
			elementContentProvider.streamToFile(null);
			configureColumnAction.setEnabled(true);
			addElementAction.setEnabled(true);
			removeColumnAction.setEnabled(true);
			streamToFileAction.setChecked(false);
		} catch (Exception e) {
			OseeLog.log(Activator.class, Level.SEVERE, "Erri while attempting to stop streaming", e);
		}	 }
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
				if (viewer.getTable().isDisposed()) {
					return;
				}
				viewer.setInput(null);
			}
		});

	}
}