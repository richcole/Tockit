/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco;
import java.io.IOException;

import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.tockit.docco.events.QueryEvent;
import org.tockit.docco.gui.DoccoMainFrame;
import org.tockit.docco.handlers.QueryEventHandler;
import org.tockit.docco.query.QueryDecomposer;
import org.tockit.docco.query.QueryEngine;
import org.tockit.events.EventBroker;




public class Docco {
	
	
	private EventBroker eventBroker = new EventBroker();
	
	public Docco () {
		try {
			QueryDecomposer queryDecomposer = new QueryDecomposer(GlobalVars.FIELD_QUERY_BODY, new StandardAnalyzer());
			QueryEngine queryEngine = new QueryEngine(GlobalVars.DEFAULT_INDEX_LOCATION, 
													GlobalVars.FIELD_QUERY_BODY, 
													new StandardAnalyzer(),
													queryDecomposer);

			this.eventBroker.subscribe(new QueryEventHandler(eventBroker, queryEngine), QueryEvent.class, Object.class);
		
			DoccoMainFrame mainFrame = new DoccoMainFrame(this.eventBroker);
			mainFrame.setVisible(true);
		}
		catch (IOException e) {
			ErrorDialog.showError(null, e, "Error", "\nPlease check if you have created an index using docsearcher.\n" + 
							" Index name should be '" + GlobalVars.DEFAULT_INDEX_NAME + "'");
		}
		catch (Exception e) {
			ErrorDialog.showError(null, e, "Error");
		}
	}
	
	public static void main (String[] args) {
		new Docco();
	}

}
