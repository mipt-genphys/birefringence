package ru.mipt.physics.birefringence

import static java.lang.Math.*;
import ru.mipt.physics.birefringence.Vector

/**
 * Created by darksnake on 18-May-16.
 */
class Evaluator {
    Vector nVector(Vector phi1, Vector psi, double a) {
        Vector phi2 = psi - phi1 + a;
        return (phi1.sin()**2 + phi2.sin()**2 + phi1.sin() * phi2.sin() * cos(a) * 2).sqrt() / sin(a);
    }

    double n(double phi1, double psi, double a) {
        double phi2 = psi - phi1 + a;
        return sqrt(sin(phi1)**2 + sin(phi2)**2 + sin(phi1) * sin(phi2) * cos(a) * 2) / sin(a);
    }

    Vector costhVector(Vector phi1, Vector n) {
        return phi1.sin() / n;
    }

    double costh(double phi1, double n) {
        return sin(phi1) / n;
    }

    double sigman(double phi1, double psi, double n, double a, double aErr, double phi1Err, double psiErr) {
        double phi2 = psi - phi1 + a;
        def nal = (0.5 * sin(2 * phi1) - 0.5 * sin(2 * phi2) -
                cos(a) * sin(phi1 - phi2)) / (2 * (n * sin(a)**2));
        def nb = -(sin(phi2) * cos(phi2) +
                cos(a) * sin(phi1) * cos(phi2)) / (n * sin(a)**2);
        def na = -(n * cos(a)) / sin(a) + (0.5 * sin(2 * phi2) +
                sin(phi1) * cos(a + phi2)) / (n * sin(a)**2)
        double res = sqrt((2 * phi1Err * nal)**2 + (nb * psiErr)**2 + (na * aErr)**2)
        return res;
    }

    Vector sigmanVector(Vector phi1, Vector psi, Vector n, double a, double aErr, double phi1Err, double psiErr) {
        Vector phi2 = psi - phi1 + a;
        double[] res = new double[phi1.size()];
        for (int i = 0; i < phi1.size(); i++) {
            def nal = (0.5 * sin(2 * phi1[i]) - 0.5 * sin(2 * phi2[i]) -
                    cos(a) * sin(phi1[i] - phi2[i])) / (2 * (n[i] * sin(a)**2));
            def nb = -(sin(phi2[i]) * cos(phi2[i]) +
                    cos(a) * sin(phi1[i]) * cos(phi2[i])) / (n[i] * sin(a)**2);
            def na = -(n[i] * cos(a)) / sin(a) + (0.5 * sin(2 * phi2[i]) +
                    sin(phi1[i]) * cos(a + phi2[i])) / (n[i] * sin(a)**2)
            res[i] = sqrt((2 * phi1Err * nal)**2 + (nb * psiErr)**2 + (na * aErr)**2)
        }
        return new Vector(res);
    }


    /**
     * Adjustment check
     * @param data
     * @return
     */
    @Deprecated
    def checkAdjustment(Vector nVector, Vector costhVector, Vector sigmanVector) {

        double chi2Min = Double.MAX_VALUE;
        double minSlope = 0;
        double minBase = 0;

        //TODO replace by analytic solution
        // Direct 2-parameter search
        for (double base = 1; base < 2; base += 0.002) {
            for (double slope = -0.2; slope < 0.2; slope += 0.002) {
                def chi2 = (((nVector - costhVector**2 * slope - base) / sigmanVector)**2)// chi2 expression
                        .values() // as double array
                        .sum(); // sum of the array
                if (chi2 < chi2Min) {
                    chi2Min = chi2;
                    minSlope = slope;
                    minBase = base
                }
            }
        }
        return new Tuple<>(minBase, minSlope, chi2Min);
    }

    /**
     * calculating no as a weighted average
     * @param data
     */
    def average(Vector nVector, Vector sigmanVector) {
        Vector weights = new Vector(sigmanVector.values().collect { 1 / it**2 })
        // weighted average
        double sum = (nVector * weights).values().sum()
        double weight = weights.values().sum()
        def no = sum / weight
        def noErr = sqrt(1 / weight)
        return new Tuple2<>(no, noErr)
    }

    def calculate(Vector nVector, Vector costhVector, Vector sigmanVector) {
        double[][] m = new double[2][2];//initialized with zeroes
        double b0 = 0;
        double b1 = 0;
        for (int i = 0; i < nVector.size(); i++) {
            def d1 = nVector[i]**6 / sigmanVector[i]**4 / 4d;
            def d2 = (costhVector[i])**2
            m[0][0] += d1
            m[0][1] += d1 * d2
            m[1][1] += d2 * d2 * d1;
            b0 += d1 / nVector[i]**2;
            b1 += d1 * d2 / nVector[i]**2;
        }
        m[1][0] = m[0][1]
        def det = m[0][0] * m[1][1] - m[1][0] * m[0][1];
        double[][] mInv = new double[2][2];
        mInv[0][0] = m[1][1] / det;
        mInv[0][1] = -m[1][0] / det;
        mInv[1][0] = -m[0][1] / det;
        mInv[1][1] = m[0][0] / det;

        def base = mInv[0][0] * b0 + mInv[0][1] * b1;
        def slope = mInv[1][0] * b0 + mInv[1][1] * b1;

        def ne = 1 / sqrt(base);
        def no = 1 / sqrt(slope + ne**(-2));
        def neErr = sqrt(mInv[0][0]) * 0.5 * base **(-3 / 2)
        return new Tuple(no, ne, neErr);
    }

    /**
     * The angle of least deviation
     * @param psiomin
     * @param psiemin
     * @param a
     * @return
     */
    def leastDev(double psiomin, double psiemin, double a) {
        def no = sin((psiomin + a) / 2) / sin(a / 2)
        def ne = sin((psiemin + a) / 2) / sin(a / 2)
        return new Tuple2<>(no, ne);
    }

    /**
     * Total reflection angle
     * @param phi1o
     * @param phi1e
     * @param a
     */
    def totalRef(double phi1o, double phi1e, double a) {
        def no = sqrt((sin(phi1o))**2 + 1 + (2 * sin(phi1o) * cos(a))) / sin(a);
        def ne = sqrt((sin(phi1e))**2 + 1 + (2 * sin(phi1e) * cos(a))) / sin(a);
        return new Tuple2<>(no, ne);
    }
}
