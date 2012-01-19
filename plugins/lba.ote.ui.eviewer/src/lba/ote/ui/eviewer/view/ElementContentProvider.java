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
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import lba.ote.ui.eviewer.Activator;
import lba.ote.ui.eviewer.jobs.CopyToClipboardJob;
import lba.ote.ui.eviewer.jobs.CopyToCsvFileJob;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ote.client.msg.IOteMessageService;
import org.eclipse.osee.ote.message.ElementPath;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Ken J. Aguilar
 */
public class ElementContentProvider implements Listener, IStructuredContentProvider, IUpdateListener {
   private final ArrayList<SubscriptionDetails> subscriptions = new ArrayList<SubscriptionDetails>(32);
   private TableViewer viewer;
   private IOteMessageService service;
   private final ArrayList<ElementColumn> elementColumns = new ArrayList<ElementColumn>();
   private final int limit;
   private ViewRefresher refresher;
   private boolean autoReveal = true;

   private static final String COLUMN_NAME_STORE_SECTION = "lba.ote.ui.eviewer.views.ElementViewer.columnsection";
   private static final String COLUMN_NAMES_KEY = "lba.ote.ui.eviewer.views.ElementViewer.columnsection.names";

   private volatile PrintWriter streamToFileWriter = null;

   private HashMap<ElementColumn, Integer> valueMap = new HashMap<ElementColumn, Integer>();

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
         refresher = new ViewRefresher(this.viewer, limit);
         refresher.setAutoReveal(autoReveal);
         refresher.start();
      }
   }

   public void add(ElementPath path) {
      add(path, true);
   }

   public synchronized void add(ElementPath path, boolean save) {
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
      if (save) {
         IDialogSettings settings = Activator.getDefault().getDialogSettings();
         IDialogSettings section = settings.getSection(COLUMN_NAME_STORE_SECTION);
         if (section == null) {
            section = settings.addNewSection(COLUMN_NAME_STORE_SECTION);
         }
         String[] columnNames = section.getArray(COLUMN_NAMES_KEY);
         if (columnNames == null) {
            columnNames = new String[0];
         }
         LinkedHashSet<String> names = new LinkedHashSet<String>(Arrays.asList(columnNames));
         names.add(path.encode());
         section.put(COLUMN_NAMES_KEY, names.toArray(new String[names.size()]));
      }
   }

   private void add(Collection<ElementPath> columns) {
      add(columns, false);
   }

   private void add(Collection<ElementPath> columns, boolean save) {

      for (ElementPath path : columns) {
         add(path, false);
      }
      if (save) {
         IDialogSettings settings = Activator.getDefault().getDialogSettings();
         IDialogSettings section = settings.getSection(COLUMN_NAME_STORE_SECTION);
         if (section == null) {
            section = settings.addNewSection(COLUMN_NAME_STORE_SECTION);
         }
         String[] columnNames = section.getArray(COLUMN_NAMES_KEY);
         if (columnNames == null) {
            columnNames = new String[0];
         }
         LinkedHashSet<String> names = new LinkedHashSet<String>(Arrays.asList(columnNames));
         for (ElementPath path : columns) {
            names.add(path.encode());
         }
         section.put(COLUMN_NAMES_KEY, names.toArray(columnNames));
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
   public  synchronized void update(SubscriptionDetails details, BitSet deltaSet) {
      final ElementUpdate update = new ElementUpdate(valueMap, elementColumns, deltaSet);

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

   public synchronized void removeColumn(ElementColumn column) {
      if (elementColumns.remove(column)) {
         column.removeMoveListener(this);
         SubscriptionDetails subscription = findDetails(column.getMessageClassName());
         if (subscription.removeColumn(column)) {
            subscription.dispose();
            subscriptions.remove(subscription);
         }
         indexAndSortColumns();
         IDialogSettings settings = Activator.getDefault().getDialogSettings();
         IDialogSettings section = settings.getSection(COLUMN_NAME_STORE_SECTION);
         if (section == null) {
            return;
         }
         String[] columnNames = section.getArray(COLUMN_NAMES_KEY);
         if (columnNames == null) {
            return;
         }
         LinkedHashSet<String> names = new LinkedHashSet<String>(Arrays.asList(columnNames));
         names.remove(column.getElementPath().encode());
         section.put(COLUMN_NAMES_KEY, names.toArray(new String[names.size()]));
      }
   }

   public synchronized void saveColumnsToFile(File file) throws FileNotFoundException, IOException {
      PrintWriter writer = new PrintWriter(new FileOutputStream(file));
      try {
         int i;
         for (i = 0; i < elementColumns.size() - 1; i++) {
            ElementColumn column = elementColumns.get(i);
            writer.write(column.getElementPath().encode());
            writer.write(',');
         }
         ElementColumn column = elementColumns.get(i);
         writer.write(column.getElementPath().encode());
         writer.write('\n');
         writer.flush();
      } finally {
         writer.close();
      }
   }

   public void loadColumnsFromFile(File file) throws FileNotFoundException, IOException {
      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
      try {

         String[] columnNames = reader.readLine().split(",");
         LinkedList<ElementPath> columnsToAdd = new LinkedList<ElementPath>();
         for (String name : columnNames) {
            columnsToAdd.add(ElementPath.decode(name));
         }
         add(columnsToAdd, true);
         viewer.refresh();
      } finally {
         reader.close();
      }
   }

   public void removeAll() {
      disposeAllColumns();
      IDialogSettings settings = Activator.getDefault().getDialogSettings();
      IDialogSettings section = settings.getSection(COLUMN_NAME_STORE_SECTION);
      if (section == null) {
         return;
      }
      section.put(COLUMN_NAMES_KEY, new String[0]);
      refresher.clearUpdates();
   }

   private void disposeAllColumns() {
      // we must remove all the move listeners first before we dispose or else bad things happen
      for (SubscriptionDetails details : subscriptions) {
         for (ElementColumn c : details.getColumns()) {
            c.removeMoveListener(this);
         }
      }
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
      IDialogSettings settings = Activator.getDefault().getDialogSettings();
      settings = settings.getSection(COLUMN_NAME_STORE_SECTION);
      if (settings == null) {
         return;
      }
      String[] columnNames = settings.getArray(COLUMN_NAMES_KEY);
      if (columnNames == null) {
         return;
      }
      LinkedList<ElementPath> columnsToAdd = new LinkedList<ElementPath>();
      for (String path : columnNames) {
         columnsToAdd.add(ElementPath.decode(path));
      }
      add(columnsToAdd);
      viewer.refresh();
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
   public synchronized void handleEvent(Event event) {

      if (event.widget.isDisposed()) {
         return;
      }
      indexAndSortColumns();
      LinkedHashSet<String> set = new LinkedHashSet<String>();
      for (ElementColumn c : elementColumns) {
         set.add(c.getElementPath().encode());
      }

      IDialogSettings settings = Activator.getDefault().getDialogSettings();
      IDialogSettings section = settings.getSection(COLUMN_NAME_STORE_SECTION);
      if (section == null) {
         section = settings.addNewSection(COLUMN_NAME_STORE_SECTION);
      }
      section.put(COLUMN_NAMES_KEY, set.toArray(new String[set.size()]));
   }

   public TableViewer getViewer() {
      return viewer;
   }
}
