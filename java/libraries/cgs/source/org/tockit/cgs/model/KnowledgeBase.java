/*
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Aug 21, 2002
 * Time: 12:43:07 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.tockit.cgs.model;

import org.jdom.Element;
import org.tockit.cgs.util.IdPool;
import org.tockit.cgs.model.events.*;
import org.tockit.events.EventBroker;

import java.util.*;

public class KnowledgeBase {
    private Element element = null; // root element
    private Hashtable graphDict = new Hashtable();
    private Hashtable nodeDict = new Hashtable();
    private Hashtable typeDict = new Hashtable();
    private Hashtable instanceDict = new Hashtable();
    private Hashtable linkDict = new Hashtable();
    private Hashtable relationDict = new Hashtable();
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
        return (ConceptualGraph) graphDict.get(graphId);
    }

    public void addNode(Node node) {
        nodeDict.put(node.getId(), node);
    }

    public Node getNode(String nodeId) {
        return (Node) nodeDict.get(nodeId);
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
        return (Type) typeDict.get(typeId);
    }

    public void addInstance(Instance instance, boolean addXML) {
        instanceDict.put(instance.getIdentifier(), instance);
        if (addXML) {
            this.element.addContent(instance.getElement());
        }
        this.eventBroker.processEvent(new NewInstanceCreatedEvent(this,instance));
    }

    public Instance getInstance(String instanceId) {
        return (Instance) instanceDict.get(instanceId);
    }

    public void addLink(Link link) {
        linkDict.put(link.getId(), link);
    }

    public Link getLink(String linkId) {
        return (Link) linkDict.get(linkId);
    }

    public void addRelation(Relation relation, boolean addXML) {
        relationDict.put(relation.getName(), relation);
        if (addXML) {
            this.element.addContent(relation.getElement());
        }
        this.eventBroker.processEvent(new NewRelationCreatedEvent(this,relation));
    }

    public Relation getRelation(String relationName) {
        return (Relation) this.relationDict.get(relationName);
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

    public Collection getGraphIds() {
        return this.graphDict.keySet();
    }

    public Collection getRelationNames() {
        return this.relationDict.keySet();
    }

    public Collection getInstanceIdentifiers() {
        return this.instanceDict.keySet();
    }

    public Collection getTypeNames() {
        return this.typeDict.keySet();
    }

    public Collection getRelations() {
        return this.relationDict.values();
    }

    public Collection getTypes() {
        return this.typeDict.values();
    }

    public void remove(Node node) {
        this.nodeDict.remove(node.getId());
        Collection graphs = this.graphDict.values();
        for (Iterator iterator = graphs.iterator(); iterator.hasNext();) {
            ConceptualGraph graph = (ConceptualGraph) iterator.next();
            graph.remove(node);
        }
    }

    public void remove(Link link) {
        this.linkDict.remove(link.getId());
        Collection graphs = this.graphDict.values();
        for (Iterator iterator = graphs.iterator(); iterator.hasNext();) {
            ConceptualGraph graph = (ConceptualGraph) iterator.next();
            graph.remove(link);
        }
    }

    public Collection getDirectSubtypes(Type type) {
        Set retVal = new HashSet();
        Collection types = getTypes();
        if (type != Type.ABSURD) {
            for (Iterator iterator = types.iterator(); iterator.hasNext();) {
                Type other = (Type) iterator.next();
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

    public Collection getDirectSubtypes(Relation relation) {
        Set retVal = new HashSet();
        Collection types = getRelations();
        for (Iterator iterator = types.iterator(); iterator.hasNext();) {
            Relation other = (Relation) iterator.next();
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

    public Collection getInstancesForType(Type type) {
        Collection retVal = new HashSet();
        Iterator it = this.instanceDict.values().iterator();
        while (it.hasNext()) {
            Instance instance = (Instance) it.next();
            if(instance.getType().hasSupertype(type)) {
                retVal.add(instance);
            }
        }
        return retVal;
    }

    public Collection getInstances() {
        return this.instanceDict.values();
    }

    public Collection getGraphs() {
        return this.graphDict.values();
    }
}
