/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cgs.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.jdom.Element;
import org.tockit.cgs.model.events.NewInstanceCreatedEvent;
import org.tockit.cgs.model.events.NewRelationCreatedEvent;
import org.tockit.cgs.model.events.NewTypeCreatedEvent;
import org.tockit.events.EventBroker;
import org.tockit.util.IdPool;

public class KnowledgeBase {
    private Element element = null; // root element
    private Hashtable<String, ConceptualGraph> graphDict = new Hashtable<String, ConceptualGraph>();
    private Hashtable<String, Node> nodeDict = new Hashtable<String, Node>();
    private Hashtable<String, Type> typeDict = new Hashtable<String, Type>();
    private Hashtable<String, Instance> instanceDict = new Hashtable<String, Instance>();
    private Hashtable<String, Link> linkDict = new Hashtable<String, Link>();
    private Hashtable<String, Relation> relationDict = new Hashtable<String, Relation>();
    private IdPool nodeIdPool = new IdPool();
    private IdPool linkIdPool = new IdPool();
    private IdPool graphIdPool = new IdPool();
    private EventBroker eventBroker;

    public KnowledgeBase(EventBroker eventBroker) {
        this.eventBroker = eventBroker;
        this.element = new Element("knowledgeBase");
        Type.setDefaultKnowledgeBase(this);
        Relation.setDefaultKnowledgeBase(this);
    }

    public KnowledgeBase(Element element, EventBroker eventBroker) {
        this.eventBroker = eventBroker;
        this.element = element;
        Type.setDefaultKnowledgeBase(this);
        Relation.setDefaultKnowledgeBase(this);

        Iterator it = element.getChildren("conceptualGraph").iterator();
        while (it.hasNext()) {
            Element descriptorElem = (Element) it.next();
            new ConceptualGraph(this, descriptorElem);
        }

        it = element.getChildren("node").iterator();
        while (it.hasNext()) {
            Element nodeElem = (Element) it.next();
            new Node(this, nodeElem);
        }

        it = element.getChildren("type").iterator();
        while (it.hasNext()) {
            Element typeElem = (Element) it.next();
            new Type(this, typeElem);
        }

        it = element.getChildren("instance").iterator();
        while (it.hasNext()) {
            Element instanceElem = (Element) it.next();
            new Instance(this, instanceElem);
        }

        it = element.getChildren("link").iterator();
        while (it.hasNext()) {
            Element linkElem = (Element) it.next();
            new Link(this, linkElem);
        }

        it = element.getChildren("relation").iterator();
        while (it.hasNext()) {
            Element relationElem = (Element) it.next();
            new Relation(this, relationElem);
        }
    }

    public void addGraph(ConceptualGraph graph, boolean addXML) {
        graphDict.put(graph.getId(), graph);
        if (addXML) {
            this.element.addContent(graph.getElement());
        }
    }

    public ConceptualGraph getGraph(String graphId) {
        return graphDict.get(graphId);
    }

    public void addNode(Node node) {
        nodeDict.put(node.getId(), node);
    }

    public Node getNode(String nodeId) {
        return nodeDict.get(nodeId);
    }

    public void addType(Type type, boolean addXML) {
        typeDict.put(type.getName(), type);
        if (addXML) {
            this.element.addContent(type.getElement());
        }
        this.eventBroker.processEvent(new NewTypeCreatedEvent(this,type));
    }

    public Type getType(String typeId) {
        if(typeId == null) {
            return Type.UNIVERSAL;
        }
        return typeDict.get(typeId);
    }

    public void addInstance(Instance instance, boolean addXML) {
        instanceDict.put(instance.getIdentifier(), instance);
        if (addXML) {
            this.element.addContent(instance.getElement());
        }
        this.eventBroker.processEvent(new NewInstanceCreatedEvent(this,instance));
    }

    public Instance getInstance(String instanceId) {
        return instanceDict.get(instanceId);
    }

    public void addLink(Link link) {
        linkDict.put(link.getId(), link);
    }

