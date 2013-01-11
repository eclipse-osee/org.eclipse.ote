/*
 * Created on Oct 8, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package lba.ote.ui.eviewer.action;

import lba.ote.ui.eviewer.Activator;
import lba.ote.ui.eviewer.view.ColumnConfiguration;
import lba.ote.ui.eviewer.view.ColumnConfigurationDialog;
import lba.ote.ui.eviewer.view.ElementContentProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Ken J. Aguilar
 */
public class ConfigureColumnsAction extends Action {

   private final ElementContentProvider provider;

   public ConfigureColumnsAction(ElementContentProvider provider) {
      super("Configure Columns", IAction.AS_PUSH_BUTTON);
      setImageDescriptor(Activator.getImageDescriptor("icons/table_config.gif"));
      this.provider = provider;
   }

   @Override
   public void run() {
      Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
      ColumnConfiguration configuration = new ColumnConfiguration(provider);
      ColumnConfigurationDialog dialog = new ColumnConfigurationDialog(shell, configuration);
      if (dialog.open() == Window.OK) {
         configuration.apply(provider);
      }
   }
}
