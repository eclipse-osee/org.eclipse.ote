/*
 * Created on Apr 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package lba.ote.ui.eviewer.action;

import lba.ote.ui.eviewer.view.ElementColumn;
import lba.ote.ui.eviewer.view.ElementContentProvider;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * @author Ken J. Aguilar
 */
public class RemoveColumnAction extends Action {

	private final ElementContentProvider elementContentProvider;

	public RemoveColumnAction(ElementContentProvider elementContentProvider) {
		super("Remove columns");
		this.elementContentProvider = elementContentProvider;
	}

	@Override
	public void run() {

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(Display.getCurrent().getActiveShell(),new ElementLabelProvider());
		dialog.setElements(elementContentProvider.getColumns().toArray());
		dialog.setTitle("Remove Columns");
		dialog.setMessage("Select one or more columns to remove");
		dialog.setEmptySelectionMessage("no columns selected");
		dialog.setMultipleSelection(true);
		dialog.open();
		Object[] selection = dialog.getResult();
		if (selection != null) {
			for (Object item : selection) {
				elementContentProvider.removeColumn((ElementColumn)item);
			}
		}
	}
	
	
}
