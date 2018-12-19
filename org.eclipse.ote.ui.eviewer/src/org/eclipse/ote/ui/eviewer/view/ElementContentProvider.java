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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.eclipse.ote.ui.eviewer.Constants;
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
   private final ArrayList<SubscriptionDetails> subscriptions = new ArrayList<SubscriptionDetails>(32);
   private TableViewer viewer;
   private IOteMessageService service;
   private final ArrayList<ViewerColumn> viewerColumns = new ArrayList<ViewerColumn>();
   private final ArrayList<ViewerColumnElement> elementColumns = new ArrayList<ViewerColumnElement>();
   private final int limit;
   private ViewRefresher refresher;
   private boolean autoReveal = true;
   private boolean showNumbersAsHex = false;
   private volatile PrintWriter streamToFileWriter = null;

   private HashMap<ViewerColumn, Integer> valueMap = new HashMap<ViewerColumn, Integer>();

   private RowUpdate last = null;
   private ReentrantLock streamWriteLock = new ReentrantLock();
   private volatile boolean acceptUpdates = true;
   private boolean showEnumAsNumber = false;
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
   public synchronized void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
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

         final RowUpdate update;
         if (last == null) {
            update = new RowUpdate(valueMap, viewerColumns);
         } else {
            update = last.next(valueMap, viewerColumns);
         }
         last = update;
         refresher.addUpdate(update);
         writeToStream(update);
      }
   }

   private void writeToStream(RowUpdate update) {
      try{
         streamWriteLock.lock();
         if (streamToFileWriter != null) {
            int i;
            //            streamToFileWriter.append(Long.toString(timeColumn.getLong()));
            //            streamToFileWriter.append(',');
            //            streamToFileWriter.append(Long.toString(timeDeltaColumn.getLong()));
            //            streamToFileWriter.append(',');
            for (i = 0; i < viewerColumns.size() - 1; i++) {
               Object o = update.getValue(viewerColumns.get(i));
               if (o != null) {
                  streamToFileWriter.append('"').append(o.toString()).append('"');
               }
               streamToFileWriter.append(',');
            }
            Object o = update.getValue(viewerColumns.get(i));
            if (o != null) {
               streamToFileWriter.append('"').append(o.toString()).append('"');
            }
            streamToFileWriter.append('\n');
         }
      } finally {
         streamWriteLock.unlock();
      }
   }

   public void clearAllUpdates() {
      refresher.clearUpdates();
      for(ViewerColumnElement column:elementColumns){
         column.getColumnElement().clearValue();
      }
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

   public void setEnumOutputNumber(boolean isNumber){
      for(ViewerColumnElement col:elementColumns){
         col.setEnumOutputNumber(isNumber);
      }
      this.showEnumAsNumber  = isNumber;
   }

   public void showNumbersAsHex(boolean showNumbersAsHex){
      for(ViewerColumnElement col:elementColumns){
         col.setShowNumbersAsHex(showNumbersAsHex);
      }
      this.showNumbersAsHex  = showNumbersAsHex;
   }

   public boolean updateInternalFile() {
      try {
         saveColumnsToFile(OseeData.getFile(Constants.INTERNAL_FILE_NAME));
         return true;
      } catch (Exception e) {
         OseeLog.log(Activator.class, Level.SEVERE, "could not write columns", e);
         return false;
      }
   }
   public synchronized void saveColumnsToFile(File file) throws FileNotFoundException, IOException {
      PrintWriter writer = new PrintWriter(new FileOutputStream(file));
      try {
         if (viewerColumns.isEmpty()) {
            return;
         }
         int[] ordering = viewer.getTable().getColumnOrder();
         for (int i = 0; i < elementColumns.size(); i++) {
            // the indices for the actual ordering are 2 over from where you would expect (think 2-indexed instead of 0)
            // but the elementColumns is a 0-indexed data structure.
            ViewerColumnElement column = elementColumns.get((ordering[i+2] - 2));
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

   public void loadColumns(List<ColumnEntry> columnEntries) {
      LinkedList<ElementPath> columnsToAdd = new LinkedList<ElementPath>();
      HashSet<ElementPath> inactiveColumns = new HashSet<ElementPath>();

      viewer.getTable().setRedraw(false);
      for (ColumnEntry entry : columnEntries) {
         columnsToAdd.add(entry.getPath());
         if (!entry.isActive()) {
            inactiveColumns.add(entry.getPath());
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

   public synchronized void removeAll() {
      refresher.clearUpdates();
      disposeAllColumns();
      updateInternalFile();
   }

   private void disposeAllColumns() {
      // we must remove all the move listeners first before we dispose or else bad things happen
      enableMoveListeneing(false);
      for (SubscriptionDetails details : subscriptions) {
         details.dispose();
      }
      subscriptions.clear();

      elementColumns.clear();

      viewerColumns.clear();
      viewerColumns.add(timeColumn);
      viewerColumns.add(timeDeltaColumn);
      viewer.refresh();
   }

   public void toClipboard(Clipboard clipboard) {
      CopyToClipboardJob job = new CopyToClipboardJob(clipboard, viewerColumns, refresher.getUpdates());
      job.schedule();
   }

   public void toCsv(File file) {
      CopyToCsvFileJob job = new CopyToCsvFileJob(file, viewerColumns, refresher.getUpdates());
      job.schedule();
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
         //         streamToFileWriter.write(timeColumn.getName());
         //         streamToFileWriter.write(',');
         //         streamToFileWriter.write(timeDeltaColumn.getName());
         //         streamToFileWriter.write(',');
         for (i = 0; i < viewerColumns.size() - 1; i++) {
            streamToFileWriter.write(viewerColumns.get(i).getVerboseName());
            streamToFileWriter.write(',');
         }
         if (viewerColumns.size() > 0) {

            streamToFileWriter.write(viewerColumns.get(i).getVerboseName());
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
