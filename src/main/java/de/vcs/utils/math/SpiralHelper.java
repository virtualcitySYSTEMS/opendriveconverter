package de.vcs.utils.math;

import de.vcs.model.odr.geometry.Spiral;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

/**
 * sources:
 *  - https://opentrafficsim.org/docs/0.12.01/ots-core/jacoco/org.opentrafficsim.core.geometry/Clothoid.java.html
 *  - http://www.opendrive.org/tools/odrSpiral.zip
 */
public final class SpiralHelper {

    /**
     * S(x) for small x numerator.
     */
    static final double[] SN = {
            -2.99181919401019853726E3,
            7.08840045257738576863E5,
            -6.29741486205862506537E7,
            2.54890880573376359104E9,
            -4.42979518059697779103E10,
            3.18016297876567817986E11
    };
    /**
     * S(x) for small x denominator.
     */
    static final double[] SD = {
            2.81376268889994315696E2,
            4.55847810806532581675E4,
            5.17343888770096400730E6,
            4.19320245898111231129E8,
            2.24411795645340920940E10,
            6.07366389490084639049E11
    };
    /**
     * C(x) for small x numerator.
     */
    static final double[] CN = {
            -4.98843114573573548651E-8,
            9.50428062829859605134E-6,
            -6.45191435683965050962E-4,
            1.88843319396703850064E-2,
            -2.05525900955013891793E-1,
            9.99999999999999998822E-1
    };
    /**
     * C(x) for small x denominator.
     */
    static final double[] CD = {
            3.99982968972495980367E-12,
            9.15439215774657478799E-10,
            1.25001862479598821474E-7,
            1.22262789024179030997E-5,
            8.68029542941784300606E-4,
            4.12142090722199792936E-2,
            1.00000000000000000118E0
    };
    /**
     * Auxiliary function f(x) numerator.
     */
    static final double[] FN = {
            4.21543555043677546506E-1,
            1.43407919780758885261E-1,
            1.15220955073585758835E-2,
            3.45017939782574027900E-4,
            4.63613749287867322088E-6,
            3.05568983790257605827E-8,
            1.02304514164907233465E-10,
            1.72010743268161828879E-13,
            1.34283276233062758925E-16,
            3.76329711269987889006E-20
    };
    /**
     * Auxiliary function f(x) denominator.
     */
    static final double[] FD = {
            7.51586398353378947175E-1,
            1.16888925859191382142E-1,
            6.44051526508858611005E-3,
            1.55934409164153020873E-4,
            1.84627567348930545870E-6,
            1.12699224763999035261E-8,
            3.60140029589371370404E-11,
            5.88754533621578410010E-14,
            4.52001434074129701496E-17,
            1.25443237090011264384E-20
    };
    /**
     * Auxiliary function g(x) numerator.
     */
    static final double[] GN = {
            5.04442073643383265887E-1,
            1.97102833525523411709E-1,
            1.87648584092575249293E-2,
            6.84079380915393090172E-4,
            1.15138826111884280931E-5,
            9.82852443688422223854E-8,
            4.45344415861750144738E-10,
            1.08268041139020870318E-12,
            1.37555460633261799868E-15,
            8.36354435630677421531E-19,
            1.86958710162783235106E-22
    };
    /**
     * Auxiliary function g(x) denominator.
     */
    static final double[] GD = {
            1.47495759925128324529E0,
            3.37748989120019970451E-1,
            2.53603741420338795122E-2,
            8.14679107184306179049E-4,
            1.27545075667729118702E-5,
            1.04314589657571990585E-7,
            4.60680728146520428211E-10,
            1.10273215066240270757E-12,
            1.38796531259578871258E-15,
            8.39158816283118707363E-19,
            1.86958710162783236342E-22
    };

    /**
     * Compute a polynomial in x.
     *
     * @param x value
     * @param coef coefficients
     * @return polynomial in x
     */
    private static double polevl(final double x, final double[] coef) {
        double result = coef[0];
        for (double v : coef) {
            result = result * x + v;
        }
        return result;
    }

    /**
     * Compute a polynomial in x.
     * @param x value
     * @param coef coefficients
     * @return polynomial in x
     */
    private static double p1evl(final double x, final double[] coef) {
        double result = x + coef[0];
        for (double v : coef) {
            result = result * x + v;
        }
        return result;
    }

    /**
     * Approximate the Fresnel function.
     * @param xxa the xxa parameter
     * @return array with two double values c and s
     */
    private static double[] fresnel(final double xxa) {
        final double x = Math.abs(xxa);
        final double x2 = x * x;
        double cc, ss;
        if (x2 < 2.5625) {
            final double t = x2 * x2;
            ss = x * x2 * polevl(t, SN) / p1evl(t, SD);
            cc = x * polevl(t, CN) / polevl(t, CD);
        } else
            if (x > 36974.0) {
                cc = 0.5;
                ss = 0.5;
            } else {
                double t = Math.PI * x2;
                final double u = 1.0 / (t * t);
                t = 1.0 / t;
                final double f = 1.0 - u * polevl(u, FN) / p1evl(u, FD);
                final double g = t * polevl(u, GN) / p1evl(u, GD);
                t = Math.PI * 0.5 * x2;
                final double c = Math.cos(t);
                final double s = Math.sin(t);
                t = Math.PI * x;
                cc = 0.5 + (f * s - g * c) / t;
                ss = 0.5 - (f * c + g * s) / t;
            }
        if (xxa < 0.0) {
            cc = -cc;
            ss = -ss;
        }
        return new double[]{cc, ss};
    }

    /**
     * Approximate one point of the "standard" spiral (curvature at start is 0).
     * @param s run-length along spiral
     * @param cDot first derivative of curvature [1/m2]
     * @param initialCurvature curvature at start
     * @return array of three double values containing x, y, and tangent direction
     */
    private static double[] odrSpiral(final double s, final double cDot, final double initialCurvature) {
        double a = Math.sqrt(Math.PI / Math.abs(cDot));
        double[] xy = fresnel(initialCurvature + s / a);
        return new double[]{xy[0] * a, xy[1] * a * Math.signum(cDot), s * s * cDot * 0.5};
    }

    /**
     *
     * @param spiral ODR geometry
     * @param ds local s on geometry
     * @param t offset perpendicular to curve
     * @return uv point
     */
    public static Point calcUVPoint(Spiral spiral, double ds, double t) {
        double[] xyt = odrSpiral(ds,(spiral.getCurvEnd() - spiral.getCurvStart()) / spiral.getLength(), spiral.getCurvStart());
        Point uvpoint = new GeometryFactory().createPoint(new Coordinate(xyt[0], xyt[1]));
        double offsetX = 0.0;
        double offsetY = 0.0;
        if (t != 0.0) {
            Point nvpoint = calcNormalVector(xyt[2]);
            offsetX = nvpoint.getY() * t;
            offsetY = nvpoint.getY() * t;
        }
        return new GeometryFactory().createPoint(
                new Coordinate(uvpoint.getX() - offsetX, uvpoint.getY() + offsetY));
    }

    /**
     *
     * @param tau tangent angle
     * @return normal vector
     */
    private static Point calcNormalVector(double tau) {
        double tu = Math.cos(tau + Math.PI/2);
        double tv = Math.sin(tau + Math.PI/2);
        return new GeometryFactory().createPoint(new Coordinate(tu, tv));
    }
}
