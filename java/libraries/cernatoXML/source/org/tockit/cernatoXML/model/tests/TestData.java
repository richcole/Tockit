/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cernatoXML.model.tests;

import java.util.*;

import org.tockit.cernatoXML.model.*;

public class TestData {
    public static CernatoModel Model;
    public static NumericalType NumType1;
    public static NumericalValueGroup NumGroup1;
    public static NumericalValueGroup NumGroup2;
    public static NumericalValueGroup NumGroup3;
    public static NumericalValueGroup NumGroup4;
    public static NumericalValueGroup NumGroup5;
    public static NumericalValueGroup NumGroup6;
    public static NumericalValueGroup NumGroup7;
    public static NumericalValueGroup NumGroup8;
    public static TextualType TextType1;
    public static TextualValueGroup TextGroup1;
    public static TextualValueGroup TextGroup2;
    public static TextualValueGroup TextGroup3;
    public static TextualValueGroup TextGroup4;
    public static TextualType TextType2;
    public static TextualValueGroup TextGroup5;
    public static Property Property1;
    public static Property Property2;
    public static Property Property3;
    public static Property Property4;
    public static Property Property5;
    public static Property Property6;
    public static CernatoObject Object1;
    public static CernatoObject Object2;
    public static CernatoObject Object3;
    public static CernatoObject Object4;
    public static CernatoObject Object5;
    public static CernatoObject Object6;
    public static CernatoObject Object7;
    public static View View1;
    public static View View2;
    public static View View3;
    // this is Hashtable(objects -> Hashtable(properties -> List(all positive value groups)))
    public static Hashtable<CernatoObject, Hashtable<Property, Set<ValueGroup>>> ScaledRelation;

    static {
        initializeTestModel();
        createScaledRelation();
    }

