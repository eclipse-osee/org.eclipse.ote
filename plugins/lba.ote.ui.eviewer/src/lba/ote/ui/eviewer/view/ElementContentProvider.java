/*
 * Created on Apr 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package lba.ote.ui.eviewer.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import lba.ote.ui.eviewer.Activator;
import lba.ote.ui.eviewer.jobs.CopyToClipboardJob;
import lba.ote.ui.eviewer.jobs.CopyToCsvFileJob;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.ote.client.msg.IOteMessageService;
import org.eclipse.osee.ote.message.ElementPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Ken J. Aguilar
 */
public class ElementContentProvider implements Listener, IStructuredContentProvider, IUpdateListener {
	private static final String INTERNAL_FILE_NAME = "element_viewer_column_state.csv";
	private final ArrayList<SubscriptionDetails> subscriptions = new ArrayList<SubscriptionDetails>(32);
	private TableViewer viewer;
	private IOteMessageService service;
	private final ArrayList<ElementColumn> elementColumns = new ArrayList<ElementColumn>();
	private final int limit;
	private ViewRefresher refresher;
	private boolean autoReveal = true;

	private volatile PrintWriter streamToFileWriter = null;

	private HashMap<ElementColumn, Integer> valueMap = new HashMap<ElementColumn, Integer>();

	private ElementUpdate last = null;
	
	public ElementContentProvider(int limit) {
		this.limit = limit;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return refresher.getUpdates();
	}

