/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.ui.message.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.ote.message.lookup.MessageInput;
import org.eclipse.ote.message.lookup.MessageInputItem;
import org.eclipse.ote.message.lookup.MessageInputUtil;
import org.eclipse.ote.message.lookup.MessageLookup;
import org.eclipse.ote.ui.message.util.CheckedSelectionDialog;
import org.eclipse.ote.ui.message.view.internal.MessageInputComponent;
import org.eclipse.ote.ui.message.view.internal.MessageViewContentProvider;
import org.eclipse.ote.ui.message.view.internal.MessageViewLabelProvider;
import org.eclipse.ote.ui.message.view.internal.MessageViewServiceUtility;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;

public class MessageView extends ViewPart {
   public static final String VIEW_ID = "org.eclipse.ote.ui.message.view.MessageView";
   public static final String PLUGIN_ID = "org.eclipse.ote.ui.message.view";
   protected static final String SEARCHING_ALL_TYPES = "Current Filter:\nSearching All Message Types";

   private TreeViewer treeViewer;
   private Text searchText;
   private Action expandAction, collapseAction, filterAction;
   private Label startLabel;
   private Composite parentComposite;
   private Button searchButton;
   private Map<String, Boolean> filters;

   public MessageView() {
      super();
      filters = new HashMap<String, Boolean>();
   }

   @Override
   public void createPartControl(Composite parent) {
      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      layout.verticalSpacing = 0;
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      parent.setLayout(layout);
      parentComposite = new Composite(parent, SWT.NONE);
      GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
      parentComposite.setLayoutData(layoutData);
      Widgets.setFormLayout(parentComposite, 5, 5);

      startLabel = new Label(parentComposite, SWT.LEFT);
      Widgets.attachToParent(startLabel, SWT.TOP, 0, 0);
      Widgets.attachToParent(startLabel, SWT.LEFT, 0, 0);
      Widgets.attachToParent(startLabel, SWT.RIGHT, 50, 0);

      // Create the tree treeViewer as a child of the composite parent
      treeViewer = new TreeViewer(parentComposite);
      final Tree tree = treeViewer.getTree();
      Widgets.attachToControl(tree, startLabel, SWT.TOP, SWT.BOTTOM, 5);
      Widgets.attachToParent(tree, SWT.BOTTOM, 100, -50);
      Widgets.attachToParent(tree, SWT.LEFT, 0, 0);
      Widgets.attachToParent(tree, SWT.RIGHT, 100, 0);
      treeViewer.setUseHashlookup(true);
      treeViewer.setContentProvider(new MessageViewContentProvider());
      treeViewer.setLabelProvider(new MessageViewLabelProvider());
      tree.addMouseListener(new MouseAdapter() {

         @Override
         public void mouseDown(MouseEvent e) {
            if (e.button == 3) {
               Point p = new Point(e.x, e.y);
               final Menu contextMenu = getPopupMenu(tree.getParent());
               if (contextMenu != null) {
                  p = tree.toDisplay(p);
                  contextMenu.setLocation(p);
                  contextMenu.setVisible(true);
               }
            }
         }

      });

      /*
       * Create a text field to be used for filtering the elements displayed by the tree treeViewer
       */
      Group grp = new Group(parentComposite, SWT.NONE);
      Widgets.attachToControl(grp, tree, SWT.TOP, SWT.BOTTOM, 5);
      Widgets.attachToParent(grp, SWT.LEFT, 0, 0);
      Widgets.attachToParent(grp, SWT.RIGHT, 100, 0);
      layout = new GridLayout();
      layout.numColumns = 3;
      grp.setLayout(layout);
      Label l = new Label(grp, SWT.NULL);
      l.setText("Search:");
      l.setToolTipText("Enter a search string.\n* is the wildcard.\nAn integer will search message ids.\n* will return all messages.\n** will return all messages that have elements.");

      searchText = new Text(grp, SWT.SINGLE | SWT.BORDER);
      searchText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      searchText.addTraverseListener(new TraverseListener() {

         @Override
         public void keyTraversed(TraverseEvent event) {
            if (event.detail == SWT.TRAVERSE_RETURN) {
               search(searchText.getText());
            }
         }

      });

      searchText.addVerifyListener(new VerifyListener() {
         @Override
         public void verifyText(VerifyEvent e) {
            e.text = e.text.toUpperCase();
         }
      });

      searchButton = new Button(grp, SWT.PUSH);
      searchButton.setText("Go");
      searchButton.addSelectionListener(new SelectionListener() {

         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
         }

         @Override
         public void widgetSelected(SelectionEvent e) {
            search(searchText.getText());
         }
      });