    private static void initializeTestModel() {
        Model = new CernatoModel();

        NumType1 = new NumericalType("numtype1");

        NumGroup1 = new NumericalValueGroup(NumType1, "num1", "num1", 0, true, 3, true);
        NumGroup2 = new NumericalValueGroup(NumType1, "num2", "num2", 0, true, 3, false);
        NumGroup3 = new NumericalValueGroup(NumType1, "num3", "num3", 0, false, 3, false);
        NumGroup4 = new NumericalValueGroup(NumType1, "num4", "num4", 1, true, 2, true);
        NumGroup5 = new NumericalValueGroup(NumType1, "num5", "num5", 1, true, 2, true);
        NumGroup6 = new NumericalValueGroup(NumType1, "num6", "num6", 0, false, 3, true);
        NumGroup7 = new NumericalValueGroup(NumType1, "num7", "num7", 3, true, 5, true);
        NumGroup8 = new NumericalValueGroup(NumType1, "num8", "num8", 3, true, 3, true);

        TextType1 = new TextualType("texttype1");
        TextType2 = new TextualType("texttype2");

        TextGroup1 = new TextualValueGroup(TextType1, "text1", "text1");
        TextGroup1.addValue(new TextualValue("one"));
        TextGroup1.addValue(new TextualValue("two"));
        TextGroup2 = new TextualValueGroup(TextType1, "text2", "text2");
        TextGroup2.addValue(new TextualValue("one"));
        TextGroup3 = new TextualValueGroup(TextType1, "text3", "text3");
        TextGroup3.addValue(new TextualValue("two"));
        TextGroup4 = new TextualValueGroup(TextType1, "text4", "text4");
        TextGroup5 = new TextualValueGroup(TextType2, "text5", "text5");
        TextGroup5.addValue(new TextualValue("two"));

        Model.getTypes().add(NumType1);
        Model.getTypes().add(TextType1);
        Model.getTypes().add(TextType2);

        CernatoTable context = Model.getContext();

        Property1 = new Property(NumType1, "prop1");
        Property2 = new Property(NumType1, "prop2");
        Property3 = new Property(TextType1, "prop3");
        Property4 = new Property(TextType1, "prop4");
        Property5 = new Property(TextType2, "prop5");
        Property6 = new Property(TextType2, "prop6");

        context.add(Property1);
        context.add(Property2);
        context.add(Property3);
        context.add(Property4);
        context.add(Property5);
        context.add(Property6);

        Object1 = new CernatoObject("object1");
        Object2 = new CernatoObject("object2");
        Object3 = new CernatoObject("object3");
        Object4 = new CernatoObject("object4");
        Object5 = new CernatoObject("object5");
        Object6 = new CernatoObject("object6");
        Object7 = new CernatoObject("object7");

        context.add(Object1);
        context.add(Object2);
        context.add(Object3);
        context.add(Object4);
        context.add(Object5);
        context.add(Object6);
        context.add(Object7);

        context.setRelationship(Object1, Property1, new NumericalValue(0));
        context.setRelationship(Object1, Property2, new NumericalValue(1));
        context.setRelationship(Object1, Property3, new TextualValue("one"));
        context.setRelationship(Object1, Property4, new TextualValue("two"));
        context.setRelationship(Object1, Property5, new TextualValue("one"));
        context.setRelationship(Object1, Property6, new TextualValue("two"));

        context.setRelationship(Object2, Property1, new NumericalValue(0.5));
        context.setRelationship(Object2, Property2, new NumericalValue(1.5));
        context.setRelationship(Object2, Property3, new TextualValue("two"));
        context.setRelationship(Object2, Property4, new TextualValue("two"));
        context.setRelationship(Object2, Property5, new TextualValue("one"));
        context.setRelationship(Object2, Property6, new TextualValue("two"));

        context.setRelationship(Object3, Property1, new NumericalValue(1));
        context.setRelationship(Object3, Property2, new NumericalValue(2));
        context.setRelationship(Object3, Property3, new TextualValue("one"));
        context.setRelationship(Object3, Property4, new TextualValue("two"));
        context.setRelationship(Object3, Property5, new TextualValue("two"));
        context.setRelationship(Object3, Property6, new TextualValue("two"));

        context.setRelationship(Object4, Property1, new NumericalValue(1.5));
        context.setRelationship(Object4, Property2, new NumericalValue(2.5));
        context.setRelationship(Object4, Property3, new TextualValue("one"));
        context.setRelationship(Object4, Property4, new TextualValue("one"));
        context.setRelationship(Object4, Property5, new TextualValue("one"));
        context.setRelationship(Object4, Property6, new TextualValue("two"));

        context.setRelationship(Object5, Property1, new NumericalValue(2.5));
        context.setRelationship(Object5, Property2, new NumericalValue(0.5));
        context.setRelationship(Object5, Property3, new TextualValue("one"));
        context.setRelationship(Object5, Property4, new TextualValue("two"));
        context.setRelationship(Object5, Property5, new TextualValue("one"));
        context.setRelationship(Object5, Property6, new TextualValue("one"));

        context.setRelationship(Object6, Property1, new NumericalValue(1));
        context.setRelationship(Object6, Property2, new NumericalValue(0));
        context.setRelationship(Object6, Property3, new TextualValue("one"));
        context.setRelationship(Object6, Property4, new TextualValue("one"));
        context.setRelationship(Object6, Property5, new TextualValue("two"));
        context.setRelationship(Object6, Property6, new TextualValue("two"));

        context.setRelationship(Object7, Property1, new NumericalValue(3));
        context.setRelationship(Object7, Property2, new NumericalValue(1));
        context.setRelationship(Object7, Property3, new TextualValue("two"));
        context.setRelationship(Object7, Property4, new TextualValue("one"));
        context.setRelationship(Object7, Property5, new TextualValue("one"));
        context.setRelationship(Object7, Property6, new TextualValue("two"));

        Vector<View> views = Model.getViews();

        View1 = new View("View1");
        View1.addCriterion(new Criterion(Property1, NumGroup1));
        View1.addCriterion(new Criterion(Property1, NumGroup2));
        View1.addCriterion(new Criterion(Property1, NumGroup3));
        View1.addCriterion(new Criterion(Property1, NumGroup4));
        View1.addCriterion(new Criterion(Property1, NumGroup5));
        View1.addCriterion(new Criterion(Property1, NumGroup6));
        View1.addCriterion(new Criterion(Property1, NumGroup7));
        View1.addCriterion(new Criterion(Property1, NumGroup8));

        View2 = new View("View2");
        View2.addCriterion(new Criterion(Property1, NumGroup1));
        View2.addCriterion(new Criterion(Property1, NumGroup2));
        View2.addCriterion(new Criterion(Property1, NumGroup3));
        View2.addCriterion(new Criterion(Property1, NumGroup4));
        View2.addCriterion(new Criterion(Property2, NumGroup1));
        View2.addCriterion(new Criterion(Property2, NumGroup2));
        View2.addCriterion(new Criterion(Property2, NumGroup3));
        View2.addCriterion(new Criterion(Property2, NumGroup4));

        View3 = new View("View3");
        View3.addCriterion(new Criterion(Property3, TextGroup1));
        View3.addCriterion(new Criterion(Property3, TextGroup2));
        View3.addCriterion(new Criterion(Property3, TextGroup3));
        View3.addCriterion(new Criterion(Property3, TextGroup4));
        View3.addCriterion(new Criterion(Property4, TextGroup1));
        View3.addCriterion(new Criterion(Property4, TextGroup2));
        View3.addCriterion(new Criterion(Property4, TextGroup3));
        View3.addCriterion(new Criterion(Property4, TextGroup4));
        View3.addCriterion(new Criterion(Property5, TextGroup5));
        View3.addCriterion(new Criterion(Property6, TextGroup5));

        views.add(View1);
        views.add(View2);
        views.add(View3);
    }

