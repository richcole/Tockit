/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */

package org.tockit.docco.indexer;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.tockit.docco.GlobalConstants;
import org.tockit.docco.indexer.documenthandler.DocumentHandlerException;
import org.tockit.docco.indexer.filefilter.NotFoundFileExtensionException;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Indexer implements Runnable {
    public interface CallbackRecipient {
		void showFeedbackMessage(String message);
	}

    private File baseDirectory;
    private File indexLocation;
	private CallbackRecipient callbackRecipient;
    private DocumentProcessingFactory docProcessingFactory;
    private boolean shuttingDown = false;
	
	public Indexer(File indexLocation, File baseDirectory, DocumentHandlerRegistry docHandlerRegistry, CallbackRecipient output) {
		this.indexLocation = indexLocation;
		this.baseDirectory = baseDirectory;
		this.callbackRecipient = output;
		try {
            this.docProcessingFactory = new DocumentProcessingFactory(docHandlerRegistry);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stopIndexing() {
		this.shuttingDown = true;
	}

	public void run() {
		try {
			// first check all documents in the index if they disappeared or changed
			IndexReader reader = IndexReader.open(this.indexLocation);
			Set knownDocuments = new HashSet(reader.numDocs());
			Set documentsToUpdate = new HashSet();
			for(int i = 0; i < reader.maxDoc(); i++) {
				if(this.shuttingDown) {
					break;
				}
				if(!reader.isDeleted(i)) {
					Document doc = reader.document(i);
					String path = doc.get(GlobalConstants.FIELD_DOC_PATH);
					knownDocuments.add(path);
					File file = new File(path);
					if(!file.exists()) {
						reader.delete(i);
					} else {
						String dateIndex = doc.get(GlobalConstants.FIELD_DOC_MODIFICATION_DATE);
						String dateFS = DateField.dateToString(new Date(file.lastModified()));
						if(!dateFS.equals(dateIndex)) {
							reader.delete(i);
							documentsToUpdate.add(file);	
						}
					}
				}
			}
			reader.close();
			
			// add the files we need to update
			for (Iterator iter = documentsToUpdate.iterator(); iter.hasNext();) {
                File file = (File) iter.next();
                indexFile(file);
            }
	
			// then search for new files
			findNewFiles(this.baseDirectory, knownDocuments);
			
			// and optimize in the end
			IndexWriter writer = new IndexWriter(indexLocation,
										  GlobalConstants.DEFAULT_ANALYZER,
										  false);
			writer.optimize();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void findNewFiles(File file, Set knownDocuments) throws IOException {
		if(this.shuttingDown) {
			return;
		}
		if (file.isDirectory()) {
			String[] files = file.list();
			if(files == null) {
				// seems to happen if dir access denied
				return; 
			}
			for (int i = 0; i < files.length; i++) {
				findNewFiles(new File(file, files[i]), knownDocuments);
			}
		}
		else {
			if(!knownDocuments.contains(file.getPath())) {
				indexFile(file);
			}
		}
	}

	private void indexFile(File file) throws IOException {
		IndexWriter writer = new IndexWriter(indexLocation,
									  GlobalConstants.DEFAULT_ANALYZER,
									  false);
		showProgress(writer.docCount(), file.getAbsolutePath());
		try {
			writer.addDocument(this.docProcessingFactory.processDocument(file));
		} catch (UnknownFileTypeException e) {
		} catch (NotFoundFileExtensionException e) {
		} catch (DocumentHandlerException e) {
			System.err.println("Error processing document " + file.getAbsolutePath() + ": " + e.getMessage());
		} catch (DocumentProcessingException e) {
			System.err.println("Error processing document " + file.getAbsolutePath() + ": " + e.getMessage());
		} catch (Exception e) {
			// sometimes shit happens. E.g. the PDF header might be screwed. Some other things
			// might be broken. We don't want to stop indexing whenever one document fails to be
			// read properly, so we just ignore it for now. Of course we should consider
			// @todo some error handling/reporting
			//e.printStackTrace();
			System.err.println("Error processing document " + file.getAbsolutePath() + ": " + e.getMessage());
		} finally {
			writer.close();
		}
	}


	private void showProgress(int indexed, String dir) {
		showFeedbackMessage("Indexing: " + indexed + " documents so far" + " (" + dir + ")");
	}

	private void showFeedbackMessage(String string) {
		if(this.callbackRecipient != null) {
			this.callbackRecipient.showFeedbackMessage(string);
		}
    }    
}
