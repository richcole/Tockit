/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.fca;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.diagram.WriteableDiagram2D;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.ndimdiagram.NDimDiagram;
import net.sourceforge.toscanaj.model.ndimdiagram.NDimDiagramNode;

import org.tockit.docco.gui.GuiMessages;
import org.tockit.docco.query.HitReference;
import org.tockit.docco.query.QueryWithResult;

public class DiagramGenerator {
	/**
	 * @todo the code in here implements a more general notion of creating a lattice
	 * diagramm from a set of FCAElement/set pairs. This is not specific to Docco and
	 * could be reused. In math terms it models the mapping:
	 * 
	 *   { (m, m') | m \in M } --> B(G,M,I) with 
	 *                                G = \bigcup_{m \in M} m'
	 *                                I = { (g,m) | g \in m' }
	 */	
	public static Diagram2D createDiagram(QueryWithResult[] results, boolean includePhantomNodes) {
		Concept[] concepts = createConcepts(results);
        
		Diagram2D diagram;
		if(includePhantomNodes) {
			Point2D[] baseVectors = createBase(results);
			diagram = createDiagram(concepts, baseVectors);
		} else {
			final Concept[] finalConcepts = reduceConceptsToRealizedOnes(concepts);
			Lattice lattice = new Lattice(){
				public Concept[] getConcepts() {
					return finalConcepts;
				}
				public Concept getBottom() {
					return finalConcepts[finalConcepts.length-1];
				}
				public Concept getTop() {
					return finalConcepts[0];
				}
			};
			diagram = NDimLayoutOperations.createDiagram(lattice,GuiMessages.getString("DiagramGenerator.diagram.title"),new DefaultDimensionStrategy()); //$NON-NLS-1$
		}
		return diagram;
	}

	/**
	 * Note: this method has the side effect of changing the upsets and downsets
	 * of the concepts involved. Not suited for reuse unless this is fixed.
	 */
	private static Concept[] reduceConceptsToRealizedOnes(Concept[] concepts) {
		List realizedConcepts = new ArrayList();

		outerLoop: for (int i = 0; i < concepts.length; i++) {
			Concept concept = concepts[i];
			// we still assume the binary encoding of the intent in the concept numbering
			for (int j = i + 1; j < concepts.length; j++) {
				ConceptImplementation subconcept = (ConceptImplementation) concepts[j];
				if( (i & j) != i ){
					continue; // not a subconcept
				}
				if(concept.getExtentSize() == subconcept.getExtentSize()) { // not realized
					// move FCAElement contingent down. We know there is an infimum on the
					// set of concepts with the same extent, so that is ok.
					Iterator it = concept.getAttributeContingentIterator();
					while(it.hasNext()) {
						FCAElement attribute = (FCAElement) it.next();
						subconcept.addAttribute(attribute);
					}
					continue outerLoop;
				}
			}
			realizedConcepts.add(concept);
		}

		for (Iterator it = realizedConcepts.iterator();it.hasNext();) {
			ConceptImplementation concept = (ConceptImplementation) it.next();
			concept.getUpset().retainAll(realizedConcepts);
			concept.getDownset().retainAll(realizedConcepts);
		}
        
		return (Concept[]) realizedConcepts.toArray(new Concept[realizedConcepts.size()]);
	}

	/**
	 * Creates base vectors as described in Frank Vogt's book on page 61.
	 * 
	 * $w_m:=(2^i-2^{n-i-1},-2^i-2^{n-i-1})$ 
	 * 
	 * Slight changes: we use the CS coordinate system (i.e. inverted Y) and
	 * we scale things a bit.
	 */
	private static Point2D[] createBase(QueryWithResult[] results) {
		final double scalex = 30;
		final double scaley = 15;
		
		int n = results.length;
		Point2D[] baseVectors = new Point2D[n];
		for (int i = 0; i < baseVectors.length; i++) {
			double x = (1<<i) - (1<<(n-i-1));
			double y = (1<<i) + (1<<(n-i-1));
			baseVectors[i] = new Point2D.Double(scalex * x, scaley * y);
		}
		return baseVectors;
	}

	private static WriteableDiagram2D createDiagram(Concept[] concepts,	Point2D[] baseVectors) {
        Vector baseNdim = new Vector();
        for (int i = 0; i < baseVectors.length; i++) {
            baseNdim.add(baseVectors[i]);
        }
		NDimDiagram diagram = new NDimDiagram(baseNdim);
        NDimDiagramNode[] nodes = new NDimDiagramNode[concepts.length];
		for (int i = 0; i < concepts.length; i++) {
            double[] vector = new double[baseVectors.length];
			for (int j = 0; j < baseVectors.length; j++) {
				int currentBit = 1<<j;
				if ((i & currentBit) == currentBit) {
                    vector[j]=1;
				}
			}
			nodes[i] = new NDimDiagramNode(diagram, String.valueOf(i), vector, concepts[i],
												new LabelInfo(), new LabelInfo(), null);
			diagram.addNode(nodes[i]);
		}
		
		for (int i = 0; i < concepts.length - 1; i++) {
			for (int j = 0; j < baseVectors.length; j++) {
				int currentBit = 1<<j;
				if( (i | currentBit ) != i ) {
					diagram.addLine(nodes[i], nodes[i | currentBit]);
				}
			}
		}
		
		return diagram;
	}

	private static Concept[] createConcepts(QueryWithResult[] results) {
		Set allObjects = new HashSet();
        for (int i = 0; i < results.length; i++) {
            QueryWithResult queryWithResult = results[i];
            allObjects.addAll(queryWithResult.getResultSet());
		}
		
		int n = results.length;
		int numConcepts = 1<<n; // 2 to the power of n
		
		ConceptImplementation[] concepts = new ConceptImplementation[numConcepts];
		for (int i = 0; i < concepts.length; i++) {
			concepts[i] = new ConceptImplementation();
		}
		
		for (int i = 0; i < concepts.length; i++) {
			ConceptImplementation concept = concepts[i];
			Set objectContingent = new HashSet(allObjects);
			for (int j = 0; j < n; j++) {
				int currentBit = 1<<j;
				if (i == currentBit) {
					concept.addAttribute(new FCAElementImplementation(results[j].getLabel()));
				}
				Set currentHitReferences = results[j].getResultSet();
				if ((i & currentBit) == currentBit) {
					objectContingent.retainAll(currentHitReferences);
				} else {
					objectContingent.removeAll(currentHitReferences);
				}
			}
			for (Iterator iter = objectContingent.iterator(); iter.hasNext();) {
				HitReference reference = (HitReference) iter.next();
				concept.addObject(new FCAElementImplementation(reference));
			}
			for( int j =0; j < numConcepts; j ++) {
				if((i & j) == i) {
					concepts[i].addSubConcept(concepts[j]);
					concepts[j].addSuperConcept(concepts[i]);
				}
			}
		}
		return concepts;
	}
}
