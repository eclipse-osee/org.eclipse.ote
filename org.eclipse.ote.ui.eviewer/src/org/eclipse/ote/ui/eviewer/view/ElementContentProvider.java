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
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.ote.client.msg.IOteMessageService;
import org.eclipse.osee.ote.message.ElementPath;
import org.eclipse.ote.ui.eviewer.Activator;
import org.eclipse.ote.ui.eviewer.jobs.CopyToClipboardJob;
import org.eclipse.ote.ui.eviewer.jobs.CopyToCsvFileJob;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
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
   private final ArrayList<ViewerColumn> viewerColumns = new ArrayList<ViewerColumn>();
   private final ArrayList<ViewerColumnElement> elementColumns = new ArrayList<ViewerColumnElement>();
   private final int limit;
   private ViewRefresher refresher;
   private boolean autoReveal = true;


   private volatile PrintWriter streamToFileWriter = null;

   private HashMap<ViewerColumn, Integer> valueMap = new HashMap<ViewerColumn, Integer>();

   private ElementUpdate last = null;
   private ReentrantLock streamWriteLock = new ReentrantLock();
   private volatile boolean acceptUpdates = true;

   private ViewerColumnLong timeColumn;
   private ViewerColumnLong timeDeltaColumn;

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

   public void forceUpdate(){
      refresher.forceUpdate();
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      this.viewer = (TableViewer) viewer;
      if (newInput != null) {
         if (oldInput != null) {
            dispose();
         }

         timeColumn = new ViewerColumnLong(this.viewer, viewerColumns.size(), "Env Time", "Test environment time (ms)");
         viewerColumns.add(timeColumn);
         timeDeltaColumn = new ViewerColumnLong(this.viewer, viewerColumns.size(), "Time Delta", "Test environment time since previous update (ms)");
         viewerColumns.add(timeDeltaColumn);

         service = (IOteMessageService) newInput;
         indexAndSortColumns();
         last = null;
         refresher = new ViewRefresher(this.viewer, limit);
         refresher.setAutoReveal(autoReveal);
         refresher.start();
      }
   }

   public ViewerColumnLong getTimeColumn() {
      return timeColumn;
   }

   public ViewerColumnLong getTimeDeltaColumn() {
      return timeDeltaColumn;
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

   private ViewerColumn create(ElementPath path) {
      ViewerColumnElement newColumn = new ViewerColumnElement(viewer, viewerColumns.size(), path);
      SubscriptionDetails details = findDetails(path.getMessageClass());
      if (details == null) {
         details = new SubscriptionDetails(service.subscribe(path.getMessageClass()), this);
         subscriptions.add(details);
      }
      details.addColumn(newColumn.getColumnElement());
      viewerColumns.add(newColumn);
      elementColumns.add(newColumn);
      indexAndSortColumns();
      newColumn.addMoveListener(this);
      return newColumn;
   }

   private boolean findColumn(ElementPath path) {
      String encodedPath = path.encode();
      for (ViewerColumnElement column : elementColumns) {
         if (column.getColumnElement().getElementPath().encode().equals(encodedPath)) {
            return true;
         }
      }
      return false;
   }

   private synchronized void add(Collection<ElementPath> columns, boolean save) {

      HashSet<String> existingColumns = new HashSet<String>();
      for (ViewerColumnElement column : elementColumns) {
         existingColumns.add(column.getColumnElement().getElementPath().encode());
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
   public  synchronized void update(SubscriptionDetails details, long envTime) {
      if(acceptUpdates ){
         // TODO set the delta here before we change 'last'
         final long lastTime = timeColumn.getLong();
         if (lastTime != 0) {
            timeDeltaColumn.setLong(envTime - lastTime);
         } else {
            timeDeltaColumn.setLong(0);
         }
         timeColumn.setLong(envTime);

         final ElementUpdate update;
         if (last == null) {
            update = new ElementUpdate(valueMap, viewerColumns);
         } else {
            update = last.next(valueMap, viewerColumns);
         }
         last = update;
         refresher.addUpdate(update);
         writeToStream(update);
      }
   }

   private void writeToStream(ElementUpdate update) {
      try{
         streamWriteLock.lock();
         if (streamToFileWriter != null) {
            int i;
            for (i = 0; i < viewerColumns.size() - 1; i++) {
               Object o = update.getValue(viewerColumns.get(i));
               if (o != null) {
                  streamToFileWriter.append(o.toString());
               }
               streamToFileWriter.append(',');
            }
            Object o = update.getValue(viewerColumns.get(i));
            if (o != null) {
               streamToFileWriter.append(o.toString());
            }
            streamToFileWriter.append('\n');
         }
      } finally {
         streamWriteLock.unlock();
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

      valueMap = new HashMap<ViewerColumn, Integer>();
      ColumnSorter sorter = new ColumnSorter(viewer.getTable().getColumnOrder());
      for (ViewerColumn column : viewerColumns) {
         valueMap.put(column, sorter.orderOf(column.recheckIndex()));
      }

      determineConflicts();

      sorter.sort(viewerColumns);
   }

   void determineConflicts() {
      Map<String, Boolean> conflicts = new HashMap<String, Boolean>();
      for (ViewerColumnElement column : elementColumns) {
         if(conflicts.containsKey(column.getName())){
            conflicts.put(column.getName(), true);
         } else {
            conflicts.put(column.getName(), false);
         }
      }
      for (ViewerColumnElement column : elementColumns) {
         if(conflicts.get(column.getName())){
            column.setDuplicateName(true);
         } else {
            column.setDuplicateName(false);
         }
      }
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

   public synchronized List<ViewerColumnElement> getElementColumns() {
      return new ArrayList<ViewerColumnElement>(elementColumns);
   }

   public synchronized List<ViewerColumn> getAllColumns() {
      return new ArrayList<ViewerColumn>(viewerColumns);
   }

   //   public synchronized void putColumnsInList(List<ViewerColumn> list) {
   //      list.addAll(viewerColumns);
   //   }
   //
   //   public synchronized void removeColumn(ViewerColumn column) {
   //      if (viewerColumns.remove(column)) {
   //         column.removeMoveListener(this);
   //         SubscriptionDetails subscription = findDetails(column.getMessageClassName());
   //         if (subscription.removeColumn(column)) {
   //            subscription.dispose();
   //            subscriptions.remove(subscription);
   //         }
   //         indexAndSortColumns();
   //         updateInternalFile();
   //      }
   //   }

   private void enableMoveListeneing(boolean enable) {
      for (ViewerColumn column : viewerColumns) {
         if (enable) {
            column.addMoveListener(this);
         } else {
            column.removeMoveListener(this);
         }
      }
   }

   public synchronized void removeColumn(Collection<ViewerColumnElement> columns) {
      enableMoveListeneing(false);
      viewerColumns.removeAll(columns);
      elementColumns.removeAll(columns);
      viewer.getTable().setRedraw(false);
      for (ViewerColumnElement column : columns) {
         SubscriptionDetails subscription = findDetails(column.getColumnElement().getMessageClassName());
         if (subscription.removeColumn(column.getColumnElement())) {
            subscription.dispose();
            subscriptions.remove(subscription);
         }
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
         if (viewerColumns.isEmpty()) {
            return;
         }
         int i;
         for (i = 0; i < elementColumns.size(); i++) {
            ViewerColumnElement column = elementColumns.get(i);
            writer.write(column.getColumnElement().getElementPath().encode());
            writer.write('=');
            writer.write(column.isActive() ? "active" : "inactive");
            if (i != elementColumns.size()-1) {
               writer.write(',');
            } else {
               writer.write('\n');
            }
         }
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


      for (ViewerColumnElement column : elementColumns) {
         if (inactiveColumns.contains(column.getColumnElement().getElementPath())) {
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
      viewerColumns.clear();
   }

   public void toClipboard(Clipboard clipboard) {
      CopyToClipboardJob job = new CopyToClipboardJob(clipboard, viewerColumns, refresher.getUpdates());
      job.schedule();
   }

   public void toCsv(File file) throws IOException {
      CopyToCsvFileJob job = new CopyToCsvFileJob(file, viewerColumns, refresher.getUpdates());
      job.schedule();
   }

   public void loadLastColumns() {
      try {
         File file = OseeData.getFile(INTERNAL_FILE_NAME);
         if (file.isFile()) {
            loadColumnsFromFile(file);
         }

      } catch (Exception e) {
         OseeLog.log(Activator.class, Level.SEVERE, "could not read columns file", e);
      }
   }

   public void streamToFile(File file) throws FileNotFoundException, IOException {
      try{
         streamWriteLock.lock();
         if (streamToFileWriter != null) {
            // stop streaming
            streamToFileWriter.flush();
            streamToFileWriter.close();
            streamToFileWriter = null;
         }
         if (file == null) {
            setMoveableColumns(true);
            return;
         }
         setMoveableColumns(false);

         streamToFileWriter = new PrintWriter(new FileOutputStream(file));
         int i;
         for (i = 0; i < viewerColumns.size() - 1; i++) {
            streamToFileWriter.write(viewerColumns.get(i).getName());
            streamToFileWriter.write(',');
         }
         if (viewerColumns.size() > 0) {
            streamToFileWriter.write(viewerColumns.get(i).getName());
            streamToFileWriter.write('\n');
         }

         streamToFileWriter.flush();
      } finally {
         streamWriteLock.unlock();
      }
   }

   private void setMoveableColumns(boolean moveable) {
      for (ViewerColumn column : viewerColumns) {
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

   public void setUpdateView(boolean updateView) {
      refresher.setUpdateView(updateView);
   }

   public void togglePauseUpdates() {
      acceptUpdates = !acceptUpdates;
   }
}
