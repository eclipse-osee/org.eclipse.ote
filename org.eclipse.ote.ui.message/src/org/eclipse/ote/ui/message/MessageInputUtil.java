package org.eclipse.ote.ui.message;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.ote.message.lookup.MessageInputItem;
import org.eclipse.ote.ui.message.watch.AddWatchParameter;
import org.eclipse.ote.ui.message.watch.ElementPath;
import org.eclipse.ote.ui.message.watch.WatchView;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class MessageInputUtil {
   public static void add(List<MessageInputItem> items, boolean recurse) {
      AddWatchParameter watchParameter = new AddWatchParameter();
      add(watchParameter, items, recurse);
      try {
         final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
         WatchView watchView = (WatchView) page.showView(WatchView.VIEW_ID);
         watchView.addWatchMessage(watchParameter);
      } catch (PartInitException e) {
         OseeLog.log(MessageInputUtil.class, Level.SEVERE, "Unable to add messages to MessageWatch", e);
      }
   }

   private static void add(AddWatchParameter watchParameter, List<MessageInputItem> items, boolean recurse){
      for(MessageInputItem item:items){
         Object[] obj = item.getElementPath();
         if(obj != null){
            watchParameter.addMessage(item.getMessageClass(), new ElementPath(obj));
         } else {
            watchParameter.addMessage(item.getMessageClass());
         }
         if (recurse) {
            add(watchParameter, item.getChildren(), true);
         }
      }
   }


}