	@Override
	public void dispose() {
		if (refresher != null) {
			refresher.clearUpdates();
			refresher.stop();
		}
		disposeAllColumns();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TableViewer) viewer;
		if (newInput != null) {
			if (oldInput != null) {
				dispose();
			}
			service = (IOteMessageService) newInput;
			indexAndSortColumns();
			last = null;
			refresher = new ViewRefresher(this.viewer, limit);
			refresher.setAutoReveal(autoReveal);
			refresher.start();
		}
	}

	public void add(ElementPath path) {
		add(path, true);
	}

	public synchronized void add(ElementPath path, boolean save) {
		if (findColumn(path)) {
			return;
		}
		create(path);
		if (save) {
			updateInternalFile();
		}
	}

	private ElementColumn create(ElementPath path) {
		ElementColumn newColumn = new ElementColumn(viewer, elementColumns.size(), path);
		SubscriptionDetails details = findDetails(path.getMessageClass());
		if (details == null) {
			details = new SubscriptionDetails(service.subscribe(path.getMessageClass()), this);
			subscriptions.add(details);
		}
		details.addColumn(newColumn);
		elementColumns.add(newColumn);
		indexAndSortColumns();
		newColumn.addMoveListener(this);
		return newColumn;
	}

	private boolean findColumn(ElementPath path) {
		String encodedPath = path.encode();
		for (ElementColumn column : elementColumns) {
			if (column.getElementPath().encode().equals(encodedPath)) {
				return true;
			}
		}
		return false;
	}

	private synchronized void add(Collection<ElementPath> columns, boolean save) {

		HashSet<String> existingColumns = new HashSet<String>();
		for (ElementColumn column : elementColumns) {
			existingColumns.add(column.getElementPath().encode());
		}
		for (ElementPath path : columns) {
			if (existingColumns.contains(path.encode())) {
				continue;
			}
			create(path);
		}
		if (save) {
			updateInternalFile();
		}
	}

	public SubscriptionDetails findDetails(String messageClassName) {
		for (SubscriptionDetails details : subscriptions) {
			if (details.getSubscription().getMessageClassName().equals(messageClassName)) {
				return details;
			}
		}
		return null;
	}

	@Override
	public  synchronized void update(SubscriptionDetails details) {
		final ElementUpdate update;
		if (last == null) {
			update = new ElementUpdate(valueMap, elementColumns);
		} else {
			update = last.next(valueMap, elementColumns);
		}
		last = update;
		refresher.addUpdate(update);
		writeToStream(update);
	}

	private void writeToStream(ElementUpdate update) {
		if (streamToFileWriter != null) {
			int i;
			for (i = 0; i < elementColumns.size() - 1; i++) {
				Object o = update.getValue(elementColumns.get(i));
				if (o != null) {
					streamToFileWriter.append(o.toString());
				}
				streamToFileWriter.append(',');
			}
			Object o = update.getValue(elementColumns.get(i));
			if (o != null) {
				streamToFileWriter.append(o.toString());
			}
			streamToFileWriter.append('\n');
		}
	}

	public void clearAllUpdates() {
		refresher.clearUpdates();
		last = null;
	}

	/**
	 * @return the autoReveal
	 */
	public boolean isAutoReveal() {
		return autoReveal;
	}

	private void indexAndSortColumns() {
		
		valueMap = new HashMap<ElementColumn, Integer>();
		ColumnSorter sorter = new ColumnSorter(viewer.getTable().getColumnOrder());
		for (ElementColumn column : elementColumns) {
			valueMap.put(column, sorter.orderOf(column.recheckIndex()));
		}

		sorter.sort(elementColumns);

	}

	/**
	 * @param autoReveal the autoReveal to set
	 */
	public void setAutoReveal(boolean autoReveal) {
		this.autoReveal = autoReveal;
		if (refresher != null) {
			refresher.setAutoReveal(autoReveal);
		}
	}

	public synchronized List<ElementColumn> getColumns() {
		return new ArrayList<ElementColumn>(elementColumns);
	}

	public synchronized void putColumnsInList(List<ElementColumn> list) {
		list.addAll(elementColumns);
	}
	public synchronized void removeColumn(ElementColumn column) {
		if (elementColumns.remove(column)) {
			column.removeMoveListener(this);
			SubscriptionDetails subscription = findDetails(column.getMessageClassName());
			if (subscription.removeColumn(column)) {
				subscription.dispose();
				subscriptions.remove(subscription);
			}
			indexAndSortColumns();
			updateInternalFile();
		}
	}
	
	private void enableMoveListeneing(boolean enable) {
		for (ElementColumn column : elementColumns) {
			if (enable) {
				column.addMoveListener(this);
			} else {
				column.removeMoveListener(this);
			}
		}
	}

	public synchronized void removeColumn(Collection<ElementColumn> columns) {
		enableMoveListeneing(false);
		elementColumns.removeAll(columns);
		viewer.getTable().setRedraw(false);
		for (ElementColumn column : columns) {
			SubscriptionDetails subscription = findDetails(column.getMessageClassName());
			if (subscription.removeColumn(column)) {
				subscription.dispose();
				subscriptions.remove(subscription);
			}
			Display.getCurrent().readAndDispatch();
		}
		enableMoveListeneing(true);
		indexAndSortColumns();
		viewer.getTable().setRedraw(true);
		updateInternalFile();
	} 
	private void updateInternalFile() {
		try {
			saveColumnsToFile(OseeData.getFile(INTERNAL_FILE_NAME));
		} catch (Exception e) {
			OseeLog.log(Activator.class, Level.SEVERE, "could not write columns", e);
		}
	}
	public synchronized void saveColumnsToFile(File file) throws FileNotFoundException, IOException {
		PrintWriter writer = new PrintWriter(new FileOutputStream(file));
		try {
			if (elementColumns.isEmpty()) {
				return;
			}
			int i;
			for (i = 0; i < elementColumns.size() - 1; i++) {
				ElementColumn column = elementColumns.get(i);
				writer.write(column.getElementPath().encode());            
				writer.write('=');
				writer.write(column.isActive() ? "active" : "inactive");
				writer.write(',');
			}
			ElementColumn column = elementColumns.get(i);
			writer.write(column.getElementPath().encode());
			writer.write('=');
			writer.write(column.isActive() ? "active" : "inactive");
			writer.write('\n');
			writer.flush();
		} finally {
			writer.close();
		}
	}

	private void loadColumns(String[] columnNames) {
		LinkedList<ElementPath> columnsToAdd = new LinkedList<ElementPath>();
		HashSet<ElementPath> inactiveColumns = new HashSet<ElementPath>();

		viewer.getTable().setRedraw(false);
		for (String name : columnNames) {
			String[] parts = name.split("=");    
			ElementPath path = ElementPath.decode(parts[0]);
			columnsToAdd.add(path);
			if (parts.length > 1 && parts[1].equals("inactive")) {
				inactiveColumns.add(path);
			}
		}
		add(columnsToAdd, false);


		for (ElementColumn column : elementColumns) {
			if (inactiveColumns.contains(column.getElementPath())) {
				column.setActive(false);
			}
		}
		viewer.getTable().setRedraw(true);
		updateInternalFile();
	}

	public void loadColumnsFromFile(File file) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		try {
			String line = reader.readLine();
			if (line == null) {
				// empty file
				return;
			}
			String[] columnNames = line.split(",");
			loadColumns(columnNames);
		} finally {
			reader.close();
		}
	}

	public synchronized void removeAll() {
		disposeAllColumns();
		updateInternalFile();
		refresher.clearUpdates();
	}

	private void disposeAllColumns() {
		// we must remove all the move listeners first before we dispose or else bad things happen
		enableMoveListeneing(false);
		for (SubscriptionDetails details : subscriptions) {
			details.dispose();
		}
		subscriptions.clear();
		elementColumns.clear();
	}

	public void toClipboard(Clipboard clipboard) {
		CopyToClipboardJob job = new CopyToClipboardJob(clipboard, elementColumns, refresher.getUpdates());
		job.schedule();
	}

	public void toCsv(File file) throws IOException {
		CopyToCsvFileJob job = new CopyToCsvFileJob(file, elementColumns, refresher.getUpdates());
		job.schedule();
	}

	public void loadLastColumns() {
		try {
			loadColumnsFromFile(OseeData.getFile(INTERNAL_FILE_NAME));

		} catch (Exception e) {
			OseeLog.log(Activator.class, Level.SEVERE, "could not read columns file", e);
		}
	}

	public synchronized void streamToFile(File file) throws FileNotFoundException, IOException {

		if (streamToFileWriter != null) {
			// stop streaming
			streamToFileWriter.close();
			streamToFileWriter = null;
			setMoveableColumns(true);
		}
		if (file == null) {
			return;
		}
		setMoveableColumns(false);
		streamToFileWriter = new PrintWriter(new FileOutputStream(file));
		int i;
		for (i = 0; i < elementColumns.size() - 1; i++) {
			streamToFileWriter.write(elementColumns.get(i).getName());
			streamToFileWriter.write(',');
		}
		streamToFileWriter.write(elementColumns.get(i).getName());
		streamToFileWriter.write('\n');
		streamToFileWriter.flush();

	}

	private void setMoveableColumns(boolean moveable) {
		for (ElementColumn column : elementColumns) {
			column.getColumn().setMoveable(moveable);
		}
	}

	/**
	 * handles the reordering of columns
	 */
	@Override
	public void handleEvent(Event event) {

		if (event.widget.isDisposed() || event.type != SWT.Move) {
			return;
		}
		synchronized (this) {
			indexAndSortColumns();
			updateInternalFile();
		}

	}

	public TableViewer getViewer() {
		return viewer;
	}
}
