/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.gui;

import org.tockit.cgs.model.Relation;
import org.tockit.cgs.model.Type;

import javax.swing.tree.*;
import java.util.Enumeration;

import com.sun.jimi.util.ArrayEnumeration;

public class RelationHierachyTreeNode implements TreeNode {
    private Relation relation;
    private TreeNode parent;
    private RelationHierachyTreeNode[] subtypeNodes;

    public RelationHierachyTreeNode(Relation relation, TreeNode parent) {
        super();
        this.relation = relation;
        this.parent = parent;
        Relation[] subtypes = relation.getDirectSubtypes();
        this.subtypeNodes = new RelationHierachyTreeNode[subtypes.length];
        for (int i = 0; i < subtypes.length; i++) {
            Relation subtype = subtypes[i];
            this.subtypeNodes[i] = new RelationHierachyTreeNode(subtype, this);
        }
    }

    public TreeNode getChildAt(int childIndex) {
        return this.subtypeNodes[childIndex];
    }

    public int getChildCount() {
        return this.subtypeNodes.length;
    }

    public TreeNode getParent() {
        return this.parent;
    }

    public int getIndex(TreeNode node) {
        for (int i = 0; i < subtypeNodes.length; i++) {
            RelationHierachyTreeNode subtypeNode = subtypeNodes[i];
            if(subtypeNode == node) {
                return i;
            }
        }
        return -1;
    }

    public boolean getAllowsChildren() {
        return true;
    }

    public boolean isLeaf() {
        return subtypeNodes.length == 0;
    }

    public Enumeration children() {
        return new ArrayEnumeration(this.subtypeNodes);
    }

    /// @todo signature might be better as flyout (tooltip)
    public String toString() {
        String retVal = this.relation.getName() + " (";
        Type[] signature = this.relation.getSignature();
        for (int i = 0; i < signature.length; i++) {
            Type type = signature[i];
            if(type == Type.UNIVERSAL) {
                retVal += "U";
            }
            else {
                retVal += type.getName();
            }
            if(i < signature.length - 1) {
                retVal += ",";
            }
        }
        retVal += ")";
        return retVal;
    }
}
