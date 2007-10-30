/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cernatoXML.parser;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.tockit.cernatoXML.model.*;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class CernatoXMLParser {
    public static CernatoModel importCernatoXMLFile(File cernatoXMLFile)
            throws IOException, DataFormatException, JDOMException {
        SAXBuilder parser = new SAXBuilder();
        Document document = parser.build(cernatoXMLFile);

        Element rootElement = document.getRootElement();
        if (!rootElement.getName().equals("cernatodata")) {
            throw new DataFormatException("Input file is not Cernato XML export");
        }

        CernatoModel model = new CernatoModel();

        Hashtable<String, TypeImplementation> types = new Hashtable<String, TypeImplementation>();
        Hashtable<String, Property> properties = new Hashtable<String, Property>();
        Hashtable<String, CernatoObject> objects = new Hashtable<String, CernatoObject>();

        parseTypes(rootElement, model, types);
        parseProperties(rootElement, model, types, properties);
        parseObjects(rootElement, model, objects);
        parseRelation(rootElement, model, properties, objects);
        parseViews(rootElement, model, properties);

        return model;
    }

    @SuppressWarnings("unchecked")
	private static void parseTypes(Element root, CernatoModel model, Hashtable<String, TypeImplementation> types) throws DataFormatException {
        Element typesElement = root.getChild("types");
        if (typesElement == null) {
            throw new DataFormatException("Could not find types");
        }
        List<Element> typeElements = typesElement.getChildren();
        for (Iterator<Element> iterator = typeElements.iterator(); iterator.hasNext();) {
            Element element = iterator.next();
            if (element.getName().equals("type_textual")) {
                parseTextualType(element, model, types);
            } else if (element.getName().equals("type_numerical")) {
                parseNumericalType(element, model, types);
            }
        }
    }

    @SuppressWarnings("unchecked")
	private static void parseTextualType(Element element, CernatoModel model, Hashtable<String, TypeImplementation> types) throws DataFormatException {
        String id = element.getAttributeValue("id");
        if (id == null) {
            throw new DataFormatException("Type missing id");
        }
        String name = element.getChild("name").getText();
        if (name == null) {
            throw new DataFormatException("Type missing name");
        }
        TextualType newType = new TextualType(name);
        List<Element> typeValues = element.getChildren("text_value");
        for (Iterator<Element> iterator = typeValues.iterator(); iterator.hasNext();) {
            Element valueElement = iterator.next();
            newType.addValue(new TextualValue(valueElement.getText()));
        }
        List<Element> valueGroups = element.getChildren("text_value_group");
        for (Iterator<Element> iterator = valueGroups.iterator(); iterator.hasNext();) {
            Element valueGroupElement = iterator.next();
            String groupId = valueGroupElement.getAttributeValue("id");
            String groupName = valueGroupElement.getChild("name").getText();
            TextualValueGroup group = new TextualValueGroup(newType, groupName, groupId);
            List<Element> values = valueGroupElement.getChildren("text_value");
            for (Iterator<Element> iterator2 = values.iterator(); iterator2.hasNext();) {
                Element valueElement = iterator2.next();
                group.addValue(new TextualValue(valueElement.getText()));
            }
        }
        types.put(id, newType);
        model.getTypes().add(newType);
    }

    @SuppressWarnings("unchecked")
	private static void parseNumericalType(Element element, CernatoModel model, Hashtable<String, TypeImplementation> types) throws DataFormatException {
        String id = element.getAttributeValue("id");
        if (id == null) {
            throw new DataFormatException("Type missing id");
        }
        String name = element.getChild("name").getText();
        if (name == null) {
            throw new DataFormatException("Type missing name");
        }
        double typeMin = Double.parseDouble(element.getChild("minval").getText());
        double typeMax = Double.parseDouble(element.getChild("maxval").getText());
        int decimals = Integer.parseInt(element.getChild("decimals").getText());

        NumericalType newType = new NumericalType(name);
        newType.addValue(new NumericalValue(typeMin));
        newType.addValue(new NumericalValue(typeMax));
        newType.setNumberOfDecimals(decimals);
        
        List<Element> valueGroups = element.getChildren("num_value_group");
        for (Iterator<Element> iterator = valueGroups.iterator(); iterator.hasNext();) {
            Element valueGroupElement = iterator.next();
            String groupId = valueGroupElement.getAttributeValue("id");
            String groupName = valueGroupElement.getChild("name").getText();
            double min = Double.parseDouble(valueGroupElement.getChild("lower_border").getText());
            boolean minIncluded = valueGroupElement.getChild("lower_border").getAttributeValue("included").equals("yes");
            double max = Double.parseDouble(valueGroupElement.getChild("upper_border").getText());
            boolean maxIncluded = valueGroupElement.getChild("upper_border").getAttributeValue("included").equals("yes");
            new NumericalValueGroup(newType, groupName, groupId, min, minIncluded, max, maxIncluded);
        }
        types.put(id, newType);
        model.getTypes().add(newType);
    }

    @SuppressWarnings("unchecked")
	private static void parseProperties(Element root, CernatoModel model, Hashtable<String, TypeImplementation> types, Hashtable<String, Property> properties)
            throws DataFormatException {
        Element propertiesElement = root.getChild("properties");
        if (propertiesElement == null) {
            throw new DataFormatException("Could not find properties");
        }
        List<Element> propertyElements = propertiesElement.getChildren("property");
        for (Iterator<Element> iterator = propertyElements.iterator(); iterator.hasNext();) {
            Element propElem = iterator.next();
            String id = propElem.getAttributeValue("id");
            String name = propElem.getChild("name").getText();
            String typeref = propElem.getChild("type_ref").getAttributeValue("type");
            Property property = new Property(types.get(typeref), name);
            properties.put(id, property);
            model.getContext().add(property);
        }
    }

    @SuppressWarnings("unchecked")
	private static void parseObjects(Element root, CernatoModel model, Hashtable<String, CernatoObject> objects)
            throws DataFormatException {
        Element objectsElement = root.getChild("objects");
        if (objectsElement == null) {
            throw new DataFormatException("Could not find objects");
        }
        List<Element> objectElement = objectsElement.getChildren("object");
        for (Iterator<Element> iterator = objectElement.iterator(); iterator.hasNext();) {
            Element objElem = iterator.next();
            String id = objElem.getAttributeValue("id");
            String name = objElem.getChild("name").getText();
            CernatoObject object = new CernatoObject(name);
            objects.put(id, object);
            model.getContext().add(object);
        }
    }

    @SuppressWarnings("unchecked")
	private static void parseRelation(Element root, CernatoModel model, Hashtable<String, Property> properties, Hashtable<String, CernatoObject> objects)
            throws DataFormatException {
        Element relationElement = root.getChild("relation");
        if (relationElement == null) {
            throw new DataFormatException("Could not find relation");
        }
        List<Element> rowElems = relationElement.getChildren("row");
        for (Iterator<Element> iterator = rowElems.iterator(); iterator.hasNext();) {
            Element rowElem = iterator.next();
            String objectid = rowElem.getAttributeValue("object");
            CernatoObject object = objects.get(objectid);
            List<Element> cellElems = rowElem.getChildren("cell");
            for (Iterator<Element> iterator2 = cellElems.iterator(); iterator2.hasNext();) {
                Element cellElem = iterator2.next();
                String content = cellElem.getText();
                if(content == null || content.length() == 0) { // empty cell
                	continue;
                }
                String propertyid = cellElem.getAttributeValue("property");
                Property property = properties.get(propertyid);
                if (property.getType() instanceof TextualType) {
                    model.getContext().setRelationship(object, property, new TextualValue(content));
                } else if (property.getType() instanceof NumericalType) {
                    model.getContext().setRelationship(object, property, new NumericalValue(Double.parseDouble(content)));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
	private static void parseViews(Element rootElement, CernatoModel model, Hashtable<String, Property> properties)
            throws DataFormatException {
        Element viewsElem = rootElement.getChild("views");
        if (viewsElem == null) {
            throw new DataFormatException("Could not find views");
        }
        List<Element> viewElems = viewsElem.getChildren("view");
        for (Iterator<Element> iterator = viewElems.iterator(); iterator.hasNext();) {
            Element viewElem = iterator.next();
            String name = viewElem.getChild("name").getText();
            View view = new View(name);
            List<Element> criteriaElems = viewElem.getChildren("criterion");
            for (Iterator<Element> iterator2 = criteriaElems.iterator(); iterator2.hasNext();) {
                Element criterionElem = iterator2.next();
                Property property = properties.get(criterionElem.getChild("property_ref").
                        getAttributeValue("property"));
                ValueGroup valgroup = property.getType().getValueGroup(criterionElem.getChild("value_group_ref").
                        getAttributeValue("value_group"));
                view.addCriterion(new Criterion(property, valgroup));
            }
            model.getViews().add(view);
        }
    }
}
