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

import java.util.*;

public class KnowledgeBase {
    private Element element = null; // root element
    private Hashtable cgs = new Hashtable();
    private Hashtable nodes = new Hashtable();
    private Hashtable types = new Hashtable();
    private Hashtable instances = new Hashtable();
    private Hashtable links = new Hashtable();
    private Hashtable relations = new Hashtable();
    private IdPool nodeIdPool = new IdPool();
    private IdPool linkIdPool = new IdPool();
    private IdPool graphIdPool = new IdPool();

    public KnowledgeBase() {
        this.element = new Element("knowledgeBase");
    }

    public KnowledgeBase(Element element) {
        this.element = element;

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
        cgs.put(graph.getId(), graph);
        if (addXML) {
            this.element.addContent(graph.getElement());
        }
    }

    public ConceptualGraph getGraph(String graphId) {
        return (ConceptualGraph) cgs.get(graphId);
    }

    public void addNode(Node node) {
        nodes.put(node.getId(), node);
    }

    public Node getNode(String nodeId) {
        return (Node) nodes.get(nodeId);
    }

    public void addType(Type type, boolean addXML) {
        types.put(type.getName(), type);
        if (addXML) {
            this.element.addContent(type.getElement());
        }
    }

    public Type getType(String typeId) {
        return (Type) types.get(typeId);
    }

    public void addInstance(Instance instance, boolean addXML) {
        instances.put(instance.getIdentifier(), instance);
        if (addXML) {
            this.element.addContent(instance.getElement());
        }
    }

    public Instance getInstance(String instanceId) {
        return (Instance) instances.get(instanceId);
    }

    public void addLink(Link link) {
        links.put(link.getId(), link);
    }

    public Link getLink(String linkId) {
        return (Link) links.get(linkId);
    }

    public void addRelation(Relation relation, boolean addXML) {
        relations.put(relation.getName(), relation);
        if (addXML) {
            this.element.addContent(relation.getElement());
        }
    }

    public Relation getRelation(String relationName) {
        return (Relation) this.relations.get(relationName);
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
        return this.cgs.keySet();
    }
}
