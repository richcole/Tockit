package org.tockit.docco.documenthandler;

import java.util.ArrayList;

import org.tockit.docco.filefilter.DoccoFileFilter;
import org.tockit.docco.filefilter.ExtensionFileFilterFactory;
import org.tockit.docco.gui.GuiMessages;
import org.tockit.docco.indexer.DocumentSummary;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OpenDocumentFormatDocumentHandler extends OpenOfficeDocumentHandler {
    private static final String OFFICE_NAMESPACE = "urn:oasis:names:tc:opendocument:xmlns:office:1.0"; //$NON-NLS-1$
    private static final String META_NAMESPACE = "urn:oasis:names:tc:opendocument:xmlns:meta:1.0"; //$NON-NLS-1$

    public String getOfficeNameSpace() {
        return OFFICE_NAMESPACE;
    }

    protected void extractKeywords(DocumentSummary docSummary, Element metaElem) {
        docSummary.keywords = new ArrayList();

        NodeList nodes = metaElem.getElementsByTagNameNS(META_NAMESPACE,"keyword"); //$NON-NLS-1$
        for (int i=0; i<nodes.getLength(); i++) {
            Node n = nodes.item(i);
            docSummary.keywords.add(n.getFirstChild().getNodeValue());
        }
        
        // we treat user-defined attributes just like keywords
        nodes = metaElem.getElementsByTagNameNS(META_NAMESPACE,"user-defined"); //$NON-NLS-1$
        for (int i=0; i<nodes.getLength(); i++) {
            Node n = nodes.item(i);
            Node firstChild = n.getFirstChild();
            if (firstChild != null) { // user-defined fields can be empty
                docSummary.keywords.add(firstChild.getNodeValue());
            }
        }
    }

    public String getDisplayName() {
        return GuiMessages.getString("OpenDocumentFormatDocumentHandler.name"); //$NON-NLS-1$
    }

    public DoccoFileFilter getDefaultFilter() {
        return new ExtensionFileFilterFactory().createNewFilter("odt;ods;odp;odg;odm;odf"); //$NON-NLS-1$
    }
}
