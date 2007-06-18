package org.tockit.cass.javaexport.popup.actions;

import java.sql.SQLException;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.tockit.cass.javaexport.SourceExport;

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
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
        Shell shell = new Shell();
		DirectoryDialog dd = new DirectoryDialog(shell);
        dd.setText("Choose directory to export Java source graph into");
        String selected = dd.open();
        if (selected != null) {
			try {
				SourceExport.exportSource(theProject, selected);
			} catch (SQLException e) {
				MessageDialog.openError(shell, "Error exporting Java source", e.getLocalizedMessage());
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				MessageDialog.openError(shell, "Error exporting Java source", e.getLocalizedMessage());
				e.printStackTrace();
			} catch (JavaModelException e) {
				MessageDialog.openError(shell, "Error exporting Java source", e.getLocalizedMessage());
				e.printStackTrace();
			}
        }
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		theProject = (IJavaProject) ((StructuredSelection) selection).getFirstElement();
	}
}