      // Create menu, toolbars, filters, sorters.
      createActions();
      createMenus();
      createToolbar();

      treeViewer.expandToLevel(0);

   }

   /**
    * sets the filter for searches
    */
   private void search(final String searchPattern) {
      final Color bgColor = treeViewer.getTree().getBackground();
      treeViewer.getTree().setBackground(Displays.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
      searchText.setEnabled(false);
      searchButton.setEnabled(false);
      final Job searchJob = new Job("Searching Messages") {

    	  @Override
    	  public IStatus run(IProgressMonitor monitor) {
    		  try {
    			  String searchTxt = searchPattern;
    			  if (searchPattern.equals("")) {
    				  return new Status(IStatus.OK, PLUGIN_ID, "Empty Search String");
    			  }
    			  MessageLookup messageLookup = MessageViewServiceUtility.getService(MessageLookup.class);
    			  if(messageLookup != null){
    				  final List<MessageInputItem> results;
    				  List<String> searchFilters = new ArrayList<String>();
    				  for(Entry<String, Boolean> entry:filters.entrySet()){
    				     if(entry.getValue()){
    				        searchFilters.add(entry.getKey());
    				     }
    				  }
    				  if(isFiltered() && searchFilters.size() > 0){
    				     results = MessageInputUtil.messageLookupResultToMessageInputItem( messageLookup.lookup(searchTxt, searchFilters.toArray(new String[0])));
    				  } else {
    				     results = MessageInputUtil.messageLookupResultToMessageInputItem( messageLookup.lookup(searchTxt));
    				  }
    				  Displays.ensureInDisplayThread(new Runnable() {
    					  @Override
    					  public void run() {
    						  treeViewer.setInput(results);
    					  }
    				  });
    			  }
    			  return new Status(IStatus.OK, PLUGIN_ID, IStatus.OK, "", null);
    		  } catch (Throwable t) {
    			  OseeLog.log(MessageView.class, Level.SEVERE, "exception during search operation", t);
    			  return new Status(IStatus.CANCEL, PLUGIN_ID, IStatus.CANCEL, "", t);
    		  } finally {
    			  Displays.pendInDisplayThread(new Runnable() {
    				  @Override
    				  public void run() {
    					  treeViewer.getTree().setBackground(bgColor);
    					  searchText.setEnabled(true);
    					  searchButton.setEnabled(true);
    				  }
    			  });
    		  }
    	  }

      };
      searchJob.setUser(true);
      searchJob.schedule();
   }

   private Menu getPopupMenu(final Composite composite) {
      final IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
      MessageInputComponent messageInputComponent = MessageViewServiceUtility.getService(MessageInputComponent.class);
      
      if(selection.isEmpty() || messageInputComponent.getMessageInputs().size() == 0){
    	  return null;
      }
      final Menu previewMenu = new Menu(composite);
      for(final MessageInput messageInput: messageInputComponent.getMessageInputs()){
    	  MenuItem item = new MenuItem(previewMenu, SWT.CASCADE);
    	  item.setText("Add to " + messageInput.getLabel());
    	  item.addSelectionListener(new SelectionAdapter() {
    		  @Override
    		  public void widgetSelected(SelectionEvent e) {
    			  List<MessageInputItem> selectedItems = new ArrayList<MessageInputItem>();
    			  @SuppressWarnings("rawtypes")
    			  Iterator it = selection.iterator();
    			  while(it.hasNext()){
    				  Object selectedItem = it.next();
    				  if(selectedItem instanceof MessageInputItem){
    					  selectedItems.add((MessageInputItem)selectedItem);
    				  }
    			  }
    			  messageInput.add(selectedItems);
    		  }
    	  });
      }
      return previewMenu;
   }

   private void createActions() {
      final TreeViewer ftv = treeViewer;
      expandAction = new Action("Expand All") {

         @Override
         public void run() {
            treeViewer.getTree().setRedraw(false);
            ftv.expandAll();
            treeViewer.getTree().setRedraw(true);
         }
      };
      expandAction.setImageDescriptor(ImageManager.getImageDescriptor(OteMessageViewImage.EXPAND_STATE));
      expandAction.setToolTipText("Expand All");

      collapseAction = new Action("Collapse All") {

         @Override
         public void run() {
            treeViewer.getTree().setRedraw(false);
            ftv.collapseAll();
            treeViewer.getTree().setRedraw(true);
         }
      };
      collapseAction.setImageDescriptor(ImageManager.getImageDescriptor(OteMessageViewImage.COLLAPSE_STATE));
      collapseAction.setToolTipText("Collapse All");
      
      filterAction = new Action("Search Filters"){
         @Override
         public void run() {
            Shell shell = Displays.getActiveShell();
            MessageLookup messageLookup = MessageViewServiceUtility.getService(MessageLookup.class);
            for(String filter:messageLookup.getAvailableMessageTypes()){
               Boolean val = filters.get(filter);
               if(val == null){
                  filters.put(filter, true);
               } else {
                  filters.put(filter, val);
               }
            }
            CheckedSelectionDialog msgSelectionDialog = new CheckedSelectionDialog(shell, "Message Types To Search", filters);
            if (msgSelectionDialog.open() == Window.OK) {
               if(allOff()){
                  selectAllMemTypes();
               }
               filterAction.setToolTipText(getFilterTooltip(filters));
            }
         }

         private String getFilterTooltip(Map<String, Boolean> filters) {
            if(isFiltered()){
               StringBuilder sb = new StringBuilder();
               sb.append("Current Filter:\n");
               List<String> keys = new ArrayList<String>();
               keys.addAll(filters.keySet());
               Collections.sort(keys);
               for(String key:keys){
                  if(filters.get(key)){
                     sb.append("Searching ");
                  } else {
                     sb.append("Ignoring ");
                  }
                  sb.append(key);
                  sb.append(" Message Type\n");
               }
               return sb.toString();
            } else {
               return SEARCHING_ALL_TYPES;
            }
         }
      };
      filterAction.setToolTipText(SEARCHING_ALL_TYPES);
   }
   
   private void selectAllMemTypes(){
      for(Entry<String, Boolean> entry:filters.entrySet()){
         entry.setValue(true);
      }
   }
   
   private boolean isFiltered(){
      boolean allOn = true;
      for(Boolean selected:filters.values()){
         if(!selected){
            allOn = false;
            break;
         }
      }
      return !allOn;
   }
   
   private boolean allOff(){
      boolean allOff = true;
      for(Boolean selected:filters.values()){
         if(selected){
            allOff = false;
            break;
         }
      }
      return allOff;
   }

   private void createMenus() {
      IMenuManager rootMenuManager = getViewSite().getActionBars().getMenuManager();
      rootMenuManager.setRemoveAllWhenShown(true);
      rootMenuManager.addMenuListener(new IMenuListener() {

         @Override
         public void menuAboutToShow(IMenuManager mgr) {
            fillMenu(mgr);
         }
      });
      fillMenu(rootMenuManager);
   }

   private void fillMenu(IMenuManager rootMenuManager) {
      rootMenuManager.add(expandAction);
      rootMenuManager.add(collapseAction);
   }

   private void createToolbar() {
      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(filterAction);
      toolbarManager.add(expandAction);
      toolbarManager.add(collapseAction);
      
   }

   /*
    * @see IWorkbenchPart#setFocus()
    */
   @Override
   public void setFocus() {
      parentComposite.setFocus();
   }

   @Override
   public void dispose() {
      super.dispose();
   }

}