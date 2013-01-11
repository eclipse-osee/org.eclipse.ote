/*
 * Created on Oct 7, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package lba.ote.ui.eviewer.action;

import lba.ote.ui.eviewer.Activator;
import lba.ote.ui.eviewer.view.ElementViewer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Ken J. Aguilar
 */
public class StreamToFileAction extends Action {
	private final ElementViewer elementViewer;


	public StreamToFileAction(ElementViewer elementViewer) {
		super("Stream To File", IAction.AS_CHECK_BOX);
		this.elementViewer = elementViewer;
		setImageDescriptor(Activator.getImageDescriptor("icons/stream.gif"));
	}

	@Override
	public void run() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		if (isChecked()) {
			FileDialog dialog = new FileDialog(shell, SWT.SAVE);
			dialog.setFilterExtensions(new String[] {"*.csv"});
			dialog.setText("Save CSV file");
			String result = dialog.open();
			if (result != null) {
				elementViewer.startStreaming(null, result, false);

			} else {
				setChecked(false);
			}
		} else {
			elementViewer.stopStreaming();
		}
	}

}
