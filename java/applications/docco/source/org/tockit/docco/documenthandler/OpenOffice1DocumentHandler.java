package org.tockit.docco.documenthandler;

import java.util.ArrayList;

import org.tockit.docco.filefilter.DoccoFileFilter;
import org.tockit.docco.filefilter.ExtensionFileFilterFactory;
import org.tockit.docco.gui.GuiMessages;
import org.tockit.docco.indexer.DocumentSummary;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OpenOffice1DocumentHandler extends OpenOfficeDocumentHandler {
    private static final String OFFICE_NAMESPACE = "http://openoffice.org/2000/office"; //$NON-NLS-1$
    private static final String META_NAMESPACE = "http://openoffice.org/2000/meta"; //$NON-NLS-1$

    public String getOfficeNameSpace() {
        return OFFICE_NAMESPACE;
    }

    protected void extractKeywords(DocumentSummary docSummary, Element metaElem) {
        NodeList nodes;
        Element keywordsElem = (Element) metaElem.getElementsByTagNameNS(META_NAMESPACE,"keywords").item(0); //$NON-NLS-1$
        if(keywordsElem != null) {
            nodes = keywordsElem.getElementsByTagNameNS(META_NAMESPACE,"keyword"); //$NON-NLS-1$
            docSummary.keywords = new ArrayList();
            for (int i=0; i<nodes.getLength(); i++) {
                Node n = nodes.item(i);
                docSummary.keywords.add(n.getFirstChild().getNodeValue());
            }
        }
    }

    public String getDisplayName() {
        return GuiMessages.getString("OpenOffice1DocumentHandler.name"); //$NON-NLS-1$
    }

    public DoccoFileFilter getDefaultFilter() {
        return new ExtensionFileFilterFactory().createNewFilter("sxw;sxc;sxi;sxd"); //$NON-NLS-1$
    }
}
