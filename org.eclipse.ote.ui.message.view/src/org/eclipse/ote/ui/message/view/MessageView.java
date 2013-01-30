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
import java.util.Iterator;
import java.util.List;
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
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.ote.message.lookup.MessageInput;
import org.eclipse.ote.message.lookup.MessageInputItem;
import org.eclipse.ote.message.lookup.MessageInputUtil;
import org.eclipse.ote.message.lookup.MessageLookup;
import org.eclipse.ote.ui.message.view.internal.MessageInputComponent;
import org.eclipse.ote.ui.message.view.internal.MessageViewContentProvider;
import org.eclipse.ote.ui.message.view.internal.MessageViewLabelProvider;
import org.eclipse.ote.ui.message.view.internal.ServiceUtility;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;

public class MessageView extends ViewPart {
   public static final String VIEW_ID = "org.eclipse.ote.ui.message.view.MessageView2";
   public static final String PLUGIN_ID = "org.eclipse.ote.ui.message.view";

   private TreeViewer treeViewer;
   private Text searchText;
   private Action expandAction, collapseAction;
   private Label startLabel;
   private Composite parentComposite;
   private Button searchButton;

   public MessageView() {
      super();
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
      l.setToolTipText("Enter a search string.\n* is the wildcard.\nAn integer will search message ids.");

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
    			  MessageLookup messageLookup = ServiceUtility.getService(MessageLookup.class);
    			  if(messageLookup != null){
    				  final List<MessageInputItem> results = MessageInputUtil.messageLookupResultToMessageInputItem( messageLookup.lookup(searchTxt));
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
      MessageInputComponent messageInputComponent = ServiceUtility.getService(MessageInputComponent.class);
      
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