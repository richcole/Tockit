/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.tockit.docco.GlobalConstants;
import org.tockit.docco.indexer.DocumentHandlerRegistry;
import org.tockit.docco.indexer.Indexer;

public class Index {
	private String infoFilesPath;
	private String indexLocation;
	private File[] filesIndexed = new File[0];
	private Indexer indexThread;
	private DocumentHandlerRegistry docHandlersRegistry;
	
	public Index(String indexLocation, Indexer.CallbackRecipient callbackRecipient) throws IOException {
		this.indexLocation = indexLocation;
		this.infoFilesPath = indexLocation;
		if(infoFilesPath.endsWith(File.separator)) {
			infoFilesPath = infoFilesPath.substring(0, infoFilesPath.length() - File.separator.length());
		}
		
		File indexContents = getContentsFile();
		if(indexContents.exists()) {
			String[] paths = getLinesOfFile(indexContents);
			this.filesIndexed = new File[paths.length];
			for (int i = 0; i < paths.length; i++) {
                String string = paths[i];
                this.filesIndexed[i] = new File(paths[i]); 
            }
		}

		File mappingsFile = getMappingsFile();
		if(mappingsFile.exists()) {
			this.docHandlersRegistry = new DocumentHandlerRegistry(getLinesOfFile(mappingsFile));
		} else {
			this.docHandlersRegistry = new DocumentHandlerRegistry(DocumentHandlerRegistry.DEFAULT_MAPPINGS);
		}
		
        this.indexThread = new Indexer(this.docHandlersRegistry, callbackRecipient);
		this.indexThread.start();
	}

	private File getContentsFile() {
		return new File(infoFilesPath + ".contents");
	}

	private File getMappingsFile() {
		return new File(infoFilesPath + ".mappings");
	}

    private String[] getLinesOfFile(File inputFile) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        List lines = new ArrayList();
        while(reader.ready()) {
        	lines.add(reader.readLine());
        }
        reader.close();
        return (String[]) lines.toArray(new String[lines.size()]);
    }
	
	public String getIndexLocation() {
		return this.indexLocation;
	}
	
	public void addFilesToIndex(File[] files) {
		/// @todo we probably want absolute paths here
		List newFileList = new ArrayList(); 
		newFileList.addAll(Arrays.asList(this.filesIndexed));
		newFileList.addAll(Arrays.asList(files));
		this.filesIndexed = (File[]) newFileList.toArray(new File[newFileList.size()]);
		this.indexThread.enqueue(files);
	}
	
	public File[] getFilesIndexed() {
		return this.filesIndexed;
	}
	
	public void setFilesToIndex(File[] files) {
		this.filesIndexed = files;
		this.indexThread.enqueue(files);
	}
	
	public void updateIndex() throws IOException {
		// first check all documents in the index if they disappeared or changed
		this.indexThread.stopIndexing();
		IndexReader reader = IndexReader.open(this.indexLocation);
		Set knownDocuments = new HashSet(reader.numDocs());
		for(int i = 0; i < reader.maxDoc(); i++) {
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
						this.indexThread.enqueue(file);	
					}
				}
			}
		}
		reader.close();

		// then search for new files
		for (int i = 0; i < this.filesIndexed.length; i++) {
            File file = this.filesIndexed[i];
            updateFile(file, knownDocuments);
        }

		// and off we go...
		this.indexThread.startIndexing(this.indexLocation);
	}

	private void updateFile(File file, Set knownDocuments) {
		if (file.isDirectory()) {
			String[] files = file.list();
			if(files == null) {
				// seems to happen if dir access denied
				return; 
			}
			for (int i = 0; i < files.length; i++) {
				updateFile(new File(file, files[i]), knownDocuments);
			}
		}
		else {
			if(!knownDocuments.contains(file.getPath())) {
				this.indexThread.enqueue(file);
			}
		}
    }

    public void start() throws IOException {
		this.indexThread.startIndexing(this.indexLocation);
	}
	
	public void stop() throws IOException {
		this.indexThread.stopIndexing();
	}
	
	public void shutdown() throws IOException {
		stop();
		PrintStream out = new PrintStream(new FileOutputStream(getContentsFile()));
		for (int i = 0; i < this.filesIndexed.length; i++) {
			File file = this.filesIndexed[i];
			out.println(file.getPath());
		}
		out.close();
		out = new PrintStream(new FileOutputStream(getMappingsFile()));
		String[] mappings = this.docHandlersRegistry.getMappingStringsList();
        for (int i = 0; i < mappings.length; i++) {
			out.println(mappings[i]);
		}
		out.close();
	}
	
	public boolean isWorking() {
		return this.indexThread.isIndexing();
	}
	
    public DocumentHandlerRegistry getDocHandlersRegistry() {
        return docHandlersRegistry;
    }
}
