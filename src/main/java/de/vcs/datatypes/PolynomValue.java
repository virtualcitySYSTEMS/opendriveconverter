package de.vcs.datatypes;

/**
 * Serves as container class for u and v values calculated by {@link de.vcs.utils.math.ParamPolynomHelper}
 */
public class PolynomValue {

    private double u;
    private double v;

    public PolynomValue() {
    }

    /**
     * @param u u value of polynom
     * @param v v value of polynom
     */
    public PolynomValue(double u, double v) {
        this.u = u;
        this.v = v;
    }

    /**
     * @return the u value
     */
    public double getU() {
        return u;
    }

    /**
     * @param u the u value
     */
    public void setU(double u) {
        this.u = u;
    }

    /**
     * @return the v value
     */
    public double getV() {
        return v;
    }

    /**
     * @param v the u value
     */
    public void setV(double v) {
        this.v = v;
    }
}
