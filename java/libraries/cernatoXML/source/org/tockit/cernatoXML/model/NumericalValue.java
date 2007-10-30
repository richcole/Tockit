/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cernatoXML.model;

public class NumericalValue implements Value {
    private double value;

    public NumericalValue(double value) {
        this.value = value;
    }

    public String getDisplayString() {
        return String.valueOf(value);
    }

    public double getValue() {
        return value;
    }
    
    @Override
	public String toString() {
    	return getDisplayString();
    }
    
    @Override
	public boolean equals(Object other) {
        // copied from Double.equals()
        return (other instanceof NumericalValue)
               && (Double.doubleToLongBits(((NumericalValue)other).value) ==
                  Double.doubleToLongBits(this.value));
    }
    
    @Override
	public int hashCode() {
    	// copied from Double.hashCode()
        long bits = Double.doubleToLongBits(value);
        return (int)(bits ^ (bits >>> 32));
    }
    
    public boolean isLesserThan(Value other) {
    	if(!(other instanceof NumericalValue)) {
    		return false;
    	}
    	NumericalValue otherNV = (NumericalValue) other;
        return this.value < otherNV.value;
    }
    
    public boolean isEqual(Value other) {
        return this.equals(other);
    }
}
