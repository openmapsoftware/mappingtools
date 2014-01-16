package com.openMap1.mapper.core;

/**
 * stores a multiplicity constraint for the end of an association or a property.
 * Min multiplicity can be 0 or 1, denoted by boolean minIs1.
 * Max multiplicity can be 1 or N, denoted by boolean maxIs1.
 */

public class multiplicity {

    private boolean minIs1;
    private boolean maxIs1;

    public multiplicity(boolean min1, boolean max1)
    {
        minIs1 = min1;
        maxIs1 = max1;
    }

    public boolean minIs1() {return minIs1;}
    public boolean maxIs1() {return maxIs1;}

    public void setMinIs1(boolean min1) {minIs1 = min1;}
    public void setMaxIs1(boolean max1) {maxIs1 = max1;}

    public String stringForm()
    {
        String sMin = "0";
        if (minIs1) sMin = "1";
        String sMax = "N";
        if (maxIs1) sMax = "1";
        return "(" + sMin + ".." + sMax + ")";
    }
}