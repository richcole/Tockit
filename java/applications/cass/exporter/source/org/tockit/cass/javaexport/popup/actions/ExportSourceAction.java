package org.tockit.cass.javaexport.popup.actions;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.tockit.cass.javaexport.SourceExportJob;

public class ExportSourceAction implements IObjectActionDelegate {
	private IJavaProject theProject = null;

	/**
	 * Constructor for Action1.
	 */
	public ExportSourceAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// nothing to do
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
        Shell shell = new Shell();
        String exportLocation = System.getProperty("org.tockit.cass.javaexport.ExportLocation");
		if (exportLocation == null) {
			DirectoryDialog dd = new DirectoryDialog(shell);
			dd.setText("Choose directory to export Java source graph into");
			exportLocation = dd.open();
		}		
		if (exportLocation != null) {
			SourceExportJob job = new SourceExportJob(theProject, exportLocation);
			job.schedule();
        }
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		theProject = (IJavaProject) ((StructuredSelection) selection).getFirstElement();
	}
}
