package org.tockit.cass.javaexport.popup.actions;

import java.io.File;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.tockit.cass.javaexport.SourceExportJob;

public class ExportSourceAction implements IObjectActionDelegate {
	private IJavaProject theProject = null;

	private String lastFile = null;
	
	private static final String[] IGNORE_LIST = new String[]{
		".*\\.jar",
		".*\\.test",
		".*\\.tests"
	};

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
		FileDialog fd = new FileDialog(shell, SWT.SAVE);
		if (lastFile != null) {
			File lastFolder = (new File(lastFile)).getParentFile();
			fd.setFilterPath(lastFolder.getAbsolutePath());
			// proposed file name has the same extension as last time,
			// just with the current project name
			String proposedFileName = theProject.getElementName()
					+ lastFile.substring(lastFile.lastIndexOf('.'));
			fd.setFileName(proposedFileName);
		}
		fd.setText("Choose file to export Java source graph into");
		String selectedFile = fd.open();
		if (selectedFile == null) { // user cancelled
			return;
		}

		lastFile = selectedFile; // TODO figure out how to persist this
		// without being a view part
		File targetFile = new File(selectedFile);
		String targetFormat = "RDF/XML";
		if (targetFile.getName().toLowerCase().endsWith(".n3")) {
			targetFormat = "N3";
		}

		SourceExportJob job = new SourceExportJob(theProject, targetFile,
				targetFormat, IGNORE_LIST);
		job.schedule();
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		theProject = (IJavaProject) ((StructuredSelection) selection)
				.getFirstElement();
	}
}