    public Link getLink(String linkId) {
        return linkDict.get(linkId);
    }

    public void addRelation(Relation relation, boolean addXML) {
        relationDict.put(relation.getName(), relation);
        if (addXML) {
            this.element.addContent(relation.getElement());
        }
        this.eventBroker.processEvent(new NewRelationCreatedEvent(this,relation));
    }

    public Relation getRelation(String relationName) {
        return this.relationDict.get(relationName);
    }

    String createNewNodeId() {
        return this.nodeIdPool.getFreeId();
    }

    String createNewLinkId() {
        return this.linkIdPool.getFreeId();
    }

    String createNewGraphId() {
        return this.graphIdPool.getFreeId();
    }

    void reserveNodeId(String id) {
        this.nodeIdPool.reserveId(id);
    }

    void reserveLinkId(String id) {
        this.linkIdPool.reserveId(id);
    }

    void reserveGraphId(String id) {
        this.graphIdPool.reserveId(id);
    }

    public Element getXMLElement() {
        return element;
    }

    public Collection<String> getGraphIds() {
        return this.graphDict.keySet();
    }

    public Collection<String> getRelationNames() {
        return this.relationDict.keySet();
    }

    public Collection<String> getInstanceIdentifiers() {
        return this.instanceDict.keySet();
    }

    public Collection<String> getTypeNames() {
        return this.typeDict.keySet();
    }

    public Collection<Relation> getRelations() {
        return this.relationDict.values();
    }

    public Collection<Type> getTypes() {
        return this.typeDict.values();
    }

    public void remove(Node node) {
        this.nodeDict.remove(node.getId());
        Collection<ConceptualGraph> graphs = this.graphDict.values();
        for (Iterator<ConceptualGraph> iterator = graphs.iterator(); iterator.hasNext();) {
            ConceptualGraph graph = iterator.next();
            graph.remove(node);
        }
    }

    public void remove(Link link) {
        this.linkDict.remove(link.getId());
        Collection<ConceptualGraph> graphs = this.graphDict.values();
        for (Iterator<ConceptualGraph> iterator = graphs.iterator(); iterator.hasNext();) {
            ConceptualGraph graph = iterator.next();
            graph.remove(link);
        }
    }

    public Collection<Type> getDirectSubtypes(Type type) {
        Set<Type> retVal = new HashSet<Type>();
        Collection<Type> types = getTypes();
        if (type != Type.ABSURD) {
            for (Iterator<Type> iterator = types.iterator(); iterator.hasNext();) {
                Type other = iterator.next();
                Type[] candidates = other.getDirectSupertypes();
                for (int i = 0; i < candidates.length; i++) {
                    Type candidate = candidates[i];
                    if(candidate == type) {
                        retVal.add(other);
                    }
                }
            }
        }
        return retVal;
    }

    public Collection<Relation> getDirectSubtypes(Relation relation) {
        Set<Relation> retVal = new HashSet<Relation>();
        Collection<Relation> types = getRelations();
        for (Iterator<Relation> iterator = types.iterator(); iterator.hasNext();) {
            Relation other = iterator.next();
            Relation[] candidates = other.getDirectSupertypes();
            for (int i = 0; i < candidates.length; i++) {
                Relation candidate = candidates[i];
                if(candidate == relation) {
                    retVal.add(other);
                }
            }
        }
        return retVal;
    }

    public Collection<Instance> getInstancesForType(Type type) {
        Collection<Instance> retVal = new HashSet<Instance>();
        Iterator<Instance> it = this.instanceDict.values().iterator();
        while (it.hasNext()) {
            Instance instance = it.next();
            if(instance.getType().hasSupertype(type)) {
                retVal.add(instance);
            }
        }
        return retVal;
    }

    public Collection<Instance> getInstances() {
        return this.instanceDict.values();
    }

    public Collection<ConceptualGraph> getGraphs() {
        return this.graphDict.values();
    }
}