    private static void createScaledRelation() {
        ScaledRelation = new Hashtable<CernatoObject, Hashtable<Property, Set<ValueGroup>>>();
        Hashtable<Property, Set<ValueGroup>> propMap;
        Set<ValueGroup> positives;

        // object 1
        propMap = new Hashtable<Property, Set<ValueGroup>>();
        positives = new HashSet<ValueGroup>();
        positives.add(NumGroup1);
        positives.add(NumGroup2);
        propMap.put(Property1, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(NumGroup1);
        positives.add(NumGroup2);
        positives.add(NumGroup3);
        positives.add(NumGroup4);
        positives.add(NumGroup5);
        positives.add(NumGroup6);
        propMap.put(Property2, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(TextGroup1);
        positives.add(TextGroup2);
        propMap.put(Property3, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(TextGroup1);
        positives.add(TextGroup3);
        propMap.put(Property4, positives);
        positives = new HashSet<ValueGroup>();
        propMap.put(Property5, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(TextGroup5);
        propMap.put(Property6, positives);
        ScaledRelation.put(Object1, propMap);

        // object 2
        propMap = new Hashtable<Property, Set<ValueGroup>>();
        positives = new HashSet<ValueGroup>();
        positives.add(NumGroup1);
        positives.add(NumGroup2);
        positives.add(NumGroup3);
        positives.add(NumGroup6);
        propMap.put(Property1, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(NumGroup1);
        positives.add(NumGroup2);
        positives.add(NumGroup3);
        positives.add(NumGroup4);
        positives.add(NumGroup5);
        positives.add(NumGroup6);
        propMap.put(Property2, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(TextGroup1);
        positives.add(TextGroup3);
        propMap.put(Property3, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(TextGroup1);
        positives.add(TextGroup3);
        propMap.put(Property4, positives);
        positives = new HashSet<ValueGroup>();
        propMap.put(Property5, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(TextGroup5);
        propMap.put(Property6, positives);
        ScaledRelation.put(Object2, propMap);

        // object 3
        propMap = new Hashtable<Property, Set<ValueGroup>>();
        positives = new HashSet<ValueGroup>();
        positives.add(NumGroup1);
        positives.add(NumGroup2);
        positives.add(NumGroup3);
        positives.add(NumGroup4);
        positives.add(NumGroup5);
        positives.add(NumGroup6);
        propMap.put(Property1, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(NumGroup1);
        positives.add(NumGroup2);
        positives.add(NumGroup3);
        positives.add(NumGroup4);
        positives.add(NumGroup5);
        positives.add(NumGroup6);
        propMap.put(Property2, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(TextGroup1);
        positives.add(TextGroup2);
        propMap.put(Property3, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(TextGroup1);
        positives.add(TextGroup3);
        propMap.put(Property4, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(TextGroup5);
        propMap.put(Property5, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(TextGroup5);
        propMap.put(Property6, positives);
        ScaledRelation.put(Object3, propMap);

        // object 4
        propMap = new Hashtable<Property, Set<ValueGroup>>();
        positives = new HashSet<ValueGroup>();
        positives.add(NumGroup1);
        positives.add(NumGroup2);
        positives.add(NumGroup3);
        positives.add(NumGroup4);
        positives.add(NumGroup5);
        positives.add(NumGroup6);
        propMap.put(Property1, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(NumGroup1);
        positives.add(NumGroup2);
        positives.add(NumGroup3);
        positives.add(NumGroup6);
        propMap.put(Property2, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(TextGroup1);
        positives.add(TextGroup2);
        propMap.put(Property3, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(TextGroup1);
        positives.add(TextGroup2);
        propMap.put(Property4, positives);
        positives = new HashSet<ValueGroup>();
        propMap.put(Property5, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(TextGroup5);
        propMap.put(Property6, positives);
        ScaledRelation.put(Object4, propMap);

        // object 5
        propMap = new Hashtable<Property, Set<ValueGroup>>();
        positives = new HashSet<ValueGroup>();
        positives.add(NumGroup1);
        positives.add(NumGroup2);
        positives.add(NumGroup3);
        positives.add(NumGroup6);
        propMap.put(Property1, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(NumGroup1);
        positives.add(NumGroup2);
        positives.add(NumGroup3);
        positives.add(NumGroup6);
        propMap.put(Property2, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(TextGroup1);
        positives.add(TextGroup2);
        propMap.put(Property3, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(TextGroup1);
        positives.add(TextGroup3);
        propMap.put(Property4, positives);
        positives = new HashSet<ValueGroup>();
        propMap.put(Property5, positives);
        positives = new HashSet<ValueGroup>();
        propMap.put(Property6, positives);
        ScaledRelation.put(Object5, propMap);

        // object 6
        propMap = new Hashtable<Property, Set<ValueGroup>>();
        positives = new HashSet<ValueGroup>();
        positives.add(NumGroup1);
        positives.add(NumGroup2);
        positives.add(NumGroup3);
        positives.add(NumGroup4);
        positives.add(NumGroup5);
        positives.add(NumGroup6);
        propMap.put(Property1, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(NumGroup1);
        positives.add(NumGroup2);
        propMap.put(Property2, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(TextGroup1);
        positives.add(TextGroup2);
        propMap.put(Property3, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(TextGroup1);
        positives.add(TextGroup2);
        propMap.put(Property4, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(TextGroup5);
        propMap.put(Property5, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(TextGroup5);
        propMap.put(Property6, positives);
        ScaledRelation.put(Object6, propMap);

        // object 7
        propMap = new Hashtable<Property, Set<ValueGroup>>();
        positives = new HashSet<ValueGroup>();
        positives.add(NumGroup1);
        positives.add(NumGroup6);
        positives.add(NumGroup7);
        positives.add(NumGroup8);
        propMap.put(Property1, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(NumGroup1);
        positives.add(NumGroup2);
        positives.add(NumGroup3);
        positives.add(NumGroup4);
        positives.add(NumGroup5);
        positives.add(NumGroup6);
        propMap.put(Property2, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(TextGroup1);
        positives.add(TextGroup3);
        propMap.put(Property3, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(TextGroup1);
        positives.add(TextGroup2);
        propMap.put(Property4, positives);
        positives = new HashSet<ValueGroup>();
        propMap.put(Property5, positives);
        positives = new HashSet<ValueGroup>();
        positives.add(TextGroup5);
        propMap.put(Property6, positives);
        ScaledRelation.put(Object7, propMap);
    }

    static boolean isInScaledRelation(CernatoObject object, Criterion criterion) {
        Hashtable<Property,Set<ValueGroup>> propMap = ScaledRelation.get(object);
        if (propMap == null) {
            return false;
        }
        Set<ValueGroup> positives = propMap.get(criterion.getProperty());
        if (positives == null) {
            return false;
        }
        return positives.contains(criterion.getValueGroup());
    }
}
