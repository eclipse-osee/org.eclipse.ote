/*
 * Created on Apr 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.ote.ui.eviewer.action;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ote.ui.eviewer.Activator;
import org.eclipse.ote.ui.eviewer.view.ElementColumn;
import org.eclipse.ote.ui.eviewer.view.ElementContentProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListSelectionDialog;

/**
 * @author Ken J. Aguilar
 */
public class SetActiveColumnAction extends Action  {

	private final ElementContentProvider elementContentProvider;

	public SetActiveColumnAction(ElementContentProvider elementContentProvider) {
		super("Set Active columns");
		this.elementContentProvider = elementContentProvider;
		setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("ACTIVE_PNG"));
	}

	@Override
	public void run() {
		final IStructuredContentProvider contentProvider = new IStructuredContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void dispose() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				return ((List<?>)inputElement).toArray();
			}
		};
		ListSelectionDialog dialog = new ListSelectionDialog(Display.getCurrent().getActiveShell(), elementContentProvider.getColumns(), contentProvider, new ElementLabelProvider(), "Check all columns that should be active");
		dialog.setTitle("Set Active Columns");
		LinkedList<ElementColumn> list = new LinkedList<ElementColumn>();
		for (ElementColumn column : elementContentProvider.getColumns()) {
			if (column.isActive()) {
				list.add(column);
			}
		}
		dialog.setInitialElementSelections(list);
		dialog.open();
		Object[] selection = dialog.getResult();
		if (selection != null) {
			HashSet<Object> activeColumnSet = new HashSet<Object>(Arrays.asList(selection));
			for (ElementColumn column : elementContentProvider.getColumns()) {
				column.setActive(activeColumnSet.contains(column));
			}
		}
	}


}