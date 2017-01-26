package org.eclipse.ote.ui.message.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.ote.message.lookup.MessageLookupResult;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;

public class MessageSelectComposite extends Composite{

	
	private final TableViewer viewer;

	private final HashSet<MessageLookupResult> items = new HashSet<MessageLookupResult>();

	private final AddMessagesAction addMessageAction;
	private final DeleteMessagesAction deleteMessageAction;
	private final DeleteAllMessagesAction deleteAllMessagesAction;
	
//	private Button browseBtn;

	public MessageSelectComposite(Composite parent, int style) {
		super(parent, style);
		Widgets.setFormLayout(this, 5, 5);
//		Label label = new Label(this, SWT.RIGHT);
//		label.setText("Output File:");
		
//		outputFilePathTxt = new Text(this, SWT.SINGLE | SWT.BORDER);
//		outputFilePathTxt.addModifyListener(new ModifyListener() {
//			
//			@Override
//			public void modifyText(ModifyEvent e) {
////				updateUiStatus();
//			}
//		});
//		browseBtn = new Button(this, SWT.PUSH);
//		browseBtn.setText("Browse");
//		browseBtn.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				doBrowseForFile();
//			}
//		});
		addMessageAction = new AddMessagesAction(this);
      deleteMessageAction = new DeleteMessagesAction(this);
      deleteAllMessagesAction = new DeleteAllMessagesAction(this);
      
		Label titleLabel = new Label(this, SWT.LEFT);
		titleLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		titleLabel.setText("Selected Messages");		
		titleLabel.setBackground(getBackground());
		viewer = new TableViewer(this, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new LabelProvider() {
			
			@Override
			public String getText(Object element) {
			   MessageLookupResult item = (MessageLookupResult) element;
				return String.format("%s [%s]", item.getMessageName(), item.getMessageType());
			}
			
			
		});
		
		
		ActionContributionItem addItem = new ActionContributionItem(addMessageAction);
		addItem.fill(this);
		Button addButton = (Button)addItem.getWidget();
		
		ActionContributionItem delItem = new ActionContributionItem(deleteMessageAction);
		delItem.fill(this);
      Button delButton = (Button)delItem.getWidget();
      
      ActionContributionItem delAllItem = new ActionContributionItem(deleteAllMessagesAction);
      delAllItem.fill(this);
      Button delAllButton = (Button)delAllItem.getWidget();
		
		
//		Label statusLabel = new Label(this, SWT.RIGHT);
//		statusLabel.setText("Status:");
//		
//		statusValueLabel = new Label(this, SWT.LEFT);
//		statusValueLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		
//		Widgets.attachToParent(outputFilePathTxt, SWT.TOP, 0, 5);
//		Widgets.attachToControl(label, outputFilePathTxt, SWT.TOP, SWT.CENTER, 0);
//		Widgets.attachToControl(browseBtn, outputFilePathTxt, SWT.TOP, SWT.CENTER, 0);
//		
//		Widgets.attachToParent(label, SWT.LEFT, 0, 0);
//		Widgets.attachToControl(outputFilePathTxt, label, SWT.LEFT, SWT.RIGHT, 5);
//		Widgets.attachToControl(outputFilePathTxt, browseBtn, SWT.RIGHT, SWT.LEFT, -5);

//		Widgets.attachToParent(browseBtn, SWT.RIGHT, 100, 0);
//
//		Widgets.attachToControl(titleLabel, browseBtn, SWT.TOP, SWT.BOTTOM, 5);
		Widgets.attachToParent(titleLabel, SWT.LEFT, 0, 0);
		Widgets.attachToParent(titleLabel, SWT.RIGHT, 100, 0);

		Widgets.attachToControl(viewer.getControl(), titleLabel, SWT.TOP, SWT.BOTTOM, 0);
		Widgets.attachToParent(viewer.getControl(), SWT.LEFT, 0, 0, 400, 600);
//		final Object ld = viewer.getControl().getLayoutData();
//		final FormData fd = ld != null ? (FormData) ld : new FormData();
//      fd.bottom = new FormAttachment(100, 100, 0);
      
      Widgets.attachToParent(viewer.getControl(), SWT.BOTTOM, 100, 0);
		
		Widgets.attachToParent(addButton, SWT.RIGHT, 100, 0, 400, 600);
		Widgets.attachToParent(delButton, SWT.RIGHT, 100, 0, 400, 600);
		Widgets.attachToParent(delAllButton, SWT.RIGHT, 100, 0, 400, 600);
		
		Widgets.attachToControl(viewer.getControl(), addButton, SWT.RIGHT, SWT.LEFT, 0);
		Widgets.attachToControl(viewer.getControl(), delButton, SWT.RIGHT, SWT.LEFT, 0);
		Widgets.attachToControl(viewer.getControl(), delAllButton, SWT.RIGHT, SWT.LEFT, 0);

		Widgets.attachToControl(addButton, titleLabel, SWT.TOP, SWT.BOTTOM, 0);
		Widgets.attachToControl(delButton, addButton, SWT.TOP, SWT.BOTTOM, 0);
		Widgets.attachToControl(delAllButton, delButton, SWT.TOP, SWT.BOTTOM, 0);
		
		hookContextMenu();
	}
	
	private void hookContextMenu() {
      MenuManager menuMgr = new MenuManager("#PopupMenu");
      menuMgr.setRemoveAllWhenShown(true);
      menuMgr.addMenuListener(new IMenuListener() {
         @Override
         public void menuAboutToShow(IMenuManager manager) {
            manager.add(addMessageAction);
            manager.add(deleteMessageAction);
            manager.add(deleteAllMessagesAction);
         }
      });
      Menu menu = menuMgr.createContextMenu(this.getViewer().getControl());
      this.getViewer().getControl().setMenu(menu);
   }

	
//	private void doBrowseForFile() {
//		FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
//		dialog.setFilterExtensions(new String[]{"*.bmr"});
//		dialog.setFilterIndex(0);
//		dialog.setOverwrite(false);
//		String path = dialog.open();
//		if (path != null) {
//			outputFilePathTxt.setText(path);
//		}
//	}
	
	TableViewer getViewer() {
		return viewer;
	}

	void addMessages(Collection<MessageLookupResult> messages) {
		items.addAll(messages);
		setInput();
	}
	
	void deleteAll() {
		items.clear();
		setInput();
	}
	
	void deleteSelection() {
		items.removeAll(Arrays.asList(((IStructuredSelection)viewer.getSelection()).toArray()));
		setInput();
	}
	
	private void setInput() {
		viewer.setInput(items.toArray());
	}
	
//	private static boolean isValidPath(String string) {
//		if (string == null || string.isEmpty()) {
//			return false;
//		}
//		File file = new File(string);
//		// check parent directory
//		File dir = file.getParentFile();
//		return dir != null && dir.isDirectory() && dir.exists();
//	}
	
	public AddMessagesAction getAddMessageAction() {
		return addMessageAction;
	}

	public DeleteMessagesAction getDeleteMessageAction() {
		return deleteMessageAction;
	}

	public DeleteAllMessagesAction getDeleteAllMessagesAction() {
		return deleteAllMessagesAction;
	}

   public HashSet<MessageLookupResult> getMessages() {
      return items;
   }

   public void setMessages(HashSet<MessageLookupResult> messages) {
      if(messages != null){
         items.addAll(messages);
         setInput();
      }
   }

}
