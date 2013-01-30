package org.eclipse.ote.ui.message;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.ote.message.lookup.MessageInput;
import org.eclipse.ote.message.lookup.MessageInputItem;
import org.eclipse.ote.ui.message.watch.AddWatchParameter;
import org.eclipse.ote.ui.message.watch.ElementPath;
import org.eclipse.ote.ui.message.watch.WatchView;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


public class MessageInputComponent implements MessageInput {

	@Override
	public String getLabel() {
		return "Message Watch";
	}

	@Override
	public void add(List<MessageInputItem> items) {
		AddWatchParameter watchParameter = new AddWatchParameter();
		recursiveAdd(watchParameter, items);
		try {
			final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			WatchView watchView;
			watchView = (WatchView) page.showView(WatchView.VIEW_ID);
			watchView.addWatchMessage(watchParameter);
		} catch (PartInitException e) {
			OseeLog.log(getClass(), Level.SEVERE, "Unable to add messages to MessageWatch", e);
		}
	}
	
	private void recursiveAdd(AddWatchParameter watchParameter, List<MessageInputItem> items){
		for(MessageInputItem item:items){
			Object[] obj = item.getElementPath();
			if(obj != null){
				watchParameter.addMessage(item.getMessageClass(), new ElementPath(obj));
			} else {
				watchParameter.addMessage(item.getMessageClass());
			}
			recursiveAdd(watchParameter, item.getChildren());
		}
	}

}
